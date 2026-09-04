# TC-article-IT-014 断言明细

执行日期: 2026-09-04

- [x] 前置文章创建 200 — 实际 200
- [x] 返回 200 — 实际 200 {"id":"01a06b3a-17c7-77c7-ba11-a2e2578b0889","image":{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png","url":"https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png?
- [x] 含图片、标题、副标题字段 — []
- [x] contentHtml 文本与后台保存一致
- [x] img src 为签名 URL — ['https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1401.png?Expires=17885

## ⚠️ 契约漂移 / 备注
- 契约 GET /api/app/articles/{id} 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过