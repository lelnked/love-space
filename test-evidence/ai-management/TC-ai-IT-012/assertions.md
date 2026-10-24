# TC-ai-IT-012 断言明细

> PUT /api/admin/ai/scenes/{id}/models 清除备用模型

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 204 | 通过 | 204 | 204 |
| 2 | 步骤4 backupModel=null | 通过 | null | null |
| 3 | 步骤5 204 | 通过 | 204 | 204 |
| 4 | 步骤6 backupModel=null | 通过 | null | null |

## 备注

- 步骤5 前重新配回 backupModel=mock-fenced，使「空串清除」独立可观察。
