# TC-system-config-IT-009 断言明细

> GET /page 按 keyword 匹配 key 或名称（不区分大小写）

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 状态码 | 通过 | 200 | 200 |
| 2 | 步骤3 content keys | 通过 | ['home.notice_t792qv'] | ['home.notice_t792qv'] |
| 3 | 步骤3 每项字段齐全 | 通过 | ['createdAt', 'description', 'id', 'key', 'name', 'updatedAt', 'value'] | [['createdAt', 'description', 'id', 'key', 'name', 'updatedAt', 'value']] |
| 4 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 5 | 步骤4 大写 状态码 | 通过 | 200 | 200 |
| 6 | 步骤4 大写 content keys | 通过 | ['home.notice_t792qv'] | ['home.notice_t792qv'] |
| 7 | 步骤4 大写 每项字段齐全 | 通过 | ['createdAt', 'description', 'id', 'key', 'name', 'updatedAt', 'value'] | [['createdAt', 'description', 'id', 'key', 'name', 'updatedAt', 'value']] |
| 8 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 9 | 步骤5 按名称 状态码 | 通过 | 200 | 200 |
| 10 | 步骤5 按名称 content keys | 通过 | ['cycle_default_days_t792qv'] | ['cycle_default_days_t792qv'] |
| 11 | 步骤5 按名称 每项字段齐全 | 通过 | ['createdAt', 'description', 'id', 'key', 'name', 'updatedAt', 'value'] | [['createdAt', 'description', 'id', 'key', 'name', 'updatedAt', 'value']] |
| 12 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 13 | 步骤6 状态码 | 通过 | 200 | 200 |
| 14 | 步骤6 content | 通过 | [] | [] |
| 15 | 步骤6 totalElements | 通过 | 0 | 0 |
| 16 | 收尾删除 204 | 通过 | 204 | 204 |
| 17 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
