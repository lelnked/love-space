## ADDED Requirements

### Requirement: AI 调用配置来自环境变量
AI 基础模块（`love-space-ai`）SHALL 从环境变量读取全部模型调用配置：`ARK_API_KEY`（必填）、`ARK_BASE_URL`（默认 `https://ark.cn-beijing.volces.com/api/v3`）、`AI_MODELS`（必填，逗号分隔的模型 ID 列表）、`AI_TEMPERATURE` / `AI_TOP_P` / `AI_MAX_TOKENS`（未配置时不传给方舟）、`AI_TIMEOUT_SECONDS`（默认 30）、`AI_MAX_RETRIES`（默认 1，不含首次）。

数据库与任何接口响应 SHALL NOT 存储或返回 API Key；生成记录中的请求快照 SHALL NOT 包含密钥与认证头。

`GET /api/admin/ai/models` SHALL 返回 `AI_MODELS` 解析后的模型 ID 数组（去除首尾空白、忽略空项，保持配置顺序）。

#### Scenario: 模型下拉来自环境变量
- **GIVEN** admin 进程配置 `AI_MODELS=model-a, model-b`
- **WHEN** 已登录 Manager 请求 `GET /api/admin/ai/models`
- **THEN** 返回 200 与 `["model-a","model-b"]`

#### Scenario: 请求快照不含密钥
- **GIVEN** 一次 App 生成已完成
- **WHEN** 查看该生成记录的 `request_messages` 与 `request_params`
- **THEN** 两者均不含 `ARK_API_KEY` 的值与 `Authorization` 头

### Requirement: 提示词变量校验与渲染
AI 基础模块 SHALL 提供提示词校验与渲染能力：

- 变量声明为 `[{name, required, description}]`；`name` SHALL 匹配 `^[A-Za-z_][A-Za-z0-9_]*$` 且在同一模板内不重复，否则为变量声明错误。
- 系统提示词与输入模板中以 `{{name}}` 引用变量；引用了未声明的变量时，校验结果 SHALL 列出每个未声明变量的变量名与位置（`SYSTEM_PROMPT` 系统提示词 / `USER_PROMPT_TEMPLATE` 输入模板）。
- 渲染输出 messages `[{role: system, content}, {role: user, content}]`；必填变量缺失（未传或为 null）SHALL 抛校验异常并指出变量名；选填变量缺失替换为空串。
- 变量值为字符串时原样代入；非字符串（数字、布尔、对象、数组）SHALL 按其 JSON 文本代入。

#### Scenario: 未声明变量被指出位置
- **GIVEN** 模板声明变量 `age`，输入模板含 `{{age}}{{mood}}`，系统提示词含 `{{tone}}`
- **WHEN** 调用校验
- **THEN** 结果列出 `mood`（输入模板）与 `tone`（系统提示词）两项

#### Scenario: 必填变量缺失渲染失败
- **GIVEN** 模板声明必填变量 `age`
- **WHEN** 以不含 `age` 的 inputs 渲染
- **THEN** 抛校验异常，信息包含 `age`

#### Scenario: 选填变量缺失替换为空串
- **GIVEN** 模板声明选填变量 `user_input`，输入模板为 `说明：{{user_input}}`
- **WHEN** 以不含 `user_input` 的 inputs 渲染
- **THEN** user 消息内容为 `说明：`

#### Scenario: 非字符串值按 JSON 文本代入
- **GIVEN** 模板声明变量 `tags`，输入模板为 `{{tags}}`
- **WHEN** 以 `{"tags": ["a","b"]}` 渲染
- **THEN** user 消息内容为 `["a","b"]`

#### Scenario: 变量名非法或重复为声明错误
- **GIVEN** 变量声明含 `1abc`，或两个同名 `age`
- **WHEN** 调用校验
- **THEN** 结果包含变量声明错误

### Requirement: 生成重试、切备用与结果判定
AI 基础模块的生成调用 SHALL 按以下顺序尝试：主模型首次 + `AI_MAX_RETRIES` 次重试；仍失败且配置了备用模型时，备用模型首次 + `AI_MAX_RETRIES` 次重试；任一次成功即停止。

单次调用的状态判定：

| 状态 | 判定 |
|---|---|
| SUCCESS | 方舟正常返回、内容非空、内容可解析为 JSON 对象或数组 |
| REFUSED | `finish_reason=content_filter` |
| EMPTY | 返回内容为空或仅空白 |
| FORMAT_ERROR | 内容不能解析为 JSON 对象或数组 |
| TIMEOUT | 超过 `AI_TIMEOUT_SECONDS` |
| RATE_LIMITED | 方舟返回 HTTP 429 |
| UPSTREAM_ERROR | 其他 HTTP 错误或网络异常 |

解析前 SHALL 去除包裹整段内容的 Markdown 代码块标记（如 ```json … ```），原始输出仍按原样保存。

除 SUCCESS 外的状态 SHALL 都触发重试 / 切备用。最终结果 SHALL 取最后一次调用的状态、内容与模型；`promptTokens` / `completionTokens` 为各次调用合计；`latencyMs` 为总耗时；`attempts[]` 按顺序记录每一次调用，触发原因为 `INITIAL`（首次）、`RETRY`（同模型重试）、`FALLBACK`（切到备用模型的首次）。

