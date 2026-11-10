# TC-system-config-IT-007 断言明细

> POST value 序列化长度边界 65535 / 65536

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 状态码 | 通过 | 201 | 201 |
| 2 | 详情状态码 | 通过 | 200 | 200 |
| 3 | 详情 value 为长 65533 的原字符串 | 通过 | str len 65533 | str len 65533 |
| 4 | 步骤3 状态码 | 通过 | 400 | 400 |
| 5 | 步骤3 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 6 | 步骤3 message | 通过 | value 过长 | value 过长 |
| 7 | v.over 未创建 totalElements | 通过 | 0 | 0 |
| 8 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
