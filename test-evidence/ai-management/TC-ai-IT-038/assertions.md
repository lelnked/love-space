# TC-ai-IT-038 断言明细

> POST /api/app/ai/generations 生成成功

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | Content-Type | 通过 | application/json | application/json |
| 3 | status=SUCCESS | 通过 | SUCCESS | SUCCESS |
| 4 | content 为 mock 输出 JSON | 通过 | {"echo":"年龄28"} | {"echo":"年龄28"} |
| 5 | requestId 非空 | 通过 | 非空 | 01a16f0a-4151-78a1-99b7-f33f2d3ed80f |
| 6 | clientRequestId/sceneId 与请求一致 | 通过 | c-c5518e12-dc1c-43a0-8f97-6e7e1c183991 / t_75600e4e | c-c5518e12-dc1c-43a0-8f97-6e7e1c183991 / t_75600e4e |
| 7 | 契约字段 GenerationResponse | 通过 | requestId,clientRequestId,sceneId,status,content | 一致 |
