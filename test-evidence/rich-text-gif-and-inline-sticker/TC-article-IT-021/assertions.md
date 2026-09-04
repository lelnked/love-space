# TC-article-IT-021 断言明细

执行日期: 2026-09-04

- [x] 请求契约自检（ArticleUpsertRequest 必填字段齐全）
- [x] 步骤 1 返回 200 — 实际 200 {"id":"01a06b3a-17fb-74d3-bbba-473d29c9cbe7","image":{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8dd
- [x] admin 详情返回 200 — 实际 200
- [x] admin contentHtml 含 2 个 img — 2
- [x] admin 第一个 img src 与 D1 逐字符相等 — data:image/webp;base64,HEa15H4JaGnjls8xdYC8uRxRfTg
- [x] admin 第二个 img src 为签名 URL — http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bou
- [x] app 详情返回 200 — 实际 200
- [x] app contentHtml 含 2 个 img — 2
- [x] app 第一个 img src 与 D1 逐字符相等 — data:image/webp;base64,HEa15H4JaGnjls8xdYC8uRxRfTg
- [x] app 第二个 img src 为签名 URL — https://test.oss-cn-test.aliyuncs.com/bound/0199aaaa-bbbb-7c
- [x] 步骤 4 PUT 返回 200 — 实际 200 {"id":"01a06b3a-17fb-74d3-bbba-473d29c9cbe7","image":{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0002.png","url":"http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/0199aaaa-bbbb-7ccc-8dd
- [x] 回传后 src 仍与 D1 相等

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/articles 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过