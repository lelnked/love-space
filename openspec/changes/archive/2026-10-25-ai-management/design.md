## Context

仓库目前没有任何 AI 能力。本 change 新建「场景 → 模型 → 提示词 → App 生成」主链路（proposal F0–F5），涉及四处：

- 新子项目 `love-space-ai`（纯 Java 库），admin 与 app 共用；
- admin：场景 / 模型 / 模板管理 + 模板测试 + 连接测试，Liquibase 建 4 张表；
- app：唯一的写接口 `POST /api/app/ai/generations`（生成 + 落库 + 防刷）；
- web：「AI 管理」菜单与 7 个路由页。

现状约束（读代码确认）：

- admin `GlobalExceptionHandler` 没有 404 / 409 映射，现有模块"不存在"抛 `IllegalArgumentException` → 400；app 有 `ResourceNotFoundException` → 404，但没有 409 / 429，也没有 `MissingRequestHeaderException`（缺头会落到兜底 500）。
- admin `BaseAuditEntity` 只有 id / createdAt / updatedAt，没有 createdBy / updatedBy / version。
- app `ddl-auto: validate`，表只由 admin Liquibase 建；app 测试库 `love_space_app_test` 是 create-drop，靠实体建表。
- app SecurityConfig 已 `csrf.disable()`，`/api/app/**` 走 `ApiKeyAuthFilter`，POST 不需额外放行。
- web 无 diff 库；表单弹窗规则：<5 个字段可用弹窗（场景 3 个、模型 2 个 → 弹窗合规）；模板编辑是独立路由页。
- 部署：`deploy/deploy-apps.sh` 用 `docker run -e` 逐个传环境变量，变量值来自 `deploy/.env.<环境>`（不入库，仅 `.env.example` 入库）。

## Goals / Non-Goals

**Goals:**
- AI 调用逻辑只写一份（`love-space-ai`），两后端仅做"取数据 → 调库 → 落库"。
- App 生成可幂等重放、可审计（每次模型调用都有明细）、费用有硬上限（全站日额度）。
- 运营可自助完成：建场景 → 配模型 → 写模板 → 测试 → 发布 / 切回旧版本。

**Non-Goals:**
- 生成记录后台查看与人工评价（原 F6，见 `docs/AI生成记录-F6-待产品确认.md`）。
- 费用统计、流式输出、记录清理、模板删除、通用权限点、请求签名/设备证明。
- 操作日志页的模块下拉与中文映射不加 AI 模块（现行规则：未映射回落显示英文原值）。

## Decisions

### D1 `love-space-ai` 形态：独立 Maven 项目 + Boot 自动装配
- 根目录 `love-space-ai/`，父 POM `spring-boot-starter-parent 4.0.6`，坐标 `com.loves:love-space-ai:0.0.1-SNAPSHOT`，包 `com.loves.ai`，带 Maven Wrapper。依赖只有 `spring-boot-starter-web`（RestClient + Jackson），两后端本就是 web 应用，直接 compile 依赖。
- 自动装配：`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` → `AiAutoConfiguration`，注册 `AiProperties`、`AiModelCatalog`、`PromptRenderer`、`AiGenerator`。
- 包结构：`com.loves.ai.{config, prompt, generation, ark}`；`ark` 是方舟 HTTP 适配（`ArkClient`），只被 `AiGenerator` 使用。
- 不建接口抽象（`AiGenerator` 是具体类）；测试直接起假 HTTP 服务。
- 替代方案：两后端各写一份（违背"只写一份"）；抽父 POM 多模块（两后端不是同一父 POM，改动面大）。→ 选独立库 + `./mvnw install`。

### D2 配置绑定：两后端 `application.yml` 显式映射环境变量
`AiProperties`（`@ConfigurationProperties("love-space.ai")`）字段：`apiKey, baseUrl, models(List), temperature, topP, maxTokens(Integer, 可空), timeoutSeconds=30, maxRetries=1`。两后端 yml 写：

