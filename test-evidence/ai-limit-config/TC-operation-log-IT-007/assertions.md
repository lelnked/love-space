# TC-operation-log-IT-007 断言明细

> 嵌套资源（商户评价）的 target 取父级商户 id

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 200 | 通过 | 200 | 200 |
| 2 | R ≠ M | 通过 | "不等" | ["01a17740-9050-795b-945f-c1caf9c633e8", "01a17344-1338-79b7-a888-3221df236f96"] |
| 3 | target = 商户 id M（非 R） | 通过 | "01a17344-1338-79b7-a888-3221df236f96" | "01a17344-1338-79b7-a888-3221df236f96" |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
