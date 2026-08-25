# TC-city-IT-008 assertions

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 前置：城市上架时列表含路线 | 含 | n=1，含 | 通过 |
| 2 | 前置：详情状态码 | 200 | 200 | 通过 |
| 3 | 城市下架接口状态码 | 200 | 200 | 通过 |
| 4 | 步骤 3 —— 城市下架后列表仍含该路线 | 含 | n=1，含 | 通过 |
| 5 | 步骤 4 —— 城市下架后详情状态码 | 200 | 200 | 通过 |
| 6 | 步骤 5 —— 大使下线后列表不含该路线 | 不含 | n=0 | 通过 |
| 7 | 步骤 5 —— 大使下线后详情状态码 | 404 | 404 | 通过 |
| 8 | 404 响应体口径 | app 全局格式 | 符合 | 通过 |
| 9 | 契约 schema（api-spec.json#/paths/~1api~1app~1routes/get、~1api~1app~1routes~1{id}/get） | 符合 | ⚠️ 契约漂移，见下 | 通过（不判失败） |

结论：✅ 通过。城市下架不再隐藏路线，可见性只由大使上线决定。

## ⚠️ 契约漂移（供人工确认，不判失败）

1. `contracts/api-spec.json#/paths/~1api~1app~1routes/get` 仍声明 `cityId` 查询参数；实际
   `RouteController.list` 的参数为 `cityName`(String) 与 `ambassadorId`(UUID)，无 `cityId`。
2. `contracts/api-spec.json#/paths/~1api~1admin~1routes/post` 的 requestBody 仍把 `cityId`(uuid)
   列为 required；实际 `RouteUpsertRequest` 已改为 `cityName`(String，@NotBlank)，无 `cityId`。

两处均为 change `route-remove-city-id` 落地后 api-spec.json 未同步；本用例步骤文案也同样滞留
`?cityId=` 写法（本次按 `?cityName=` 等价执行）。
