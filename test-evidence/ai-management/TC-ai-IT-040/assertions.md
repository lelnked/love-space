# TC-ai-IT-040 断言明细

> 生成记录请求快照不含密钥与认证头

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | request_messages 为 [system,user] | 通过 | ["system","user"] | ["system","user"] |
| 2 | request_params 含必需键 | 通过 | primaryModel,backupModel,temperature,topP,maxTokens,timeoutSeconds,maxRetries | ["topP","maxTokens","maxRetries","backupModel","temperature","primaryModel","timeoutSeconds"] |
| 3 | 不含 ARK_API_KEY 值与 Authorization | 通过 | 不含 | {"m":[{"role":"system","content":"你是助手，只输出 JSON"},{"role":"user","content":"年龄28"}],"p":{"topP":null,"maxTokens":null,"maxRetries":1,"backupModel":null,"temperature":null,"primaryModel":"mock-ok","timeoutSeconds":3}} |

## 备注

- ARK_API_KEY 实值为 `test`（假方舟），提示词刻意不含 test 字样，故以「快照文本不含 test/Authorization/Bearer/apiKey」核对。
