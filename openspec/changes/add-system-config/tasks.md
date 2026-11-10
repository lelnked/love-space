## 1. 数据库（admin Liquibase）

- [x] 1.1 新增 `love-space-admin/src/main/resources/db/changelog/changes/030-create-system-config.sql`：建表 `loves_system_config`（id uuid PK、config_key text NOT NULL、name text NOT NULL、config_value jsonb NOT NULL、description text、created_at timestamptz NOT NULL、updated_at timestamptz），唯一约束 `uk_loves_system_config_key`，带 `--rollback DROP TABLE loves_system_config;`；确认 master changelog 能 include 到它

## 2. admin 后端 `modules/systemconfig`

- [x] 2.1 实体 `SystemConfig extends BaseAuditEntity`：`key`→`config_key`、`name`、`value`→`config_value`（String + `@ColumnTransformer(write = "?::jsonb")`）、`description`
- [x] 2.2 `SystemConfigRepository extends JpaRepository, JpaSpecificationExecutor`：`existsByKey`
- [x] 2.3 DTO：`SystemConfigCreateRequest{key, name, value: JsonNode, description}`（key `@NotBlank @Pattern(^[a-z][a-z0-9_.]{1,63}$)`、name `@NotBlank @Size(64)`、description `@Size(500)`，中文 message）；`SystemConfigUpdateRequest{name, value, description}`；`SystemConfigItemResponse{id, key, name, value(@JsonRawValue String), description, createdAt, updatedAt}` + `from(entity)`
- [x] 2.4 `SystemConfigService`：page（keyword 匹配 key/名称，`updatedAt DESC, createdAt DESC`）、detail、create（预检 409「配置 key 已存在」）、update、delete（404「配置不存在」）；value 校验 null/NullNode →「value 不能为空」、`toString()` > 65535 →「value 过长」
- [x] 2.5 `SystemConfigController` `/api/admin/system-configs`：`GET page`、`GET /{id}`、`POST`（201）、`PUT /{id}`、`DELETE /{id}`（204），三个写接口加 `@OperationLog("system-config:create|update|delete")`，中文 javadoc
- [x] 2.6 UT/IT（MockMvc + Postgres，`@scenario` 注释）：
  - `system-config/系统配置数据模型#value 以 JSON 原值往返`、`#标量与数组均可作为 value`
  - `system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称`、`#详情不存在返回 404`、`#未登录被拒绝`
  - `system-config/后台新增系统配置#新增成功`、`#key 重复返回 409`、`#key 格式非法返回 400`、`#value 为 null 返回 400`
  - `system-config/后台编辑系统配置#编辑不能修改 key`
  - `system-config/后台删除系统配置#删除后 App 查不到`（admin 侧断言删除 204 + 再查详情 404）
  - `operation-log/运营写操作留痕#系统配置增改删留痕`

## 3. app 后端 `modules/systemconfig`

- [x] 3.1 只读实体 `SystemConfig`（映射 `loves_system_config`，`config_value` 以 jsonb String 读取）与 `SystemConfigRepository`：`findByKey`、`findAllByKeyIn`
- [x] 3.2 `SystemConfigItemResponse{key, value(@JsonRawValue String)}` + `from(entity)`，record `@param` 写全（openapi 生成依赖）
- [x] 3.3 `SystemConfigQueryService`（`@Transactional(readOnly = true)`）：`get(key)` 不存在抛 `ResourceNotFoundException("配置不存在")`；`list(keys)` 拆分/strip/去空/保序去重，0 个或 > 50 个抛 `IllegalArgumentException`，按请求顺序输出
- [x] 3.4 `SystemConfigController` `/api/app/system-configs`：`GET /{key}`、`GET ?keys=`，中文 javadoc
- [x] 3.5 IT（`AbstractPostgresIntegrationTest` + MockMvc，`@scenario` 注释）：
  - `system-config/App 查询系统配置#单个 key 查询`、`#单个 key 不存在返回 404`、`#批量查询忽略不存在的 key 并保持顺序`、`#批量查询 keys 缺失或超限返回 400`、`#缺少 API-key 返回 401`