连接测试 SHALL 向指定模型发送一条极短请求，返回 `{ok, latencyMs, message}`；方舟正常返回即 `ok=true`，不校验 JSON。

#### Scenario: 主模型首次成功
- **GIVEN** 主模型返回合法 JSON
- **WHEN** 发起生成
- **THEN** 状态 SUCCESS，`attempts` 仅 1 条（INITIAL），`finalModel` 为主模型

#### Scenario: 主模型失败后重试成功
- **GIVEN** `AI_MAX_RETRIES=1`，主模型首次返回非 JSON、第二次返回合法 JSON
- **WHEN** 发起生成
- **THEN** 状态 SUCCESS，`attempts` 为 INITIAL(FORMAT_ERROR)、RETRY(SUCCESS)，tokens 为两次合计

#### Scenario: 主模型耗尽后切备用
- **GIVEN** `AI_MAX_RETRIES=1`，主模型两次均超时，备用模型首次成功
- **WHEN** 发起生成
- **THEN** 状态 SUCCESS，`attempts` 为 INITIAL、RETRY、FALLBACK 共 3 条，`finalModel` 为备用模型

#### Scenario: 无备用模型时全部失败
- **GIVEN** `AI_MAX_RETRIES=1`，未配置备用模型，主模型两次均返回 HTTP 429
- **WHEN** 发起生成
- **THEN** 状态 RATE_LIMITED，`attempts` 共 2 条，`content` 为空

#### Scenario: 代码块包裹的 JSON 视为成功
- **GIVEN** 模型返回 ```` ```json\n{"a":1}\n``` ````
- **WHEN** 发起生成
- **THEN** 状态 SUCCESS，`content` 为 `{"a":1}`，`rawOutput` 保留原文

#### Scenario: 各失败状态判定
- **GIVEN** 方舟分别返回 `finish_reason=content_filter`、空内容、非 JSON 文本、HTTP 500
- **WHEN** 发起生成（无重试、无备用）
- **THEN** 状态分别为 REFUSED、EMPTY、FORMAT_ERROR、UPSTREAM_ERROR

#### Scenario: 连接测试
- **GIVEN** 已登录 Manager，`model-a` 在 `AI_MODELS` 内且方舟可达
- **WHEN** 请求 `POST /api/admin/ai/models/test-connection` body `{model: "model-a"}`
- **THEN** 返回 200 与 `{ok: true, latencyMs: <正整数>, message}`；方舟不可达时返回 200 与 `ok: false` 及原因

### Requirement: 业务场景管理
admin 端 SHALL 提供业务场景（scene）管理：

- 字段：`sceneId`（必填，匹配 `^[a-z][a-z0-9_]{1,63}$`，全局唯一，**创建后不可修改**）、`name`（必填，≤64）、`description`（≤500）、`enabled`（创建默认 true）。
- `POST /api/admin/ai/scenes` 创建，返回 201 与 SceneDetail；`sceneId` 已存在返回 409「scene_id 已存在」；格式/长度不符返回 400。
- `PUT /api/admin/ai/scenes/{id}` 只更新 `name` 与 `description`，请求体中的 sceneId 被忽略。
- `PUT /api/admin/ai/scenes/{id}/enabled` 切换启用状态，返回 204。
- `GET /api/admin/ai/scenes/page` 分页，过滤 `keyword`（模糊匹配 sceneId 或名称）、`enabled`；按创建时间倒序；分页遵循项目统一口径（page 1 基，size 仅 20/200）。
- `GET /api/admin/ai/scenes/options` 返回全量 `[{id, sceneId, name, enabled}]`。
- `GET /api/admin/ai/scenes/{id}` 返回详情；不存在返回 404。
- SceneItem / SceneDetail 结构 SHALL 为 `{id, sceneId, name, description, enabled, primaryModel, backupModel, activeTemplate: {id, seq, name, publishedAt, publishedBy} | null, createdAt, updatedAt}`。
- 场景不可删除。

#### Scenario: 创建场景
- **GIVEN** 已登录 Manager
- **WHEN** 提交 `{sceneId: "cycle_advice", name: "周期建议"}`
- **THEN** 返回 201，`enabled=true`、`primaryModel=null`、`activeTemplate=null`

#### Scenario: sceneId 重复被拒绝
- **GIVEN** 已存在 sceneId `cycle_advice`
- **WHEN** 再次以 `cycle_advice` 创建
- **THEN** 返回 409，message 为「scene_id 已存在」

#### Scenario: sceneId 格式非法
- **GIVEN** 已登录 Manager
- **WHEN** 以 `Cycle-Advice` 或单字符 `a` 创建
- **THEN** 返回 400 及中文校验信息

#### Scenario: 更新不改 sceneId
- **GIVEN** 一个 sceneId 为 `cycle_advice` 的场景
- **WHEN** PUT 更新时 body 携带 `sceneId: "other"` 与新名称
- **THEN** 返回 200，名称已更新，sceneId 仍为 `cycle_advice`

#### Scenario: 按关键字与状态筛选
- **GIVEN** 存在启用场景 `cycle_advice`（周期建议）与禁用场景 `mood_reply`（情绪回应）
- **WHEN** 以 `keyword=cycle` 查询；再以 `enabled=false` 查询
- **THEN** 前者仅返回 `cycle_advice`；后者仅返回 `mood_reply`

