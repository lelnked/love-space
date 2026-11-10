# TC-system-config-IT-015 断言明细

> PUT /{id} 不存在与校验失败

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 状态码 | 通过 | 404 | 404 |
| 2 | 步骤2 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 3 | 步骤2 message | 通过 | 配置不存在 | 配置不存在 |
| 4 | 步骤4 状态码 | 通过 | 400 | 400 |
| 5 | 步骤4 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 6 | 步骤4 message | 通过 | value 不能为空 | value 不能为空 |
| 7 | 步骤5 状态码 | 通过 | 400 | 400 |
| 8 | 步骤5 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 9 | 步骤5 message | 通过 | value 不能为空 | value 不能为空 |
| 10 | 步骤6 状态码 | 通过 | 400 | 400 |
| 11 | 步骤7 状态码 | 通过 | 400 | 400 |
| 12 | 步骤7 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 13 | 步骤7 message | 通过 | value 过长 | value 过长 |
| 14 | 步骤8 value 仍为 1 | 通过 | 1 | 1 |
| 15 | 步骤8 name 未变 | 通过 | x | x |
| 16 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
