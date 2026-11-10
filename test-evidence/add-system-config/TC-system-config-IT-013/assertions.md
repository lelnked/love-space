# TC-system-config-IT-013 断言明细

> MEMBER 角色可完整使用系统配置接口

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 创建 MEMBER 201 | 通过 | 201 | 201 |
| 2 | 角色为 MEMBER | 通过 | MEMBER | MEMBER |
| 3 | MEMBER 登录 200 | 通过 | 200 | 200 |
| 4 | GET /page 200 | 通过 | 200 | 200 |
| 5 | POST 201 | 通过 | 201 | 201 |
| 6 | GET /{id} 200 | 通过 | 200 | 200 |
| 7 | PUT /{id} 200 | 通过 | 200 | 200 |
| 8 | DELETE /{id} 204 | 通过 | 204 | 204 |

## 备注

- 收尾：后台无 DELETE /api/admin/managers/{id} 接口（契约仅有 enable/disable/password），改为禁用账号 it_sc_member_b695b4（PUT disable → 204），账号记录残留于库。
- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
