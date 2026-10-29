# TC-ai-IT-002 断言明细

> POST /api/admin/ai/scenes 创建场景成功

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 创建返回 201 | 通过 | 201 |
| 2 | Content-Type 含 application/json | 通过 | application/json |
| 3 | enabled=true | 通过 | True |
| 4 | primaryModel=null | 通过 | None |
| 5 | backupModel=null | 通过 | None |
| 6 | thinkingEnabled=false（键存在且为布尔 false） | 通过 | False |
| 7 | activeTemplate=null | 通过 | None |
| 8 | createdAt 非空 | 通过 | 2026-10-29T06:35:05.107086981Z |
| 9 | 详情 sceneId/name/description 与提交一致 | 通过 | ('cycle_advice_3cbdca77', '周期建议', '按周期给建议') |
| 10 | 详情 thinkingEnabled=false | 通过 | False |

**结论**: 通过

**契约漂移（⚠️ 不判失败，供人工确认）**:
- SceneDetail 实际返回 ['thinkingEnabled']，contracts/api-spec.json 对应 operation description 列出的字段未包含（仅 AiSceneModelsRequest 声明了 thinkingEnabled）
