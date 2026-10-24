# TC-ai-IT-006 断言明细

> GET /api/admin/ai/scenes/page 按关键字与状态筛选

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 200 | 通过 | 200 | 200 |
| 2 | 步骤3 仅返回 cycle_ | 通过 | [cycle_13909147] | ["cycle_13909147"] |
| 3 | 步骤4 仅返回 mood_ 且 enabled=false | 通过 | [mood_13909147] enabled=false | [["mood_13909147",false]] |
| 4 | 步骤5 按 createdAt 倒序 | 通过 | 倒序 | ["2026-10-24T17:51:37.162009Z","2026-10-24T17:51:37.146125Z","2026-10-24T17:51:37.071948Z","2026-10-24T17:51:37.036485Z","2026-10-24T17:51:37.020712Z"] |
| 5 | 步骤5 page=1 | 通过 | 1 | 1 |
| 6 | 契约字段 PageResponse | 通过 | content,page,size,totalElements,totalPages | 一致 |
| 7 | 契约字段 SceneItem | 通过 | id,sceneId,name,description,enabled,primaryModel,backupModel,activeTemplate,createdAt,updatedAt | 一致 |

## 备注

- 为避免库内既有「情绪回应」测试场景干扰「仅返回」断言，禁用场景名取「情绪回应-13909147」，关键字同名（仍走名称匹配）。
