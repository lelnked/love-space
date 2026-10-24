# TC-ai-IT-009 断言明细

> PUT /api/admin/ai/scenes/{id}/models 配置主备模型

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 204 | 通过 | 204 | 204 |
| 2 | primaryModel=model-a(mock-ok) | 通过 | mock-ok | mock-ok |
| 3 | backupModel=model-b(mock-fenced) | 通过 | mock-fenced | mock-fenced |
