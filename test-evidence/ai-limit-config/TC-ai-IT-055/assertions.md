# TC-ai-IT-055 断言明细

> 匿名 ID 超出每日次数返回 429

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 PUT PER_ANON_DAILY=2 返回 200 | 通过 | 200 | 200 |
| 2 | 步骤2 429 | 通过 | 429 | 429 |
| 3 | message「今日生成次数已达上限」 | 通过 | "今日生成次数已达上限" | "今日生成次数已达上限" |
| 4 | 今日记录数仍为 2 | 通过 | 2 | 2 |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