```yaml
love-space:
  ai:
    api-key: ${ARK_API_KEY:}
    base-url: ${ARK_BASE_URL:https://ark.cn-beijing.volces.com/api/v3}
    models: ${AI_MODELS:}
    temperature: ${AI_TEMPERATURE:}
    top-p: ${AI_TOP_P:}
    max-tokens: ${AI_MAX_TOKENS:}
    timeout-seconds: ${AI_TIMEOUT_SECONDS:30}
    max-retries: ${AI_MAX_RETRIES:1}
```
app 额外 `love-space.ai.limit-per-anon-daily: ${AI_LIMIT_PER_ANON_DAILY:50}`、`limit-global-daily: ${AI_LIMIT_GLOBAL_DAILY:5000}`，由 app 自己的 `AiLimitProperties` 读（库不认识额度）。
- 原因：环境变量名 `AI_LIMIT_PER_ANON_DAILY` 走 relaxed binding 会被拆成 `ai.limit.per.anon.daily`，与驼峰字段对不上；显式映射最不容易错，且沿用项目 `${APP_SECURITY_API_KEYS:}` 写法。
- **`ARK_API_KEY` / `AI_MODELS` 为空时不阻止启动**，只打 WARN：模型列表为空、调用直接返回 `UPSTREAM_ERROR`（message「未配置 ARK_API_KEY」）、ping 返回 `ok=false`。理由：两后端现有 UT/IT 与本机开发不应因缺 AI 配置而起不来；proposal 的"必填"落在部署检查上（D10）。

### D3 方舟调用：RestClient + JDK HttpClient，超时即 TIMEOUT
- `RestClient` + `JdkClientHttpRequestFactory`，connect 超时 5s、read 超时 `timeoutSeconds`；请求 `POST {baseUrl}/chat/completions`，body `{model, messages, temperature?, top_p?, max_tokens?}`（空值不传），头 `Authorization: Bearer {apiKey}`。
- 判定：`ResourceAccessException` 根因为 `HttpTimeoutException` / `SocketTimeoutException` → TIMEOUT；HTTP 429 → RATE_LIMITED；其余 4xx/5xx 与 IO 异常 → UPSTREAM_ERROR；`finish_reason=content_filter` → REFUSED；`choices[0].message.content` 空白 → EMPTY；去掉整段 ```` ``` ```` 代码块包裹后用 Jackson 解析，非对象/数组 → FORMAT_ERROR。
- 每次调用产出 `AiAttempt{seq, triggerReason, model, status, errorCode, errorMessage, providerRequestId(响应 id 字段), rawResponse(响应体原文), parsedContent, validationError, promptTokens, completionTokens, usage(原样 JsonNode), startedAt, finishedAt, latencyMs}`。
- 重试不加退避间隔（`ponytail:` 立即重试；RATE_LIMITED 多发时再加固定退避）。
- ping：`messages=[{user:"ping"}]`、`max_tokens=1`，HTTP 2xx 即 `ok=true`，不校验 JSON。
- 替代方案：火山方舟官方 SDK（新增依赖、只用到一个接口）→ 不用。

### D4 测试替身：单文件假方舟 `scripts/mock-ark.mjs`，行为由模型名决定
- UT（`love-space-ai`）用 JDK 自带 `com.sun.net.httpserver.HttpServer`，不加依赖。
- IT / WEB 需要可复现的 AI 行为，新增 `node scripts/mock-ark.mjs [port=18080]`（只用 node 内置 `http`），按请求 `model` 返回：

| model | 行为 |
|---|---|
| `mock-ok` | 200，content = `{"echo": <user 消息>}` |
| `mock-fenced` | 200，content 用 ```` ```json ```` 包裹 |
| `mock-format-error` | 200，content = `not json` |
| `mock-empty` | 200，content = `""` |
| `mock-refused` | 200，`finish_reason=content_filter` |
| `mock-429` / `mock-500` | 对应 HTTP 状态 |
| `mock-timeout` | 挂起 `AI_TIMEOUT_SECONDS`+5 秒 |
| `mock-flaky` | 同一 messages 第 1 次 FORMAT_ERROR、第 2 次成功（内存计数） |
| `mock-slow` | 2 秒后成功（制造 PROCESSING 窗口，须小于超时） |

本地 IT 启动参数：`ARK_BASE_URL=http://localhost:18080 ARK_API_KEY=test AI_MODELS=mock-ok,mock-fenced,mock-format-error,mock-empty,mock-refused,mock-429,mock-500,mock-timeout,mock-flaky,mock-slow AI_TIMEOUT_SECONDS=3`。不新增 npm 依赖，不进生产镜像。

