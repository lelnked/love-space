# TC-system-config-IT-005 断言明细

> POST key 格式与长度边界

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | key 大写与连字符(11) 状态码 | 通过 | 400 | 400 |
| 2 | key 数字开头(4) 状态码 | 通过 | 400 | 400 |
| 3 | key 1 位(1) 状态码 | 通过 | 400 | 400 |
| 4 | key 65 位(65) 状态码 | 通过 | 400 | 400 |
| 5 | 不含 key 状态码 | 通过 | 400 | 400 |
| 6 | 非法 key 未创建（1abc） | 通过 | 无 1abc | 无 |
| 7 | 65 位 key 未创建 | 通过 | 无 | 无 |
| 8 | 2 位 key 状态码 | 通过 | 201 | 201 |
| 9 | 2 位 key 回显 | 通过 | ka | ka |
| 10 | 64 位 key 状态码(len=64) | 通过 | 201 | 201 |
| 11 | 64 位 key 回显 | 通过 | k_edzhlaridm.zwd78warcoj11pazsyy728trdor1brilcilflfe3cyl3361mfdp | k_edzhlaridm.zwd78warcoj11pazsyy728trdor1brilcilflfe3cyl3361mfdp |
| 12 | 收尾删除 204 | 通过 | 204 | 204 |
| 13 | 收尾删除 204 | 通过 | 204 | 204 |

## 备注

- 契约 schema：admin/app system-config operation 均未声明 `responses`（仅 description 文字），按 components.schemas 中 SystemConfigItem / AppSystemConfig 及 description 校验；契约缺 responses 声明属 ⚠️ 契约漂移（文档层），不判失败。
