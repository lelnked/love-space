# TC-system-config-IT-023 断言明细

> App 系统配置接口 API-key 缺失或非法返回 401

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤1 无 key | 通过 | 401 | 401 |
| 2 | 步骤2 wrong-key | 通过 | 401 | 401 |
| 3 | 步骤3 批量无 key | 通过 | 401 | 401 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