#### Scenario: 禁用场景
- **GIVEN** 一个启用中的场景
- **WHEN** `PUT /scenes/{id}/enabled` body `{enabled: false}`
- **THEN** 返回 204，详情 `enabled=false`

### Requirement: 场景模型配置
admin 端 SHALL 支持为场景配置主模型与备用模型：`PUT /api/admin/ai/scenes/{id}/models` body `{primaryModel, backupModel?}`，成功返回 204，保存即对之后的新请求生效（不做版本/发布）。

校验：`primaryModel` 必填；`primaryModel` 与非空 `backupModel` 都 SHALL 在 `AI_MODELS` 内；`backupModel` 不得与 `primaryModel` 相同；任一不满足返回 400 及中文信息。`backupModel` 为 null 或空串表示不使用备用模型。

`POST /api/admin/ai/models/test-connection` 的 `model` 不在 `AI_MODELS` 内时返回 400。

#### Scenario: 配置主备模型
- **GIVEN** `AI_MODELS=model-a,model-b`，一个场景
- **WHEN** 提交 `{primaryModel: "model-a", backupModel: "model-b"}`
- **THEN** 返回 204，详情 `primaryModel=model-a`、`backupModel=model-b`

#### Scenario: 模型不在白名单
- **GIVEN** `AI_MODELS=model-a,model-b`
- **WHEN** 提交 `{primaryModel: "model-x"}`
- **THEN** 返回 400，场景模型不变

#### Scenario: 主备模型相同
- **GIVEN** `AI_MODELS=model-a,model-b`
- **WHEN** 提交 `{primaryModel: "model-a", backupModel: "model-a"}`
- **THEN** 返回 400，场景模型不变

#### Scenario: 清除备用模型
- **GIVEN** 场景已配置备用模型 `model-b`
- **WHEN** 提交 `{primaryModel: "model-a", backupModel: null}`
- **THEN** 返回 204，详情 `backupModel=null`

### Requirement: 提示词模板维护
admin 端 SHALL 提供提示词模板的新建、修改与查询：

- 模板归属一个场景（按 sceneId），创建时场景不存在返回 404。字段：`name`（必填，≤64）、`systemPrompt`（必填）、`userPromptTemplate`（必填）、`variables`（`[{name, required, description}]`，默认 `[]`）、`remark`。
- `seq` 在场景内从 1 开始自增，由服务端分配；同场景并发创建 SHALL NOT 产生重复 seq。
- 引用了未声明变量的模板 SHALL 允许保存；变量声明错误（名称非法/重复）SHALL 返回 400。
- 当前生效模板（场景 `active_template_id` 指向）SHALL NOT 被修改，返回 409「生效中的模板不能修改」；未生效模板（含以前发布过的）可修改。
- 修改与发布 SHALL 携带 `version` 乐观锁；与当前版本不一致返回 409「内容已被他人更新，请刷新」。
- 任何修改 SHALL 把 `tested` 置为 false。
- 模板不可删除；没有"复制"接口。
- `GET /api/admin/ai/prompt-templates/page?sceneId=` 按 seq 倒序分页，条目 `{id, seq, name, remark, active, tested, lastTestAt, publishedAt, publishedBy, updatedBy, updatedAt}`；`GET /prompt-templates/{id}` 返回全部字段 + `active`；不存在返回 404。
- `createdBy` / `updatedBy` 记录操作 Manager 的用户名。

#### Scenario: 新建模板自动分配 seq
- **GIVEN** 场景 `cycle_advice` 下已有 V1、V2
- **WHEN** 新建模板
- **THEN** 返回 201，`seq=3`、`tested=false`、`active=false`、`createdBy` 为当前用户名

#### Scenario: 引用未声明变量也可保存
- **GIVEN** 已登录 Manager
- **WHEN** 新建模板，输入模板含 `{{mood}}` 但 variables 未声明 mood
- **THEN** 返回 201

#### Scenario: 变量名非法被拒绝
- **GIVEN** 已登录 Manager
- **WHEN** 新建模板，variables 含 `{name: "1abc"}`
- **THEN** 返回 400 及中文信息

#### Scenario: 修改已测试模板重置测试标记
- **GIVEN** 一个未生效、`tested=true` 的模板
- **WHEN** 携带正确 version 修改其系统提示词
- **THEN** 返回 200，`tested=false`，`version` 递增

#### Scenario: 生效中模板不能修改
- **GIVEN** 场景当前生效模板为 V3
- **WHEN** 修改 V3
- **THEN** 返回 409「生效中的模板不能修改」，内容不变

#### Scenario: 版本冲突
- **GIVEN** 模板当前 version 为 2
- **WHEN** 携带 `version: 1` 修改
- **THEN** 返回 409「内容已被他人更新，请刷新」

#### Scenario: 模板列表按 seq 倒序
- **GIVEN** 场景下有 V1、V2、V3，V2 生效
- **WHEN** 查询该场景模板列表
- **THEN** 顺序为 V3、V2、V1，仅 V2 的 `active=true`

### Requirement: 提示词模板测试
admin 端 SHALL 提供 `POST /api/admin/ai/prompt-templates/{id}/test` body `{inputs}`，以场景当前配置的主/备模型调用 AI 基础模块生成（重试与切备用规则同基础模块）：

