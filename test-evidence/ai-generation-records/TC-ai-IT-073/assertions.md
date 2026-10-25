# TC-ai-IT-073 断言明细

> GET /api/admin/ai/generations/page 非法日期与枚举返回 400

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | rating=GOOD → 400 | 通过 | 400 | 400 |
| 2 | rating=GOOD 错误体 {status,error,message,path} | 通过 | ['error', 'message', 'path', 'status'] | ['error', 'message', 'path', 'status'] |
| 3 | startDate=2026/10/20 → 400 | 通过 | 400 | 400 |
| 4 | startDate=2026/10/20 错误体 {status,error,message,path} | 通过 | ['error', 'message', 'path', 'status'] | ['error', 'message', 'path', 'status'] |
| 5 | endDate=2026-13-01 → 400 | 通过 | 400 | 400 |
| 6 | endDate=2026-13-01 错误体 {status,error,message,path} | 通过 | ['error', 'message', 'path', 'status'] | ['error', 'message', 'path', 'status'] |
| 7 | status=DONE → 400 | 通过 | 400 | 400 |
| 8 | status=DONE 错误体 {status,error,message,path} | 通过 | ['error', 'message', 'path', 'status'] | ['error', 'message', 'path', 'status'] |
| 9 | tag=X → 400 | 通过 | 400 | 400 |
| 10 | tag=X 错误体 {status,error,message,path} | 通过 | ['error', 'message', 'path', 'status'] | ['error', 'message', 'path', 'status'] |
