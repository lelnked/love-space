# Proposal: ai-management（v1.3 AI 管理）

## Why

App 要按业务入口调用大模型生成内容，运营需要在后台自己维护"哪些业务场景能调 AI、用哪个模型、用哪段提示词"，并把每一次真实生成完整落库，供后续回看与标注。目前仓库内没有任何 AI 能力，本 change 建立：场景 → 模型 → 提示词 → App 生成（含落库与防刷）这条主链路。

## What Changes

按功能先后顺序（也是实现顺序）：

| # | 功能 | 端 | 后台菜单 |
|---|---|---|---|
| F0 | AI 基础模块：独立子项目 `love-space-ai`，封装方舟调用、提示词渲染、重试/切备用、JSON 校验，admin 与 app 都引用它 | ai（库） | — |
| F1 | 业务配置：增改、启用/禁用业务场景（scene_id） | admin + web | AI 管理 › 业务配置 |
| F2 | 模型配置：给场景选主模型 / 备用模型、测试连接 | admin + web | AI 管理 › 模型配置 |
| F3 | 提示词管理：模板新增 / 编辑 / 测试 / 对比 / 发布 | admin + web | AI 管理 › 提示词管理 |
| F4 | App 生成接口：按 scene_id 取生效模板、调 AI 模块生成、去重，返回 AI 输出内容 | app | — |
| F5 | 生成接口防刷：IP 限流（nginx）、匿名 ID 每日限次、全站每日总量、输入长度限制 | app + nginx | — |

明确**不做**：费用/单价、输出协议版本、发布配置（组合版本）、通用权限体系、流式输出、记录清理、后台测试写入生成记录、重新生成关联原请求、模板删除、App 展示回执（App 端只有一个生成接口，调用明细只在后台可见）。

**剥离到后续 change**：原 F6「生成记录后台查看与人工评价」（列表筛选、详情、评价与问题标签）有待产品确认的问题，已移出本 change，草案见 `docs/AI生成记录-F6-待产品确认.md`。本 change 只负责把生成记录与调用明细**写入**数据库（F4），后台暂无查看页面。

### 全局约定（各功能共用）

- **scene_id**：运营录入的业务编码，全局唯一、必填、**创建后不可修改**；格式 `^[a-z][a-z0-9_]{1,63}$`（如 `cycle_advice`）。提示词模板、生成记录都用 scene_id 归属与隔离。
- **模型供应商**：火山方舟（OpenAI 兼容 Chat Completions，`POST {ARK_BASE_URL}/chat/completions`）。密钥与调用参数全部在服务器环境变量里，数据库与后台都不存密钥。
- **新增环境变量**（由 F0 AI 模块读取；admin 与 app 两个后端进程都要配，admin 用于测试/测试连接，app 用于正式生成）：

| 变量 | 说明 | 默认 |
|---|---|---|
| `ARK_API_KEY` | 火山方舟 API Key | 必填，无默认 |
| `ARK_BASE_URL` | 接入地址 | `https://ark.cn-beijing.volces.com/api/v3` |
| `AI_MODELS` | 后台可选模型 ID 列表，逗号分隔（方舟 Model ID 或推理接入点 `ep-xxx`） | 必填 |
| `AI_TEMPERATURE` / `AI_TOP_P` / `AI_MAX_TOKENS` | 模型调用参数 | 方舟默认（不传） |
| `AI_TIMEOUT_SECONDS` | 单次调用超时 | 30 |
| `AI_MAX_RETRIES` | 每个模型失败后的重试次数（不含首次） | 1 |
| `AI_LIMIT_PER_ANON_DAILY` | F5：单个匿名用户 ID 每天最多生成次数（仅 app 读取） | 50 |
| `AI_LIMIT_GLOBAL_DAILY` | F5：全站每天最多生成次数（仅 app 读取） | 5000 |

- **权限**：沿用现有授权链，`/api/admin/ai/**` 所有登录运营（ADMIN、MEMBER）均可用，不新增角色或权限点。
- **操作日志**：新增、修改、启用/禁用、改模型、发布、人工评价写现有操作日志；查看、测试不留痕。
- **分页 / 错误格式**：沿用现有 `PageResponse{content,page,size,totalElements,totalPages}`（page 从 1 开始）与 `{status,error,message,path}`。
- **侧栏**：新增一级菜单「AI 管理」，子菜单顺序：业务配置、模型配置、提示词管理（生成记录菜单随 F6 后续加入）。

---

### F0 AI 基础模块 `love-space-ai`

admin（提示词测试、测试连接）和 app（正式生成）都要调 AI，调用逻辑只写一份，放进独立子项目。

