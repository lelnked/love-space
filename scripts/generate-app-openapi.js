#!/usr/bin/env node
// scripts/generate-app-openapi.js
// 从 love-space-app 的 Java 源码直接生成 OpenAPI 3.0 JSON（不依赖 springdoc/swagger jar）。
//
//   node scripts/generate-app-openapi.js            → love-space-app/docs/openapi.json
//   node scripts/generate-app-openapi.js --check    → 只校验不落盘（CI 用）
//
// 信息来源（按优先级）：
//   接口      controller 的 @RequestMapping / @GetMapping 等注解 + 方法签名
//   摘要/说明  controller 类与方法 javadoc
//   参数说明   方法 javadoc @param → PageQuery 的 @param（page/size）→ 内置字典
//   字段说明   record javadoc @param → 同模块 entity 字段 javadoc → 内置字典
//   枚举说明   enum 常量 javadoc
// 缺说明的字段/参数在末尾汇总打印，不静默。
//
// ponytail: 正则解析而非 AST——本项目 controller/record 写法高度一致，够用；
// 若日后出现多行注解、泛型嵌套超两层等写法解析失败，再换 java-parser。

const fs = require('fs');
const path = require('path');

const ROOT = path.resolve(__dirname, '..');
const SRC = path.join(ROOT, 'love-space-app/src/main/java');
const OUT = path.join(ROOT, 'love-space-app/docs/openapi.json');
const CHECK_ONLY = process.argv.includes('--check');

// ---------- 内置字典：兜底的中文说明（键可为 "Record.field" / "Controller.param" / 裸名） ----------
const FIELD_DICT = {
  id: 'ID（UUID）',
  name: '名称',
  title: '标题',
  subtitle: '副标题',
  content: '内容',
  description: '描述',
  introduction: '介绍',
  sortOrder: '排序号，升序展示',
  createdAt: '创建时间（ISO-8601）',
  updatedAt: '更新时间（ISO-8601）',
  contentHtml: '正文富文本 HTML（img src 已替换为签名 URL）',
  detailHtml: '详情富文本 HTML（img src 已替换为签名 URL）',
  categoryIds: '所属栏目 ID 列表',
  // 图片类字段：DTO 里是 ImageResponse，实体注释描述的是 objectKey 存储，不适用
  image: '图片（objectKey + 签名 URL）',
  images: '图片列表（objectKey + 签名 URL）',
  banner: 'Banner 图片（objectKey + 签名 URL）',
  thumbnail: '缩略图（objectKey + 签名 URL）',
  icon: '图标（objectKey + 签名 URL）',
  logo: 'LOGO（objectKey + 签名 URL）',
  avatar: '头像（objectKey + 签名 URL）',
  backgroundImage: '背景图（objectKey + 签名 URL）',
  'RouteDetailResponse.city': '所属城市对象（id + 中文名）；城市记录已删除时为 null',
  'RouteDetailResponse.ambassador': '关联爱女大使信息',
  'RouteItemResponse.city': '所属城市对象（id + 中文名）；城市记录已删除时为 null',
  'RouteItemResponse.ambassadorName': '关联爱女大使名称',
  'RouteDetailResponse.spots': '地点列表（按添加顺序）',
  'FeaturedItemResponse.city': '关联城市（id + 名称），跳转由 App 端自行决定',
  'FeaturedItemResponse.description': '推荐说明',
  'FeaturedItemResponse_CityRef.id': '城市 ID',
  'FeaturedItemResponse_CityRef.name': '城市中文名',
  'RouteCityResponse.id': '城市 ID',
  'RouteCityResponse.name': '城市中文名',
  'ActivityDetailResponse.periods': '适合周期（Period 枚举名列表：MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL）',
  'ActivityItemResponse.periods': '适合周期（Period 枚举名列表：MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL）',
  'ActivityItemResponse.level': '活动级别（L1/L2/L3）',
  'ActivityItineraryItem.title': '子条目标题',
  'ActivityItineraryItem.content': '子条目内容',
  'AmbassadorView.name': '大使名称',
  'AmbassadorView.tags': '大使标签（最多 3 条）',
  'RouteSpotItemResponse.name': '地点名称',
  'RouteSpotItemResponse.introduction': '地点介绍',
  'FeaturedCycleItemResponse.id': '推荐条目 ID',
  'FeaturedCycleItemResponse.type': '内容类型（ACTIVITY / ROUTE / ARTICLE），决定哪些关联 id 与文案字段有值',
  'RouteDetailResponse.cityName': '所属城市中文名；可能为空',
};
const PARAM_DICT = {
  cityId: '城市 ID',
  categoryId: '分类 ID',
  'ArticleController.categoryId': '文章栏目 ID',
  merchantId: '商户 ID',
  recommendListId: '推荐清单 ID；传入时仅返回该清单内的上架商户，按清单内排序号升序',
  ambassadorId: '爱女大使 ID',
  cityName: '城市中文名（精确匹配）；城市不存在时返回空数组',
  limit: '返回条数，默认 3，最大 20；非正数或缺省回落默认，超过上限收敛为 20',
  recommended: '是否只看推荐评价；缺省返回全部',
  type: '内容类型过滤；缺省返回全部类型',
  period: '按推荐生理周期过滤；缺省不过滤',
  page: '页码，从 1 开始；缺省或小于 1 回落 1',
  size: '每页大小，仅支持 20 或 30；其他值回落 20',
};
// 由控制器中文名推导 {id} 说明，如「活动只读 API」→「活动 ID」
function idParamDesc(tagCn) { return `${tagCn} ID`; }

