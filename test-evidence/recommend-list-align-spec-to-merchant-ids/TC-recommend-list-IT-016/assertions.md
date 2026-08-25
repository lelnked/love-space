# TC-recommend-list-IT-016 断言明细

用例: PUT /api/admin/recommend-lists/{id} merchantIds 含已下架商户被拒绝 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 前置：Mo 下架返回 200 且 online=false | ✅ | 200 online=False |
| 2 | 请求契约自检 RecommendListUpdateRequest: {"title": "下架校验清单143752", "merchantIds": ["01a0395b-327b-798b-ac7b-e691ea41549d"… | ✅ |  |
| 3 | 已下架商户: 状态码 = 400 | ✅ | 实际 400 |
| 4 | 已下架商户: message 为中文 | ✅ | message='商户「商户Mo016143752」已下架，不能加入清单' |
| 5 | 状态码 = 200 | ✅ | 实际 200 |
| 6 | Content-Type 含 application/json | ✅ | application/json |
| 7 | merchants 不含 Mo | ✅ | [] |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

⚠️ 契约漂移（不判失败）:
- PUT /api/admin/merchants/{id}/online 未登记于 api-spec.json（⚠️ 待补契约，归 merchant 域）

结论: ✅ 通过