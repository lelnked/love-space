# TC-ai-IT-095 断言明细

> 两项额度均为 -1 时不做额度检查

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 状态码 200（非 429） | 通过 | 200 | 200 |
| 2 | status=SUCCESS | 通过 | "SUCCESS" | "SUCCESS" |
| 3 | 契约 GenerationResponse 字段（生成响应） | 通过 | ["clientRequestId", "content", "requestId", "sceneId", "status"] | ["clientRequestId", "content", "requestId", "sceneId", "status"] |
| 4 | 步骤4 今日记录数 101 | 通过 | 101 | 101 |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
