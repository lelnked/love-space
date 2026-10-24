# TC-ai-IT-016 断言明细

> POST /api/admin/ai/prompt-templates 引用未声明变量也可保存

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 201 | 通过 | 201 | 201 |
| 2 | tested=false | 通过 | false | false |
| 3 | 模板已保存 | 通过 | 可查到 | 200 |
