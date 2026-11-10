# TC-system-config-IT-012 断言明细

> 未登录访问后台系统配置接口返回 401

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤1 无 token GET /page | 通过 | 401 | 401 |
| 2 | 步骤2 无 token POST | 通过 | 401 | 401 |
| 3 | 步骤3 非法 token GET /page | 通过 | 401 | 401 |
| 4 | 未创建 totalElements | 通过 | 0 | 0 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
