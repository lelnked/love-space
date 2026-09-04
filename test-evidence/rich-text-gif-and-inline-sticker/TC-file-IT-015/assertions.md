# TC-file-IT-015 断言明细

执行日期: 2026-09-04

- [x] gif key 创建返回 200 — 实际 200 {"id":"01a06b3d-21a9-74db-b9aa-9f0e53821ca7","name":"gif绑定-6e6b87","positionCode":"home-top","type":"CITY","imageUrls":[{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif","url":"http://love-space-
- [x] imageUrls[0].id 为 bound/...1501.gif（gif 后缀保持） — bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif
- [x] url 非空
- [x] svg key → 400 — 实际 400
- [x] svg 消息为「imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）」 — imageUrls 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）
- [x] PUT 返回 200 — 实际 200 {"id":"01a06b3d-21a9-74db-b9aa-9f0e53821ca7","name":"gif绑定-6e6b87","positionCode":"home-top","type":"CITY","imageUrls":[{"id":"bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif","url":"http://love-space-
- [x] key 保持不变（不追加二次 bound/） — bound/0199aaaa-bbbb-7ccc-8ddd-eeeeffff1501.gif

## ⚠️ 契约漂移 / 备注
- 契约 POST /api/admin/banners 未声明 responses/响应 schema，响应 schema 校验无基准（仅按用例字段断言）

**结论**: ✅ 通过