- **形态**：仓库根新增 `love-space-ai/`，纯 Java 库（jar，不是可运行服务），包 `com.loves.ai`，坐标 `com.loves:love-space-ai`，父 POM 同为 `spring-boot-starter-parent 4.0.6`。提供 Spring Boot 自动装配，两个后端加上依赖即获得下列 Bean，并读取上面那组环境变量。
- **边界**：不含数据库、不含 HTTP 接口。场景、模板、生成记录这些表仍由两个后端各自读写；模块只负责"把一次 AI 请求可靠地做完并给出结果"。对外提供的是 Java 调用，不是网络服务。
- **对外能力**：

| 能力 | 输入 | 输出 | 使用方 |
|---|---|---|---|
| `AiModelCatalog.models()` | — | `AI_MODELS` 列表 | admin F2 下拉、主备模型校验 |
| `PromptRenderer.validate(...)` | 系统提示词、输入模板、变量声明 | 未声明变量列表（变量名 + 位置：系统提示词/输入模板）、变量声明错误 | admin F3 测试/发布 |
| `PromptRenderer.render(...)` | 模板 + `inputs` | messages `[{role:system},{role:user}]`；必填变量缺失 → 抛校验异常 | admin F3 测试、app F4 |
| `AiGenerator.generate(...)` | messages、主模型、备用模型(可空) | `{status, content, rawOutput, finalModel, promptTokens, completionTokens, latencyMs, errorCode, errorMessage, params, attempts[]}`；`attempts[]` 每项 = 一次模型调用的明细（见 F4 attempt 表字段） | admin F3 测试、app F4 |
| `AiGenerator.ping(model)` | 模型 | `{ok, latencyMs, message}` | admin F2 测试连接 |

- **生成规则**（`generate` 内部）：主模型首次 + `AI_MAX_RETRIES` 次重试 → 仍失败且配了备用 → 备用模型首次 + 重试。成功条件 = 方舟正常返回 + 内容非空 + 能解析成 JSON。最终状态取最后一次调用的结果，tokens 为各次合计。
- **状态枚举**（定义在本模块，生成记录与模板测试共用）：

| 状态 | 含义 |
|---|---|
| SUCCESS | 内容非空且是合法 JSON，可交付——唯一计为"AI 生成成功" |
| REFUSED | 模型拒答（`finish_reason=content_filter`） |
| EMPTY | 返回内容为空 |
| FORMAT_ERROR | 不是合法 JSON |
| TIMEOUT | 超时 |
| RATE_LIMITED | 限流（HTTP 429） |
| UPSTREAM_ERROR | 其他供应商/网络异常 |

  以上失败都会触发重试 / 切备用。App 端自行展示的兜底文案不经过服务端，不会被统计为成功。

- **构建方式**：两个后端不是同一父 POM 的模块，引用方式为先在 `love-space-ai/` 执行 `./mvnw install` 装进本地 Maven 仓库，再打包 admin / app。部署文档的打包步骤相应加这一步。
- **测试**：模块自带 UT（用本地假 HTTP 服务模拟方舟），覆盖变量校验、渲染、重试、切备用、JSON 校验、各状态判定。

---

### F1 业务配置（场景管理）

**表 `loves_ai_scene`**（F2 的模型字段、F3 的生效模板字段也在这张表上，一并列出）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | uuid | PK | |
| scene_id | text | NOT NULL, UNIQUE | 业务编码，创建后不可改 |
| name | text | NOT NULL | 场景名称，≤64 |
| description | text | | 说明，≤500 |
| enabled | boolean | NOT NULL DEFAULT true | 禁用后 App 生成接口拒绝该场景 |
| primary_model | text | | F2：主模型 ID |
| backup_model | text | | F2：备用模型 ID，可空 |
| active_template_id | uuid | | F3：当前生效模板（同一场景只有一个生效） |
| created_at | timestamptz | NOT NULL | |
| updated_at | timestamptz | | |

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/admin/ai/scenes/page` | query：`keyword?`（匹配 scene_id/名称）、`enabled?`、`page`、`size`（20/200） | `PageResponse<SceneItem>` |
| GET | `/api/admin/ai/scenes/options` | 无 | `[{id, sceneId, name, enabled}]`（下拉框用，全量） |
| GET | `/api/admin/ai/scenes/{id}` | path：id | `SceneDetail` |
| POST | `/api/admin/ai/scenes` | body：`{sceneId*, name*, description?}` | 201 `SceneDetail`；scene_id 重复 → 409「scene_id 已存在」 |
| PUT | `/api/admin/ai/scenes/{id}` | body：`{name*, description?}`（不含 sceneId） | `SceneDetail` |
| PUT | `/api/admin/ai/scenes/{id}/enabled` | body：`{enabled*}` | 204 |

`SceneItem` / `SceneDetail` = `{id, sceneId, name, description, enabled, primaryModel, backupModel, activeTemplate:{id, seq, name, publishedAt, publishedBy}|null, createdAt, updatedAt}`——F1/F2/F3 三个列表页共用这一个结构。

操作日志：`ai-scene:create`、`ai-scene:update`、`ai-scene:set-enabled`

**页面 `/ai/scenes`**

```
┌ AI 管理 › 业务配置 ──────────────────────────────────────────────────┐
│ [搜索 scene_id / 名称      ]① [状态: 全部▼]②            [+ 新增场景]③ │
├──────────────────────────────────────────────────────────────────────┤
│ scene_id       名称        说明              状态     创建时间    操作 │
│ cycle_advice   周期建议    经期每日建议...    ●启用   10-20 14:00 编辑④ 禁用⑤│
│ mood_reply     情绪回应    ...               ○禁用   10-21 09:30 编辑  启用 │
├──────────────────────────────────────────────────────────────────────┤
│                                          共 2 条  < 1 >  20条/页 ⑥   │
└──────────────────────────────────────────────────────────────────────┘