// ---------- 工具 ----------
function walk(dir, out = []) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    e.isDirectory() ? walk(p, out) : p.endsWith('.java') && out.push(p);
  }
  return out;
}
function cleanDoc(raw) {
  if (!raw) return '';
  return raw
    .split('\n').map((l) => l.replace(/^\s*\*\s?/, '')).join('\n')
    .replace(/\{@(?:code|link|value)\s+#?([^}]*)\}/g, '$1')
    .replace(/<p>/g, '\n').replace(/<\/?ul>/g, '').replace(/<li>/g, '- ').replace(/<\/li>/g, '')
    .replace(/<[^>]+>/g, '')
    .split('\n').map((l) => l.trim()).filter(Boolean).join('\n')
    .trim();
}
function parseJavadoc(raw) {
  const text = cleanDoc(raw);
  const lines = text.split('\n');
  const desc = [];
  const params = {};
  let cur = null;
  for (const l of lines) {
    const m = l.match(/^@param\s+(\w+)\s*(.*)$/);
    if (m) { cur = m[1]; params[cur] = m[2]; continue; }
    if (l.startsWith('@')) { cur = null; continue; }
    if (cur) params[cur] += ' ' + l; else desc.push(l);
  }
  for (const k in params) params[k] = params[k].replace(/\s+/g, ' ').trim();
  return { desc: desc.join('\n').trim(), params };
}
function summaryOf(desc) {
  const first = desc.split('\n')[0];
  return first.split(/[。；;]/)[0].trim();
}
function stripTrailingPeriod(s) { return s.replace(/[。]+$/, ''); }
function splitTopLevel(s) {
  const out = []; let depth = 0, cur = '';
  for (const ch of s) {
    if (ch === '<' || ch === '(') depth++;
    if (ch === '>' || ch === ')') depth--;
    if (ch === ',' && depth === 0) { out.push(cur); cur = ''; } else cur += ch;
  }
  if (cur.trim()) out.push(cur);
  return out.map((x) => x.trim()).filter(Boolean);
}

// ---------- 索引：records / enums / entity 字段注释 ----------
const JAVADOC = '(?:/\\*\\*((?:(?!\\*/)[\\s\\S])*?)\\*/\\s*)?';
const ANNOS = '(?:@\\w+(?:\\([^)]*\\))?\\s*)*';
const records = {};   // name → {javadoc, components:[{type,name}], module, outer}
const enums = {};     // name → {javadoc, constants:[{name, doc}]}
const entityFields = {}; // module → {fieldName → doc}

function moduleOf(file) {
  const m = file.match(/modules[\\/]([^\\/]+)[\\/]/);
  return m ? m[1] : 'common';
}

for (const file of walk(SRC)) {
  const src = fs.readFileSync(file, 'utf8');
  const mod = moduleOf(file);
  const outerMatch = src.match(/public\s+(?:final\s+)?(?:record|class|enum)\s+(\w+)/);
  const outer = outerMatch ? outerMatch[1] : path.basename(file, '.java');

  const recRe = new RegExp(JAVADOC + ANNOS + 'public\\s+(?:static\\s+)?record\\s+(\\w+)(?:<[^>]*>)?\\s*\\(([\\s\\S]*?)\\)\\s*(?:implements[^{]*)?\\{', 'g');
  let m;
  while ((m = recRe.exec(src))) {
    const [, doc, name, paramList] = m;
    const components = splitTopLevel(paramList).map((p) => {
      const cleaned = p.replace(/@\w+(?:\([^)]*\))?\s*/g, '').trim();
      const idx = cleaned.lastIndexOf(' ');
      return { type: cleaned.slice(0, idx).trim(), name: cleaned.slice(idx + 1).trim() };
    });
    const isNested = name !== outer;
    const key = isNested ? `${outer}_${name}` : name;
    records[key] = { javadoc: parseJavadoc(doc), components, module: mod, simple: name, outer: isNested ? outer : null };
  }

  const enumRe = new RegExp(JAVADOC + ANNOS + 'public\\s+enum\\s+(\\w+)\\s*\\{([\\s\\S]*?)\\}', 'g');
  while ((m = enumRe.exec(src))) {
    const [, doc, name, body] = m;
    const constants = [];
    const cRe = new RegExp(JAVADOC + '\\b([A-Z][A-Z0-9_]*)\\b\\s*(?:\\([^)]*\\))?\\s*[,;]?', 'g');
    let c;
    while ((c = cRe.exec(body))) constants.push({ name: c[2], doc: stripTrailingPeriod(cleanDoc(c[1])) });
    enums[name] = { javadoc: parseJavadoc(doc), constants };
  }

  if (/@Entity\b/.test(src)) {
    const fRe = new RegExp(JAVADOC + '(?:@[^\\n]*\\n\\s*)*private\\s+[\\w<>, ?]+\\s+(\\w+)\\s*(?:=|;)', 'g');
    entityFields[mod] = entityFields[mod] || {};
    while ((m = fRe.exec(src))) {
      if (m[1]) entityFields[mod][m[2]] = stripTrailingPeriod(cleanDoc(m[1]))
        .replace(/，?jsonb/g, '').replace(/（无 FK）/g, '').replace(/，创建后不可变/g, '').trim();
    }
  }
}

// ---------- 类型 → schema ----------
const schemas = {};
const gaps = [];
// 这些字段实体注释描述的是存储形态（objectKey / jsonb），DTO 已换成签名 URL，字典优先于实体
const DICT_FIRST = new Set(['image', 'images', 'banner', 'thumbnail', 'icon', 'logo', 'avatar', 'backgroundImage', 'detailHtml', 'contentHtml']);

function fieldDesc(recordKey, rec, comp) {
  const fromDoc = rec.javadoc.params[comp.name];
  if (fromDoc) return fromDoc;
  if (DICT_FIRST.has(comp.name) && FIELD_DICT[comp.name] && !FIELD_DICT[`${recordKey}.${comp.name}`]) {
    return FIELD_DICT[comp.name];
  }
  const dictKeyed = FIELD_DICT[`${recordKey}.${comp.name}`] || FIELD_DICT[`${rec.simple}.${comp.name}`];
  if (dictKeyed) return dictKeyed;
  const fromEntity = (entityFields[rec.module] || {})[comp.name];
  if (fromEntity) return fromEntity;
  if (FIELD_DICT[comp.name]) return FIELD_DICT[comp.name];
  gaps.push(`字段 ${recordKey}.${comp.name}`);
  return '';
}

function resolveRecordKey(typeName, fromRecord) {
  if (fromRecord && records[`${fromRecord}_${typeName}`]) return `${fromRecord}_${typeName}`;
  if (records[typeName]) return typeName;
  return null;
}

function ensureEnum(name) {
  if (schemas[name]) return;
  const e = enums[name];
  const lines = e.constants.map((c) => `- \`${c.name}\`${c.doc ? '：' + c.doc : ''}`);
  schemas[name] = {
    type: 'string',
    enum: e.constants.map((c) => c.name),
    description: [e.javadoc.desc.split('\n')[0], ...lines].filter(Boolean).join('\n'),
  };
}

function ensureRecord(key) {
  if (schemas[key]) return;
  const rec = records[key];
  schemas[key] = { type: 'object' }; // 先占位，防止自引用死循环
  const properties = {};
  for (const comp of rec.components) {
    const schema = typeToSchema(comp.type, rec.outer || rec.simple);
    const d = fieldDesc(key, rec, comp);
    if (d) schema.description = schema.$ref ? undefined : d;
    // $ref 旁不能放 description（OAS 3.0），用 allOf 包一层
    properties[comp.name] = schema.$ref && d ? { allOf: [schema], description: d } : schema;
  }
  schemas[key] = { type: 'object', description: rec.javadoc.desc || undefined, properties };
}

function typeToSchema(type, fromRecord) {
  type = type.trim();
  const generic = type.match(/^(\w+)<(.*)>$/);
  const base = generic ? generic[1] : type;
  const args = generic ? splitTopLevel(generic[2]) : [];
  switch (base) {
    case 'String': return { type: 'string' };
    case 'UUID': return { type: 'string', format: 'uuid' };
    case 'int': case 'Integer': return { type: 'integer', format: 'int32' };
    case 'long': case 'Long': return { type: 'integer', format: 'int64' };
    case 'short': case 'Short': return { type: 'integer', format: 'int32' };
    case 'boolean': case 'Boolean': return { type: 'boolean' };
    case 'BigDecimal': case 'double': case 'Double': return { type: 'number' };
    case 'OffsetDateTime': case 'Instant': case 'LocalDateTime': return { type: 'string', format: 'date-time' };
    case 'LocalDate': return { type: 'string', format: 'date' };
    case 'Object': return {};
    case 'List': case 'Set': case 'Collection':
      return { type: 'array', items: typeToSchema(args[0], fromRecord) };
    case 'Map': {
      if (enums[args[0]]) { // Map<Enum, V>：把枚举常量展开为固定属性
        ensureEnum(args[0]);
        const props = {};
        for (const c of enums[args[0]].constants) {
          props[c.name] = { ...typeToSchema(args[1], fromRecord), description: c.doc || undefined };
        }
        return { type: 'object', description: `键为 ${args[0]} 枚举值，键恒在`, properties: props };
      }
      return { type: 'object', additionalProperties: args[1] === 'Object' ? true : typeToSchema(args[1], fromRecord) };
    }
    case 'PageResponse': {
      const inner = typeToSchema(args[0], fromRecord);
      const innerName = args[0].replace(/[<>, ]/g, '');
      const key = `PageResponse_${innerName}`;
      if (!schemas[key]) {
        const page = records.PageResponse;
        const props = {};
        for (const comp of page.components) {
          const s = comp.type.startsWith('List<T>') ? { type: 'array', items: inner } : typeToSchema(comp.type);
          s.description = page.javadoc.params[comp.name];
          props[comp.name] = s;
        }
        schemas[key] = { type: 'object', description: page.javadoc.desc, properties: props };
      }
      return { $ref: `#/components/schemas/${key}` };
    }
    default: {
      if (enums[base]) { ensureEnum(base); return { $ref: `#/components/schemas/${base}` }; }
      const key = resolveRecordKey(base, fromRecord);
      if (key) { ensureRecord(key); return { $ref: `#/components/schemas/${key}` }; }
      throw new Error(`无法解析类型：${type}（来自 ${fromRecord || '?'}）`);
    }
  }
}

// ---------- 控制器 → paths ----------
const paths = {};
const tags = [];
const controllerFiles = walk(SRC).filter((f) => /[\\/]controller[\\/]\w+Controller\.java$/.test(f)).sort();

for (const file of controllerFiles) {
  const src = fs.readFileSync(file, 'utf8');
  const cls = src.match(new RegExp(JAVADOC + ANNOS + 'public\\s+class\\s+(\\w+)'));
  const className = cls[2];
  const classDoc = parseJavadoc(cls[1]);
  const basePath = (src.match(/@RequestMapping\(\s*(?:value\s*=\s*)?"([^"]*)"\s*\)/) || [])[1] || '';
  const tag = className.replace(/Controller$/, '');
  const tagCn = (classDoc.desc.split('\n')[0].match(/^(?:App 端\s*)?(.+?)\s*只读 API/) || [, tag])[1];
  tags.push({ name: tag, description: classDoc.desc });

  const mRe = new RegExp(JAVADOC + '@(Get|Post|Put|Delete|Patch)Mapping(?:\\(\\s*(?:value\\s*=\\s*)?"([^"]*)"\\s*\\))?\\s*' + ANNOS + 'public\\s+([\\w<>, ?]+)\\s+(\\w+)\\s*\\(([\\s\\S]*?)\\)\\s*\\{', 'g');
  let m;
  while ((m = mRe.exec(src))) {
    const [, doc, verb, sub, retType, methodName, paramList] = m;
    const mdoc = parseJavadoc(doc);
    const fullPath = (basePath + (sub || '')).replace(/\/+/g, '/');
    const op = {
      tags: [tag],
      summary: summaryOf(mdoc.desc) || methodName,
      description: mdoc.desc || undefined,
      operationId: `${tag}_${methodName}`,
      parameters: [],
      responses: {},
    };
    let hasBody = false;
    for (const raw of splitTopLevel(paramList)) {
      const annos = [...raw.matchAll(/@(\w+)(?:\(([^)]*)\))?/g)];
      const cleaned = raw.replace(/@\w+(?:\([^)]*\))?\s*/g, '').trim();
      const idx = cleaned.lastIndexOf(' ');
      const jType = cleaned.slice(0, idx).trim();
      const jName = cleaned.slice(idx + 1).trim();
      const bind = annos.find((a) => ['RequestParam', 'PathVariable', 'RequestBody'].includes(a[1]));
      if (!bind) continue;
      const attrs = bind[2] || '';
      const explicitName = (attrs.match(/(?:value\s*=\s*)?"([^"]+)"/) || [])[1];
      const name = explicitName || jName;
      if (bind[1] === 'RequestBody') {
        hasBody = true;
        op.requestBody = { required: true, content: { 'application/json': { schema: typeToSchema(jType) } } };
        continue;
      }
      const isPath = bind[1] === 'PathVariable';
      const required = isPath || !/required\s*=\s*false/.test(attrs);
      const schema = typeToSchema(jType);
      const defaultValue = (attrs.match(/defaultValue\s*=\s*"([^"]*)"/) || [])[1];
      if (defaultValue !== undefined) schema.default = defaultValue;
      let desc = mdoc.params[name]
        || PARAM_DICT[`${className}.${name}`]
        || (isPath && name === 'id' ? idParamDesc(tagCn) : null)
        || PARAM_DICT[name]
        || (['page', 'size'].includes(name) && records.PageQuery ? records.PageQuery.javadoc.params[name] : null);
      if (!desc && enums[jType]) desc = enums[jType].javadoc.desc.split('\n')[0];
      if (!desc) gaps.push(`参数 ${className}.${methodName}(${name})`);
      op.parameters.push({ name, in: isPath ? 'path' : 'query', required, description: desc || undefined, schema });
    }
    if (!op.parameters.length) delete op.parameters;

    op.responses['200'] = {
      description: '成功',
      content: { 'application/json': { schema: typeToSchema(retType) } },
    };
    if (op.parameters || hasBody) op.responses['400'] = { $ref: '#/components/responses/BadRequest' };
    op.responses['401'] = { $ref: '#/components/responses/Unauthorized' };
    if (/\{id\}/.test(fullPath)) op.responses['404'] = { $ref: '#/components/responses/NotFound' };

    paths[fullPath] = paths[fullPath] || {};
    paths[fullPath][verb.toLowerCase()] = op;
  }
}

