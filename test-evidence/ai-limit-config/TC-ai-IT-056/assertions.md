# TC-ai-IT-056 断言明细

> 全站超出每日总量返回 429

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 PUT GLOBAL_DAILY=3 返回 200 | 通过 | 200 | 200 |
| 2 | 步骤2 429 | 通过 | 429 | 429 |
| 3 | message「服务繁忙，请稍后再试」 | 通过 | "服务繁忙，请稍后再试" | "服务繁忙，请稍后再试" |
| 4 | 不写记录 | 通过 | 0 | 0 |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