┌ 新增 / 编辑场景（弹窗）──────────────────┐
│ scene_id *  [cycle_advice        ] ⑦     │  ← 编辑时只读
│ 名称 *      [周期建议            ] ⑧     │
│ 说明        [                    ] ⑨     │
│                        [取消] [保存]⑩    │
└──────────────────────────────────────────┘
```
①②⑥ → `GET /scenes/page`；③⑩(新增) → `POST /scenes`；④⑩(编辑) → `GET /scenes/{id}` + `PUT /scenes/{id}`；⑤ → 二次确认后 `PUT /scenes/{id}/enabled`。

---

### F2 模型配置

**表**：无新表，写 `loves_ai_scene.primary_model / backup_model`。保存即生效（不做版本/发布），影响之后的新请求。

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/admin/ai/models` | 无 | `["doubao-seed-1-6-250615", "ep-xxxx", ...]`（来自 `AI_MODELS`） |
| PUT | `/api/admin/ai/scenes/{id}/models` | body：`{primaryModel*, backupModel?}`；必须在 `AI_MODELS` 内，且主备不能相同 → 否则 400 | 204 |
| POST | `/api/admin/ai/models/test-connection` | body：`{model*}` | `{ok, latencyMs, message}`；只发一条极短请求验证可达，不代表业务质量 |

列表复用 `GET /api/admin/ai/scenes/page`。操作日志：`ai-scene:set-models`

**页面 `/ai/models`**

```
┌ AI 管理 › 模型配置 ──────────────────────────────────────────────────┐
│ [搜索 scene_id / 名称      ]①                                         │
├──────────────────────────────────────────────────────────────────────┤
│ scene_id       名称       主模型                    备用模型     操作  │
│ cycle_advice   周期建议   doubao-seed-1-6-250615    ep-2025xxx   配置② │
│ mood_reply     情绪回应   未配置（⚠ 不可生成）       —           配置  │
└──────────────────────────────────────────────────────────────────────┘

┌ 配置模型：周期建议 (cycle_advice)（弹窗）──────────────────┐
│ 主模型 *   [doubao-seed-1-6-250615 ▼]③   [测试连接]④  ✓ 可达 320ms │
│ 备用模型   [不使用              ▼]⑤      [测试连接]               │
│ 提示：连接成功只代表服务可达，不代表生成质量                        │
│                                         [取消] [保存]⑥             │
└────────────────────────────────────────────────────────────────────┘
```
① → `GET /scenes/page`；③⑤ 选项 → `GET /models`；④ → `POST /models/test-connection`；⑥ → `PUT /scenes/{id}/models`。

---

### F3 提示词管理（模板编辑 / 测试 / 对比 / 发布）

