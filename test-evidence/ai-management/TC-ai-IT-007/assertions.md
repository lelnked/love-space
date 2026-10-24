# TC-ai-IT-007 断言明细

> GET /api/admin/ai/scenes/options 全量下拉与详情 404

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | 返回数组 | 通过 | array | object |
| 3 | 每项仅含 id,sceneId,name,enabled | 通过 | id,sceneId,name,enabled | ["id","sceneId","name","enabled"] |
| 4 | 包含启用与禁用场景 | 通过 | 两者均在 | 107 项 |
| 5 | 步骤3 404 | 通过 | 404 | 404 |
