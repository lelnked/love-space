# TC-recommend-list-IT-005 断言明细

用例: DELETE /api/admin/recommend-lists/{id} 物理删除含商户关联的清单 ｜ 执行日期: 2026-08-25

| # | 断言 | 结果 | 备注 |
|---|---|---|---|
| 1 | 删除返回 200 | ✅ | 200 |
| 2 | 删除后再查详情: 状态码 = 400 | ✅ | 实际 400 |
| 3 | 删除后再查详情: message 为中文 | ✅ | message='推荐清单不存在：01a0395b-30a4-762a-9d06-546b25f2ae51' |
| 4 | 商户 M1 仍存在（200） | ✅ | 200 |
| 5 | 商户字段不受影响（除签名 URL 外一致） | ✅ |  |

契约说明: api-spec.json 未为 recommend-lists 各 operation 声明 `responses`/响应 schema，响应侧仅做字段集合漂移核对（以 RecommendListDetailResponse 现行字段为基线）；请求侧按 requestBody schema 自检。

结论: ✅ 通过