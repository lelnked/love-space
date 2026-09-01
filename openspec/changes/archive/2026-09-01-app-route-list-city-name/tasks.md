## 1. App 后端

- [x] 1.1 `RouteItemResponse` 新增 record 组件 `String cityName`（放在 `sortOrder` 之后、`ambassadorName` 之前），补 javadoc：路线自身城市名，未填写时为 null
- [x] 1.2 `RouteQueryService.list()` 组装列表项时传入 `route.getCityName()`，并更新类/方法注释中「城市信息由 cityName 反查」的口径说明
- [x] 1.3 补 UT/IT 断言，锚定 `route/App 端路线查询#列表项返回路线自身城市名` 与 `route/App 端路线查询#城市表中无同名城市时仍返回路线且 city 为 null`（测试代码加 `@scenario` 注释）

## 2. 契约同步

- [x] 2.1 `contracts/api-spec.json`：`/api/app/routes` get 的 summary 补「列表项含 cityName（路线自身城市名，未填为 null）」
- [x] 2.2 `love-space-app/docs/openapi.json`：`RouteItemResponse` schema 增加 `cityName`（string，nullable），描述与 admin 契约一致

## 3. 验证

- [x] 3.1 跑 app 端 IT：`TC-route-IT-019`、`TC-route-IT-027`（`/run-api-test --change app-route-list-city-name`）
