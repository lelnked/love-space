# TC-ai-IT-098 断言明细

> PUT /api/admin/ai/scenes/{id}/models 开启深度思考

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 前置新建场景 thinkingEnabled=false | 通过 | False |
| 2 | 步骤3 返回 204 | 通过 | 204 |
| 3 | 步骤4 thinkingEnabled=true | 通过 | True |
| 4 | 步骤4 primaryModel=model-a | 通过 | model-a |
| 5 | 步骤5 分页 200 且含该场景 | 通过 | 200 |
| 6 | 步骤5 SceneItem thinkingEnabled=true | 通过 | True |
| 7 | 步骤6 返回 204 | 通过 | 204 |
| 8 | 步骤6 详情 thinkingEnabled=false | 通过 | False |

**结论**: 通过

**契约漂移（⚠️ 不判失败，供人工确认）**:
- SceneDetail 实际返回 ['thinkingEnabled']，contracts/api-spec.json 对应 operation description 列出的字段未包含（仅 AiSceneModelsRequest 声明了 thinkingEnabled）
- SceneItem 实际返回 ['thinkingEnabled']，contracts/api-spec.json 对应 operation description 列出的字段未包含（仅 AiSceneModelsRequest 声明了 thinkingEnabled）
