# TC-activity-IT-027 断言明细

执行日期: 2026-09-04

- [x] DSVG → 400 — 实际 400
- [x] DSVG message 为「图片对象不可用」 — 图片对象不可用
- [x] DTXT → 400 — 实际 400
- [x] DTXT message 为「图片对象不可用」 — 图片对象不可用
- [x] DNB → 400 — 实际 400
- [x] DNB message 为「图片对象不可用」 — 图片对象不可用
- [x] 活动未创建（数量不变） — 240 → 240
- [x] PUT svg → 400「图片对象不可用」 — 实际 400 图片对象不可用
- [x] 详情 detailHtml 保持更新前内容（事务回滚）

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/activities 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过