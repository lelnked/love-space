# TC-ai-IT-012 断言明细

> PUT /api/admin/ai/scenes/{id}/models 清除备用模型

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | backupModel=null 返回 204 | 通过 | 204 |
| 2 | 详情 backupModel=null | 通过 | None |
| 3 | backupModel="" 返回 204 | 通过 | 204 |
| 4 | 详情 backupModel=null | 通过 | None |

**结论**: 通过
