# TC-system-config-IT-005 断言明细

> PUT /{id} 编辑成功且忽略请求体 key

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 登录 200 且 token 为三段式 JWT | 通过 | 200 + JWT | 200 |
| 2 | page 返回 200 且含 ios_llm_popup | 通过 | 200 + 含该项 | 200 |
| 3 | 状态码 | 通过 | 200 | 200 |
| 4 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 5 | key 仍为 ios_llm_popup | 通过 | ios_llm_popup | ios_llm_popup |
| 6 | name | 通过 | iOS LLM 弹窗开关-改 | iOS LLM 弹窗开关-改 |
| 7 | value | 通过 | {"enabled": false} | {"enabled": false} |
| 8 | description | 通过 | 说明-改 | 说明-改 |
| 9 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 10 | 详情与响应一致 | 通过 | 一致 | {'key': 'ios_llm_popup', 'name': 'iOS LLM 弹窗开关-改', 'value': {'enabled': False}, 'description': '说明-改'} |
| 11 | 收尾恢复 200 且值复原 | 通过 | 200 + 初始值 | (200, {'enabled': True}) |

## 备注
- 请求契约自检: 请求体含 SystemConfigUpdateRequest 未声明的 key 字段（用例有意构造，验证被忽略）；name/value 必填齐全
