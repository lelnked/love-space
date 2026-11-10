# TC-system-config-IT-001 断言明细

> POST /api/admin/system-configs 新增成功

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 | 通过 | 201 | 201 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | id 非空 | 通过 | 非空 | 01a1c3d8-d355-7105-9960-46c65ad56e2a |
| 4 | key | 通过 | home.notice_p54wrg | home.notice_p54wrg |
| 5 | name | 通过 | 首页公告 | 首页公告 |
| 6 | value 为对象 | 通过 | {'text': 'hi'} | {'text': 'hi'} |
| 7 | description=null | 通过 | None | None |
| 8 | createdAt 非空 | 通过 | 非空 | 2026-11-10T05:05:29.429009641Z |
| 9 | 契约 SystemConfigItem schema（required/类型） | 通过 | required 齐全、类型匹配 | OK |
| 10 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
