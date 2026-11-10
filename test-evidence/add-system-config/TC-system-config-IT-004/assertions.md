# TC-system-config-IT-004 断言明细

> POST key 重复返回 409

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 201 | 通过 | 201 | 201 |
| 2 | 步骤3 状态码 | 通过 | 409 | 409 |
| 3 | 错误体 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 4 | 错误体 message | 通过 | 配置 key 已存在 | 配置 key 已存在 |
| 5 | 步骤4 状态码 | 通过 | 200 | 200 |
| 6 | 步骤4 totalElements | 通过 | 1 | 1 |
| 7 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
