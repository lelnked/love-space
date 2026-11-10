# TC-system-config-IT-006 断言明细

> PUT /{id} value 为 null 或缺失返回 400

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 登录 200 且 token 为三段式 JWT | 通过 | 200 + JWT | 200 |
| 2 | page 返回 200 且含 ios_llm_popup | 通过 | 200 + 含该项 | 200 |
| 3 | value=null 400 | 通过 | 400 | 400 |
| 4 | value=null message | 通过 | value 不能为空 | value 不能为空 |
| 5 | value 缺失 400 | 通过 | 400 | 400 |
| 6 | value 缺失 message | 通过 | value 不能为空 | value 不能为空 |
| 7 | 配置值未变 | 通过 | {"enabled": true} | {"enabled": true} |

## 备注