**规则**
- 一个场景下多个模板，`seq` 在场景内自增，显示为 V1、V2…；同一时刻只有 `loves_ai_scene.active_template_id` 指向的那个生效。
- 模板自己声明变量 `{name, required, description}`；系统提示词与输入模板里用 `{{name}}` 引用。引用了未声明的变量 → 保存可以，但**测试和发布被拒绝**并指出变量名与位置（系统提示词/输入模板）。
- **生效中的模板不能修改**；未生效模板（包括以前发布过的）可以修改。
- 任何内容修改都把 `tested` 置 false；测试中模型正常返回一次 → `tested=true`；只有 `tested=true` 才能发布。
- 发布 = 原子地把场景的 `active_template_id` 指到该模板（原生效模板自动失效）。场景下**任意一个已测试的模板**都可以发布，与版本新旧、是否发布过无关——例如 V3 生效中 → 发布 V1 切回 V1 → V3 重新编辑并测试后 → 再发布 V3 切回 V3。
- **没有单独的"回退"功能**，版本切换全部通过"发布"完成。
- 修改/发布带 `version`（乐观锁），版本不一致 → 409「内容已被他人更新，请刷新」。
- 模板创建后不能删除。"复制为新模板"由前端把旧模板内容预填到新建表单，无单独接口。
- 测试通过 F0 `AiGenerator.generate` 调用场景当前配置的主/备模型（重试/备用规则同 F0），**不写生成记录**，只把最后一次测试结果存回模板。

