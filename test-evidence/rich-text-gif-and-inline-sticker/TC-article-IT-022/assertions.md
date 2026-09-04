# TC-article-IT-022 断言明细

执行日期: 2026-09-04

- [x] D4K jpeg 4096B → 400 — 实际 400
- [x] D4K jpeg 4096B message 为「图片对象不可用」 — 图片对象不可用
- [x] DSVG svg 1024B → 400 — 实际 400
- [x] DSVG svg 1024B message 为「图片对象不可用」 — 图片对象不可用
- [x] 列表数量未增加 — 144 → 144

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/articles 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过