# TC-recommend-list-IT-007 断言明细

用例: PUT /api/admin/recommend-lists/{id} merchantIds 整体替换本城市商户并按数组顺序回显 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 请求契约自检 RecommendListUpdateRequest: {"title": "顺序清单143752", "merchantIds": ["01a0395b-3140-7a09-a1eb-103c426caf66", … | ✅ |  |
| 2 | 状态码 = 200 | ✅ | 实际 200 |
| 3 | Content-Type 含 application/json | ✅ | application/json |
| 4 | 状态码 = 200 | ✅ | 实际 200 |
| 5 | Content-Type 含 application/json | ✅ | application/json |
| 6 | merchants 顺序严格为 [M2, M1] | ✅ | 实际 ['01a0395b-3140-7a09-a1eb-103c426caf66', '01a0395b-3133-7f32-8454-673423a9ba5d'] (M1=01a0395b-3133-7f32-8454-673423a9ba5d, M2=01a0395b-3140-7a09-a1eb-103c426caf66) |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过