**表 `loves_ai_prompt_template`**

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | uuid | PK | |
| scene_id | text | NOT NULL | 所属场景 |
| seq | int | NOT NULL, UNIQUE(scene_id, seq) | 场景内版本号 |
| name | text | NOT NULL | 模板名称，≤64 |
| system_prompt | text | NOT NULL | 系统提示词 |
| user_prompt_template | text | NOT NULL | 输入模板 |
| variables | jsonb | NOT NULL DEFAULT '[]' | `[{name, required, description}]`，name 匹配 `^[A-Za-z_][A-Za-z0-9_]*$` 且不重复 |
| remark | text | | 修改说明 |
| tested | boolean | NOT NULL DEFAULT false | 当前内容是否已测试通过 |
| last_test_inputs | jsonb | | 最后一次测试填写的变量值 |
| last_test_output | text | | 最后一次测试的模型原始输出 |
| last_test_content | jsonb | | 最后一次测试解析出的 JSON（解析失败为 null） |
| last_test_model | text | | 最后一次测试实际使用的模型 |
| last_test_success | boolean | | |
| last_test_error | text | | 失败原因 |
| last_test_latency_ms | int | | |
| last_test_at | timestamptz | | |
| last_test_by | text | | 测试人 username |
| published_at | timestamptz | | 最近一次发布时间 |
| published_by | text | | 最近一次发布人 username |
| created_by / updated_by | text | | username |
| created_at / updated_at | timestamptz | | |
| version | int | NOT NULL DEFAULT 0 | 乐观锁 |

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/admin/ai/prompt-templates/page` | query：`sceneId*`、`page`、`size`；按 seq 倒序 | `PageResponse<TemplateItem>`：`{id, seq, name, remark, active, tested, lastTestAt, publishedAt, publishedBy, updatedBy, updatedAt}` |
| GET | `/api/admin/ai/prompt-templates/{id}` | path：id | `TemplateDetail`：全部字段 + `active` |
| POST | `/api/admin/ai/prompt-templates` | body：`{sceneId*, name*, systemPrompt*, userPromptTemplate*, variables:[{name*, required*, description?}], remark?}` | 201 `TemplateDetail`（seq 自动分配） |
| PUT | `/api/admin/ai/prompt-templates/{id}` | body：`{name*, systemPrompt*, userPromptTemplate*, variables, remark?, version*}` | `TemplateDetail`；生效中 → 409「生效中的模板不能修改」；版本冲突 → 409 |
| POST | `/api/admin/ai/prompt-templates/{id}/test` | body：`{inputs:{变量名: 值}}`；必填变量缺失 / 未声明变量 → 400 | `{success, model, output, content, errorCode, errorMessage, latencyMs, promptTokens, completionTokens, testedAt}`；场景未配主模型 → 409 |
| POST | `/api/admin/ai/prompt-templates/{id}/publish` | body：`{version*}` | 204；`tested=false` → 409「请先测试」；场景已禁用也允许发布（禁用只拦 App） |

对比无新接口：前端对选中的 2~3 个模板各调一次 `GET /prompt-templates/{id}`，并排展示内容与 `last_test_output`。场景列表复用 `GET /scenes/page`（带 `activeTemplate`）。

操作日志：`ai-prompt:create`、`ai-prompt:update`、`ai-prompt:publish`

**页面 1 `/ai/prompts`（按场景看当前生效）**

```
┌ AI 管理 › 提示词管理 ────────────────────────────────────────────────┐
│ [搜索 scene_id / 名称      ]①                                         │
├──────────────────────────────────────────────────────────────────────┤
│ scene_id       名称       当前生效版本        发布人   发布时间    操作 │
│ cycle_advice   周期建议   V3 温柔版           admin    10-22 10:00 管理模板②│
│ mood_reply     情绪回应   未发布（⚠ 不可生成） —        —          管理模板 │
└──────────────────────────────────────────────────────────────────────┘
```
① → `GET /scenes/page`；② → 进入页面 2。

**页面 2 `/ai/prompts/:sceneId`（场景下的模板列表）**

```
┌ 提示词管理 › 周期建议 (cycle_advice) ──────────────────────────────────┐
│ [对比所选(2)]③                                          [+ 新建模板]④ │
├──────────────────────────────────────────────────────────────────────┤
│ ☐⑤ 版本  名称      修改说明        状态      测试    更新人 更新时间  操作 │
│ ☑  V4   简洁版    缩短输出       未生效    ✓已测   ops1  10-23    编辑⑥ 复制⑦ 发布⑧│
│ ☑  V3   温柔版    调整语气       ●生效中   ✓已测   admin 10-22    查看  复制   —  │
│ ☐  V2   初版改    ...            未生效    ✗未测   admin 10-21    编辑  复制  发布(禁用)│
├──────────────────────────────────────────────────────────────────────┤
│                                          共 3 条  < 1 >  20条/页     │
└──────────────────────────────────────────────────────────────────────┘
```
列表 → `GET /prompt-templates/page?sceneId=`；③ 勾选 2~3 个 → 页面 4；④ → 页面 3（空白）；⑥ → 页面 3（编辑，生效中只读显示"查看"）；⑦ → 页面 3（新建，预填该模板内容）；⑧ → 二次确认「发布后 V4 立即对新请求生效，V3 失效」→ `POST /prompt-templates/{id}/publish`（未测试时按钮置灰并提示"请先测试"）。

**页面 3 `/ai/prompts/:sceneId/templates/new`、`/ai/prompt-templates/:id`（编辑 + 测试）**

```
┌ 提示词管理 › 周期建议 › V4 简洁版 ─────────────────────────────────────────────┐
│ ┌ 模板内容 ─────────────────────────────┐ ┌ 测试 ──────────────────────────────┐ │
│ │ 名称 *     [简洁版              ] ⑨   │ │ 使用模型：doubao-seed-1-6（主）    │ │
│ │ 修改说明   [缩短输出            ] ⑩   │ │           ep-2025xxx（备用）       │ │
│ │ 系统提示词 *                          │ │                                    │ │
│ │ ┌──────────────────────────────────┐ │ │ 1. 填写变量取值                    │ │
│ │ │你是一位温柔的健康助手...        │⑪│ │ age *         [28            ] ⑯   │ │
│ │ └──────────────────────────────────┘ │ │ cycle_phase * [黄体期        ]     │ │
│ │ 输入模板 *                            │ │ user_input    [最近有点累    ]     │ │
│ │ ┌──────────────────────────────────┐ │ │                                    │ │
│ │ │年龄{{age}}，处于{{cycle_phase}}..│⑫│ │ 2. ┌──────────────────────────┐   │ │
│ │ └──────────────────────────────────┘ │ │    │      ▶ 运行测试  ⑰      │   │ │
│ │ ⚠ 输入模板第 1 行引用了未声明变量    │ │    └──────────────────────────┘   │ │
│ │   {{mood}}                       ⑬   │ │                                    │ │
│ │ ── 变量 ──                 [+ 添加]⑭ │ │ 3. 测试结果（最后一次）         ⑱ │ │
│ │ 变量名        必填  说明         删除 │ │ 10-23 15:02 · ops1 · ✓ 成功        │ │
│ │ [age       ]  [✓]  [年龄      ]  ✕   │ │ 2.3s · 812/240 tokens · 尝试 1 次  │ │
│ │ [cycle_phase] [✓]  [周期阶段  ]  ✕   │ │ ┌──────────────────────────────┐  │ │
│ │ [user_input]  [ ]  [用户输入  ]  ✕   │ │ │{ "title": "...",             │  │ │
│ │                                       │ │ │  "tips": [...] }             │  │ │
│ │                                       │ │ └──────────────────────────────┘  │ │
│ └───────────────────────────────────────┘ └────────────────────────────────────┘ │
│ 状态：未生效 · ✗ 内容已修改，需重新测试          [返回] [保存]⑮  [发布]⑲        │
└──────────────────────────────────────────────────────────────────────────────┘
```
进入 → `GET /prompt-templates/{id}`（编辑时）；⑬ 前端实时提示未声明变量（服务端测试/发布时再校验一次）；⑮ → `POST /prompt-templates`（新建）或 `PUT /prompt-templates/{id}`（编辑，带 version）；⑯ 输入框由变量表生成，默认回填 `last_test_inputs`；⑰ 运行测试（右侧测试面板中部）→ 有未保存修改时先自动保存，再 `POST /prompt-templates/{id}/test`；变量有未声明/必填未填时按钮置灰并提示原因；新建模板未保存过时同样先保存再测试；⑱ 显示返回结果（即最后一次测试）；⑲ → `POST /prompt-templates/{id}/publish`，未测试时置灰并提示"请先运行测试"。生效中的模板左侧只读，右侧仍可运行测试（只更新测试结果，不改模板内容、不影响生效状态）。

**页面 4 `/ai/prompts/:sceneId/compare?ids=a,b[,c]`（对比）**

```
┌ 提示词管理 › 周期建议 › 对比 ──────────────────────────────────────────┐
│ ┌ V4 简洁版（未生效）─────────────┐ ┌ V3 温柔版（●生效中）────────────┐ │
│ │ 系统提示词                       │ │ 系统提示词                       │ │
│ │ 你是一位温柔的健康助手...        │ │ 你是一位温柔的健康助手...        │ │
│ │ 输入模板                         │ │ 输入模板                         │ │
│ │ 年龄{{age}}...                   │ │ 年龄{{age}}...                   │ │
│ │ 变量 age* cycle_phase* user_input│ │ 变量 age* cycle_phase*           │ │
│ │ ── 最后一次测试输入 ──           │ │ ── 最后一次测试输入 ──           │ │
│ │ {"age":28,...}                   │ │ {"age":30,...}                   │ │
│ │ ── 最后一次测试输出 ── ⑳         │ │ ── 最后一次测试输出 ──           │ │
│ │ { "title": "..." }               │ │ { "title": "..." }               │ │
│ └──────────────────────────────────┘ └──────────────────────────────────┘ │
│                                                              [返回]       │
└──────────────────────────────────────────────────────────────────────────┘
```
每列 → `GET /prompt-templates/{id}`；文本差异在前端高亮。

---

### F4 App 生成接口

**执行流程**

```
App ──POST /api/app/ai/generations──▶ app 后端
 0 （nginx）按 IP 限流，超出 → 429，请求到不了 app（F5 ①）
 1 校验 X-API-Key / X-Anonymous-Id / body，含输入长度限制（F5 ④）→ 400
 2 按 clientRequestId 查已有记录：存在 → 走"去重"分支（见下），不占用额度
 3 查场景：不存在 → 404；禁用 / 无生效模板 / 无主模型 → 409「场景不可用」
 4 渲染：F0 PromptRenderer.render，必填变量缺失 → 400；选填缺失替换为空串，快照里记 null
 5 额度检查：匿名 ID 当日次数、全站当日次数，超出 → 429（F5 ②③），不写记录
 6 先写一条生成记录（status=PROCESSING，含 inputs、模板、messages、参数快照、clientRequestId）
      └ 并发下 client_request_id 唯一冲突 → 同样走"去重"分支
 7 调用：F0 AiGenerator.generate（重试 / 切备用 / JSON 校验都在模块内）
 8 回写同一条生成记录：最终状态、AI 输出、实际模型、尝试次数、tokens、耗时、完成时间；
      同时把 attempts[] 逐条写入调用明细表（只供后台查看，App 拿不到）
 9 返回 200 {requestId, clientRequestId, status, content}
