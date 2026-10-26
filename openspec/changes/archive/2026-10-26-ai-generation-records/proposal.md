# Proposal: ai-generation-records（AI 生成记录查看与人工评价）

## Why

`ai-management` 已让 App 每次生成都写入 `loves_ai_generation`（业务请求）与 `loves_ai_generation_attempt`（调用明细），但后台没有入口查看，运营无法回看生成质量、定位失败原因，也无法给结果打质量标注。本 change 补上后台的查看与人工评价（原 `ai-management` 的 F6，草案见 `docs/AI生成记录-F6-待产品确认.md`，产品已于 2026-10-25 确认）。

## What Changes

- **生成记录列表**：按时间范围（北京时间整日，都不传默认最近 7 天）、requestId、匿名用户 ID、场景、模板版本、实际模型、状态、评价（含"未评价"）、问题标签筛选，固定按创建时间倒序分页；列表不含正文。
- **生成记录详情**：业务请求信息、输入快照、实际发给模型的 messages 与参数快照、调用明细（每次模型调用，按 seq 升序，可展开原始返回与解析错误）、最终交付内容与原始输出、当前评价。
- **人工评价（产品确认）**：**每条生成记录只有一个评价**。首次保存即创建，之后再保存都是修改这一个评价（评价值、问题标签、备注整体覆盖，更新评价人与评价时间），**不保留评价历史**；改动痕迹由操作日志 `ai-generation:rate` 留痕。评价一旦存在不能清除回"未评价"。**生成中（`PROCESSING`）的记录不能评价**。
- 侧栏「AI 管理」新增第 4 个子菜单「生成记录」。

明确**不做**：评价历史、多人多评、清除评价、正文全文搜索、导出、记录删除 / 清理、App 端任何改动。

### 全局约定（沿用 ai-management）

- 权限：`/api/admin/ai/**` 所有登录运营（ADMIN、MEMBER）均可用，不新增权限点。
- 分页 / 错误格式：`PageResponse{content,page,size,totalElements,totalPages}`（page 从 1 开始，size 20/200）与 `{status,error,message,path}`。
- "每天 / 日期"一律按北京时间（Asia/Shanghai）自然日。

### 评价字段（追加到 `loves_ai_generation`，不建新表）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| rating | text | | `USABLE` 可用 / `NEEDS_IMPROVEMENT` 需优化 / `UNUSABLE` 不可用；null = 未评价 |
| rating_tags | jsonb | NOT NULL DEFAULT '[]' | 问题标签数组，元素取下方枚举，不重复 |
| rating_remark | text | | 备注，≤500 |
| rated_by | text | | 最后一次保存评价的运营 username |
| rated_at | timestamptz | | 最后一次保存评价的时间 |

问题标签枚举：`IGNORED_INPUT` 忽略输入、`WEAK_CYCLE_LINK` 周期关联牵强、`BAD_TONE` 语气不合适、`VAGUE` 内容空泛、`INAPPLICABLE_ADVICE` 建议不适用、`OVER_MEDICALIZED` 过度医疗化、`FORMAT_ERROR` 格式错误、`OTHER` 其他。

索引：`(rating)` 供"按评价筛选"；标签筛选用 jsonb 包含查询（`rating_tags @> '["TAG"]'`），数据量上来后再加 GIN 索引。

