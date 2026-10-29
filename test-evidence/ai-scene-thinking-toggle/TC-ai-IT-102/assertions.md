# TC-ai-IT-102 断言明细

> POST /api/admin/ai/models/test-connection 场景开启深度思考时连接测试行为不变

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 前置 PUT 返回 204 | 通过 | 204 |
| 2 | 返回 200 | 通过 | 200 |
| 3 | Content-Type 含 application/json | 通过 | application/json |
| 4 | ok=true | 通过 | {'ok': True, 'latencyMs': 3, 'message': '可达'} |
| 5 | latencyMs 为正整数 | 通过 | 3 |
| 6 | 响应结构与 TC-ai-IT-013 一致 {ok,latencyMs,message} | 通过 | ['latencyMs', 'message', 'ok'] |

**结论**: 通过
