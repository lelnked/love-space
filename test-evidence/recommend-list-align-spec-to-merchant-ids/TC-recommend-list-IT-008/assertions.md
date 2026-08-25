# TC-recommend-list-IT-008 断言明细

用例: PUT /api/admin/recommend-lists/{id} merchantIds 含跨城市商户被拒绝 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 请求契约自检 RecommendListUpdateRequest: {"title": "城市A清单143752", "merchantIds": ["01a0395b-3192-7725-94f5-9416e0c1fdb0"]… | ✅ |  |
| 2 | 跨城市商户: 状态码 = 400 | ✅ | 实际 400 |
| 3 | 跨城市商户: message 为中文 | ✅ | message='商户「商户Mx008143752」不属于清单所属城市，不能加入清单' |
| 4 | 状态码 = 200 | ✅ | 实际 200 |
| 5 | Content-Type 含 application/json | ✅ | application/json |
| 6 | merchants 保持更新前状态 [M1] | ✅ | ['01a0395b-3186-71ee-a151-a2395cd254c0'] |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过