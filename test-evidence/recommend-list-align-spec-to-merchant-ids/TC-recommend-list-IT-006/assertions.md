# TC-recommend-list-IT-006 断言明细

用例: GET /api/admin/recommend-lists/page 按 sortOrder 升序并支持过滤 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 状态码 = 200 | ✅ | 实际 200 |
| 2 | Content-Type 含 application/json | ✅ | application/json |
| 3 | 返回 3 条 | ✅ | 3 |
| 4 | 按 sortOrder 1→3→5 | ✅ | [1, 3, 5] |
| 5 | 状态码 = 200 | ✅ | 实际 200 |
| 6 | Content-Type 含 application/json | ✅ | application/json |
| 7 | keyword=精选 仅返回标题含「精选」的清单 | ✅ | ['精选清单一143752'] |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过