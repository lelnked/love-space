# TC-ai-IT-005 断言明细

> PUT /api/admin/ai/scenes/{id} 更新名称且忽略 body 中 sceneId

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | PUT 状态码 | 通过 | 200 | 200 |
| 2 | 契约字段 PUT 返回 SceneDetail | 通过 | id,sceneId,name,description,enabled,primaryModel,backupModel,activeTemplate,createdAt,updatedAt | 一致 |
| 3 | name=周期建议-改 | 通过 | 周期建议-改 | 周期建议-改 |
| 4 | description=新说明 | 通过 | 新说明 | 新说明 |
| 5 | sceneId 不变 | 通过 | cycle_advice_19eabb3a | cycle_advice_19eabb3a |
| 6 | 未新增 sceneId=other 场景 | 通过 | 0（基线 0） | 0 |
