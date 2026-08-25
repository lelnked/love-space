# TC-recommend-list-IT-003 断言明细

用例: POST /api/admin/recommend-lists 不传 sortOrder 默认 0 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 请求契约自检 RecommendListCreateRequest: {"title": "默认排序清单", "cityId": "01a0395b-2f93-7bf6-aec2-4e4fcb61a4d2"}… | ✅ |  |
| 2 | 状态码 = 200 | ✅ | 实际 200 |
| 3 | Content-Type 含 application/json | ✅ | application/json |
| 4 | 状态码 = 200 | ✅ | 实际 200 |
| 5 | Content-Type 含 application/json | ✅ | application/json |
| 6 | sortOrder=0 | ✅ | 0 |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过