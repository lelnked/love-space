# TC-ai-IT-050 断言明细

> 重复提交返回原结果且不再调用模型

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | requestId/content/status 与首次相同 | 通过 | {"requestId":"01a16f0a-484d-7d1b-a12e-8c9dc0199379","clientRequestId":"c-1-6f870c62","sceneId":"t_8c38362c","status":"SUCCESS","content":{"echo":"年龄28"}} | {"requestId":"01a16f0a-484d-7d1b-a12e-8c9dc0199379","clientRequestId":"c-1-6f870c62","sceneId":"t_8c38362c","status":"SUCCESS","content":{"echo":"年龄28"}} |
| 3 | 记录仍 1 条 | 通过 | 1 | 1 |
| 4 | 未再调用模型（明细数不变） | 通过 | 1 | 1 |

## 备注

- 假方舟无调用计数接口；以「该生成的调用明细条数不变」作为不再调用模型的代理证据（每次调用模型都会写一条 attempt）。
