# TC-operation-log-IT-020 断言明细

> 修改系统配置后异步产生 system-config:update 留痕

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 登录 200 且 token 为三段式 JWT | 通过 | 200 + JWT | 200 |
| 2 | page 返回 200 且含 ios_llm_popup | 通过 | 200 + 含该项 | 200 |
| 3 | PUT 200 | 通过 | 200 | 200 |
| 4 | 轮询命中新 update 记录 | 通过 | 命中 | 第 1 次命中 |
| 5 | module=system-config action=update username=admin target=C | 通过 | system-config/update/admin/019794b6-b400-7000-8000-000000000031 | ('system-config', 'update', 'admin', '019794b6-b400-7000-8000-000000000031') |

## 备注
- 执行前已有 4 条 system-config 日志（按 id 去重，仅认新增记录）
