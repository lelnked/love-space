# TC-merchant-IT-001 断言明细

- ✅ Step 1 状态码 = 200
- ✅ Step 1 Content-Type 含 application/json
- ✅ token 为三段式 JWT
- ✅ Step 2 状态码 = 200
- ✅ Step 2 Content-Type 含 application/json
- ✅ Step 3 状态码 = 200
- ✅ Step 3 Content-Type 含 application/json
- ✅ 详情 recommendReason 逐字一致
- ⚠️ 契约 #/paths/~1api~1admin~1merchants/post 未声明请求/响应 schema（仅 summary），无漂移可判

汇总: 通过 8 / 失败 0
