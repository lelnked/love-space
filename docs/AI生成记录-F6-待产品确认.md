# AI 生成记录（原 ai-management 的 F6）— 待产品确认

> 从 change `ai-management` 剥离出的草案，**尚未进入 OpenSpec 流程**。产品确认下方问题后，另开 change（建议 id：`ai-generation-records`）。
> 前置：`ai-management` 已建好 `loves_ai_generation`（业务请求）与 `loves_ai_generation_attempt`（调用明细）两张表并由 App 生成接口写入；本功能只做后台查看与人工评价。
> 本草案实施时需给 `loves_ai_generation` 追加评价字段：`rating`（USABLE / NEEDS_IMPROVEMENT / UNUSABLE）、`rating_tags jsonb NOT NULL DEFAULT '[]'`、`rating_remark`、`rated_by`、`rated_at`，以及侧栏「AI 管理 › 生成记录」、操作日志 `ai-generation:rate`。

## 产品确认结论

1. **2026-10-25 已确认：每条生成记录只有一个评价。** 首次保存即创建评价，之后再保存都是修改这一个评价（覆盖当前值），不保留多条评价。
   - 据此删除原草案的评价历史表 `loves_ai_generation_rating_history`、详情接口的 `ratingHistory[]` 与详情页「评价历史」区块。
   - 评价直接存在 `loves_ai_generation` 的 `rating*` 字段上；谁在何时改成什么，由操作日志 `ai-generation:rate`（含请求 payload）留痕。
   - 评价不提供"清除"操作：一旦评价过，只能改成另一个评价值，不能回到"未评价"。
2. **2026-10-25 已确认：生成中（PROCESSING）的记录不能评价**（后端 409，前端评价区置灰）。
3. **2026-10-25 已确认：时间筛选开始晚于结束时后端返回 400「开始日期不能晚于结束日期」**（沿用现有参数校验口径）；前端日期控件互相限制可选范围，不让这种参数传给后端。

后续以 change `ai-generation-records` 的 proposal 为准。

## 待产品确认的问题

（暂无）

---

### F6 生成记录（列表 / 详情 / 人工评价）

**评价存储**：每条记录一个评价，直接存在 `loves_ai_generation` 上（不建历史表）：

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| rating | text | | USABLE / NEEDS_IMPROVEMENT / UNUSABLE；null = 未评价 |
| rating_tags | jsonb | NOT NULL DEFAULT '[]' | 问题标签 |
| rating_remark | text | | 备注，≤500 |
| rated_by | text | | 最后一次保存评价的运营 username |
| rated_at | timestamptz | | 最后一次保存评价的时间 |

问题标签枚举：`IGNORED_INPUT` 忽略输入、`WEAK_CYCLE_LINK` 周期关联牵强、`BAD_TONE` 语气不合适、`VAGUE` 内容空泛、`INAPPLICABLE_ADVICE` 建议不适用、`OVER_MEDICALIZED` 过度医疗化、`FORMAT_ERROR` 格式错误、`OTHER` 其他。

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/admin/ai/generations/page` | query：`startDate?` `endDate?`（`yyyy-MM-dd`，按北京时间整日，**都不传默认最近 7 天**）、`requestId?`、`anonymousUserId?`、`sceneId?`、`templateId?`、`model?`（匹配 final_model）、`status?`、`rating?`（含 `NONE`=未评价）、`tag?`、`page`、`size`；固定 created_at 倒序 | `PageResponse<GenerationItem>`：`{id, createdAt, anonymousUserId, sceneId, sceneName, templateId, templateSeq, finalModel, status, latencyMs, rating}`（不含正文） |
| GET | `/api/admin/ai/generations/{id}` | path：id | `GenerationDetail`：表全部字段（含 `rating, ratingTags, ratingRemark, ratedBy, ratedAt`）+ `sceneName` + `templateName` + `attempts[]`（调用明细，按 seq 升序） |
| PUT | `/api/admin/ai/generations/{id}/rating` | body：`{rating*, tags?:[], remark?}`（remark ≤500） | 204；首次即创建评价，之后覆盖原评价（rating / tags / remark 整体替换，更新 ratedBy / ratedAt）；不保留历史 |

操作日志：`ai-generation:rate`。筛选下拉：场景 → `GET /scenes/options`；模板 → `GET /prompt-templates/page?sceneId=&size=200`（选了场景才可选）；模型 → `GET /models`。正文不做全文搜索。

**页面 1 `/ai/generations`**

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
①~⑩ → `GET /generations/page`；④⑤⑥ 选项见上；⑪ 点行 → 详情页。

**页面 2 `/ai/generations/:id`**

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
进入 → `GET /generations/{id}`；⑫⑬⑭ 回填当前评价（未评价时为空）；⑮ 未评价时文案「保存评价」、已评价时「修改评价」→ `PUT /generations/{id}/rating`，成功后重新拉详情；⑰ 显示 `ratedBy · ratedAt`，未评价时不显示；⑯ 展开该次调用的原始返回与解析错误；"模板 V3↗" 跳转模板详情页。
