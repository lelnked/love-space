# TC-file-IT-014 断言明细

执行日期: 2026-09-04

- [x] 最低断言：image/gif 不命中入参 400「仅支持 png/jpeg/webp/gif 图片」 — 实际 HTTP 200 message=None
- [x] objectKey 匹配 ^images/<uuid>.gif$ — images/01a06b3a-15c1-77a7-8533-2f473b959f44.gif
- [x] 签名与安全令牌字段非空 — []

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/files/upload-credentials 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过