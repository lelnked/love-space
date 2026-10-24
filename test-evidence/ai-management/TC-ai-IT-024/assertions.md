# TC-ai-IT-024 断言明细

> POST /api/admin/ai/prompt-templates/{id}/test 模型返回非 JSON 测试失败

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | success=false | 通过 | false | false |
| 3 | errorCode=FORMAT_ERROR | 通过 | FORMAT_ERROR | FORMAT_ERROR |
| 4 | content=null | 通过 | null | null |
| 5 | tested=false | 通过 | false | false |
| 6 | lastTestOutput 为 mock 原始输出 | 通过 | not json | not json |
| 7 | lastTestSuccess=false | 通过 | false | false |
