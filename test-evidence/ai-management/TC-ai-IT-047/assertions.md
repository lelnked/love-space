# TC-ai-IT-047 断言明细

> POST /api/app/ai/generations 响应不含调用明细

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | status=SUCCESS | 通过 | SUCCESS | SUCCESS |
| 3 | 顶层键恰为 5 个 | 通过 | requestId,clientRequestId,sceneId,status,content | ["requestId","clientRequestId","sceneId","status","content"] |
| 4 | （佐证）调用明细 2 条 | 通过 | 2 | 2 |
