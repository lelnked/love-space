# TC-activity-IT-026 断言明细

执行日期: 2026-09-04

- [x] 4096 字节 → 400 — 实际 400
- [x] 4096 字节 message 为「图片对象不可用」 — 图片对象不可用
- [x] 3073 字节 → 400 — 实际 400
- [x] 3073 字节 message 为「图片对象不可用」 — 图片对象不可用
- [x] 3072 字节 → 200（边界放行） — 实际 200 {"id":"01a06b3a-171a-73fd-93e3-375a52acb396","images":[{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8
- [x] 详情 src 与 D3072 相等
- [x] 列表仅多出 1 条 — 239 → 240

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/activities 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过