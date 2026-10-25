# TC-ai-IT-082 断言明细

> PUT /api/admin/ai/generations/{id}/rating 生成中记录返回 409

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 409 | 通过 | 409 | 409 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | 错误体含 status/error/message/path | 通过 | ['error', 'message', 'path', 'status'] | ['error', 'message', 'path', 'status'] |
| 4 | message | 通过 | 生成中的记录不能评价 | 生成中的记录不能评价 |
| 5 | rating=null、ratingTags=[]、ratedBy=null | 通过 | (None, [], None) | (None, [], None) |
| 6 | 无 target=R 的 rate 记录 | 通过 | 无 | [] |
