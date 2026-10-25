# TC-ai-IT-038 断言明细

> POST /api/app/ai/generations 生成成功

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | status=SUCCESS | 通过 | SUCCESS | SUCCESS |
| 4 | content 为 mock 输出 JSON | 通过 | {'echo': '年龄28'} | {'echo': '年龄28'} |
| 5 | requestId 非空 | 通过 | 非空 | 01a17341-5af2-7a46-b1f9-786dbf9ccc8e |
| 6 | clientRequestId/sceneId 一致 | 通过 | ('c-cd70e7f7-4bc4-4d43-8394-5982416eacf0', 'gr_3f05e38d') | ('c-cd70e7f7-4bc4-4d43-8394-5982416eacf0', 'gr_3f05e38d') |
| 7 | 契约字段 GenerationResponse | 通过 | ['clientRequestId', 'content', 'requestId', 'sceneId', 'status'] | ['clientRequestId', 'content', 'requestId', 'sceneId', 'status'] |
