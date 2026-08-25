# TC-recommend-list-IT-010 断言明细

用例: PUT /api/admin/recommend-lists/{id} merchantIds 去掉商户即移除且不影响商户本身 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 请求契约自检 RecommendListUpdateRequest: {"title": "移除清单143752", "merchantIds": ["01a0395b-3211-725c-9216-7636c7375ecf", … | ✅ |  |
| 2 | 状态码 = 200 | ✅ | 实际 200 |
| 3 | Content-Type 含 application/json | ✅ | application/json |
| 4 | 步骤2 回显 [M1, M2] | ✅ | ['01a0395b-3211-725c-9216-7636c7375ecf', '01a0395b-321e-704f-b3dc-fb2bad5d8871'] |
| 5 | 请求契约自检 RecommendListUpdateRequest: {"title": "移除清单143752", "merchantIds": ["01a0395b-321e-704f-b3dc-fb2bad5d8871"]}… | ✅ |  |
| 6 | 状态码 = 200 | ✅ | 实际 200 |
| 7 | Content-Type 含 application/json | ✅ | application/json |
| 8 | 状态码 = 200 | ✅ | 实际 200 |
| 9 | Content-Type 含 application/json | ✅ | application/json |
| 10 | 步骤4 merchants 仅含 M2 | ✅ | ['01a0395b-321e-704f-b3dc-fb2bad5d8871'] |
| 11 | 步骤5 M1 仍存在（200） | ✅ | 200 |
| 12 | 步骤5 M1 字段不受影响 | ✅ |  |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过