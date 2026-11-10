# TC-system-config-IT-018 断言明细

> 删除后 App 查询返回 404

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 App GET 200 | 通过 | 200 | 200 |
| 2 | 步骤3 DELETE 204 | 通过 | 204 | 204 |
| 3 | 步骤4 状态码 | 通过 | 404 | 404 |
| 4 | 步骤4 message | 通过 | 配置不存在 | 配置不存在 |
| 5 | 步骤5 状态码 | 通过 | 200 | 200 |
| 6 | 步骤5 body | 通过 | [] | [] |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
