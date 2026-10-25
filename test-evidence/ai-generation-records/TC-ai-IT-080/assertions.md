# TC-ai-IT-080 断言明细

> PUT /api/admin/ai/generations/{id}/rating 再次评价整体覆盖、不保留历史

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 204 | 通过 | 204 | (204, None) |
| 2 | 步骤3 204 | 通过 | 204 | (204, None) |
| 3 | rating=UNUSABLE | 通过 | UNUSABLE | UNUSABLE |
| 4 | ratingTags=[VAGUE] | 通过 | ['VAGUE'] | ['VAGUE'] |
| 5 | ratingRemark=null | 通过 | None | None |
| 6 | ratedBy=admin | 通过 | admin | admin |
| 7 | ratedAt 晚于 t1 | 通过 | > 2026-10-25T13:30:18.955862Z | 2026-10-25T13:30:19.986143Z |
| 8 | 记录仍 1 条 | 通过 | 1 | 1 |
| 9 | 无评价历史表 | 通过 | 仅 loves_ai_generation / _attempt | ['loves_ai_generation', 'loves_ai_generation_attempt'] |
