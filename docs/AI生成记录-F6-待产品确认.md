# AI 生成记录（原 ai-management 的 F6）— 待产品确认

> 从 change `ai-management` 剥离出的草案，**尚未进入 OpenSpec 流程**。产品确认下方问题后，另开 change（建议 id：`ai-generation-records`）。
> 前置：`ai-management` 已建好 `loves_ai_generation`（业务请求）与 `loves_ai_generation_attempt`（调用明细）两张表并由 App 生成接口写入；本功能只做后台查看与人工评价。
> 本草案实施时需给 `loves_ai_generation` 追加评价字段：`rating`（USABLE / NEEDS_IMPROVEMENT / UNUSABLE）、`rating_tags jsonb NOT NULL DEFAULT '[]'`、`rating_remark`、`rated_by`、`rated_at`，以及侧栏「AI 管理 › 生成记录」、操作日志 `ai-generation:rate`。

## 待产品确认的问题

1. （待补充）

---

### F6 生成记录（列表 / 详情 / 人工评价）

**表 `loves_ai_generation_rating_history`**（评价每改一次追加一行；当前值冗余在 `loves_ai_generation.rating*` 上供筛选）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | uuid | PK | |
| generation_id | uuid | NOT NULL | |
| rating | text | NOT NULL | |
| tags | jsonb | NOT NULL DEFAULT '[]' | |
| remark | text | | |
| manager_username | text | NOT NULL | 操作人 |
| created_at | timestamptz | NOT NULL | |

问题标签枚举：`IGNORED_INPUT` 忽略输入、`WEAK_CYCLE_LINK` 周期关联牵强、`BAD_TONE` 语气不合适、`VAGUE` 内容空泛、`INAPPLICABLE_ADVICE` 建议不适用、`OVER_MEDICALIZED` 过度医疗化、`FORMAT_ERROR` 格式错误、`OTHER` 其他。

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/admin/ai/generations/page` | query：`startDate?` `endDate?`（`yyyy-MM-dd`，按北京时间整日，**都不传默认最近 7 天**）、`requestId?`、`anonymousUserId?`、`sceneId?`、`templateId?`、`model?`（匹配 final_model）、`status?`、`rating?`（含 `NONE`=未评价）、`tag?`、`page`、`size`；固定 created_at 倒序 | `PageResponse<GenerationItem>`：`{id, createdAt, anonymousUserId, sceneId, sceneName, templateId, templateSeq, finalModel, status, latencyMs, rating}`（不含正文） |
| GET | `/api/admin/ai/generations/{id}` | path：id | `GenerationDetail`：表全部字段 + `sceneName` + `templateName` + `attempts[]`（调用明细，按 seq 升序）+ `ratingHistory[]` |
| PUT | `/api/admin/ai/generations/{id}/rating` | body：`{rating*, tags?:[], remark?}`（remark ≤500） | 204；追加 history，覆盖当前值 |

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
│                                        │                          [保存评价]⑮    │ │
│                                        │ ── 评价历史 ──                          │ │
│                                        │ 10-24 16:00 ops1 需优化 [语气不合适] ... │ │
│                                        └─────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────────────────┘
```
进入 → `GET /generations/{id}`；⑫⑬⑭⑮ → `PUT /generations/{id}/rating`，成功后重新拉详情刷新历史；⑯ 展开该次调用的原始返回与解析错误；"模板 V3↗" 跳转模板详情页。