- 模板存在未声明变量或变量声明错误 → 400，信息指出变量名与位置。
- 必填变量缺失 → 400，信息指出变量名。
- 场景未配置主模型 → 409。
- 调用完成后（无论成功失败）SHALL 把本次 `inputs`、原始输出、解析出的 JSON（失败为 null）、实际模型、是否成功、失败原因、耗时、测试时间、测试人写回模板的 `last_test_*` 字段；状态 SUCCESS 时 SHALL 置 `tested=true`，否则 `tested` 不变。
- 测试 SHALL NOT 改变模板内容、`version` 与生效状态；生效中的模板也可测试。
- 测试进行期间模板内容被他人修改（version 已变）时，本次结果 SHALL NOT 写回模板、SHALL NOT 置 `tested=true`，仍向调用方返回本次结果。
- 测试 SHALL NOT 写生成记录。
- 响应 `{success, model, output, content, errorCode, errorMessage, latencyMs, promptTokens, completionTokens, testedAt}`。

#### Scenario: 测试成功置已测试
- **GIVEN** 场景已配主模型，模板变量完整且模型返回合法 JSON
- **WHEN** 以完整 inputs 测试
- **THEN** 返回 200 `success=true`，模板 `tested=true`，`lastTestInputs` 等于本次 inputs，`lastTestBy` 为当前用户名

#### Scenario: 测试失败保留未测试
- **GIVEN** 一个 `tested=false` 的模板，模型始终返回非 JSON
- **WHEN** 测试
- **THEN** 返回 200 `success=false`、`errorCode=FORMAT_ERROR`，模板 `tested=false`，`lastTestOutput` 为模型原始输出

#### Scenario: 存在未声明变量拒绝测试
- **GIVEN** 模板输入模板引用了未声明变量 `mood`
- **WHEN** 测试
- **THEN** 返回 400，信息包含 `mood` 与「输入模板」

#### Scenario: 必填变量缺失拒绝测试
- **GIVEN** 模板声明必填变量 `age`
- **WHEN** 以不含 `age` 的 inputs 测试
- **THEN** 返回 400，信息包含 `age`，模板 `last_test_*` 不变

#### Scenario: 场景未配主模型拒绝测试
- **GIVEN** 场景 `primaryModel` 为空
- **WHEN** 测试该场景下的模板
- **THEN** 返回 409

#### Scenario: 测试不写生成记录
- **GIVEN** 生成记录表中记录数为 N
- **WHEN** 对任意模板测试一次
- **THEN** 生成记录表记录数仍为 N

### Requirement: 提示词模板发布
admin 端 SHALL 提供 `POST /api/admin/ai/prompt-templates/{id}/publish` body `{version}`，成功返回 204：

- `tested=false` → 409「请先测试」。
- 存在未声明变量或变量声明错误 → 400。
- version 不一致 → 409「内容已被他人更新，请刷新」。
- 发布 SHALL 原子地把所属场景的 `active_template_id` 指向该模板，并更新模板 `published_at` / `published_by`；原生效模板自动失效。
- 场景下任意已测试模板都可发布，与 seq 新旧、是否发布过无关；没有单独的回退接口。
- 场景已禁用时也允许发布。

#### Scenario: 发布切换生效模板
- **GIVEN** 场景生效模板为 V3，V4 已测试
- **WHEN** 发布 V4
- **THEN** 返回 204，场景 `activeTemplate.seq=4`，V3 `active=false`，V4 `publishedBy` 为当前用户名

#### Scenario: 发布旧版本即回退
- **GIVEN** 场景生效模板为 V3，V1 已测试且以前发布过
- **WHEN** 发布 V1
- **THEN** 返回 204，场景 `activeTemplate.seq=1`

#### Scenario: 未测试不能发布
- **GIVEN** 一个 `tested=false` 的模板
- **WHEN** 发布
- **THEN** 返回 409「请先测试」，场景生效模板不变

#### Scenario: 禁用场景也可发布
- **GIVEN** 场景已禁用，模板已测试
- **WHEN** 发布
- **THEN** 返回 204

### Requirement: AI 管理写操作留痕
AI 管理的写接口 SHALL 通过现有操作日志留痕：

| 接口 | module:action |
|---|---|
| `POST /api/admin/ai/scenes` | `ai-scene:create` |
| `PUT /api/admin/ai/scenes/{id}` | `ai-scene:update` |
| `PUT /api/admin/ai/scenes/{id}/enabled` | `ai-scene:set-enabled` |
| `PUT /api/admin/ai/scenes/{id}/models` | `ai-scene:set-models` |
| `POST /api/admin/ai/prompt-templates` | `ai-prompt:create` |
| `PUT /api/admin/ai/prompt-templates/{id}` | `ai-prompt:update` |
| `POST /api/admin/ai/prompt-templates/{id}/publish` | `ai-prompt:publish` |

`POST /prompt-templates/{id}/test` 与 `POST /models/test-connection` SHALL NOT 留痕。

#### Scenario: 发布留痕
- **GIVEN** 以 admin 登录
- **WHEN** 发布一个已测试模板，随后查询操作日志
- **THEN** 存在 `module=ai-prompt`、`action=publish`、`target` 为该模板 id 的记录

#### Scenario: 测试不留痕
- **GIVEN** 以 admin 登录
- **WHEN** 测试一个模板，随后查询操作日志
- **THEN** 不存在 `module=ai-prompt`、`action=test` 的记录

