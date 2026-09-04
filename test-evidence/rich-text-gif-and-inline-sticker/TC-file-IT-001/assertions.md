# TC-file-IT-001 断言明细

执行日期: 2026-09-04

- [x] objectKey 匹配 ^images/<uuid>.png$ — images/01a06b3a-10c8-71af-856f-7c7f0556a1f7.png
- [x] 签名/令牌等字段均非空 — []
- [x] image/jpeg 后缀为 jpg
- [x] image/gif 后缀为 gif

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/files/upload-credentials 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过