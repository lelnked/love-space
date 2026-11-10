# TC-system-config-IT-010 断言明细

> GET /page 排序 updated_at DESC 与分页参数口径

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 PUT 200 | 通过 | 200 | 200 |
| 2 | 步骤3 状态码 | 通过 | 200 | 200 |
| 3 | 步骤3 page | 通过 | 1 | 1 |
| 4 | 步骤3 size | 通过 | 20 | 20 |
| 5 | 步骤3 totalElements | 通过 | 3 | 3 |
| 6 | 步骤3 顺序 | 通过 | ['sort_hjbj2n.a', 'sort_hjbj2n.c', 'sort_hjbj2n.b'] | ['sort_hjbj2n.a', 'sort_hjbj2n.c', 'sort_hjbj2n.b'] |
| 7 | 步骤4 状态码 | 通过 | 200 | 200 |
| 8 | 步骤4 size 校正为 20 | 通过 | 20 | 20 |
| 9 | 步骤5 状态码 | 通过 | 200 | 200 |
| 10 | 步骤5 size | 通过 | 200 | 200 |
| 11 | 收尾删除 204 | 通过 | 204 | 204 |
| 12 | 收尾删除 204 | 通过 | 204 | 204 |
| 13 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 分页响应顶层字段: ['content', 'page', 'size', 'totalElements', 'totalPages']
- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
