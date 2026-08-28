## 1. App 后端字段扩展

- [x] 1.1 `love-space-app` 的 `RouteItemResponse` 新增 `ambassadorNote` 字段（放在 `ambassadorName` 之后），补 javadoc 说明与详情同源、未填为 null
- [x] 1.2 `love-space-app` 的 `AmbassadorView` 新增 `id`（UUID，首位），补 javadoc
- [x] 1.3 `RouteQueryService.list` 映射处传入 `route.getAmbassadorNote()`；`detail` 处构造 `AmbassadorView` 传入 `ambassador.getId()`

## 2. 契约同步

- [x] 2.1 `contracts/api-spec.json`：该文件不登记 responses schema（全文件无 `responses` 节，components 仅存请求体），故在两条 path 的 summary 中注明新字段；响应结构真源为 app 端 openapi.json
- [x] 2.2 `love-space-app/docs/openapi.json` 同步同两处（若该文件由注解生成则重新生成）

## 3. 测试

- [x] 3.1 `RouteQueryServiceTest` 补/改断言：列表项返回 `ambassadorNote`（有值与 null 两种），详情 `ambassador.id` 等于关联大使 id
      —— @scenario `route/App 端路线查询#路线列表返回爱女大使说`、`route/App 端路线查询#路线详情返回大使 id`
- [x] 3.2 `tests/route/it.md` 追加 TC-route-IT-025、TC-route-IT-026（见 test-cases.md），跑 `/run-api-test --change app-route-ambassador-fields`
- [x] 3.3 回归既有路线 IT（TC-route-IT-016~019、024）确认老字段未受影响
