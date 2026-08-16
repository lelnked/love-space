# TC-recommend-list-IT-005 断言明细

- ✅ Step 1 状态码 = 200
- ✅ Step 2 状态码 = 200
- ✅ Step 3 状态码 = 200（删除成功）
- ✅ Step 4 状态码 = 400
- ✅ Step 4 Content-Type 含 application/json
- ✅ message 为中文业务错误（"推荐清单不存在：01a00b3f-8347-7d61-bc7d-225923c4ef4c"）
- ✅ Step 5 状态码 = 200
- ✅ M1 name 不受影响
- ✅ M1 recommendReason 不受影响

汇总: 通过 9 / 失败 0