### D5 数据模型：admin `026-create-ai.sql`，jsonb 用 `@JdbcTypeCode(SqlTypes.JSON)`
- 4 张表字段与索引按 proposal；`loves_ai_scene.scene_id` 唯一约束 `ux_loves_ai_scene_scene_id`；`loves_ai_prompt_template` 唯一 `(scene_id, seq)`；`loves_ai_generation.client_request_id` 唯一；不建外键（项目惯例）。changelog 带 rollback（drop 4 表）。
- 实体继承 `BaseAuditEntity`（UUIDv7 + createdAt/updatedAt）；模板额外 `createdBy/updatedBy/version(@Version int)`。`loves_ai_generation` 没有 `updated_at` 列，`completed_at` 自管——app 端该实体不继承 `BaseAuditEntity`，自带 `id`（UUIDv7）与 `createdAt`；attempt 同理。
- app 端只映射需要的表：`AiScene`（只读）、`AiPromptTemplate`（只读，`system_prompt / user_prompt_template / variables / seq`）、`AiGeneration`、`AiGenerationAttempt`；app 测试库 create-drop 会按这些实体建表——实体列定义需与 Liquibase 对齐（`columnDefinition = "jsonb"`），否则生产 `validate` 失败。
- 生成记录表虽由 app 写入，schema 仍在 admin（项目铁律：schema 只由 admin 管）。

### D6 并发与事务
- **seq 分配**：`SELECT ... FOR UPDATE` 锁场景行（`@Lock(PESSIMISTIC_WRITE)`）后取 `max(seq)+1`，唯一约束兜底。
- **修改 / 发布的 version**：服务端先比较请求 `version` 与实体 `version`，不等直接 409；并发下靠 `@Version` 抛 `ObjectOptimisticLockingFailureException`，同样映射 409「内容已被他人更新，请刷新」。
- **发布**：同一事务内锁场景行 → 校验 tested 与变量 → 改 `scene.activeTemplateId` + 模板 `publishedAt/publishedBy`（会使模板 version+1，前端发布后重新拉详情）。
- **模板测试不持事务调模型**：① 短事务读模板 + 场景、校验、渲染，记下 `version=v`；② 事务外调 `AiGenerator.generate`；③ 用 JPQL `UPDATE ... SET last_test_*, tested = CASE ... WHERE id=? AND version=?` 写回，**不递增 version**（测试不改内容，避免打断编辑者的乐观锁）；影响 0 行 = 测试期间内容被改 → 不写回，仍返回结果。
- **App 生成三段事务**：① `TransactionTemplate` 内插 PROCESSING 记录（唯一冲突 `DataIntegrityViolationException` → 重新按 clientRequestId 读 → 去重分支）；② 事务外调模型；③ 新事务回写记录 + 批量插 attempts。模型调用期间不占数据库连接。
- **PROCESSING 超时阈值** = `timeoutSeconds × (1+maxRetries) × (backupModel != null ? 2 : 1) + 30s`，取记录 `request_params` 快照里的值（配置改了也按当时的算）。就地改 TIMEOUT 用 `UPDATE ... WHERE id=? AND status='PROCESSING'`，防止与迟到的正常回写互相覆盖；若影响 0 行则重读后按最终状态返回。

### D7 额度计数：查当日记录数，北京时间
`count(*) where anonymous_user_id=? and created_at >= :startOfToday` 与全站 `count(*) where created_at >= :startOfToday`，`startOfToday = LocalDate.now(ZoneId.of("Asia/Shanghai")).atStartOfDay(zone).toInstant()`，`Clock` 注入以便 UT 控制日期。两条走现有索引。先计数再插入的超额窗口为已知上限（proposal 已接受）。

### D8 错误映射补齐（不新增第二个 advice）
- admin `GlobalExceptionHandler` 新增：`ResourceNotFoundException` → 404（新建于 `common/exception`）、`ConflictException` → 409、`ObjectOptimisticLockingFailureException` → 409「内容已被他人更新，请刷新」。只有 AI 模块使用 404 新异常，现有模块"不存在 → 400"不动。
- app `GlobalExceptionHandler` 新增：`ConflictException` → 409、`TooManyRequestsException` → 429、`MissingRequestHeaderException` → 400（中文「缺少请求头 X-Anonymous-Id」）。
- 渲染 / 校验失败：库抛 `PromptValidationException extends IllegalArgumentException`（message 已含变量名与位置），两后端现有 400 映射直接生效。
- `X-Anonymous-Id` ≤128、inputs 每值 ≤500 字符 / 整体 ≤4096 字节在 app service 里校验（inputs 是动态 Map，Bean Validation 写不了），常量 `MAX_INPUT_VALUE_CHARS=500`、`MAX_INPUTS_BYTES=4096`。