// ---------- 装配 ----------
schemas.ErrorResponse = {
  type: 'object',
  description: '业务/参数错误统一响应体（400 / 404 / 500）',
  properties: {
    status: { type: 'integer', description: 'HTTP 状态码' },
    error: { type: 'string', description: 'HTTP 状态短语，如 Bad Request / Not Found' },
    message: { type: 'string', description: '错误信息；参数校验失败时为「字段: 原因」以逗号拼接' },
    path: { type: 'string', description: '请求路径' },
  },
};

const doc = {
  openapi: '3.0.3',
  info: {
    title: 'Love Space App API',
    version: '1.0.0',
    description: [
      'Love Space 移动端只读 API。由 `scripts/generate-app-openapi.js` 从 Java 源码生成，勿手改。',
      '',
      '## 鉴权',
      '所有 `/api/app/**` 接口必须携带 `X-API-Key` 请求头，缺失或不匹配返回 401（`application/problem+json`，RFC 7807）。',
      '',
      '## 错误响应',
      '- 401：未鉴权（filter 层），`application/problem+json`',
      '- 400 / 404 / 500：业务异常，统一为 `{status, error, message, path}`（`application/json`）',
      '',
      '## 图片',
      '所有图片字段均为 `ImageResponse`：`id` 为稳定 objectKey，`url` 为当次签名的临时访问地址，客户端请勿持久化 `url`。',
    ].join('\n'),
  },
  servers: [
    { url: 'https://www.tripleyourlife.com/love-space/app', description: '生产环境（nginx 代理到 8081）' },
    { url: 'http://localhost:8081', description: '本地开发' },
  ],
  security: [{ ApiKeyAuth: [] }],
  tags,
  paths,
  components: {
    securitySchemes: {
      ApiKeyAuth: { type: 'apiKey', in: 'header', name: 'X-API-Key', description: '预共享 API Key（app.security.api-keys）' },
    },
    responses: {
      BadRequest: {
        description: '参数缺失、类型不合法或业务规则校验失败',
        content: { 'application/json': { schema: { $ref: '#/components/schemas/ErrorResponse' } } },
      },
      Unauthorized: {
        description: '缺失或非法的 X-API-Key',
        content: { 'application/problem+json': { schema: { type: 'object' } } },
      },
      NotFound: {
        description: '资源不存在或对 App 端不可见（已下线 / 所属城市已下架）',
        content: { 'application/json': { schema: { $ref: '#/components/schemas/ErrorResponse' } } },
      },
    },
    schemas: Object.fromEntries(Object.entries(schemas).sort(([a], [b]) => a.localeCompare(b))),
  },
};

