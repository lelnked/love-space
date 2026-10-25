# TC-ai-IT-076 断言明细

> GET /api/admin/ai/generations/{id} 未评价记录评价字段为空

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 200 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | rating=null | 通过 | None | None |
| 4 | ratingRemark=null | 通过 | None | None |
| 5 | ratedBy=null | 通过 | None | None |
| 6 | ratedAt=null | 通过 | None | None |
| 7 | ratingTags=[]（非 null） | 通过 | [] | [] |
| 8 | 契约: GenerationDetail 键集合 | 通过 | 30 键（见 api-spec description） | 一致 |
