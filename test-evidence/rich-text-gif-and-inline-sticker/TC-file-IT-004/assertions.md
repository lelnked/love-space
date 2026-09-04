# TC-file-IT-004 断言明细

执行日期: 2026-09-04

- [x] 创建返回 200 — 实际 200 {"id":"01a06b3a-1581-7d40-b6aa-9dfe8be01a00","name":"绑定用例-af4f49","positionCode":"home-top","type":"CITY","imageUrls":[{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png","url":"http://love-space-t
- [x] 详情返回 200
- [x] imageUrls[0].id 为 bound/ 前缀且文件名后缀不变 — bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1001.png
- [x] imageUrls[0].url 为签名 URL

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/banners 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过