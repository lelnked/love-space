# TC-merchant-IT-004 断言明细

- ✅ Step 1 状态码 = 400
- ✅ Step 1 Content-Type 含 application/json
- ✅ Step 1 message 为中文错误信息（"推荐理由长度不能超过 2000 个字符"）
- ✅ Step 2 状态码 = 200
- ✅ 同名商户查询 totalElements = 0（未创建）

汇总: 通过 5 / 失败 0
