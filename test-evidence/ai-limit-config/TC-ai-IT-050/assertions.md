# TC-ai-IT-050 断言明细

> 重复提交返回原结果且不再调用模型

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 200 | 通过 | 200 | 200 |
| 2 | requestId/content/status 与首次相同 | 通过 | ["01a17740-8df7-7dba-a96a-45e783f307a1", {"echo": "年龄28"}, "SUCCESS"] | ["01a17740-8df7-7dba-a96a-45e783f307a1", {"echo": "年龄28"}, "SUCCESS"] |
| 3 | 记录仍 1 条 | 通过 | "1" | "1" |
| 4 | 未再调用模型（调用明细数不变） | 通过 | "1" | "1" |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
- 假方舟无调用计数接口；以该生成的调用明细（loves_ai_generation_attempt）条数不变作为未再调用模型的代理证据。
