# TC-system-config-IT-003 断言明细

> 标量与数组均可作为 value

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 新增 v.num_iaosyx 状态码 | 通过 | 201 | 201 |
| 2 | 新增 v.str_iaosyx 状态码 | 通过 | 201 | 201 |
| 3 | 新增 v.bool_iaosyx 状态码 | 通过 | 201 | 201 |
| 4 | 新增 v.arr_iaosyx 状态码 | 通过 | 201 | 201 |
| 5 | 详情 v.num_iaosyx 状态码 | 通过 | 200 | 200 |
| 6 | v.num_iaosyx value 类型与值 | 通过 | int 28 | int 28 |
| 7 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 8 | 详情 v.str_iaosyx 状态码 | 通过 | 200 | 200 |
| 9 | v.str_iaosyx value 类型与值 | 通过 | str "hello" | str "hello" |
| 10 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 11 | 详情 v.bool_iaosyx 状态码 | 通过 | 200 | 200 |
| 12 | v.bool_iaosyx value 类型与值 | 通过 | bool false | bool false |
| 13 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 14 | 详情 v.arr_iaosyx 状态码 | 通过 | 200 | 200 |
| 15 | v.arr_iaosyx value 类型与值 | 通过 | list [1, 2, 3] | list [1, 2, 3] |
| 16 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 17 | 收尾删除 204 | 通过 | 204 | 204 |
| 18 | 收尾删除 204 | 通过 | 204 | 204 |
| 19 | 收尾删除 204 | 通过 | 204 | 204 |
| 20 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
