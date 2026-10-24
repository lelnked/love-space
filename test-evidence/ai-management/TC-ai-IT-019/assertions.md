# TC-ai-IT-019 断言明细

> PUT /api/admin/ai/prompt-templates/{id} 修改已测试模板重置 tested

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | tested=false | 通过 | false | false |
| 3 | version>v | 通过 | >0 | 1 |
| 4 | systemPrompt 新值 | 通过 | 新的系统提示词 | 新的系统提示词 |
| 5 | updatedBy=admin | 通过 | admin | admin |
