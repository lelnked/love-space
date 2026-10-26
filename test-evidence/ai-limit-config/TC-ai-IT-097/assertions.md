# TC-ai-IT-097 断言明细

> 配置行缺失时生成接口返回 500 且不写记录

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 返回 500 | 通过 | 500 | 500 |
| 2 | 步骤3 记录数 0 | 通过 | 0 | 0 |
| 3 | 收尾后恢复为两项 | 通过 | ["GLOBAL_DAILY", "PER_ANON_DAILY"] | ["GLOBAL_DAILY", "PER_ANON_DAILY"] |

## 备注

- 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。
