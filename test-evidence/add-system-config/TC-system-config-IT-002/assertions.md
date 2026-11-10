# TC-system-config-IT-002 断言明细

> GET /page 按 keyword 匹配 key 或名称（不区分大小写）

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 登录 200 且 token 为三段式 JWT | 通过 | 200 + JWT | 200 |
| 2 | keyword=llm_popup 200 且含 ios_llm_popup | 通过 | 200 + 含 | (200, ['ios_llm_popup']) |
| 3 | keyword=llm_popup 项字段齐全 ios_llm_popup | 通过 | 7 字段 | ['createdAt', 'description', 'id', 'key', 'name', 'updatedAt', 'value'] |
| 4 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 5 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 6 | keyword=LLM_POPUP 200 且含 ios_llm_popup | 通过 | 200 + 含 | (200, ['ios_llm_popup']) |
| 7 | keyword=LLM_POPUP 项字段齐全 ios_llm_popup | 通过 | 7 字段 | ['createdAt', 'description', 'id', 'key', 'name', 'updatedAt', 'value'] |
| 8 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 9 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 10 | keyword=弹窗开关 200 且含 ios_llm_popup | 通过 | 200 + 含 | (200, ['ios_llm_popup']) |
| 11 | keyword=弹窗开关 项字段齐全 ios_llm_popup | 通过 | 7 字段 | ['createdAt', 'description', 'id', 'key', 'name', 'updatedAt', 'value'] |
| 12 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 13 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 14 | zz_nomatch 200 content=[] totalElements=0 | 通过 | 200,[],0 | (200, [], 0) |

## 备注
- 契约 operation 未声明 `responses`（仅 description 描述状态码），状态码"在 responses 中已声明"项按 description 文字核对