```

**去重**（`client_request_id` 全局唯一索引）
- 同一个 clientRequestId 再次提交，且 sceneId、匿名用户 ID 一致：
  - 已有最终状态 → 原样返回那次的结果，不再调模型；
  - 仍是 PROCESSING 且未超时 → 409「生成中，请稍后重试」；
  - PROCESSING 已超过 `AI_TIMEOUT_SECONDS × 总尝试上限 + 30s` → 视为服务中断，就地改为 `TIMEOUT` 并返回该失败结果。
- 同一个 clientRequestId 但 sceneId 或匿名用户 ID 不同 → 409「clientRequestId 冲突」。

**状态**：生成记录状态 = `PROCESSING`（生成中）+ F0 状态枚举。

**表 `loves_ai_generation`**（一行 = 一次业务请求）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | uuid | PK | 即 requestId |
| client_request_id | text | NOT NULL, UNIQUE | 客户端幂等键 |
| anonymous_user_id | text | NOT NULL | 匿名用户 ID |
| scene_id | text | NOT NULL | |
| template_id | uuid | NOT NULL | 本次固定使用的模板 |
| template_seq | int | NOT NULL | 模板版本号快照（列表展示/筛选） |
| app_version | text | | App 版本 |
| inputs | jsonb | NOT NULL | 输入快照：App 传来的 inputs 原样（未传的声明变量记 null） |
| request_messages | jsonb | NOT NULL | 实际发给模型的 messages（不含密钥/认证头） |
| request_params | jsonb | NOT NULL | 参数快照 `{primaryModel, backupModel, temperature, topP, maxTokens, timeoutSeconds, maxRetries}` |
| status | text | NOT NULL | 见状态枚举 |
| error_code | text | | 最终失败码 |
| error_message | text | | |
| final_model | text | | 最终产出结果（或最后尝试）的模型 |
| attempt_count | int | NOT NULL DEFAULT 0 | 总调用次数（含重试与备用） |
| raw_output | text | | 最后一次模型原始输出（格式失败时排查用） |
| content | jsonb | | 最终交付给 App 的 JSON |
| prompt_tokens / completion_tokens | int | | 各次调用合计 |
| latency_ms | int | | 总耗时 |
| created_at | timestamptz | NOT NULL | |
| completed_at | timestamptz | | |

索引：`(created_at DESC)`、`(scene_id, created_at DESC)`、`(anonymous_user_id, created_at)`（F5 匿名 ID 当日计数用）。

**表 `loves_ai_generation_attempt`**（调用明细，一行 = 一次模型调用；不对 App 开放，后台查看页面随 F6 后续提供）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | uuid | PK | attempt_id |
| generation_id | uuid | NOT NULL | 关联 requestId |
| seq | int | NOT NULL | 1、2、3… |
| trigger_reason | text | NOT NULL | INITIAL 首次 / RETRY 重试 / FALLBACK 切备用 |
| model | text | NOT NULL | |
| status | text | NOT NULL | F0 状态枚举 |
| error_code | text | | 供应商错误码 / HTTP 码 |
| error_message | text | | |
| provider_request_id | text | | 方舟返回的请求 ID |
| raw_response | text | | 原始返回 |
| parsed_content | jsonb | | 解析出的 JSON |
| validation_error | text | | JSON 解析错误信息 |
| prompt_tokens / completion_tokens | int | | |
| usage | jsonb | | 方舟 usage 原样（含缓存 token 等） |
| started_at / finished_at | timestamptz | | |
| latency_ms | int | | |

索引：`(generation_id, seq)`。

**API**

`POST /api/app/ai/generations`

| 位置 | 参数 | 必填 | 说明 |
|---|---|---|---|
| header | `X-API-Key` | ✓ | 现有 App 鉴权 |
| header | `X-Anonymous-Id` | ✓ | 匿名用户 ID，≤128，服务端原样记录不校验 |
| body | `sceneId` | ✓ | |
| body | `clientRequestId` | ✓ | ≤64，建议 UUID；每次点"生成/重新生成"都新建，网络重试沿用 |
| body | `appVersion` | | ≤32 |
| body | `inputs` | | `{变量名: 值}`，变量由 App 与运营对齐；值非字符串时按 JSON 文本代入 |

响应 200（成功或已处理的失败都返回 200，App 按 `status` 区分）：
```json
{ "requestId": "uuid", "clientRequestId": "c-123", "sceneId": "cycle_advice",
  "status": "SUCCESS", "content": { "...": "模型输出的 JSON" } }