### Requirement: AI 管理权限与菜单
`/api/admin/ai/**` SHALL 对所有已登录 Manager（ADMIN、MEMBER）开放，未登录返回 401。web 端侧栏 SHALL 新增一级菜单「AI 管理」，对所有角色可见，子菜单依次为「业务配置」(`/ai/scenes`)、「模型配置」(`/ai/models`)、「提示词管理」(`/ai/prompts`)。

#### Scenario: MEMBER 可访问 AI 管理
- **GIVEN** 以 MEMBER 角色登录
- **WHEN** 请求 `GET /api/admin/ai/scenes/page`
- **THEN** 返回 200

#### Scenario: 未登录被拒绝
- **GIVEN** 未携带 token
- **WHEN** 请求 `GET /api/admin/ai/scenes/page`
- **THEN** 返回 401

#### Scenario: 侧栏菜单
- **GIVEN** 以 MEMBER 登录后台
- **WHEN** 展开侧栏「AI 管理」
- **THEN** 依次显示「业务配置」「模型配置」「提示词管理」三个子菜单

### Requirement: 业务配置页面
web 端 SHALL 在 `/ai/scenes` 提供业务配置页：

- 筛选区：①关键字输入框（匹配 scene_id / 名称）、②状态下拉（全部/启用/禁用）；③「+ 新增场景」按钮。
- 列表列：scene_id、名称、说明、状态（启用/禁用）、创建时间、操作（④编辑、⑤启用/禁用）；⑥分页器默认 20 条/页、可切 200。
- 新增/编辑为弹窗：⑦scene_id（编辑时只读）、⑧名称、⑨说明、⑩保存；后端错误 message 原样 toast。
- ⑤启用/禁用 SHALL 二次确认后执行，成功后刷新当前页。
- 列表为空时显示空态。

#### Scenario: 新增场景弹窗
- **GIVEN** 已登录并进入 `/ai/scenes`
- **WHEN** 点击③「新增场景」，填写⑦⑧后点⑩保存
- **THEN** 弹窗关闭，列表出现新行，状态列显示「启用」

#### Scenario: 编辑时 scene_id 只读
- **GIVEN** 列表中存在场景 `cycle_advice`
- **WHEN** 点击该行④编辑
- **THEN** 弹窗中⑦scene_id 输入框为只读并显示 `cycle_advice`，⑧⑨回填当前值

#### Scenario: 重复 scene_id 提示
- **GIVEN** 已存在 `cycle_advice`
- **WHEN** 新增弹窗以 `cycle_advice` 保存
- **THEN** 弹窗保持打开，toast 显示「scene_id 已存在」

#### Scenario: 禁用需确认
- **GIVEN** 一个启用中的场景
- **WHEN** 点击⑤禁用并在确认框确认
- **THEN** 该行状态列变为「禁用」，操作列按钮变为「启用」

### Requirement: 模型配置页面
web 端 SHALL 在 `/ai/models` 提供模型配置页：

- ①关键字搜索；列表列 scene_id、名称、主模型、备用模型、操作（②配置）。主模型为空时显示「未配置（⚠ 不可生成）」，备用模型为空显示「—」。
- ②配置打开弹窗：③主模型下拉、⑤备用模型下拉（含「不使用」），选项来自 `GET /api/admin/ai/models`；④每个下拉旁「测试连接」按钮，显示可达与耗时或失败原因；弹窗内固定提示「连接成功只代表服务可达，不代表生成质量」；⑥保存。
- 主备选择相同模型时前端即时提示，后端 400 信息原样展示。

#### Scenario: 未配置主模型提示
- **GIVEN** 场景 `mood_reply` 未配置主模型
- **WHEN** 进入 `/ai/models`
- **THEN** 该行主模型列显示「未配置（⚠ 不可生成）」

#### Scenario: 测试连接显示结果
- **GIVEN** 在配置弹窗③选中一个可达模型
- **WHEN** 点击④测试连接
- **THEN** 按钮旁显示「✓ 可达」与毫秒耗时

#### Scenario: 保存模型配置
- **GIVEN** 在配置弹窗选好主模型、备用模型「不使用」
- **WHEN** 点击⑥保存
- **THEN** 弹窗关闭，该行主模型列显示所选模型、备用模型列显示「—」

### Requirement: 提示词管理页面
web 端 SHALL 提供：

- **页面 1 `/ai/prompts`**：①关键字搜索；列表列 scene_id、名称、当前生效版本（`V{seq} {name}`，未发布显示「未发布（⚠ 不可生成）」）、发布人、发布时间、操作（②管理模板 → 页面 2）。
- **页面 2 `/ai/prompts/:sceneId`**：标题含场景名与 sceneId；⑤勾选框 + 列表列版本（V{seq}）、名称、修改说明、状态（●生效中/未生效）、测试（✓已测/✗未测）、更新人、更新时间、操作；按 seq 倒序分页。
  - ③「对比所选(n)」仅在勾选 2~3 个时可用，跳页面 4。
  - ④「+ 新建模板」跳空白页面 3；⑥编辑（生效中显示为「查看」）跳页面 3；⑦复制跳新建页并预填该模板内容（名称、提示词、变量、说明）。
  - ⑧发布：生效中行不显示；`tested=false` 时置灰并提示「请先测试」；点击后二次确认「发布后 V{新} 立即对新请求生效，V{旧} 失效」，成功后刷新列表。

