# TC-system-config-IT-001 断言明细

> 预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 登录 200 且 token 为三段式 JWT | 通过 | 200 + JWT | 200 |
| 2 | page 返回 200 且含 ios_llm_popup | 通过 | 200 + 含该项 | 200 |
| 3 | page 项 name=iOS LLM 弹窗开关 | 通过 | iOS LLM 弹窗开关 | iOS LLM 弹窗开关 |
| 4 | 状态码 | 通过 | 200 | 200 |
| 5 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 6 | value 为 object 且等于 {"enabled":true}（布尔） | 通过 | {"enabled": true} | {"enabled": true} |
| 7 | id 与预置 id 一致 | 通过 | 019794b6-b400-7000-8000-000000000031 | 019794b6-b400-7000-8000-000000000031 |
| 8 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |

## 备注
- 契约 operation 未声明 `responses`（仅 description 描述状态码），状态码"在 responses 中已声明"项按 description 文字核对
