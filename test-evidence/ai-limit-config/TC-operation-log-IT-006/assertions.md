# TC-operation-log-IT-006 断言明细

> 更新类操作的 target 为目标城市 id

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 200 | 通过 | 200 | 200 |
| 2 | target = 城市 id C | 通过 | "01a17740-8fdc-7968-b096-0ffd2f178a89" | "01a17740-8fdc-7968-b096-0ffd2f178a89" |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
