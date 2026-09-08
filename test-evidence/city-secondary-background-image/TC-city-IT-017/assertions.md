# TC-city-IT-017 断言明细

| # | 断言 | 结果 |
|---|---|---|
| 1 | 状态码 = 400 | ✅ |
| 2 | 响应头 Content-Type 含 application/json | ✅ |
| 3 | body `message` 为中文校验信息，且口径与 backgroundImage 一致（"仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）"） | ✅ |
| 4 | body `path` = `/api/admin/cities`，未创建任何城市 | ✅ |

结论：✅ 通过（4/4）