```
失败时 `status` 为失败枚举、`content` 为 null。其他：400 参数/必填变量缺失；401 API Key 无效；404 场景不存在；409 场景不可用 / 生成中 / clientRequestId 冲突；429 超出限流或额度（见 F5）。

---

### F5 生成接口防刷

没有用户体系时 `X-API-Key` 与 `X-Anonymous-Id` 都可被获取或伪造，无法彻底防刷；目标是**提高刷的成本，并保证费用有上限**。四层叠加：

| 层 | 规则 | 实现位置 | 超出时 |
|---|---|---|---|
| ① IP 限流 | 单个 IP 平均每分钟 30 次，允许瞬时突发 10 次 | nginx `limit_req` | 429，请求不进 app |
| ② 匿名 ID 每日限次 | 单个 `X-Anonymous-Id` 每天最多 `AI_LIMIT_PER_ANON_DAILY`（默认 50）次 | app 生成接口 | 429「今日生成次数已达上限」 |
| ③ 全站每日总量 | 全站每天最多 `AI_LIMIT_GLOBAL_DAILY`（默认 5000）次，次日自动恢复——费用兜底，换 IP / 换 ID 都绕不过 | app 生成接口 | 429「服务繁忙，请稍后再试」 |
| ④ 输入长度 | `inputs` 每个值 ≤ 500 字符、整体序列化后 ≤ 4KB；输出长度由 `AI_MAX_TOKENS` 约束 | app 生成接口（参数校验） | 400 并指出超长变量名 |

**规则细节**
- "每天"按北京时间自然日（00:00–24:00）计算；计数取 `loves_ai_generation` 当日记录数（成功、失败都算，按 `created_at`），不另建计数表、不引入 Redis。
- 同一个 clientRequestId 的重复提交走去重分支，**不重复占用额度**。
- 被 ①②③④ 拒绝的请求**不写生成记录**，避免刷接口时把记录表一起刷爆。
- 429 与"已处理的生成失败（200 + 失败 status）"区分开：App 收到 429 提示"操作太频繁 / 今日次数已用完"，不要自动重试。
- 已知上限：②③ 是"先计数再插入"，并发下可能超出上限几次；对费用兜底而言可接受，需要精确时再改为数据库行锁计数。
- ④ 的 500 字符 / 4KB 是代码常量，不做配置。

**① nginx 配置**（写入 `docs/部署服务器初始化.md`，测试与正式环境各配一次）

```nginx
# tripleyourlife.com.conf 顶部、server { } 之外（conf.d 被 include 在 http { } 内）
limit_req_zone $binary_remote_addr zone=ls_ai_gen:10m rate=30r/m;

