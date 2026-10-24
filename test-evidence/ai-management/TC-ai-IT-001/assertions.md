# TC-ai-IT-001 断言明细

> GET /api/admin/ai/models 返回环境变量配置的模型列表

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 | 通过 | 200 | 200 |
| 2 | Content-Type | 通过 | application/json | application/json |
| 3 | body 为 AI_MODELS 列表（去空白、保持顺序） | 通过 | ["mock-ok","mock-fenced","mock-format-error","mock-empty","mock-refused","mock-429","mock-500","mock-timeout","mock-flaky","mock-slow"] | ["mock-ok","mock-fenced","mock-format-error","mock-empty","mock-refused","mock-429","mock-500","mock-timeout","mock-flaky","mock-slow"] |
| 4 | 响应无 API Key 字段/值 | 通过 | 无 key/secret | ["mock-ok","mock-fenced","mock-format-error","mock-empty","mock-refused","mock-429","mock-500","mock-timeout","mock-flaky","mock-slow"] |

## 备注

- 环境 AI_MODELS 为 10 个 mock 模型（model-a/model-b 映射），断言 body 与 AI_MODELS 顺序一致。
