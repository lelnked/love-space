# TC-activity-IT-009 断言明细

执行日期: 2026-09-04

- [x] 前置活动创建 200 — 实际 200
- [x] 返回 200 — 实际 200 {"id":"01a06b3a-1689-743f-8eca-82d15ddc3bf4","images":[{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png","url":"https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.pn
- [x] Content-Type 为 application/json
- [x] 含全部展示字段 — []
- [x] 不含 cityId
- [x] detailHtml 文本与后台保存一致
- [x] img src 为签名 URL — ['https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0901.png?Expires=17885

## ⚠️ 契约漂移 / 备注
- 契约 GET /api/app/activities/{id} 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过