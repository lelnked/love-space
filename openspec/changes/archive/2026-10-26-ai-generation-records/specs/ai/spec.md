## ADDED Requirements

### Requirement: 生成记录列表查询
admin 端 SHALL 提供 `GET /api/admin/ai/generations/page` 分页查询生成记录，固定按 `createdAt` 倒序，分页遵循项目统一口径（page 1 基，size 仅 20/200）。

过滤参数（均可空、可组合）：

- `startDate` / `endDate`（`yyyy-MM-dd`）：按北京时间（Asia/Shanghai）整日、含两端；**都不传时默认最近 7 天（含今天）**；只传一端时按该端单边过滤；`startDate` 晚于 `endDate` 时 SHALL 返回 400「开始日期不能晚于结束日期」。
- `requestId`、`anonymousUserId`：精确匹配。
- `sceneId`、`templateId`：精确匹配。
- `model`：精确匹配最终模型 `finalModel`。
- `status`：`PROCESSING` 或 AI 基础库状态枚举之一。
- `rating`：`USABLE` / `NEEDS_IMPROVEMENT` / `UNUSABLE`，或 `NONE`（未评价）。
- `tag`：问题标签枚举之一，匹配评价标签中包含该标签的记录。

日期格式错误、`status` / `rating` / `tag` 不在枚举内 SHALL 返回 400（错误信息沿用项目现有参数类型错误口径）。

列表条目 SHALL 为 `{id, createdAt, anonymousUserId, sceneId, sceneName, templateId, templateSeq, finalModel, status, latencyMs, rating}`，不含输入、输出、messages 等正文。`sceneName` 取场景当前名称。

#### Scenario: 默认最近 7 天
- **GIVEN** 存在今天、6 天前、8 天前（北京时间）各一条生成记录
- **WHEN** 不传日期查询
- **THEN** 返回今天与 6 天前两条，不含 8 天前的记录

#### Scenario: 日期区间含两端整日
- **GIVEN** 存在北京时间 2026-10-20 00:00:05 与 2026-10-21 23:59:50 的两条记录
- **WHEN** 以 `startDate=2026-10-20&endDate=2026-10-21` 查询
- **THEN** 两条都返回

#### Scenario: 开始晚于结束被拒绝
- **GIVEN** 已登录 Manager
- **WHEN** 以 `startDate=2026-10-22&endDate=2026-10-20` 查询
- **THEN** 返回 400，message 为「开始日期不能晚于结束日期」

#### Scenario: 按未评价筛选
- **GIVEN** 区间内有一条已评价、一条未评价的记录
- **WHEN** 以 `rating=NONE` 查询
- **THEN** 仅返回未评价的记录

#### Scenario: 按问题标签筛选
- **GIVEN** 记录 A 的评价标签含 `BAD_TONE`，记录 B 的标签为 `VAGUE`
- **WHEN** 以 `tag=BAD_TONE` 查询
- **THEN** 仅返回记录 A

#### Scenario: 组合筛选与倒序
- **GIVEN** 同一场景下多条不同状态的记录
- **WHEN** 以 `sceneId` 与 `status=SUCCESS` 查询
- **THEN** 仅返回该场景的成功记录，按 `createdAt` 从新到旧排列

#### Scenario: 非法枚举被拒绝
- **GIVEN** 已登录 Manager
- **WHEN** 以 `rating=GOOD` 或 `startDate=2026/10/20` 查询
- **THEN** 返回 400

#### Scenario: 列表不含正文
- **GIVEN** 存在一条成功记录
- **WHEN** 查询列表
- **THEN** 条目仅含 id、createdAt、anonymousUserId、sceneId、sceneName、templateId、templateSeq、finalModel、status、latencyMs、rating 十一个字段

### Requirement: 生成记录详情
admin 端 SHALL 提供 `GET /api/admin/ai/generations/{id}` 返回生成记录详情：记录全部字段（clientRequestId、匿名用户 ID、sceneId、templateId、templateSeq、appVersion、inputs、requestMessages、requestParams、status、errorCode、errorMessage、finalModel、attemptCount、rawOutput、content、promptTokens、completionTokens、latencyMs、createdAt、completedAt，以及评价字段 `rating`、`ratingTags`、`ratingRemark`、`ratedBy`、`ratedAt`）+ `sceneName` + `templateName` + `attempts[]`（调用明细，按 seq 升序，含每次调用的触发原因、模型、状态、错误码与信息、供应商请求 ID、原始返回、解析出的 JSON、校验错误、tokens、usage、起止时间、耗时）。记录不存在 SHALL 返回 404。查看 SHALL NOT 留痕。

#### Scenario: 详情含调用明细
- **GIVEN** 一条经过 INITIAL、RETRY 两次调用后成功的记录
- **WHEN** 请求详情
- **THEN** 返回 200，`attempts` 两条且 seq 为 1、2，触发原因为 INITIAL、RETRY，`templateName` 为该模板名称

