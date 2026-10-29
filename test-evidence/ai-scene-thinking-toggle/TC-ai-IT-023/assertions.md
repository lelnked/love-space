# TC-ai-IT-023 断言明细

> POST /api/admin/ai/prompt-templates/{id}/test 测试成功置已测试

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 前置模板 tested=false | 通过 | False |
| 2 | 返回 200 | 通过 | 200 |
| 3 | success=true | 通过 | True |
| 4 | model 非空 | 通过 | mock-ok |
| 5 | content 为 JSON 对象 | 通过 | {'echo': '年龄28'} |
| 6 | latencyMs 非空 | 通过 | 5 |
| 7 | testedAt 非空 | 通过 | 2026-10-29T06:35:05.647100576Z |
| 8 | tested=true | 通过 | True |
| 9 | lastTestInputs={"age":28} | 通过 | {'age': 28} |
| 10 | lastTestBy=admin | 通过 | admin |
| 11 | lastTestSuccess=true | 通过 | True |
| 12 | version 与内容不变 | 通过 | (0, 0) |

**结论**: 通过
