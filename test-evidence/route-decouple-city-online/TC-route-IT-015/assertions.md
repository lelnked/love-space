# TC-route-IT-015 断言明细

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1a | 创建下架城市 状态码 | 200 | 200 | ✅ |
| 2 | 1a | body `online` | false | false | ✅ |
| 3 | 1c | 在下架城市下创建路线 状态码 | 200 | 200 | ✅ |
| 4 | 2 | app 列表 状态码 | 200 | 200 | ✅ |
| 5 | 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 6 | 2 | 列表包含该路线（城市下架不再过滤） | 含 routeId `01a01fb4-7408-…` | 1 条，id 匹配 | ✅ |
| 7 | 2 | 列表项含 thumbnail 签名 URL / title / ambassadorName | 非空 | 均非空（`路线大使015`） | ✅ |
| 8 | 3 | app 详情 状态码 | 200 | 200 | ✅ |
| 9 | 3 | body `cityName` | "未上线城" | "未上线城" | ✅ |
| 10 | 3 | body `cityId` | = 该城市 id | 一致 | ✅ |
| 11 | 3 | 既有口径字段：images/spots/ambassador | images 1 张签名 URL；spots 按 S1→S2；ambassador 含 name/avatar/tags | 一致 | ✅ |
| 12 | 4a | 城市上架 状态码 / online | 200 / true | 200 / true | ✅ |
| 13 | 4b | 上架后列表 | 与步骤 2 一致（仍 1 条） | 一致 | ✅ |
| 14 | 4c | 上架后详情 | 200 且 `cityName`="未上线城"，字段与步骤 3 一致 | 一致 | ✅ |
| 15 | — | 契约 schema | api-spec.json 中 `/api/app/routes`、`/api/app/routes/{id}` 无 responses schema | 无从比对 | ⚠️ 待补契约（用例本身已标注：需补 `cityName` 字段与 summary「城市上架且大使上线才可见」的口径修正） |

首个失败点: 无
