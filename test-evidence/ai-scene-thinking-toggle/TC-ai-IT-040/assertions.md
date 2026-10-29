# TC-ai-IT-040 断言明细

> 生成记录请求快照不含密钥与认证头

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 生成 200 SUCCESS | 通过 | (200, 'SUCCESS') |
| 2 | request_messages 为 [system,user] | 通过 | ['system', 'user'] |
| 3 | request_params 含 8 个键（含 thinkingEnabled） | 通过 | ['backupModel', 'maxRetries', 'maxTokens', 'primaryModel', 'temperature', 'thinkingEnabled', 'timeoutSeconds', 'topP'] |
| 4 | 文本不含 ARK_API_KEY 值（"test" 作为独立值） | 通过 | 无命中 |
| 5 | 文本不含 Authorization | 通过 | 无命中 |

**结论**: 通过
