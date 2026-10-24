# TC-ai-IT-023 断言明细

> POST /api/admin/ai/prompt-templates/{id}/test 测试成功置已测试

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | success=true | 通过 | true | true |
| 3 | model 非空 | 通过 | 非空 | mock-ok |
| 4 | content 为 JSON | 通过 | JSON 对象 | {"echo":"年龄28"} |
| 5 | latencyMs/testedAt 非空 | 通过 | 非空 | 4 2026-10-24T17:51:41.580386657Z |
| 6 | 契约字段 TestResult | 通过 | success,model,output,content,errorCode,errorMessage,latencyMs,promptTokens,completionTokens,testedAt | 一致 |
| 7 | tested=true | 通过 | true | true |
| 8 | lastTestInputs={age:28} | 通过 | {"age":28} | {"age":28} |
| 9 | lastTestBy=admin | 通过 | admin | admin |
| 10 | lastTestSuccess=true | 通过 | true | true |
| 11 | version 不变 | 通过 | 0 | 0 |
| 12 | 内容不变 | 通过 | 不变 | 模板 |
