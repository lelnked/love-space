# TC-ai-IT-058 断言明细

> 去重命中不占用额度

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 PUT PER_ANON_DAILY=1 返回 200 | 通过 | 200 | 200 |
| 2 | 步骤2 200 | 通过 | 200 | 200 |
| 3 | 步骤2 与原结果一致 | 通过 | {"requestId": "01a17740-8d7b-7525-ad56-f4537571da63", "clientRequestId": "c-1-c015d5fa", "sceneId": "t_lcc015d5fa", "status": "SUCCESS", "content": {"echo": "年龄28"}} | {"requestId": "01a17740-8d7b-7525-ad56-f4537571da63", "clientRequestId": "c-1-c015d5fa", "sceneId": "t_lcc015d5fa", "status": "SUCCESS", "content": {"echo": "年龄28"}} |
| 4 | 步骤3 429 | 通过 | 429 | 429 |
| 5 | message「今日生成次数已达上限」 | 通过 | "今日生成次数已达上限" | "今日生成次数已达上限" |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
