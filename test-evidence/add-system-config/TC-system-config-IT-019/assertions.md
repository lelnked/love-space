# TC-system-config-IT-019 断言明细

> GET /api/app/system-configs/{key} 单个查询

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | body 恰为 {key,value} | 通过 | {'key': 'home.notice_f59eup', 'value': {'text': 'hi'}} | {'key': 'home.notice_f59eup', 'value': {'text': 'hi'}} |
| 4 | 契约 AppSystemConfig schema | 通过 | {key,value} | ['key', 'value'] |
| 5 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
