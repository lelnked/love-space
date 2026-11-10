# TC-system-config-IT-014 断言明细

> PUT /{id} 编辑成功且忽略请求体 key

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 状态码 | 通过 | 200 | 200 |
| 2 | 响应 key 未变 | 通过 | home.notice_voux3d | home.notice_voux3d |
| 3 | name 新值 | 通过 | 首页公告-改 | 首页公告-改 |
| 4 | value 新值 | 通过 | {'text': '新'} | {'text': '新'} |
| 5 | description 新值 | 通过 | 顶部 | 顶部 |
| 6 | updatedAt 晚于 T0 | 通过 | > 2026-11-10T05:05:30.702162856Z | 2026-11-10T05:05:30.766543963Z |
| 7 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 8 | 步骤4 状态码 | 通过 | 200 | 200 |
| 9 | 步骤4 与 PUT 响应一致 | 通过 | {'key': 'home.notice_voux3d', 'name': '首页公告-改', 'value': {'text': '新'}, 'description': '顶部'} | {'key': 'home.notice_voux3d', 'name': '首页公告-改', 'value': {'text': '新'}, 'description': '顶部'} |
| 10 | 步骤5 totalElements | 通过 | 0 | 0 |
| 11 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- T0(updatedAt)=2026-11-10T05:05:30.702162856Z
- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
