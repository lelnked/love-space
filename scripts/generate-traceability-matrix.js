// scripts/generate-traceability-matrix.js（OpenSpec 版）
// 锚点：行为 = {domain}/{Requirement 名}，场景 = …#{Scenario 名}，用例 = TC-{domain}-{IT|WEB|APP}-NNN
// 两种模式：
//   node scripts/generate-traceability-matrix.js --change <id>  交付核对：只核 delta 涉及的 Requirement，
//     产物 openspec/changes/<id>/traceability-matrix.md（archive 前把关）
//   node scripts/generate-traceability-matrix.js                全局核对：全 living specs，
//     产物 ./traceability-matrix.md（发版前把关）
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

function read(p) { return fs.existsSync(p) ? fs.readFileSync(p, 'utf-8') : ''; }
function listDirs(p) {
  return fs.existsSync(p) ? fs.readdirSync(p, { withFileTypes: true }).filter(d => d.isDirectory()).map(d => d.name) : [];
}

// 1. 解析 spec.md 的 Requirement/Scenario（living 与 delta 通用；delta 按 ADDED/MODIFIED/REMOVED/RENAMED 分节）
//    返回 [{ requirement, scenarios: [名...], op }]，op 仅 delta 有（ADDED/MODIFIED/...）
function parseSpec(content) {
  const out = [];
  let cur = null, op = '';
  for (const line of content.split('\n')) {
    const sec = line.match(/^##\s+(ADDED|MODIFIED|REMOVED|RENAMED)\s+Requirements/i);
    if (sec) { op = sec[1].toUpperCase(); continue; }
    const req = line.match(/^###\s+Requirement:\s*(.+?)\s*$/);
    if (req) { cur = { requirement: req[1], scenarios: [], op }; out.push(cur); continue; }
    const sc = line.match(/^####\s+Scenario:\s*(.+?)\s*$/);
    if (sc && cur) cur.scenarios.push(sc[1]);
  }
  return out;
}

// living specs：{ '{domain}/{Requirement}': Set<Scenario> }
function loadLivingSpecs() {
  const map = new Map();
  for (const domain of listDirs('./openspec/specs')) {
    for (const r of parseSpec(read(`./openspec/specs/${domain}/spec.md`))) {
      map.set(`${domain}/${r.requirement}`, new Set(r.scenarios));
    }
  }
  return map;
}

// change delta：叠加进锚点表（交付时 ADDED/MODIFIED 还没合入 living specs），并返回受影响 Requirement 集合
function overlayChangeDelta(changeId, specMap) {
  const affected = new Set();
  const base = `./openspec/changes/${changeId}/specs`;
  for (const domain of listDirs(base)) {
    for (const r of parseSpec(read(`${base}/${domain}/spec.md`))) {
      const key = `${domain}/${r.requirement}`;
      if (r.op === 'REMOVED') { specMap.delete(key); continue; }
      if (r.op === 'RENAMED') continue; // FROM:/TO: 格式，锚点以 living specs 合入后为准，此处不建
      specMap.set(key, new Set(r.scenarios));
      affected.add(key);
    }
  }
  return affected;
}

// 2. 测试用例：tests/{domain}/{it,web,app}.md（含 tests/flows/web.md；文件存在才解析，端插拔零改动）
function extractTestCases() {
  const out = [];
  for (const domain of listDirs('./tests')) {
    for (const f of ['it.md', 'web.md', 'app.md']) {
      const c = read(`./tests/${domain}/${f}`);
      const re = /### (TC-[A-Za-z0-9-]+):\s*(.+?)\n([\s\S]*?)(?=\n### |\n## |$)/g;
      let m;
      while ((m = re.exec(c)) !== null) {
        const body = m[3];
        out.push({
          id: m[1], title: m[2], domain,
          req: (body.match(/\*\*关联需求\*\*:\s*(.+)/) || [, ''])[1].trim(),
          contract: (body.match(/\*\*关联契约\*\*:\s*(.+)/) || [, ''])[1].trim(),
          source: (body.match(/\*\*来源\*\*:\s*(.+)/) || [, ''])[1].trim(),
          status: (body.match(/\*\*状态\*\*:\s*([⬜✅❌])/) || [, '⬜'])[1],
          evidence: (body.match(/\*\*执行存证\*\*:\s*`([^`]+)`/) || [, ''])[1],
        });
      }
    }
  }
  return out;
}

// 3. API 契约写操作（contracts/api-spec.json，x-requirement 反链）
function extractApiSpecOps() {
  const out = [];
  let spec; try { spec = JSON.parse(read('./contracts/api-spec.json')); } catch { return out; }
  const WRITE = new Set(['post', 'put', 'patch', 'delete']);
  for (const [p, methods] of Object.entries(spec.paths || {})) {
    for (const [method, op] of Object.entries(methods || {})) {
      const m = method.toLowerCase();
      if (!/^(get|post|put|patch|delete|head|options)$/.test(m)) continue;
      const enc = p.replace(/~/g, '~0').replace(/\//g, '~1');
      out.push({ method: m, path: p, isWrite: WRITE.has(m), ptrFrag: `/paths/${enc}/${m}`, xReq: (op && op['x-requirement']) || '' });
    }
  }
  return out;
}

// 4. UT：grep 测试代码里的 @scenario 注释（{domain}/{Requirement}#{Scenario}）
function extractUtScenarios() {
  const found = new Set();
  const roots = ['./love-space-admin/src', './love-space-app/src', './love-space-web/src'];
  const walk = (dir) => {
    if (!fs.existsSync(dir)) return;
    for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
      const p = path.join(dir, e.name);
      if (e.isDirectory()) { if (e.name !== 'node_modules' && e.name !== 'generated') walk(p); continue; }
      if (!/(\.(spec|test)\.(ts|tsx|js|jsx)|(Test|IT)\.java)$/.test(e.name)) continue;
      const re = /@scenario:?\s+(\S[^\n*#]*#[^\n*]+)/g;
      let m; const c = read(p);
      while ((m = re.exec(c)) !== null) found.add(m[1].trim());
    }
  };
  roots.forEach(walk);
  return found;
}

// 5. 提交（change 模式：commit message 含 change-id）
function extractCommits(changeId) {
  try {
    return execSync(`git log --all --grep="${changeId}" --oneline`, { encoding: 'utf-8', stdio: ['ignore', 'pipe', 'ignore'] })
      .split('\n').filter(Boolean)
      .map(l => { const [h, ...msg] = l.split(' '); return { hash: h, message: msg.join(' ') }; });
  } catch { return []; }
}

function typeOf(tcId) { return (tcId.match(/-(IT|WEB|APP)-\d+$/) || [, '-'])[1]; }

function generate(changeId) {
  const specMap = loadLivingSpecs();
  let affected = null; // change 模式下的受影响 Requirement 集合；全局模式为 null（核全部）
  if (changeId) affected = overlayChangeDelta(changeId, specMap);

  const allTcs = extractTestCases();
  const changeList = changeId ? read(`./openspec/changes/${changeId}/test-cases.md`) : '';
  // change 模式的核对范围：delta 涉及的 Requirement + change 清单点名的 TC
  const inScope = (t) => !changeId
    || (affected.has(t.req.split('#')[0]) || changeList.includes(t.id) || t.source === changeId);
  const tcs = allTcs.filter(inScope);

  const utScenarios = extractUtScenarios();
  const title = changeId ? `追溯矩阵（交付核对）：${changeId}` : '追溯矩阵（全局核对）';
  let md = `# ${title}\n\n> 生成物勿手改。生成命令：\`node scripts/generate-traceability-matrix.js${changeId ? ' --change ' + changeId : ''}\`\n\n`;

  // 需求与场景清单
  const scopeReqs = [...specMap.keys()].filter(k => !affected || affected.has(k)).sort();
  md += `## 需求与场景\n`;
  scopeReqs.forEach(k => { md += `- **${k}**: ${[...specMap.get(k)].join(' / ') || '（无 Scenario）'}\n`; });

  md += `\n## 测试用例追溯\n\n| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |\n|---|---|---|---|---|---|---|---|\n`;
  tcs.forEach(t => {
    md += `| ${t.id} | ${t.title || '-'} | ${t.req || '-'} | ${t.contract || '-'} | ${t.source || '-'} | ${typeOf(t.id)} | ${t.evidence ? '`' + t.evidence + '`' : '-'} | ${t.status} |\n`;
  });

  // ── 双向覆盖核对（方案 §6.2）──
  const warns = [];
  // 正向：每个 Scenario ≥1 条端上用例（WEB/APP）或 UT
  scopeReqs.forEach(k => {
    for (const sc of specMap.get(k)) {
      const anchor = `${k}#${sc}`;
      const hasEnd = allTcs.some(t => ['WEB', 'APP'].includes(typeOf(t.id)) && t.req === anchor);
      const hasUT = utScenarios.has(anchor);
      if (!hasEnd && !hasUT) warns.push(`⚠ 未覆盖：${anchor} 无 WEB/APP 用例且无 UT(@scenario) 覆盖`);
    }
  });
  // 正向：api-spec.json 写操作 ≥1 条 IT。覆盖只认 JSON Pointer（x-requirement 是 Requirement 级
  // 反链、一对多，拿它判覆盖会让同 Requirement 的一条 IT 误盖所有写接口；它只用于圈定 change 范围）
  extractApiSpecOps().filter(o => o.isWrite).forEach(o => {
    if (affected && o.xReq && !affected.has(o.xReq)) return;
    const covered = allTcs.some(t => typeOf(t.id) === 'IT' && t.contract.includes(o.ptrFrag));
    if (!covered) warns.push(`⚠ 未覆盖：${o.method.toUpperCase()} ${o.path} 无 IT 用例`);
  });
  // 反向：关联需求必须能解析到 Requirement#Scenario（悬空多为改名没走 RENAMED/没同步用例）
  tcs.forEach(t => {
    if (!t.req) { warns.push(`⚠ 悬空用例：${t.id} 无 关联需求`); return; }
    const [reqKey, scenario] = t.req.split('#');
    if (!specMap.has(reqKey)) warns.push(`⚠ 悬空用例：${t.id} 的 关联需求 "${reqKey}" 在 specs 中不存在`);
    else if (scenario && !specMap.get(reqKey).has(scenario)) warns.push(`⚠ 悬空用例：${t.id} 的 Scenario "${scenario}" 在 "${reqKey}" 下不存在`);
  });
  // 状态真实性：✅ 必有存证目录
  tcs.forEach(t => {
    if (t.status === '✅' && t.evidence && !fs.existsSync('./' + t.evidence))
      warns.push(`⚠ 状态存疑：${t.id} 标 ✅ 但存证目录不存在`);
  });

  md += `\n## 覆盖核对\n\n` + (warns.length ? warns.map(w => `- ${w}`).join('\n') : '- ✅ 正反向覆盖完整，无悬空用例，状态可信') + '\n';

  const pass = tcs.filter(t => t.status === '✅').length;
  const fail = tcs.filter(t => t.status === '❌').length;
  const pend = tcs.filter(t => t.status === '⬜').length;
  const tot = tcs.length || 1;
  md += `\n## 测试统计\n- 总数：${tcs.length}\n- ✅ 通过：${pass} (${(pass / tot * 100).toFixed(1)}%)\n- ❌ 失败：${fail}\n- ⬜ 未测：${pend}\n`;

  if (changeId) {
    const commits = extractCommits(changeId);
    if (commits.length) md += `\n## 相关提交\n` + commits.map(c => `- \`${c.hash}\` ${c.message}`).join('\n') + '\n';
  }
  return { md, warns };
}

// ── 入口 ──
const args = process.argv.slice(2);
let changeId = null;
const ci = args.indexOf('--change');
if (ci !== -1) {
  changeId = args[ci + 1];
  if (!changeId) { console.error('用法: node generate-traceability-matrix.js [--change <change-id>]'); process.exit(1); }
  if (!fs.existsSync(`./openspec/changes/${changeId}`)) { console.error(`❌ change 不存在: openspec/changes/${changeId}`); process.exit(1); }
}
const { md, warns } = generate(changeId);
const outPath = changeId ? `./openspec/changes/${changeId}/traceability-matrix.md` : './traceability-matrix.md';
fs.writeFileSync(outPath, md, 'utf-8');
console.log(`✅ 追溯矩阵已生成：${outPath}${warns.length ? `（⚠ ${warns.length} 项待处理）` : '（无 ⚠）'}`);
