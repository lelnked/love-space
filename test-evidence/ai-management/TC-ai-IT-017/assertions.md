# TC-ai-IT-017 断言明细

> POST /api/admin/ai/prompt-templates 变量声明非法或重复返回 400

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | [{"name":"1abc","required":false}] → 400 | 通过 | 400 | 400 |
| 2 |   message 中文 | 通过 | 中文 | 变量名不合法：1abc |
| 3 | [{"name":"age","required":true},{"name":"age","required":false}] → 400 | 通过 | 400 | 400 |
| 4 |   message 中文 | 通过 | 中文 | 变量名重复：age |
| 5 | 模板数不变 | 通过 | 0 | 0 |
