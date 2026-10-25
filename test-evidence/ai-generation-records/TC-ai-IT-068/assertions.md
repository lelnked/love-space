# TC-ai-IT-068 断言明细

> GET /api/admin/ai/generations/page 开始晚于结束被拒绝

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 400 | 通过 | 400 | 400 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | 错误体含 status/error/message/path | 通过 | ['error', 'message', 'path', 'status'] | ['error', 'message', 'path', 'status'] |
| 4 | message | 通过 | 开始日期不能晚于结束日期 | 开始日期不能晚于结束日期 |
