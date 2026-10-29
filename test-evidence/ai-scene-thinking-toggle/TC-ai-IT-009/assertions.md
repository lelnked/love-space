# TC-ai-IT-009 断言明细

> PUT /api/admin/ai/scenes/{id}/models 配置主备模型

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | PUT 返回 204 | 通过 | 204 |
| 2 | primaryModel=model-a | 通过 | model-a |
| 3 | backupModel=model-b | 通过 | model-b |
| 4 | 未传 thinkingEnabled → false（回归：新增字段不影响原行为） | 通过 | False |

**结论**: 通过
