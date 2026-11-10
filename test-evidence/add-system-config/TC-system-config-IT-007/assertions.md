# TC-system-config-IT-007 断言明细

> PUT /{id} ios_llm_popup 的 enabled 非布尔值返回 400

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 登录 200 且 token 为三段式 JWT | 通过 | 200 + JWT | 200 |
| 2 | page 返回 200 且含 ios_llm_popup | 通过 | 200 + 含该项 | 200 |
| 3 | value={} 400 | 通过 | 400 | 400 |
| 4 | value={} message | 通过 | ios_llm_popup 的 enabled 必须为布尔值 | ios_llm_popup 的 enabled 必须为布尔值 |
| 5 | enabled="yes" 400 | 通过 | 400 | 400 |
| 6 | enabled="yes" message | 通过 | ios_llm_popup 的 enabled 必须为布尔值 | ios_llm_popup 的 enabled 必须为布尔值 |
| 7 | value=true 400 | 通过 | 400 | 400 |
| 8 | value=true message | 通过 | ios_llm_popup 的 enabled 必须为布尔值 | ios_llm_popup 的 enabled 必须为布尔值 |
| 9 | 配置值未变 | 通过 | {"enabled": true} | {"enabled": true} |

## 备注
