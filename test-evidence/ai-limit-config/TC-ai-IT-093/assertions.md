# TC-ai-IT-093 断言明细

> MEMBER 可查看与修改限流配置，未登录 401

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 状态码 200 | 通过 | 200 | 200 |
| 2 | 步骤4 状态码 200（非 403） | 通过 | 200 | 200 |
| 3 | 步骤4 updatedBy 为 MEMBER 用户名 | 通过 | "u_lcc015d5fa" | "u_lcc015d5fa" |
| 4 | 步骤5 GET 401 | 通过 | 401 | 401 |
| 5 | 步骤5 PUT 401 | 通过 | 401 | 401 |
| 6 | GLOBAL_DAILY 仍为 5000 | 通过 | 5000 | 5000 |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