#### Scenario: 场景列表显示未发布
- **GIVEN** 场景 `mood_reply` 无生效模板
- **WHEN** 进入 `/ai/prompts`
- **THEN** 该行当前生效版本列显示「未发布（⚠ 不可生成）」

#### Scenario: 对比按钮需勾选 2~3 个
- **GIVEN** 页面 2 中有 4 个模板
- **WHEN** 勾选 1 个；再勾选到 2 个；再勾选到 4 个
- **THEN** ③对比按钮依次为不可用、可用、不可用

#### Scenario: 未测试模板发布置灰
- **GIVEN** 页面 2 中模板 V2 `tested=false`
- **WHEN** 查看 V2 行操作列
- **THEN** ⑧发布按钮置灰，悬停提示「请先测试」

#### Scenario: 发布二次确认后切换生效
- **GIVEN** V3 生效、V4 已测试
- **WHEN** 点击 V4 行⑧发布并确认
- **THEN** 列表刷新后 V4 状态为「●生效中」，V3 为「未生效」

#### Scenario: 复制为新模板
- **GIVEN** 页面 2 中存在 V3
- **WHEN** 点击 V3 行⑦复制
- **THEN** 进入新建页，⑨名称、⑪系统提示词、⑫输入模板、⑭变量表预填 V3 内容，保存后生成新的 seq

### Requirement: 模板编辑与测试页面
web 端 SHALL 在 `/ai/prompts/:sceneId/templates/new`（新建）与 `/ai/prompt-templates/:id`（编辑/查看）提供模板编辑与测试页，左右两栏：

- **左栏模板内容**：⑨名称、⑩修改说明、⑪系统提示词、⑫输入模板、⑭变量表（变量名/必填/说明/删除，「+ 添加」）；⑬实时提示未声明变量（变量名 + 位置）；⑮保存（新建 → POST 后跳到编辑路由；编辑 → PUT 带 version）。
- **右栏测试**：顶部显示场景当前主/备模型；⑯按变量表生成输入框（必填带 *），默认回填 `lastTestInputs`；⑰运行测试——有未保存修改时先自动保存再测试；存在未声明变量或必填未填时置灰并提示原因；场景未配主模型时置灰并提示；⑱显示最后一次测试结果（时间、测试人、成功/失败、耗时、tokens、输出 JSON 或错误信息）。
- **底部**：状态（生效中/未生效 · 已测试/内容已修改需重新测试）；⑲发布，`tested=false` 或有未保存修改时置灰并提示「请先运行测试」。
- 生效中的模板左栏只读、不显示保存按钮，右栏仍可运行测试。
- 保存/发布返回 409 版本冲突时 toast 后端信息，不覆盖本地编辑内容。

#### Scenario: 未声明变量实时提示
- **GIVEN** 在编辑页变量表只有 `age`
- **WHEN** 在⑫输入模板中输入 `{{mood}}`
- **THEN** ⑬显示「输入模板引用了未声明变量 {{mood}}」，⑰运行测试置灰

#### Scenario: 运行测试前自动保存
- **GIVEN** 编辑一个未生效模板并修改了⑪系统提示词（未保存）
- **WHEN** 填好⑯变量值后点击⑰运行测试
- **THEN** 先保存成功，再显示⑱测试结果；底部状态随结果更新

#### Scenario: 生效中模板只读可测试
- **GIVEN** 打开生效中模板 V3
- **WHEN** 查看页面
- **THEN** 左栏控件只读、无⑮保存按钮，右栏⑰运行测试可用

#### Scenario: 未测试时发布置灰
- **GIVEN** 模板 `tested=false`
- **WHEN** 查看底部
- **THEN** ⑲发布置灰，提示「请先运行测试」

### Requirement: 模板对比页面
web 端 SHALL 在 `/ai/prompts/:sceneId/compare?ids=a,b[,c]` 并排展示 2~3 个模板：每列标题 `V{seq} {name}（生效状态）`，内容依次为系统提示词、输入模板、变量（必填带 *）、最后一次测试输入、⑳最后一次测试输出；各列间文本差异高亮；底部「返回」回到页面 2。`ids` 不足 2 个或超过 3 个时提示并返回页面 2。

#### Scenario: 并排对比
- **GIVEN** 页面 2 勾选 V4 与 V3
- **WHEN** 点击③对比所选
- **THEN** 进入对比页，左右两列分别显示 V4 与 V3，V3 标题含「●生效中」，不同的文本被高亮

#### Scenario: ids 数量非法
- **GIVEN** 直接访问 `compare?ids=a`
- **WHEN** 页面加载
- **THEN** 提示需选择 2~3 个模板并回到页面 2

### Requirement: App 生成接口
app 端 SHALL 提供 `POST /api/app/ai/generations`：

