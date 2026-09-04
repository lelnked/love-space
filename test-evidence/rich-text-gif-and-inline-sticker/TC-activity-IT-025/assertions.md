# TC-activity-IT-025 断言明细

执行日期: 2026-09-04

- [x] 请求契约自检（ActivityUpsertRequest 必填字段齐全）
- [x] 请求字段 periods 枚举合法
- [x] 步骤 1 返回 200 — 实际 200 {"id":"01a06b3a-16b0-7f0e-8983-292f842f88a1","images":[{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8
- [x] admin 详情返回 200 — 实际 200
- [x] admin detailHtml 含 2 个 img — 2
- [x] admin 第一个 img src 与 D1 逐字符相等（未签名替换/未改写） — data:image/gif;base64,Vkd5g+xZ4Wcjukp5ZmgZ/7Gdvo5X
- [x] admin 第二个 img src 为签名 URL — http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bou
- [x] admin 段落文本原样
- [x] app 详情返回 200 — 实际 200
- [x] app detailHtml 含 2 个 img — 2
- [x] app 第一个 img src 与 D1 逐字符相等（未签名替换/未改写） — data:image/gif;base64,Vkd5g+xZ4Wcjukp5ZmgZ/7Gdvo5X
- [x] app 第二个 img src 为签名 URL — https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7c
- [x] app 段落文本原样
- [x] 步骤 4 PUT 返回 200 — 实际 200 {"id":"01a06b3a-16b0-7f0e-8983-292f842f88a1","images":[{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8
- [x] 回传后 src 仍与 D1 相等 — ['data:image/gif;base64,Vkd5g+xZ4Wcjukp5ZmgZ/7Gdvo5X']

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/activities 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过