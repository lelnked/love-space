# TC-system-config-IT-008 断言明细

> 编辑 value 后 App 立即读到新值

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 登录 200 且 token 为三段式 JWT | 通过 | 200 + JWT | 200 |
| 2 | page 返回 200 且含 ios_llm_popup | 通过 | 200 + 含该项 | 200 |
| 3 | PUT 200 | 通过 | 200 | 200 |
| 4 | app 200 | 通过 | 200 | 200 |
| 5 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 6 | app value={"enabled":false} | 通过 | {"enabled": false} | {"enabled": false} |
| 7 | 收尾恢复 200 且值复原 | 通过 | 200 + 初始值 | (200, {'enabled': True}) |

## 备注
