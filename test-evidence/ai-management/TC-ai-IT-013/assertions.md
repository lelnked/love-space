# TC-ai-IT-013 断言明细

> POST /api/admin/ai/models/test-connection 连接测试

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 200 | 通过 | 200 | 200 |
| 2 | ok=true | 通过 | true | true |
| 3 | latencyMs 正整数 | 通过 | >0 整数 | 4 |
| 4 | message 存在 | 通过 | 有 message | {"ok":true,"latencyMs":4,"message":"可达"} |
| 5 | 契约字段 ConnectionTest | 通过 | ok,latencyMs,message | 一致 |
| 6 | mock-500 200 | 通过 | 200 | 200 |
| 7 | mock-500 ok=false 且 message 含原因 | 通过 | ok=false + message | {"ok":false,"latencyMs":4,"message":"方舟返回 HTTP 500"} |
| 8 | mock-timeout 200 | 通过 | 200 | 200 |
| 9 | mock-timeout ok=false 且 message 含原因 | 通过 | ok=false + message | {"ok":false,"latencyMs":3002,"message":"调用超时（3 秒）"} |

## 备注

- 步骤4 原写法需停掉 mock 或改 ARK_BASE_URL 重启 admin；按「不擅自停/启服务」规则，改用同一假方舟上必失败的模型 mock-500（HTTP 500）与 mock-timeout（超时）代替「方舟不可达」，验证 ok=false + 失败原因。
