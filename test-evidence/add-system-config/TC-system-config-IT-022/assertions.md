# TC-system-config-IT-022 断言明细

> 批量查询 keys 缺失、为空与 50/51 边界

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 不带 keys 400 | 通过 | 400 | 400 |
| 2 | 步骤3 keys= 400 | 通过 | 400 | 400 |
| 3 | 步骤4 仅空白 400 | 通过 | 400 | 400 |
| 4 | 步骤5 状态码 | 通过 | 200 | 200 |
| 5 | 步骤5 body | 通过 | [] | [] |
| 6 | 步骤6 51 个 400 | 通过 | 400 | 400 |
| 7 | 步骤7 状态码 | 通过 | 200 | 200 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
