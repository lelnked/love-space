# TC-recommend-list-IT-004 断言明细

用例: PUT /api/admin/recommend-lists/{id} 修改所属城市需清单内商户同属新城市 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 请求契约自检 RecommendListUpdateRequest: {"title": "改名后的清单", "introduction": "新介绍", "cityId": "01a0395b-2fbd-7ff1-908b-f0… | ✅ |  |
| 2 | 步骤2 L1 改城市: 状态码 = 400 | ✅ | 实际 400 |
| 3 | 步骤2 L1 改城市: message 为中文 | ✅ | message='清单内商户「商户M1004143752」不属于新城市，请先移除后再修改所属城市' |
| 4 | 状态码 = 200 | ✅ | 实际 200 |
| 5 | Content-Type 含 application/json | ✅ | application/json |
| 6 | 步骤3 L1 cityId 仍为 A | ✅ | 01a0395b-2fb3-7847-b29c-6934e43d8b87 |
| 7 | 步骤3 L1 merchants 仍为 [M1] | ✅ | ['01a0395b-2ff5-795d-aa6b-4511a8469d63'] |
| 8 | 请求契约自检 RecommendListUpdateRequest: {"title": "换城市的清单", "cityId": "01a0395b-2fbd-7ff1-908b-f0081eed86fd", "sortOrder… | ✅ |  |
| 9 | 状态码 = 200 | ✅ | 实际 200 |
| 10 | Content-Type 含 application/json | ✅ | application/json |
| 11 | 步骤4 返回 200 | ✅ | 200 |
| 12 | 状态码 = 200 | ✅ | 实际 200 |
| 13 | Content-Type 含 application/json | ✅ | application/json |
| 14 | 步骤5 L2 cityId 为 B | ✅ | 01a0395b-2fbd-7ff1-908b-f0081eed86fd |
| 15 | 步骤5 title="换城市的清单" | ✅ | 换城市的清单 |
| 16 | 步骤5 sortOrder=9 | ✅ | 9 |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过