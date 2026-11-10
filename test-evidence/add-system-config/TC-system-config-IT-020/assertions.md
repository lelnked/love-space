# TC-system-config-IT-020 断言明细

> GET /api/app/system-configs/{key} 不存在返回 404

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 | 通过 | 404 | 404 |
| 2 | 错误体 结构 {status,error,message,path} | 通过 | 含四字段 | ['error', 'message', 'path', 'status'] |
| 3 | 错误体 message | 通过 | 配置不存在 | 配置不存在 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