server {
    # 仅对生成接口限流；最长前缀匹配，优先于 /love-space/app/
    location /love-space/app/api/app/ai/ {
        limit_req zone=ls_ai_gen burst=10 nodelay;
        limit_req_status 429;
        proxy_pass http://172.17.0.1:8081/api/app/ai/;
    }
    location /love-space/app/ {
        proxy_pass http://172.17.0.1:8081/;
    }
}
```

上线前核对 nginx access log 中来源 IP 是各不相同的公网 IP；若全是 `172.17.0.1` 或 CDN 地址，需先改用 `real_ip` 模块取真实 IP，否则所有用户共用一份额度。

**供应商侧兜底（运维项，不写代码）**：火山方舟控制台为推理接入点设置调用频率 / token 上限，并配置账户费用预警。

**本期不做**：请求签名（密钥只能放在 App 内，可被逆向，提升有限）、设备证明（App Attest / Play Integrity，需客户端配合，后续按需）。

---

## Capabilities

### New Capabilities
- `ai`：AI 管理全链路——业务场景、场景模型配置、提示词模板（变量、测试、对比、发布）、AI 基础模块（方舟调用、渲染、重试、切备用、JSON 校验）、App 生成接口（去重，调用明细落库仅后台可见）、生成接口防刷（IP 限流、匿名 ID / 全站每日额度、输入长度）。生成记录的后台查询与人工评价不在本 change。新域 `ai` 需在 `tests/modules.md` 登记（端：web）。

### Modified Capabilities
- `operation-log`：留痕覆盖面新增 AI 管理模块的写接口（`ai-scene:*`、`ai-prompt:*`），模块/接口计数随之更新。

## Impact

- **新子项目 `love-space-ai/`**：AI 基础库（F0），方舟调用用 Spring 自带 `RestClient`，不引第三方 SDK。
- **数据库**：admin 端 Liquibase 新增 4 张表（`loves_ai_scene`、`loves_ai_prompt_template`、`loves_ai_generation`、`loves_ai_generation_attempt`），纯新增、可回滚。
- **love-space-admin**：pom 引入 `love-space-ai`；新增 `modules/ai`（场景、模型、模板、测试）。
- **love-space-app**：pom 引入 `love-space-ai`；新增 `modules/ai`（生成接口）。
- **love-space-web**：侧栏「AI 管理」+ 7 个路由页面（`/ai/scenes`、`/ai/models`、`/ai/prompts`、`/ai/prompts/:sceneId`、模板新建、模板编辑、对比）。
- **契约**：`contracts/api-spec.json` 新增 admin 端 15 个接口；`love-space-app/docs/openapi.json` 新增 1 个接口。
- **构建**：打包顺序变为 `love-space-ai` install → admin / app package，`docs/部署正式环境.md`、`docs/部署测试环境.md` 与 deploy skill 同步加这一步。
- **nginx**：测试 / 正式环境新增生成接口 IP 限流 location（F5 ①），`docs/部署服务器初始化.md` 同步；方舟控制台配置限额与费用预警。
- **部署**：`deploy/.env.prod`、`deploy/.env.test` 两个后端都要加 `ARK_*` / `AI_*` 环境变量；服务器需能访问 `ark.cn-beijing.volces.com`。
- **外部依赖**：火山方舟按量计费（本期不在系统内统计费用）。
