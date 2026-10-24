# TC-ai-IT-003 断言明细

> POST /api/admin/ai/scenes sceneId 重复返回 409

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 | 通过 | 409 | 409 |
| 2 | message | 通过 | scene_id 已存在 | scene_id 已存在 |
| 3 | 场景数不变（该 sceneId 仍 1 条） | 通过 | 1 | 1 |
