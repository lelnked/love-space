# TC-recommend-list-IT-002 断言明细

用例: POST /api/admin/recommend-lists 缺少必填项被拒绝 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 缺 title: 状态码 = 400 | ✅ | 实际 400 |
| 2 | 缺 title: message 为中文 | ✅ | message='清单标题不能为空' |
| 3 | 缺 title: Content-Type 含 application/json | ✅ |  |
| 4 | 缺 cityId: 状态码 = 400 | ✅ | 实际 400 |
| 5 | 缺 cityId: message 为中文 | ✅ | message='所属城市不能为空' |
| 6 | 清单均未创建（cityId=A 数量不变） | ✅ | 0->0 |
| 7 | 「无城市清单」未创建 | ✅ | 0 |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过