### API

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/admin/ai/generations/page` | query：`startDate?` `endDate?`（`yyyy-MM-dd`，含两端整日；都不传 = 最近 7 天含今天；只传一端按该端单边过滤；startDate 晚于 endDate → 400「开始日期不能晚于结束日期」）、`requestId?`（精确）、`anonymousUserId?`（精确）、`sceneId?`、`templateId?`、`model?`（匹配 final_model）、`status?`（含 `PROCESSING`）、`rating?`（`USABLE` / `NEEDS_IMPROVEMENT` / `UNUSABLE` / `NONE`=未评价）、`tag?`、`page`、`size`；固定 created_at 倒序 | `PageResponse<GenerationItem>`：`{id, createdAt, anonymousUserId, sceneId, sceneName, templateId, templateSeq, finalModel, status, latencyMs, rating}`（不含正文） |
| GET | `/api/admin/ai/generations/{id}` | path：id | `GenerationDetail`：表全部字段（含 `rating, ratingTags, ratingRemark, ratedBy, ratedAt`）+ `sceneName` + `templateName` + `attempts[]`（调用明细，按 seq 升序）；不存在 404 |
| PUT | `/api/admin/ai/generations/{id}/rating` | body：`{rating*, tags?:[], remark?}`（rating 取三值之一；tags 取枚举、不重复；remark ≤500） | 204；首次即创建评价，之后覆盖原评价，更新 ratedBy / ratedAt；参数非法 400；记录不存在 404；记录为 `PROCESSING` → 409「生成中的记录不能评价」 |

- 参数非法（日期格式错、startDate 晚于 endDate、rating / status / tag 不在枚举内）→ 400；startDate 晚于 endDate 的提示为「开始日期不能晚于结束日期」。
- 前端在①时间范围做输入限制，不允许把"开始晚于结束"传给后端：开始日期选择器的可选上限是已选结束日期，结束日期的可选下限是已选开始日期。
- `PROCESSING`（生成中）的记录不能评价：后端 409；前端详情页评价区⑫~⑮置灰，并提示「生成中的记录不能评价」。
- 操作日志：`ai-generation:rate`；查看不留痕。
- 筛选下拉：场景 → `GET /api/admin/ai/scenes/options`；模板版本 → `GET /api/admin/ai/prompt-templates/page?sceneId=&size=200`（选了场景才可选）；模型 → `GET /api/admin/ai/models`。正文不做全文搜索。

### 页面 1 `/ai/generations`

```
┌ AI 管理 › 生成记录 ──────────────────────────────────────────────────────────────┐
│ 时间 [2026-10-18]~[2026-10-24]①  请求ID [        ]②  匿名用户ID [        ]③       │
│ 场景 [全部▼]④  模板版本 [全部▼]⑤  模型 [全部▼]⑥  状态 [全部▼]⑦                    │
│ 评价 [全部▼]⑧  问题标签 [全部▼]⑨                              [查询]⑩ [重置]       │
├──────────────────────────────────────────────────────────────────────────────────┤
│ 时间         请求ID    匿名用户   场景      版本 实际模型     状态        耗时   评价    │
│ 10-24 15:02  8f3a…⑪   u_9a…     周期建议  V3   doubao-seed  ✓成功      2.3s   可用    │
│ 10-24 14:58  71bc…    u_02…     周期建议  V3   ep-2025xx    ✗格式失败   9.8s   —       │
│ 10-24 14:51  c0d2…    u_77…     情绪回应  V1   doubao-seed  ✓成功      1.9s   需优化  │
├──────────────────────────────────────────────────────────────────────────────────┤
│                                                  共 128 条  < 1 2 … 7 >  20条/页   │
└──────────────────────────────────────────────────────────────────────────────────┘
```
①~⑩ → `GET /generations/page`（①默认填最近 7 天；开始 / 结束日期互相限制可选范围，不能选出开始晚于结束）；④⑤⑥ 选项见上；⑪ 点行 → 详情页。

### 页面 2 `/ai/generations/:id`

```
┌ 生成记录 › 8f3a2c… ──────────────────────────────────────────────────────────────┐
│ ┌ 业务请求 ─────────────────────────────────────────────────────────────────────┐ │
│ │ requestId 8f3a…  clientRequestId c-123  匿名用户 u_9a…  场景 周期建议(cycle_advice)│ │
│ │ 模板 V3 温柔版↗  App 1.3.0  状态 ✓成功  实际模型 doubao-seed  尝试 2 次 │ │
│ │ 创建 10-24 15:02:01  完成 15:02:03  耗时 2.3s  tokens 812/240                   │ │
│ └───────────────────────────────────────────────────────────────────────────────┘ │
│ ┌ 输入快照 ────────────────────┐ ┌ 实际请求 ──────────────────────────────────────┐ │
│ │ {"age":28,"cycle_phase":"黄体期",│ │ messages: [{role:system,...},{role:user,...}] │ │
│ │  "user_input":null}           │ │ params: {temperature:0.7, maxTokens:800,...}  │ │
│ └───────────────────────────────┘ └───────────────────────────────────────────────┘ │
│ ┌ 调用明细 ─────────────────────────────────────────────────────────────────────┐ │
│ │ # 触发    模型          状态      错误码  供应商请求ID  开始      耗时  tokens  ▸ │ │
│ │ 1 首次    doubao-seed  格式失败  —       0217…        15:02:01  1.1s  400/120 ▸原始返回⑯│ │
│ │ 2 重试    doubao-seed  成功      —       0217…        15:02:02  1.2s  412/120 ▸     │ │
│ └───────────────────────────────────────────────────────────────────────────────┘ │
│ ┌ 生成结果 ──────────────────────────┐ ┌ 人工评价 ───────────────────────────────┐ │
│ │ 最终交付 content：                 │ │ 评价 (●可用 ○需优化 ○不可用) ⑫          │ │
│ │ { "title": "...", "tips": [...] }  │ │ 问题标签 ☐忽略输入 ☐周期关联牵强 ☐语气不合适│ │
│ │ 最终模型原始输出：                  │ │   ☐内容空泛 ☐建议不适用 ☐过度医疗化      │ │
│ │ —                                  │ │   ☐格式错误 ☐其他                    ⑬  │ │
│ └────────────────────────────────────┘ │ 备注 [                         ] ⑭      │ │
│                                        │ 最后评价：ops1 · 10-24 16:00 ⑰          │ │
│                                        │                 [保存评价 / 修改评价]⑮  │ │
│                                        └─────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────────────────┘
```
进入 → `GET /generations/{id}`；⑫⑬⑭ 回填当前评价（未评价时为空）；记录状态为生成中时⑫~⑮置灰并提示「生成中的记录不能评价」；⑮ 未评价时文案「保存评价」、已评价时「修改评价」→ `PUT /generations/{id}/rating`，成功后重新拉详情；⑰ 显示 `ratedBy · ratedAt`，未评价时不显示；⑯ 展开该次调用的原始返回与解析错误；"模板 V3↗" 跳转模板详情页 `/ai/prompt-templates/:id`。

## Capabilities

### New Capabilities
（无——归入 `ai` 域。）

### Modified Capabilities
- `ai`：新增生成记录查询、详情与单一人工评价的 Requirement；侧栏「AI 管理」子菜单由 3 个增为 4 个（修改「AI 管理权限与菜单」）。
- `operation-log`：留痕覆盖面新增 `ai-generation:rate`，模块 / 接口计数随之更新（19 个模块、68 个写接口）。

> **前置**：`ai-management` 已于 2026-10-25 归档，本 change 的 delta 以归档后的 living specs 为基线。

## Impact

- **数据库**：admin Liquibase 新增 `027-add-ai-generation-rating.sql`：`loves_ai_generation` 追加 5 个评价列 + `(rating)` 索引，纯新增、可回滚。app 端实体不映射新列，不受影响（`ddl-auto: validate` 只校验已映射列）。
- **love-space-admin**：`modules/ai` 新增生成记录只读实体映射（`loves_ai_generation` / `loves_ai_generation_attempt`）、查询 / 详情 / 评价接口。
- **love-space-web**：侧栏「生成记录」+ 2 个路由页（`/ai/generations`、`/ai/generations/:id`）。
- **love-space-app**：无改动。
- **契约**：`contracts/api-spec.json` 新增 admin 端 3 个接口。
- **测试**：`tests/ai/{it,web}.md` 追加用例；`tests/operation-log/it.md` 追加留痕用例。