### D9 前端结构与组件映射
API 层一个文件 `src/api/ai.ts`（类型 + `listAiScenes / getAiScene / createAiScene / updateAiScene / setAiSceneEnabled / listAiSceneOptions / listAiModels / setAiSceneModels / testAiModelConnection / listAiPromptTemplates / getAiPromptTemplate / createAiPromptTemplate / updateAiPromptTemplate / testAiPromptTemplate / publishAiPromptTemplate`）。共享工具 `src/pages/AiPrompts/promptVariables.ts`：`findUndeclaredVariables(systemPrompt, userPromptTemplate, variables)`（与库端同一正则 `\{\{\s*([A-Za-z_][A-Za-z0-9_]*)\s*\}\}`）。

| 线框区域 | 组件 / 文件 |
|---|---|
| 侧栏「AI 管理」三子菜单 | `src/layout/AppSidebar.tsx`（`subItems`） |
| 路由 | `src/App.tsx`：`/ai/scenes`、`/ai/models`、`/ai/prompts`、`/ai/prompts/:sceneId`、`/ai/prompts/:sceneId/templates/new`、`/ai/prompt-templates/:id`、`/ai/prompts/:sceneId/compare` |
| F1 ①② 筛选 / ⑥ 分页 | `src/pages/AiScenes/List.tsx`：`FilterBar`（text keyword、select enabled）+ `DataTable` + `Pagination` |
| F1 ③④⑤ 新增 / 编辑 / 启停 | 同上；⑤ 走 `useConfirm()` |
| F1 ⑦–⑩ 场景弹窗 | `src/pages/AiScenes/SceneFormModal.tsx`（`Modal showBackdrop={false}`，编辑态 sceneId `readOnly` + 「创建后不可修改」小字） |
| F2 ① 搜索、列表、② 配置 | `src/pages/AiModels/List.tsx`（复用 `listAiScenes`） |
| F2 ③④⑤⑥ 模型弹窗 | `src/pages/AiModels/ModelConfigModal.tsx`（两个 `Select` + 各自测试连接状态 `idle/loading/ok/fail`） |
| F3 页面1 ①② | `src/pages/AiPrompts/SceneList.tsx` |
| F3 页面2 ③–⑧ | `src/pages/AiPrompts/TemplateList.tsx`（勾选状态本页内存，不跨页保留） |
| F3 页面3 ⑨–⑮ 左栏 | `src/pages/AiPrompts/TemplateEditor.tsx` + `src/pages/AiPrompts/VariableTable.tsx` |
| F3 页面3 ⑯–⑱ 右栏测试 | `src/pages/AiPrompts/TestPanel.tsx` |
| F3 页面3 底部状态 + ⑲ | `TemplateEditor.tsx` 底部动作条 |
| F3 页面4 对比 + ⑳ | `src/pages/AiPrompts/Compare.tsx` + `src/pages/AiPrompts/lineDiff.ts` |

- 复制：`navigate("/ai/prompts/:sceneId/templates/new?copyFrom=<id>")`，新建页读 query 后 `getAiPromptTemplate` 预填（遵守手册"数据不靠 location.state"）。
- 新建页首次保存成功后 `navigate(/ai/prompt-templates/{id}, {replace:true})`；⑰测试前若 `dirty` 先走保存。
- 对比高亮：`ponytail:` 不引 diff 库，按行比较——某行不在其他任一列中出现即高亮（`lineDiff.ts` ≈20 行）；需要字符级 diff 时再引库。
- 409 版本冲突：toast 后端 message，保留本地编辑内容不重置。

