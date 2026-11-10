# TC-system-config-IT-021 断言明细

> GET /api/app/system-configs?keys= 批量查询忽略不存在 key 并保持顺序

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 状态码 | 通过 | 200 | 200 |
| 2 | 步骤2 数组 | 通过 | [{'key': 'b.two_94d5hc', 'value': 'two'}, {'key': 'a.one_94d5hc', 'value': 1}] | [{'key': 'b.two_94d5hc', 'value': 'two'}, {'key': 'a.one_94d5hc', 'value': 1}] |
| 3 | 契约 AppSystemConfig schema | 通过 | {key,value} | ['key', 'value'] |
| 4 | 契约 AppSystemConfig schema | 通过 | {key,value} | ['key', 'value'] |
| 5 | 步骤3 状态码 | 通过 | 200 | 200 |
| 6 | 步骤3 数组 | 通过 | [{'key': 'b.two_94d5hc', 'value': 'two'}, {'key': 'a.one_94d5hc', 'value': 1}] | [{'key': 'b.two_94d5hc', 'value': 'two'}, {'key': 'a.one_94d5hc', 'value': 1}] |
| 7 | 收尾删除 204 | 通过 | 204 | 204 |
| 8 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
