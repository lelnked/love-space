# TC-ai-IT-057 断言明细

> 北京时间昨日记录不计入今日额度

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 PUT PER_ANON_DAILY=2 返回 200 | 通过 | 200 | 200 |
| 2 | 前置今日记录数 0（2 条均为北京时间昨日 23:30） | 通过 | 0 | 0 |
| 3 | 步骤2 200（非 429） | 通过 | 200 | 200 |
| 4 | 契约 GenerationResponse 字段（生成响应） | 通过 | ["clientRequestId", "content", "requestId", "sceneId", "status"] | ["clientRequestId", "content", "requestId", "sceneId", "status"] |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