### D10 构建与部署
- 打包顺序：`cd love-space-ai && ./mvnw install` → admin / app `./mvnw package`。改 `docs/部署正式环境.md`、`docs/部署测试环境.md`、`.claude/skills/deploy`。
- `deploy/deploy-apps.sh` 两个 `docker run` 各加 `-e ARK_API_KEY -e ARK_BASE_URL -e AI_MODELS -e AI_TEMPERATURE -e AI_TOP_P -e AI_MAX_TOKENS -e AI_TIMEOUT_SECONDS -e AI_MAX_RETRIES`，app 再加两个 `AI_LIMIT_*`；`ARK_API_KEY`、`AI_MODELS` 加入脚本已有的"必填为空即退出"检查；`deploy/.env.example` 补占位。
- nginx `limit_req` 配置按 proposal 写入 `docs/部署服务器初始化.md`。

## 已定决策

| # | 决策 | 理由 |
|---|---|---|
| 1 | 缺 `ARK_API_KEY` / `AI_MODELS` 不阻止进程启动，部署脚本层强制必填 | 不破坏现有测试与本地开发；生产由 deploy 脚本兜住 |
| 2 | 成功 JSON 必须是对象或数组；解析前剥离整段 Markdown 代码块 | 大模型常带 ```` ```json ````，裸字符串/数字对 App 无用；已写入 spec |
| 3 | 重试无退避 | 首版简单；429 频发再加 |
| 4 | 模板测试写回不递增 version，且以 version 为条件写回 | 测试不改内容，不应打断编辑；防止把旧内容的测试结果算到新内容上；已写入 spec |
| 5 | admin 只为 AI 模块引入 404（`ResourceNotFoundException`），存量模块不动 | proposal 要求 404；改存量属行为变化，超出本 change |
| 6 | `test-connection` 的 model 不在 `AI_MODELS` → 400 | 与 F2 保存校验一致，避免后台成为任意模型探测器 |
| 7 | 场景列表按 createdAt 倒序 | 项目列表默认口径 |
| 8 | 假方舟 `scripts/mock-ark.mjs` 由模型名驱动行为 | IT/WEB 可复现、零依赖 |
| 9 | 对比页行级高亮，不引 diff 库 | 无新依赖；够用 |
| 10 | 复制走 `?copyFrom=` query | 刷新不丢、符合 web 手册 |
| 11 | 模板测试只有结果为 SUCCESS（输出是合法 JSON）才置 `tested=true`，而不是"模型有返回"就算 | 输出不是 JSON 的模板发布后 App 必然失败，不应允许发布 |
| 12 | 发布校验顺序：version → tested → 变量；未声明变量的模板无法通过测试，故发布端的变量 400 只作防御，不单独出 IT | 正常 API 路径不可达 |

## Risks / Trade-offs

- [`love-space-ai` 需先 install，CI/新机器漏装会编译失败] → 部署文档与 deploy skill 首步写明；admin/app README 命令段加一行。
- [app 测试 create-drop 建表与生产 Liquibase 不一致导致上线 validate 失败] → 实体 jsonb 列显式 `columnDefinition`；交付前用 admin 迁移后的真实库起一次 app（`ddl-auto=validate`）。
- [额度先计数后插入，并发可超几次] → proposal 已接受；需要精确时改行锁计数。
- [nginx 取到的来源 IP 全是 docker 网关/CDN，所有用户共用 IP 额度] → 上线前按 proposal 核对 access log，必要时配 `real_ip`。
- [生成记录表无清理，长期增长] → 本期不做；`(created_at DESC)` 索引保证计数与后续 F6 查询。
- [方舟长时间无响应拖住 app 线程] → read 超时 = `AI_TIMEOUT_SECONDS`，单请求最坏 `timeout × 尝试上限`；IP 限流 + 日额度限制并发量。
- [`last_test_output` / `raw_output` 可能含敏感用户输入] → 只在 admin 可见（且 F6 前无查看入口），不对 App 返回。

## Migration Plan

1. `love-space-ai` install → admin 打包上线（Liquibase 026 自动建 4 张表，纯新增）→ app 打包上线 → web 发布。
2. 部署前在 `.env.<环境>` 配 `ARK_*` / `AI_*`；服务器验证可访问 `ark.cn-beijing.volces.com`。
3. nginx 加限流 location 并 reload；方舟控制台设接入点限额与费用预警。
4. 回滚：web / app / admin 回退上一版本镜像即可；4 张表无外部引用，可保留或执行 changelog rollback 删除。

## Open Questions

无阻塞项。生成记录查看与评价待产品确认，见 `docs/AI生成记录-F6-待产品确认.md`。
