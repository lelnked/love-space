# TC-file-IT-007 断言明细

执行日期: 2026-09-04

- [x] 'images/abc.exe' → 400 — 实际 400
- [x] 'images/abc.exe' 消息为「imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）」 — imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）
- [x] 'images/../../etc/passwd.png' → 400 — 实际 400
- [x] 'images/../../etc/passwd.png' 消息为「imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）」 — imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）
- [x] '' → 400 — 实际 400
- [x] '' 消息为「图片不能为空」 — imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）, 图片不能为空
- [x] 三次均未创建数据 — []

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/banners 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）
- 空值实际消息为「imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）, 图片不能为空」：BannerCreateRequest 元素上 @NotBlank 与 @Pattern 同时触发、消息拼接，属既有 DTO 行为（本 change 未改），按「含」判定通过，用例文案或 DTO 是否收紧请人工裁决

**结论**: ✅ 通过