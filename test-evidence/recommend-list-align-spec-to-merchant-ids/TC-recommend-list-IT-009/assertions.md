# TC-recommend-list-IT-009 断言明细

用例: PUT /api/admin/recommend-lists/{id} merchantIds 重复商户被拒绝 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 请求契约自检 RecommendListUpdateRequest: {"title": "重复校验清单143752", "merchantIds": ["01a0395b-31cb-7446-a097-7cc6383f989d"… | ✅ |  |
| 2 | 重复商户: 状态码 = 400 | ✅ | 实际 400 |
| 3 | 重复商户: message 为中文 | ✅ | message='同一商户不能重复添加到清单' |
| 4 | 状态码 = 200 | ✅ | 实际 200 |
| 5 | Content-Type 含 application/json | ✅ | application/json |
| 6 | merchants 保持更新前状态 [M2] | ✅ | ['01a0395b-31d7-7606-b558-a0484f406edd'] |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过