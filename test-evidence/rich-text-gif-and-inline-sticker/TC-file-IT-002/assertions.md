# TC-file-IT-002 断言明细

执行日期: 2026-09-04

- [x] application/pdf → 400 — 实际 400
- [x] application/pdf 响应 Content-Type 为 application/json
- [x] application/pdf 错误消息为「仅支持 png/jpeg/webp/gif 图片」 — 仅支持 png/jpeg/webp/gif 图片
- [x] application/pdf 响应不含签名/令牌字段 — []
- [x] image/svg+xml → 400 — 实际 400
- [x] image/svg+xml 响应 Content-Type 为 application/json
- [x] image/svg+xml 错误消息为「仅支持 png/jpeg/webp/gif 图片」 — 仅支持 png/jpeg/webp/gif 图片
- [x] image/svg+xml 响应不含签名/令牌字段 — []
- [x] image/bmp → 400 — 实际 400
- [x] image/bmp 响应 Content-Type 为 application/json
- [x] image/bmp 错误消息为「仅支持 png/jpeg/webp/gif 图片」 — 仅支持 png/jpeg/webp/gif 图片
- [x] image/bmp 响应不含签名/令牌字段 — []

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/files/upload-credentials 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过