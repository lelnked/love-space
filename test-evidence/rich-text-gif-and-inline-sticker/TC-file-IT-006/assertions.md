# TC-file-IT-006 断言明细

执行日期: 2026-09-04

- [x] 返回 400 — 实际 400
- [x] 消息为「imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）」 — imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）
- [x] banner 未创建 — totalElements=0

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/banners 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过