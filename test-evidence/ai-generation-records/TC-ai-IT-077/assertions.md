# TC-ai-IT-077 断言明细

> GET /api/admin/ai/generations/{id} 记录不存在返回 404

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 404 | 通过 | 404 | 404 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | 错误体含 status/error/message/path | 通过 | ['error', 'message', 'path', 'status'] | ['error', 'message', 'path', 'status'] |
