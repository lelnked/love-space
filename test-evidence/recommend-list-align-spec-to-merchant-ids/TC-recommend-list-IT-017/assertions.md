# TC-recommend-list-IT-017 断言明细

用例: POST /api/admin/recommend-lists status 默认 ONLINE 且可带 status/merchantIds 创建 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 请求契约自检 RecommendListCreateRequest: {"title": "默认上架清单", "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519"}… | ✅ |  |
| 2 | 状态码 = 200 | ✅ | 实际 200 |
| 3 | Content-Type 含 application/json | ✅ | application/json |
| 4 | 状态码 = 200 | ✅ | 实际 200 |
| 5 | Content-Type 含 application/json | ✅ | application/json |
| 6 | id1 status="ONLINE" | ✅ | ONLINE |
| 7 | id1 merchants 为空数组 | ✅ | [] |
| 8 | 请求契约自检 RecommendListCreateRequest: {"title": "下架带商户清单", "cityId": "01a0395b-32c0-71a0-9a5f-24d14cb39519", "status":… | ✅ |  |
| 9 | 状态码 = 200 | ✅ | 实际 200 |
| 10 | Content-Type 含 application/json | ✅ | application/json |
| 11 | 状态码 = 200 | ✅ | 实际 200 |
| 12 | Content-Type 含 application/json | ✅ | application/json |
| 13 | id2 status="OFFLINE" | ✅ | OFFLINE |
| 14 | id2 merchants 仅含 M1 | ✅ | ['01a0395b-32c9-7cb9-b756-7a8ca2673460'] |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过