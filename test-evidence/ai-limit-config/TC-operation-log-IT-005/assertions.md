# TC-operation-log-IT-005 断言明细

> 创建类操作的 target 为 null

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 创建城市成功 | 通过 | "200/201" | 200 |
| 2 | 命中 city:create 新记录 | 通过 | "命中" | true |
| 3 | target 为 null | 通过 | null | null |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
