# TC-ai-IT-099 断言明细

> PUT /api/admin/ai/scenes/{id}/models 未传或 null 的 thinkingEnabled 视为关闭

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 步骤3 未传 返回 204 | 通过 | 204 |
| 2 | 步骤3 详情 thinkingEnabled=false | 通过 | False |
| 3 | 步骤5 null 返回 204 | 通过 | 204 |
| 4 | 步骤5 详情 thinkingEnabled=false | 通过 | False |
| 5 | 步骤6 返回 400 | 通过 | 400 |
| 6 | 步骤6 message 为中文 | 通过 | 主模型不在可选模型列表中：model-x |
| 7 | 步骤6 详情 thinkingEnabled=true 不变 | 通过 | True |
| 8 | 步骤6 详情 primaryModel=model-a 不变 | 通过 | model-a |

**结论**: 通过
