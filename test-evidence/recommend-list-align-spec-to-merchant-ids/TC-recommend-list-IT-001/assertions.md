# TC-recommend-list-IT-001 断言明细

用例: POST /api/admin/recommend-lists 创建清单成功 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 请求契约自检 RecommendListCreateRequest: {"title": "江畔约会精选", "introduction": "沿江十家小店", "cityId": "01a0395b-2f20-78d4-b766… | ✅ |  |
| 2 | 状态码 = 200 | ✅ | 实际 200 |
| 3 | Content-Type 含 application/json | ✅ | application/json |
| 4 | 状态码 = 200 | ✅ | 实际 200 |
| 5 | Content-Type 含 application/json | ✅ | application/json |
| 6 | title="江畔约会精选" | ✅ | 江畔约会精选 |
| 7 | introduction="沿江十家小店" | ✅ | 沿江十家小店 |
| 8 | cityId 与提交一致 | ✅ | 01a0395b-2f20-78d4-b766-0b7f3acbf1da |
| 9 | sortOrder=3 | ✅ | 3 |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过