# TC-recommend-list-IT-001 断言明细

- ✅ Step 1 状态码 = 200
- ✅ 请求契约自检：body 满足 RecommendListCreateRequest（title/cityId 必填均在，sortOrder 为 integer）
- ✅ Step 2 状态码 = 200
- ✅ Step 2 Content-Type 含 application/json
- ✅ Step 3 状态码 = 200
- ✅ Step 3 Content-Type 含 application/json
- ✅ 详情 title
- ✅ 详情 introduction
- ✅ 详情 cityId 与提交一致
- ✅ 详情 sortOrder = 3

汇总: 通过 10 / 失败 0
