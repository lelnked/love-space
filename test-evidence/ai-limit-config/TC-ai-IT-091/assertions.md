# TC-ai-IT-091 断言明细

> PUT 取值边界 1 与 2147483647 通过

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | value=1 状态码 200 | 通过 | 200 | 200 |
| 2 | value=1 响应 value | 通过 | 1 | 1 |
| 3 | 契约 schema AiLimit（PUT 1） | 通过 | "key/name/value 必填，类型匹配" | "{\"key\": \"GLOBAL_DAILY\", \"name\": \"全站每日上限\", \"value\": 1, \"updatedBy\": \"admin\", \"updatedAt\": \"2026-10-26T16:10:08.962452414+08:00\"}" |
| 4 | value=2147483647 状态码 200 | 通过 | 200 | 200 |
| 5 | value=2147483647 响应 value | 通过 | 2147483647 | 2147483647 |
| 6 | 契约 schema AiLimit（PUT 2147483647） | 通过 | "key/name/value 必填，类型匹配" | "{\"key\": \"GLOBAL_DAILY\", \"name\": \"全站每日上限\", \"value\": 2147483647, \"updatedBy\": \"admin\", \"updatedAt\": \"2026-10-26T16:10:08.980233322+08:00\"}" |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lc25cc4459`（fixture 密码 `Member#2026x`）；场景 `t_lc25cc4459`（主模型 mock-ok，模板 `01a17742-7d68-7052-aab3-e4a752e6685e` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
