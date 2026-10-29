# TC-ai-IT-013 断言明细

> POST /api/admin/ai/models/test-connection 连接测试

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 返回 200 | 通过 | 200 |
| 2 | ok=true | 通过 | {'ok': True, 'latencyMs': 72, 'message': '可达'} |
| 3 | latencyMs 为正整数 | 通过 | 72 |
| 4 | message 存在 | 通过 | 可达 |
| 5 | 响应键集合 {ok,latencyMs,message} | 通过 | ['latencyMs', 'message', 'ok'] |
| 6 | 失败场景返回 200 | 通过 | 200 |
| 7 | 失败场景 ok=false | 通过 | {'ok': False, 'latencyMs': 3, 'message': '方舟返回 HTTP 500'} |
| 8 | 失败场景 message 含失败原因 | 通过 | 方舟返回 HTTP 500 |

**结论**: 通过