- 请求头：`X-API-Key`（现有鉴权，无效 401）、`X-Anonymous-Id`（必填，≤128，原样记录）。
- 请求体：`sceneId`（必填）、`clientRequestId`（必填，≤64）、`appVersion`（≤32）、`inputs`（对象，可空）。参数缺失/超长 → 400。
- 处理顺序：参数与长度校验 → 去重查找 → 场景校验（不存在 404；禁用、无生效模板、无主模型 → 409「场景不可用」）→ 渲染（必填变量缺失 400）→ 额度检查（429）→ 写 PROCESSING 记录 → 调用 AI 基础模块 → 回写记录 → 返回。
- 生成固定使用请求时刻场景的生效模板与主/备模型。
- 响应 200 `{requestId, clientRequestId, sceneId, status, content}`：成功与已处理的失败都返回 200，App 按 `status` 区分；失败时 `content` 为 null。响应 SHALL NOT 包含模型名、原始输出、tokens、attempts 等调用明细。

#### Scenario: 生成成功
- **GIVEN** 场景启用、已发布模板、已配主模型，模型返回合法 JSON
- **WHEN** App 以有效 API Key 与匿名 ID 提交生成
- **THEN** 返回 200，`status=SUCCESS`，`content` 为模型输出的 JSON，`requestId` 为生成记录 id

#### Scenario: 已处理的失败返回 200
- **GIVEN** 场景可用，主备模型均返回非 JSON
- **WHEN** App 提交生成
- **THEN** 返回 200，`status=FORMAT_ERROR`，`content=null`

#### Scenario: 场景不存在
- **GIVEN** 不存在 sceneId `unknown`
- **WHEN** App 以 `sceneId=unknown` 提交
- **THEN** 返回 404

#### Scenario: 场景不可用
- **GIVEN** 场景分别处于：已禁用 / 无生效模板 / 未配主模型
- **WHEN** App 提交生成
- **THEN** 均返回 409「场景不可用」，不写生成记录

#### Scenario: 必填变量缺失
- **GIVEN** 生效模板声明必填变量 `age`
- **WHEN** App 提交的 inputs 不含 `age`
- **THEN** 返回 400，信息包含 `age`，不写生成记录

#### Scenario: 缺少匿名 ID
- **GIVEN** 有效 API Key
- **WHEN** 请求不带 `X-Anonymous-Id`
- **THEN** 返回 400

#### Scenario: API Key 无效
- **GIVEN** 错误的 `X-API-Key`
- **WHEN** 提交生成
- **THEN** 返回 401

#### Scenario: 响应不含调用明细
- **GIVEN** 一次经过重试才成功的生成
- **WHEN** 查看响应体
- **THEN** 仅含 requestId、clientRequestId、sceneId、status、content 五个字段

### Requirement: 生成请求去重
`client_request_id` SHALL 全局唯一。同一 clientRequestId 再次提交时：

- sceneId 与匿名 ID 均一致：
  - 已有最终状态 → 原样返回那次结果（同一 requestId），不再调用模型，不占用额度；
  - 仍为 PROCESSING 且创建时间距今未超过 `AI_TIMEOUT_SECONDS × 总尝试上限 + 30s`（总尝试上限 = `(1 + AI_MAX_RETRIES) × (配置了备用模型 ? 2 : 1)`，按记录中的参数快照计算）→ 409「生成中，请稍后重试」；
  - PROCESSING 已超过上述时长 → 就地改为 `TIMEOUT`（写 `completed_at`）并返回该失败结果。
- sceneId 或匿名 ID 不一致 → 409「clientRequestId 冲突」。
- 并发下插入 PROCESSING 记录遇唯一冲突时 SHALL 同样走去重分支。
- 去重查找在场景校验与额度检查之前执行。

#### Scenario: 重复提交返回原结果
- **GIVEN** clientRequestId `c-1` 的生成已 SUCCESS
- **WHEN** 以相同 sceneId、匿名 ID、`c-1` 再次提交
- **THEN** 返回 200 且 requestId、content 与首次相同，模型调用次数不增加，生成记录数不增加

#### Scenario: 生成中重复提交
- **GIVEN** `c-2` 的记录为 PROCESSING 且刚创建
- **WHEN** 以相同参数再次提交
- **THEN** 返回 409「生成中，请稍后重试」

#### Scenario: PROCESSING 超时就地失败
- **GIVEN** `c-3` 的记录为 PROCESSING 且创建时间早于超时阈值
- **WHEN** 以相同参数再次提交
- **THEN** 返回 200 `status=TIMEOUT`，该记录状态变为 TIMEOUT 且有 `completed_at`

#### Scenario: clientRequestId 冲突
- **GIVEN** `c-4` 已由匿名 ID `anon-A` 提交
- **WHEN** 匿名 ID `anon-B` 以 `c-4` 提交
- **THEN** 返回 409「clientRequestId 冲突」

#### Scenario: 重复提交不受场景状态影响
- **GIVEN** `c-5` 已 SUCCESS，之后该场景被禁用
- **WHEN** 以相同参数再次提交 `c-5`
- **THEN** 返回 200 与原结果（不返回 409 场景不可用）

### Requirement: 生成记录与调用明细落库
每次通过额度检查的生成请求 SHALL 在调用模型前写入一条 `loves_ai_generation` 记录（`status=PROCESSING`），包含 clientRequestId、匿名 ID、sceneId、模板 id 与 seq、appVersion、inputs 快照（未传的声明变量记 null）、实际 messages、参数快照 `{primaryModel, backupModel, temperature, topP, maxTokens, timeoutSeconds, maxRetries}`。

