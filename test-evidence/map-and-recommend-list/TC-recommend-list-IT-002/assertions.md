# TC-recommend-list-IT-002 断言明细

- ✅ Step 1 状态码 = 400
- ✅ Step 1 Content-Type 含 application/json
- ✅ Step 1 message 为中文错误信息（"清单标题不能为空"）
- ✅ Step 2 状态码 = 400
- ✅ Step 2 Content-Type 含 application/json
- ✅ Step 2 message 为中文错误信息（"所属城市不能为空"）
- ✅ Step 3 状态码 = 200
- ✅ 城市下清单总数不变（均未创建）

汇总: 通过 8 / 失败 0
