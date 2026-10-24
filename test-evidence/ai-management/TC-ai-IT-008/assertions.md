# TC-ai-IT-008 断言明细

> PUT /api/admin/ai/scenes/{id}/enabled 禁用场景

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 204 | 通过 | 204 | 204 |
| 2 | 步骤4 enabled=false | 通过 | false | false |
| 3 | 步骤5 204 | 通过 | 204 | 204 |
| 4 | 步骤6 enabled=true | 通过 | true | true |
