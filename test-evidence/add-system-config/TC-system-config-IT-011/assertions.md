# TC-system-config-IT-011 断言明细

> GET /api/app/system-configs?keys= 批量查询忽略不存在 key 并保持顺序

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | 数组 1 项且为 ios_llm_popup | 通过 | [{"key":"ios_llm_popup","value":{"enabled":true}}] | [{"key":"ios_llm_popup","value":{"enabled": true}}] |
| 4 | 契约 AppSystemConfig schema（required key/value，key 为 string） | 通过 | required 齐全 | OK |

## 备注
