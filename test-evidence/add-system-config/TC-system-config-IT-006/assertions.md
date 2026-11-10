# TC-system-config-IT-006 断言明细

> POST value 缺失或为 null 返回 400

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 状态码 | 通过 | 400 | 400 |
| 2 | 步骤2 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 3 | 步骤2 message | 通过 | value 不能为空 | value 不能为空 |
| 4 | 步骤3 状态码 | 通过 | 400 | 400 |
| 5 | 步骤3 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 6 | 步骤3 message | 通过 | value 不能为空 | value 不能为空 |
| 7 | 步骤4 状态码 | 通过 | 400 | 400 |
| 8 | 步骤5 状态码 | 通过 | 200 | 200 |
| 9 | 步骤5 totalElements | 通过 | 0 | 0 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