调用结束后 SHALL 回写同一条记录：最终状态、errorCode、errorMessage、finalModel、attemptCount、rawOutput、content、tokens 合计、latencyMs、completedAt；并把每次模型调用逐条写入 `loves_ai_generation_attempt`（seq、触发原因、模型、状态、错误码与信息、供应商请求 ID、原始返回、解析出的 JSON、校验错误、tokens、usage 原样、起止时间、耗时）。

被参数校验、场景校验、渲染、额度检查拒绝的请求 SHALL NOT 写记录。调用明细 SHALL NOT 通过任何 App 接口返回。

#### Scenario: 成功生成写完整记录
- **GIVEN** 一次主模型首次成功的生成
- **WHEN** 查询数据库
- **THEN** `loves_ai_generation` 有 1 条 `status=SUCCESS`、`attempt_count=1`、`completed_at` 非空的记录；`loves_ai_generation_attempt` 有 1 条 `trigger_reason=INITIAL` 的明细

#### Scenario: 重试与切备用逐条记明细
- **GIVEN** 一次 INITIAL、RETRY 失败后 FALLBACK 成功的生成
- **WHEN** 查询该记录的调用明细
- **THEN** 有 3 条，seq 为 1、2、3，触发原因依次为 INITIAL、RETRY、FALLBACK，记录 `attempt_count=3`

#### Scenario: 选填变量未传记 null
- **GIVEN** 生效模板声明选填变量 `user_input`
- **WHEN** App 提交的 inputs 不含 `user_input`
- **THEN** 生成记录 `inputs` 中 `user_input` 为 null

### Requirement: 生成接口每日额度
app 端 SHALL 在写生成记录前检查每日额度，"每天"按北京时间（Asia/Shanghai）自然日计算，计数取 `loves_ai_generation` 当日 `created_at` 记录数（所有状态都计入）：

- 当前匿名 ID 当日记录数 ≥ `AI_LIMIT_PER_ANON_DAILY`（默认 50）→ 429「今日生成次数已达上限」；
- 全站当日记录数 ≥ `AI_LIMIT_GLOBAL_DAILY`（默认 5000）→ 429「服务繁忙，请稍后再试」。

超额被拒的请求 SHALL NOT 写记录；去重命中的重复提交 SHALL NOT 占用额度。并发下可能超出上限少量次数（已知上限，可接受）。

#### Scenario: 匿名 ID 超出每日次数
- **GIVEN** `AI_LIMIT_PER_ANON_DAILY=2`，匿名 ID `anon-A` 今日已有 2 条记录
- **WHEN** `anon-A` 以新的 clientRequestId 提交
- **THEN** 返回 429「今日生成次数已达上限」，记录数不变

#### Scenario: 全站超出每日总量
- **GIVEN** `AI_LIMIT_GLOBAL_DAILY=3`，今日全站已有 3 条记录
- **WHEN** 任意匿名 ID 以新的 clientRequestId 提交
- **THEN** 返回 429「服务繁忙，请稍后再试」

#### Scenario: 昨日记录不计入
- **GIVEN** `AI_LIMIT_PER_ANON_DAILY=2`，`anon-A` 昨日（北京时间）有 2 条、今日 0 条
- **WHEN** `anon-A` 提交
- **THEN** 正常生成

#### Scenario: 去重不占额度
- **GIVEN** `AI_LIMIT_PER_ANON_DAILY=1`，`anon-A` 今日已有 1 条 SUCCESS 记录 `c-1`
- **WHEN** `anon-A` 重复提交 `c-1`
- **THEN** 返回 200 与原结果（不返回 429）

### Requirement: 生成接口输入长度限制
app 端 SHALL 校验 `inputs`：每个值（字符串原值或非字符串的 JSON 文本）≤ 500 字符，整体 JSON 序列化后 ≤ 4KB（4096 字节）；超出返回 400 并指出超长的变量名（整体超长时提示整体超限）。限制值为代码常量。

#### Scenario: 单个变量超长
- **GIVEN** 有效请求头
- **WHEN** inputs 中 `user_input` 为 501 个字符
- **THEN** 返回 400，信息包含 `user_input`，不写记录

#### Scenario: 边界值通过
- **GIVEN** 有效请求头与可用场景
- **WHEN** inputs 中 `user_input` 恰为 500 个字符且整体 ≤ 4KB
- **THEN** 通过长度校验

#### Scenario: 整体超长
- **GIVEN** 有效请求头
- **WHEN** inputs 含 10 个各 450 字符的变量（整体 > 4KB）
- **THEN** 返回 400

### Requirement: 生成接口 IP 限流
部署层 SHALL 在 nginx 对 `/love-space/app/api/app/ai/` 按客户端 IP 限流：平均每分钟 30 次，突发 10 次（`nodelay`），超出返回 429，请求不到达 app；其他 `/love-space/app/` 路径不受此限流影响。配置写入 `docs/部署服务器初始化.md`，测试与正式环境各配置一次。

#### Scenario: 同一 IP 突发超限
- **GIVEN** 测试环境 nginx 已按文档配置
- **WHEN** 同一 IP 在 1 秒内连续请求生成接口 15 次
- **THEN** 前 11 次左右到达 app，其余返回 429

#### Scenario: 非生成接口不受影响
- **GIVEN** 同上
- **WHEN** 同一 IP 在 1 秒内连续请求 `/love-space/app/api/app/cities` 15 次
- **THEN** 均不因该限流返回 429
