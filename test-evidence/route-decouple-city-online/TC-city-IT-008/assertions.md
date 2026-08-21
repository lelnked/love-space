# TC-city-IT-008 断言明细

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1d | 前置：城市上架时 app 列表含该路线 | 1 条 | 1 条（id `01a01fb5-0148-…`） | ✅ |
| 2 | 2 | 城市下架 状态码 / online | 200 / false | 200 / false | ✅ |
| 3 | 3 | app 列表 状态码 | 200 | 200 | ✅ |
| 4 | 3 | 城市下架后列表**仍包含**该路线 | 含该 routeId | 含（1 条） | ✅ |
| 5 | 4 | app 详情 状态码 | 200 | 200 | ✅ |
| 6 | 4 | body `cityName` | 所属城市中文名 | "级联城008" | ✅ |
| 7 | 5a | 大使下线 状态码 / online | 200 / false | 200 / false | ✅ |
| 8 | 5b | 大使下线后列表 | 不含该路线 | `[]` | ✅ |
| 9 | 5c | 大使下线后详情 | 404 | 404，message `route not found: …` | ✅ |
| 10 | 5c | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 11 | — | 契约 schema | `/api/app/routes`、`/api/app/routes/{id}` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