#### Scenario: 未评价记录的评价字段为空
- **GIVEN** 一条未评价的记录
- **WHEN** 请求详情
- **THEN** `rating`、`ratedBy`、`ratedAt` 为 null，`ratingTags` 为空数组

#### Scenario: 记录不存在
- **GIVEN** 已登录 Manager
- **WHEN** 请求不存在的 id
- **THEN** 返回 404

### Requirement: 生成记录人工评价
admin 端 SHALL 提供 `PUT /api/admin/ai/generations/{id}/rating` body `{rating, tags?, remark?}`，成功返回 204：

- **每条生成记录只有一个评价**：首次保存即创建评价，之后再保存 SHALL 覆盖原评价——`rating`、`tags`、`remark` 整体替换（未传的 tags 视为空数组、未传的 remark 视为清空），并更新 `ratedBy`（当前 Manager 用户名）与 `ratedAt`。SHALL NOT 保留评价历史。
- `rating` 必填，取 `USABLE` / `NEEDS_IMPROVEMENT` / `UNUSABLE` 之一；`tags` 元素须为问题标签枚举（`IGNORED_INPUT`、`WEAK_CYCLE_LINK`、`BAD_TONE`、`VAGUE`、`INAPPLICABLE_ADVICE`、`OVER_MEDICALIZED`、`FORMAT_ERROR`、`OTHER`）且不重复；`remark` ≤500 字符。不满足 SHALL 返回 400。
- 评价 SHALL NOT 能被清除：接口不接受空 `rating`，已评价的记录不能回到未评价。
- 记录状态为 `PROCESSING`（生成中）时 SHALL 返回 409「生成中的记录不能评价」，评价不变。
- 记录不存在 SHALL 返回 404。
- 每次成功保存 SHALL 写操作日志 `ai-generation:rate`（target 为记录 id，payload 含本次评价内容）。

#### Scenario: 首次评价
- **GIVEN** 一条状态为 SUCCESS、未评价的记录
- **WHEN** 提交 `{rating: "USABLE"}`
- **THEN** 返回 204，详情 `rating=USABLE`、`ratingTags=[]`、`ratedBy` 为当前用户名、`ratedAt` 非空

#### Scenario: 再次评价覆盖原评价
- **GIVEN** 记录已被 ops1 评为 `NEEDS_IMPROVEMENT`，标签 `[BAD_TONE]`，备注「语气生硬」
- **WHEN** admin 提交 `{rating: "UNUSABLE", tags: ["VAGUE"]}`
- **THEN** 返回 204，详情 `rating=UNUSABLE`、`ratingTags=["VAGUE"]`、`ratingRemark` 为 null、`ratedBy=admin`；不存在其他评价记录

#### Scenario: 生成中的记录不能评价
- **GIVEN** 一条状态为 PROCESSING 的记录
- **WHEN** 提交评价
- **THEN** 返回 409「生成中的记录不能评价」，详情评价字段不变

#### Scenario: 失败记录可以评价
- **GIVEN** 一条状态为 FORMAT_ERROR 的记录
- **WHEN** 提交 `{rating: "UNUSABLE", tags: ["FORMAT_ERROR"]}`
- **THEN** 返回 204

#### Scenario: 评价参数非法
- **GIVEN** 一条成功记录
- **WHEN** 分别提交缺少 rating、`rating: "GOOD"`、`tags: ["BAD_TONE","BAD_TONE"]`、`tags: ["X"]`、501 字符的 remark
- **THEN** 均返回 400，评价不变

#### Scenario: 评价留痕
- **GIVEN** 以 admin 登录
- **WHEN** 评价一条记录，随后查询操作日志
- **THEN** 存在 `module=ai-generation`、`action=rate`、`target` 为该记录 id 的记录

### Requirement: 生成记录列表页面
web 端 SHALL 在 `/ai/generations` 提供生成记录列表页：

- 筛选区：①时间范围（开始、结束两个日期，进入页面默认填最近 7 天）；②请求 ID、③匿名用户 ID（文本，精确）；④场景（选项来自场景下拉接口）、⑤模板版本（选了场景才可选，选项为该场景模板 `V{seq} {name}`，切换场景时清空）、⑥模型（选项来自可选模型接口）、⑦状态、⑧评价（全部 / 可用 / 需优化 / 不可用 / 未评价）、⑨问题标签；⑩查询 / 重置（重置回到默认最近 7 天），查询与重置都回到第 1 页。
- ①时间范围 SHALL 做输入限制，不允许出现开始晚于结束：开始日期的可选上限为已选结束日期，结束日期的可选下限为已选开始日期。
- 列表列：时间、请求 ID（缩略显示，悬停显示全文）、匿名用户、场景名称、版本（V{seq}）、实际模型、状态（中文 + 成功 / 失败标识）、耗时（秒）、评价（中文，未评价显示「—」）；⑪点击行进入详情页。分页器默认 20 条/页、可切 200。
- 列表为空显示空态；加载失败 toast 后端信息。

