# TC-ai-IT-053 断言明细

> clientRequestId 被不同匿名 ID 复用返回 409

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 409 | 通过 | 409 | 409 |
| 2 |   message=clientRequestId 冲突 | 通过 | clientRequestId 冲突 | clientRequestId 冲突 |
| 3 | 步骤3 409 | 通过 | 409 | 409 |
| 4 |   message=clientRequestId 冲突 | 通过 | clientRequestId 冲突 | clientRequestId 冲突 |
