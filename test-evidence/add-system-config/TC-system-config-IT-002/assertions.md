# TC-system-config-IT-002 断言明细

> value 以 JSON 原值往返（对象）

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 状态码 | 通过 | 201 | 201 |
| 2 | 步骤3 状态码 | 通过 | 200 | 200 |
| 3 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 4 | value JSON 类型为 object | 通过 | object | dict |
| 5 | value 等值 | 通过 | {'text': '放假通知', 'show': True} | {'show': True, 'text': '放假通知'} |
| 6 | show 为布尔 true | 通过 | True | True |
| 7 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 8 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
