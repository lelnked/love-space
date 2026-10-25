# TC-ai-IT-078 断言明细

> PUT /api/admin/ai/generations/{id}/rating 首次评价

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 204 无 body | 通过 | 204 | (204, None) |
| 2 | rating=USABLE | 通过 | USABLE | USABLE |
| 3 | ratingTags=[] | 通过 | [] | [] |
| 4 | ratingRemark=null | 通过 | None | None |
| 5 | ratedBy=admin | 通过 | admin | admin |
| 6 | ratedAt 非空 | 通过 | 非空 | 2026-10-25T13:30:18.604134Z |
| 7 | 列表条目 rating=USABLE | 通过 | USABLE | USABLE |