#### Scenario: 默认展示最近 7 天
- **GIVEN** 已登录并进入 `/ai/generations`
- **WHEN** 页面加载完成
- **THEN** ①开始日期为 6 天前、结束日期为今天，列表按时间倒序展示

#### Scenario: 时间范围互相限制
- **GIVEN** ①结束日期已选 2026-10-20
- **WHEN** 打开开始日期选择器
- **THEN** 2026-10-20 之后的日期不可选；同理先选开始日期后，结束日期选择器中早于开始日期的日期不可选

#### Scenario: 模板版本依赖场景
- **GIVEN** ④场景为「全部」
- **WHEN** 查看⑤模板版本
- **THEN** ⑤不可选；选择一个场景后⑤可选并列出该场景的模板版本

#### Scenario: 按未评价筛选
- **GIVEN** 列表中有已评价与未评价的记录
- **WHEN** ⑧选择「未评价」并点⑩查询
- **THEN** 列表仅展示评价列为「—」的记录，分页回到第 1 页

#### Scenario: 点击行进入详情
- **GIVEN** 列表中存在记录
- **WHEN** 点击某一行⑪
- **THEN** 进入 `/ai/generations/{id}` 详情页

### Requirement: 生成记录详情页面
web 端 SHALL 在 `/ai/generations/:id` 提供生成记录详情页：

- **业务请求**区：requestId、clientRequestId、匿名用户、场景（名称 + sceneId）、模板（`V{seq} {name}`，可点击跳转 `/ai/prompt-templates/:id`）、App 版本、状态、实际模型、尝试次数、创建 / 完成时间、耗时、tokens。
- **输入快照**区：inputs JSON；**实际请求**区：messages 与参数快照 JSON。
- **调用明细**区：每次调用一行（#、触发原因中文、模型、状态、错误码、供应商请求 ID、开始时间、耗时、tokens），⑯每行可展开查看原始返回与解析错误。
- **生成结果**区：最终交付 content（格式化 JSON，失败显示「—」）与最终模型原始输出。
- **人工评价**区：⑫评价单选（可用 / 需优化 / 不可用）、⑬问题标签多选（8 个）、⑭备注（≤500）、⑮保存按钮——未评价时文案「保存评价」，已评价时「修改评价」；⑫⑬⑭回填当前评价；⑰已评价时显示「最后评价：{ratedBy} · {ratedAt}」，未评价时不显示。未选评价时⑮不可用。保存成功 toast 并重新拉取详情。
- 记录状态为生成中时，⑫~⑮置灰不可操作，并提示「生成中的记录不能评价」。
- 记录不存在或加载失败 toast 后端信息并返回列表页。

#### Scenario: 未评价记录保存评价
- **GIVEN** 打开一条未评价的成功记录
- **WHEN** ⑫选「可用」、⑬勾选「语气不合适」后点⑮「保存评价」
- **THEN** toast 成功，⑮文案变为「修改评价」，⑰显示当前用户与时间

#### Scenario: 修改已有评价
- **GIVEN** 打开一条已被评为「需优化」的记录
- **WHEN** 查看人工评价区
- **THEN** ⑫回填「需优化」、⑬回填原标签、⑭回填原备注，⑮文案为「修改评价」；改为「不可用」保存后详情显示新评价，页面不出现评价历史

#### Scenario: 生成中记录评价区置灰
- **GIVEN** 打开一条状态为生成中的记录
- **WHEN** 查看人工评价区
- **THEN** ⑫~⑮均不可操作，并显示「生成中的记录不能评价」

#### Scenario: 展开调用原始返回
- **GIVEN** 一条有两次调用的记录
- **WHEN** 点击第 1 次调用行的⑯展开
- **THEN** 显示该次调用的原始返回与解析错误

## MODIFIED Requirements

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
| `PUT /api/admin/ai/generations/{id}/rating` | `ai-generation:rate` |

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
`/api/admin/ai/**` SHALL 对所有已登录 Manager（ADMIN、MEMBER）开放，未登录返回 401。web 端侧栏 SHALL 新增一级菜单「AI 管理」，对所有角色可见，子菜单依次为「业务配置」(`/ai/scenes`)、「模型配置」(`/ai/models`)、「提示词管理」(`/ai/prompts`)、「生成记录」(`/ai/generations`)。

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
- **THEN** 依次显示「业务配置」「模型配置」「提示词管理」「生成记录」四个子菜单

#### Scenario: MEMBER 可评价生成记录
- **GIVEN** 以 MEMBER 角色登录，存在一条成功的生成记录
- **WHEN** 请求 `PUT /api/admin/ai/generations/{id}/rating`
- **THEN** 返回 204（不返回 403）
