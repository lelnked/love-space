# TC-city-IT-003 断言明细

| # | 断言层 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 状态码 | PUT /api/admin/cities/{id}（editorNote 201 字） | 400 | 400 | ✅ |
| 2 | body | message 为中文校验错误 | 中文提示 | 编辑说长度不能超过 200 个字符 | ✅ |
| 3 | 响应头 | Content-Type | application/json | application/json | ✅ |
| 4 | body | 城市 editorNote 保持原值（长度 200） | 200 | 200 | ✅ |

**结论**: ✅ 通过
