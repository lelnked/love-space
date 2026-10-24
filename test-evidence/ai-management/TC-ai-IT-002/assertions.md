# TC-ai-IT-002 断言明细

> POST /api/admin/ai/scenes 创建场景成功

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 创建状态码 | 通过 | 201 | 201 |
| 2 | Content-Type | 通过 | application/json | application/json |
| 3 | enabled=true | 通过 | true | true |
| 4 | primaryModel=null | 通过 | null | null |
| 5 | backupModel=null | 通过 | null | null |
| 6 | activeTemplate=null | 通过 | null | null |
| 7 | createdAt 非空 | 通过 | 非空 | 2026-10-24T17:51:36.724598808Z |
| 8 | 契约字段 SceneDetail | 通过 | id,sceneId,name,description,enabled,primaryModel,backupModel,activeTemplate,createdAt,updatedAt | 一致 |
| 9 | 详情 200 | 通过 | 200 | 200 |
| 10 | 详情字段与提交一致 | 通过 | {"sceneId":"cycle_advice_55c74ed7","name":"周期建议","description":"按周期给建议"} | {"id":"01a16f0a-1f14-7ab3-b418-bd4622371eda","sceneId":"cycle_advice_55c74ed7","name":"周期建议","description":"按周期给建议","enabled":true,"primaryModel":null,"backupModel":null,"activeTemplate":null,"createdAt":"2026-10-24T17:51:36.724599Z","updatedAt":"2026-10-24T17:51:36.724599Z"} |
