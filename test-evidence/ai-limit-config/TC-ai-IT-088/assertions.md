# TC-ai-IT-088 断言明细

> GET /api/admin/ai/limits 初始数据

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 200 | 通过 | 200 | 200 |
| 2 | 响应头 Content-Type 含 application/json（GET） | 通过 | "application/json" | "application/json" |
| 3 | 数组共 2 项 | 通过 | 2 | 2 |
| 4 | [0] key/name/value | 通过 | ["GLOBAL_DAILY", "全站每日上限", 5000] | ["GLOBAL_DAILY", "全站每日上限", 5000] |
| 5 | [0] updatedBy/updatedAt 为 null | 通过 | [null, null] | [null, null] |
| 6 | [1] key/name/value | 通过 | ["PER_ANON_DAILY", "单个匿名 ID 每日上限", 50] | ["PER_ANON_DAILY", "单个匿名 ID 每日上限", 50] |
| 7 | [1] updatedBy/updatedAt 为 null | 通过 | [null, null] | [null, null] |
| 8 | 契约 schema array<AiLimit>（GET 列表） | 通过 | "array" | "list" |
| 9 | 契约 schema AiLimit（GET 列表[0]） | 通过 | "key/name/value 必填，类型匹配" | "{\"key\": \"GLOBAL_DAILY\", \"name\": \"全站每日上限\", \"value\": 5000, \"updatedBy\": null, \"updatedAt\": null}" |
| 10 | 契约 schema AiLimit（GET 列表[1]） | 通过 | "key/name/value 必填，类型匹配" | "{\"key\": \"PER_ANON_DAILY\", \"name\": \"单个匿名 ID 每日上限\", \"value\": 50, \"updatedBy\": null, \"updatedAt\": null}" |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
