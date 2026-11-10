# TC-system-config-IT-017 断言明细

> 编辑 value 后 App 立即读到新值

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 状态码 | 通过 | 200 | 200 |
| 2 | 步骤3 value | 通过 | {'text': '旧'} | {'text': '旧'} |
| 3 | 契约 AppSystemConfig schema | 通过 | {key,value} | ['key', 'value'] |
| 4 | 步骤4 状态码 | 通过 | 200 | 200 |
| 5 | 步骤5 状态码 | 通过 | 200 | 200 |
| 6 | 步骤5 value | 通过 | {'text': '新'} | {'text': '新'} |
| 7 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