// ---------- 自检：所有 $ref 可解析、每个 op 都有 200 ----------
const json = JSON.stringify(doc, null, 2);
for (const ref of json.matchAll(/"\$ref":\s*"#\/components\/(\w+)\/([\w.]+)"/g)) {
  if (!doc.components[ref[1]] || !doc.components[ref[1]][ref[2]]) throw new Error(`悬空引用：${ref[0]}`);
}
for (const [p, ops] of Object.entries(paths)) {
  for (const [v, op] of Object.entries(ops)) if (!op.responses['200']) throw new Error(`${v.toUpperCase()} ${p} 缺 200`);
}

const opCount = Object.values(paths).reduce((n, ops) => n + Object.keys(ops).length, 0);
if (!CHECK_ONLY) fs.writeFileSync(OUT, json + '\n');
console.log(`${CHECK_ONLY ? '✅ 校验通过' : '✅ 已生成 ' + path.relative(ROOT, OUT)}：${opCount} 个接口，${Object.keys(paths).length} 个路径，${Object.keys(doc.components.schemas).length} 个 schema`);
if (gaps.length) {
  console.log(`⚠ ${gaps.length} 处缺中文说明（补 javadoc @param 或脚本内字典）：`);
  for (const g of gaps) console.log('  - ' + g);
}
