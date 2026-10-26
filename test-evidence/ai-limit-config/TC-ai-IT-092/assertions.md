# TC-ai-IT-092 断言明细

> PUT 不存在的限制返回 404

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 404 | 通过 | 404 | 404 |
| 2 | 错误体 {status,error,message,path}（404） | 通过 | ["error", "message", "path", "status"] | ["error", "message", "path", "status"] |
| 3 | 仍只有 GLOBAL_DAILY、PER_ANON_DAILY 两项 | 通过 | ["GLOBAL_DAILY", "PER_ANON_DAILY"] | ["GLOBAL_DAILY", "PER_ANON_DAILY"] |
| 4 | 库中无 NOT_EXIST 行 | 通过 | "0" | "0" |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
