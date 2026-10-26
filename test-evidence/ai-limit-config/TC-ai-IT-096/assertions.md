# TC-ai-IT-096 断言明细

> 后台修改额度后下一次请求即生效

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 第 1 次 200 | 通过 | 200 | 200 |
| 2 | 步骤2 第 2 次 200 | 通过 | 200 | 200 |
| 3 | 步骤3 429 | 通过 | 429 | 429 |
| 4 | 步骤3 message「今日生成次数已达上限」 | 通过 | "今日生成次数已达上限" | "今日生成次数已达上限" |
| 5 | 步骤4 PUT=3 返回 200 | 通过 | 200 | 200 |
| 6 | 步骤5 200 正常生成 | 通过 | "200 SUCCESS" | [200, "SUCCESS"] |
| 7 | 契约 GenerationResponse 字段（步骤5） | 通过 | ["clientRequestId", "content", "requestId", "sceneId", "status"] | ["clientRequestId", "content", "requestId", "sceneId", "status"] |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
