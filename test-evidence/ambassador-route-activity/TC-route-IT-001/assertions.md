# TC-route-IT-001 断言明细

执行日期: 2026-08-16

- [x] 创建返回 200
- [x] Content-Type 为 application/json
- ℹ️ 请求体已按契约 AmbassadorUpsertRequest 自检（required: avatar,name；tags≤3）通过
- [x] 详情返回 200
- [x] name="小满"
- [x] tags 顺序与提交一致 ["古着","咖啡"]
- [x] online=true
- [x] avatar 为签名 URL（http 开头且含 bound/）
- ℹ️ 契约未声明响应 schema，响应字段按用例预期断言
