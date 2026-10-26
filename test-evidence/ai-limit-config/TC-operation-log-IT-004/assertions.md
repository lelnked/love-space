# TC-operation-log-IT-004 断言明细

> 创建运营账号的 payload 中 password 被脱敏

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 201 | 通过 | 201 | 201 |
| 2 | 留痕落库（manager:create 新记录） | 通过 | "命中" | true |
| 3 | password 为 [REDACTED] | 通过 | "[REDACTED]" | "[REDACTED]" |
| 4 | 全文不含明文密码 | 通过 | "不含" | "不含" |
| 5 | username 明文 | 通过 | "logredactc015d5fa" | "logredactc015d5fa" |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