- [x] 3.6 重跑 `node scripts/generate-app-openapi.js` 更新 `love-space-app/docs/openapi.json`，无缺说明字段（生成脚本自 2026-09 起无法解析 featuredcycle 的 sealed interface，非本 change 引入；按脚本输出格式手工补入 2 个接口与 schema）

## 4. web 前端

- [x] 4.1 `src/api/systemConfigs.ts`：类型 `SystemConfigItem`（value: `unknown`）、`SystemConfigQuery`、`SystemConfigCreateRequest`、`SystemConfigUpdateRequest`；函数 `listSystemConfigs`（page）、`getSystemConfig`、`createSystemConfig`、`updateSystemConfig`、`deleteSystemConfig`，走 `apiClient`
- [x] 4.2 `src/pages/SystemConfigs/List.tsx` 列表区：`PageMeta` + `ComponentCard`；① `FilterBar`（text `keyword`，Apply/Reset）；② 「+ 新增配置」按钮打开弹窗；`DataTable` 列 key / 名称 / value 预览（单行 `JSON.stringify`，>40 字符截断加「…」，`title` 显示全文）/ 更新时间（说明不在列表展示） / 操作；加载中与空数据沿用 `DataTable` 既有口径；加载失败 `toast.error(detail ?? "加载失败")`
- [x] 4.3 `List.tsx` 操作列：③ 编辑 → `getSystemConfig(id)`（失败 toast）后以 edit 模式打开弹窗；④ 删除 → `useConfirm()` 提示「删除后 App 将无法读取该配置」→ `deleteSystemConfig` → `toast.success` 并刷新当前页（失败 toast 后端 detail）
- [x] 4.4 `List.tsx` ⑤ `Pagination`（表格右下角，20/200），翻页与筛选联动 `listSystemConfigs`
- [x] 4.5 `src/pages/SystemConfigs/ConfigEditModal.tsx` 弹窗外壳：`Modal showBackdrop={false}`，标题「新增配置 / 编辑配置」，提交中按钮禁用（「提交中...」）
- [x] 4.6 弹窗字段：⑥ key（必填，edit 模式 `disabled` + 小字「创建后不可修改」）、⑦ 名称（必填，≤64）、⑩ 说明（选填，≤500）；必填为空时字段下方红字，不发请求
- [x] 4.7 弹窗 ⑨ value：等宽字体多行 `textarea`，edit 模式回填 `JSON.stringify(value, null, 2)`；`onBlur` 与提交时校验——空或解析为 `null` →「value 不能为空」，解析失败 →「value 必须是合法的 JSON」，错误显示在文本框下方 `text-error-500`，校验失败不发请求；⑧「格式化」按钮：合法时 2 空格缩进重排，非法时显示同样的错误且不改文本
- [x] 4.8 弹窗常驻提示「App 可读取全部配置，请勿填写密钥、密码等机密」；⑪ 保存：请求体 `value` 传解析后的 JSON 值；成功 `toast.success` + 关闭弹窗 + 刷新列表；失败把后端 `detail`（如「配置 key 已存在」）显示在弹窗内，弹窗不关闭
- [x] 4.9 `src/App.tsx` 在 `<AppLayout />` 内注册 `/system-configs`；`src/layout/AppSidebar.tsx` 在「AI 管理」与「操作日志」之间新增一级菜单「系统配置」（选一个现有图标）
- [x] 4.10 `NODE_ENV=development npm run build` 与 `npm run lint` 通过（本机无 .env.production，按手册校验拦截；以 `tsc -b` + `vite build --mode test` 验证通过）

## 5. 收尾

- [x] 5.1 admin 与 app 全量 UT + 本 change 新增 IT 跑绿（两后端 IT 不并行）
- [x] 5.2 核对 `contracts/api-spec.json` 与实现一致（路径、状态码、错误文案、字段名）
