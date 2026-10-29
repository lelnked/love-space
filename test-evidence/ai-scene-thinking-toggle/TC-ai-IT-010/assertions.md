# TC-ai-IT-010 断言明细

> PUT /api/admin/ai/scenes/{id}/models 模型不在白名单返回 400

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | {"primaryModel": "model-x"} 返回 400 | 通过 | 400 |
| 2 | {"primaryModel": "model-x"} message 为中文 | 通过 | 主模型不在可选模型列表中：model-x |
| 3 | {"primaryModel": "model-a", "backupModel": "model-x"} 返回 400 | 通过 | 400 |
| 4 | {"primaryModel": "model-a", "backupModel": "model-x"} message 为中文 | 通过 | 备用模型不在可选模型列表中：model-x |
| 5 | {} 返回 400 | 通过 | 400 |
| 6 | {} message 为中文 | 通过 | 主模型不能为空 |
| 7 | 场景仍为 primaryModel=model-a | 通过 | model-a |

**结论**: 通过
