# TC-ai-IT-090 断言明细

> PUT 非法取值返回 400 且数据不变

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | {"value":0} → 状态码 400 | 通过 | 400 | 400 |
| 2 | 错误体 {status,error,message,path}（{"value":0}） | 通过 | ["error", "message", "path", "status"] | ["error", "message", "path", "status"] |
| 3 | {"value":0} → message 含「取值须为 -1 或正整数」 | 通过 | "含「取值须为 -1 或正整数」" | "取值须为 -1 或正整数" |
| 4 | {"value":-2} → 状态码 400 | 通过 | 400 | 400 |
| 5 | 错误体 {status,error,message,path}（{"value":-2}） | 通过 | ["error", "message", "path", "status"] | ["error", "message", "path", "status"] |
| 6 | {"value":-2} → message 含「取值须为 -1 或正整数」 | 通过 | "含「取值须为 -1 或正整数」" | "取值须为 -1 或正整数" |
| 7 | {} → 状态码 400 | 通过 | 400 | 400 |
| 8 | 错误体 {status,error,message,path}（{}） | 通过 | ["error", "message", "path", "status"] | ["error", "message", "path", "status"] |
| 9 | {} → message 含「取值须为 -1 或正整数」 | 通过 | "含「取值须为 -1 或正整数」" | "取值须为 -1 或正整数" |
| 10 | {"value":1.5} → 状态码 400 | 通过 | 400 | 400 |
| 11 | 错误体 {status,error,message,path}（{"value":1.5}） | 通过 | ["error", "message", "path", "status"] | ["error", "message", "path", "status"] |
| 12 | {"value":1.5} → message 含「取值须为 -1 或正整数」 | 通过 | "含「取值须为 -1 或正整数」" | "取值须为 -1 或正整数" |
| 13 | GLOBAL_DAILY value/updatedBy/updatedAt 与基线一致 | 通过 | [5000, null, null] | [5000, null, null] |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lc25cc4459`（fixture 密码 `Member#2026x`）；场景 `t_lc25cc4459`（主模型 mock-ok，模板 `01a17742-7d68-7052-aab3-e4a752e6685e` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
