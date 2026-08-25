# TC-city-IT-014 断言明细

| # | 断言层 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 状态码 | POST /api/admin/cities（前置） | 200 | 200 | ✅ |
| 2 | 状态码 | POST /api/admin/ambassadors（前置） | 200 | 200 | ✅ |
| 3 | 状态码 | POST /api/admin/routes（前置） | 200 | 200 | ✅ |
| 4 | body | 路线响应无 cityId 字段（只有自由文本 cityName） | 无 cityId | 无 cityId，cityName="路线地图1787658319" | ✅ |
| 5 | 状态码 | **DELETE /api/admin/cities/{id}**（系统内有路线） | 200，不被拒绝 | 200 | ✅ |
| 6 | body | 删除响应无「存在路线拒绝删除」中文错误 | 无错误体 | 空响应体 | ✅ |
| 7 | 状态码 | GET /api/admin/routes/{id}（删除后） | 200 | 200 | ✅ |
| 8 | body | 路线字段不受影响（title/cityName/ambassadorId/spots） | 与创建一致 | 与创建逐字一致 | ✅ |
| 9 | 响应头 | GET 路线 Content-Type | application/json | application/json | ✅ |
| 10 | 契约 | api-spec.json 中 delete operation 描述 | 与实际行为一致 | summary 仍写「有路线时返回 400 拒绝删除」 | ⚠️ |

**结论**: ✅ 通过（功能断言 1–9 全绿；第 10 条契约漂移同 TC-city-IT-013，不判失败）
