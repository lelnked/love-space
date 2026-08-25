# TC-recommend-list-IT-018 断言明细

用例: POST /api/admin/recommend-lists/{id}/online 人工恢复清单（含下架商户拒绝、成功、幂等） ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 步骤2 M1 下架 200 | ✅ | 200 |
| 2 | 步骤3 含下架商户恢复: 状态码 = 400 | ✅ | 实际 400 |
| 3 | 步骤3 含下架商户恢复: message 为中文 | ✅ | message='清单内存在未上架商户，请先清理后再恢复清单' |
| 4 | 状态码 = 200 | ✅ | 实际 200 |
| 5 | Content-Type 含 application/json | ✅ | application/json |
| 6 | 步骤4 status 仍为 "OFFLINE" | ✅ | OFFLINE |
| 7 | 步骤5 M1 上架 200 | ✅ | 200 |
| 8 | 状态码 = 200 | ✅ | 实际 200 |
| 9 | Content-Type 含 application/json | ✅ | application/json |
| 10 | 步骤6 响应 status="ONLINE" | ✅ | ONLINE |
| 11 | 状态码 = 200 | ✅ | 实际 200 |
| 12 | Content-Type 含 application/json | ✅ | application/json |
| 13 | 步骤7 status 仍为 "ONLINE"（幂等返回详情） | ✅ | ONLINE |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

⚠️ 契约漂移（不判失败）:
- PUT /api/admin/merchants/{id}/online 未登记于 api-spec.json（⚠️ 待补契约，归 merchant 域）

结论: ✅ 通过