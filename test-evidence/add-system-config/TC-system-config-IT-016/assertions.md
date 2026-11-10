# TC-system-config-IT-016 断言明细

> DELETE /{id} 物理删除与重复删除

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 DELETE 204 | 通过 | 204 | 204 |
| 2 | 步骤4 状态码 | 通过 | 404 | 404 |
| 3 | 步骤4 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 4 | 步骤4 message | 通过 | 配置不存在 | 配置不存在 |
| 5 | 步骤5 状态码 | 通过 | 404 | 404 |
| 6 | 步骤5 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 7 | 步骤5 message | 通过 | 配置不存在 | 配置不存在 |
| 8 | 步骤6 状态码 | 通过 | 201 | 201 |
| 9 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
