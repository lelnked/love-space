## ADDED Requirements

### Requirement: 生成调用深度思考参数
AI 基础模块的生成调用 SHALL 按场景 `thinkingEnabled` 在每次方舟请求 body 中显式传 `thinking`：开启为 `{"type": "enabled"}`，关闭为 `{"type": "disabled"}`。同一次生成的主模型、重试、备用模型调用 SHALL 使用同一取值。admin 提示词模板测试与 app 生成接口均 SHALL 读取场景当前 `thinkingEnabled`。连接测试 SHALL NOT 传 `thinking`。

#### Scenario: 关闭时显式传 disabled
- **GIVEN** 场景 `thinkingEnabled=false`
- **WHEN** 发起一次生成
- **THEN** 方舟请求 body 含 `"thinking": {"type": "disabled"}`，参数快照 `thinkingEnabled=false`

#### Scenario: 开启时传 enabled 且切备用沿用
- **GIVEN** 场景 `thinkingEnabled=true`，主模型失败后切备用模型
- **WHEN** 发起一次生成
- **THEN** 每次方舟请求（含 FALLBACK）body 均含 `"thinking": {"type": "enabled"}`

#### Scenario: 连接测试不传 thinking
- **WHEN** 调用 `POST /api/admin/ai/models/test-connection`
- **THEN** 方舟请求 body 不含 `thinking` 字段

## MODIFIED Requirements

### Requirement: 业务场景管理
admin 端 SHALL 提供业务场景（scene）管理：

- 字段：`sceneId`（必填，匹配 `^[a-z][a-z0-9_]{1,63}$`，全局唯一，**创建后不可修改**）、`name`（必填，≤64）、`description`（≤500）、`enabled`（创建默认 true）。
- `POST /api/admin/ai/scenes` 创建，返回 201 与 SceneDetail；`sceneId` 已存在返回 409「scene_id 已存在」；格式/长度不符返回 400。
- `PUT /api/admin/ai/scenes/{id}` 只更新 `name` 与 `description`，请求体中的 sceneId 被忽略。
- `PUT /api/admin/ai/scenes/{id}/enabled` 切换启用状态，返回 204。
- `GET /api/admin/ai/scenes/page` 分页，过滤 `keyword`（模糊匹配 sceneId 或名称）、`enabled`；按创建时间倒序；分页遵循项目统一口径（page 1 基，size 仅 20/200）。
- `GET /api/admin/ai/scenes/options` 返回全量 `[{id, sceneId, name, enabled}]`。
- `GET /api/admin/ai/scenes/{id}` 返回详情；不存在返回 404。
- SceneItem / SceneDetail 结构 SHALL 为 `{id, sceneId, name, description, enabled, primaryModel, backupModel, thinkingEnabled, activeTemplate: {id, seq, name, publishedAt, publishedBy} | null, createdAt, updatedAt}`。
- 场景不可删除。

#### Scenario: 创建场景
- **GIVEN** 已登录 Manager
- **WHEN** 提交 `{sceneId: "cycle_advice", name: "周期建议"}`
- **THEN** 返回 201，`enabled=true`、`primaryModel=null`、`thinkingEnabled=false`、`activeTemplate=null`

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
admin 端 SHALL 支持为场景配置主模型、备用模型与深度思考开关：`PUT /api/admin/ai/scenes/{id}/models` body `{primaryModel, backupModel?, thinkingEnabled?}`，成功返回 204，保存即对之后的新请求生效（不做版本/发布）。

校验：`primaryModel` 必填；`primaryModel` 与非空 `backupModel` 都 SHALL 在 `AI_MODELS` 内；`backupModel` 不得与 `primaryModel` 相同；任一不满足返回 400 及中文信息。`backupModel` 为 null 或空串表示不使用备用模型。`thinkingEnabled` 未传或为 null 视为 `false`；场景默认 `thinkingEnabled=false`（关闭深度思考）。

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

#### Scenario: 开启深度思考
- **GIVEN** `AI_MODELS=model-a,model-b`，一个 `thinkingEnabled=false` 的场景
- **WHEN** 提交 `{primaryModel: "model-a", thinkingEnabled: true}`
- **THEN** 返回 204，详情 `thinkingEnabled=true`

#### Scenario: 未传深度思考视为关闭
- **GIVEN** 场景当前 `thinkingEnabled=true`
- **WHEN** 提交 `{primaryModel: "model-a"}`（不含 thinkingEnabled）
- **THEN** 返回 204，详情 `thinkingEnabled=false`

### Requirement: 模型配置页面
web 端 SHALL 在 `/ai/models` 提供模型配置页：

- ①关键字搜索；列表列 scene_id、名称、主模型、备用模型、操作（②配置）。主模型为空时显示「未配置（⚠ 不可生成）」，备用模型为空显示「—」。
- ②配置打开弹窗：③主模型下拉、⑤备用模型下拉（含「不使用」），选项来自 `GET /api/admin/ai/models`；④每个下拉旁「测试连接」按钮，显示可达与耗时或失败原因；⑦「深度思考」开关（初值取场景 `thinkingEnabled`，默认关，旁注「开启后响应更慢、token 消耗更高」）；弹窗内固定提示「连接成功只代表服务可达，不代表生成质量」；⑥保存。
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

#### Scenario: 配置深度思考
- **GIVEN** 场景 `thinkingEnabled=false`，打开配置弹窗
- **WHEN** 打开⑦深度思考开关并点击⑥保存
- **THEN** 弹窗关闭；再次打开该场景配置弹窗，⑦开关为开启状态

### Requirement: 生成记录与调用明细落库
每次通过额度检查的生成请求 SHALL 在调用模型前写入一条 `loves_ai_generation` 记录（`status=PROCESSING`），包含 clientRequestId、匿名 ID、sceneId、模板 id 与 seq、appVersion、inputs 快照（未传的声明变量记 null）、实际 messages、参数快照 `{primaryModel, backupModel, thinkingEnabled, temperature, topP, maxTokens, timeoutSeconds, maxRetries}`。

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
