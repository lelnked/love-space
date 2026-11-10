# TC-operation-log-IT-020 断言明细

> 系统配置新增、编辑、删除后异步产生 system-config 三条留痕

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 201 | 通过 | 201 | 201 |
| 2 | 步骤3 200 | 通过 | 200 | 200 |
| 3 | 步骤4 204 | 通过 | 204 | 204 |
| 4 | 轮询超时前命中三条 | 通过 | create/update/delete 均出现 | ['create', 'delete', 'update'] |
| 5 | create module | 通过 | system-config | system-config |
| 6 | create username | 通过 | admin | admin |
| 7 | update module | 通过 | system-config | system-config |
| 8 | update username | 通过 | admin | admin |
| 9 | delete module | 通过 | system-config | system-config |
| 10 | delete username | 通过 | admin | admin |
| 11 | create target 为 null | 通过 | None | None |
| 12 | update target=C | 通过 | 01a1c3d8-dac8-7d78-a64c-f49e25b59d6e | 01a1c3d8-dac8-7d78-a64c-f49e25b59d6e |
| 13 | delete target=C | 通过 | 01a1c3d8-dac8-7d78-a64c-f49e25b59d6e | 01a1c3d8-dac8-7d78-a64c-f49e25b59d6e |
| 14 | 响应不含 payload | 通过 | 无 payload | ['action', 'createdAt', 'id', 'module', 'target', 'username'] |

## 备注

- 轮询第 1 次命中（存证仅保留最后一次轮询）；新记录以留痕前基线 id 集合差分识别
- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
