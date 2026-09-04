# TC-activity-IT-006 断言明细

执行日期: 2026-09-04

- [x] 请求契约自检（ActivityUpsertRequest 必填字段齐全）
- [x] 请求字段 periods 枚举合法
- [x] 创建返回 200 — 实际 200 {"id":"01a06b3a-163d-7b6d-b7fc-357f9366c3fe","images":[{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8
- [x] 详情返回 200
- [x] 文本部分与提交一致
- [x] 2 个 img 的 src 均为 http 签名 URL — ['http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bou', 'http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bou']
- [x] src 含 bound/ 路径片段（存储层为 bound objectKey）
- [x] 无 img 更新返回 200 — 实际 200 {"id":"01a06b3a-163d-7b6d-b7fc-357f9366c3fe","images":[{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8
- [x] 无 img HTML 原样往返 — <p>纯文本无图</p>

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/activities 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过