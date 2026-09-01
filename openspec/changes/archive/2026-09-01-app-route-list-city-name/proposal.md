## Why

App 端路线列表项只有反查城市表得到的 `city` 对象，城市表中没有同名城市时 `city` 为 `null`，列表项就完全拿不到城市名——而路线自身其实存着 `cityName`。同一个字段在详情接口是恒有的（`RouteDetailResponse.cityName`），列表与详情行为不对称，客户端列表页在城市未建档时无法展示所属城市。

## What Changes

- App 端路线列表项（`GET /api/app/routes`）新增 `cityName` 字段：取路线自身的城市名原样返回，与详情同源，不依赖城市表；路线未填城市名时为 `null`。
- 现有 `city` 对象语义不变（反查城市表，无同名城市为 `null`，多条同名取最新创建）。
- 纯新增字段，不改任何既有字段，无 BREAKING。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities
- `route`: app 端路线列表项的响应字段集合增加 `cityName`，使列表与详情在城市名上口径一致。

## Impact

- `love-space-app`：`RouteItemResponse`（新增 record 组件）、`RouteQueryService.list()`（组装时带上 `route.getCityName()`）。
- `contracts/api-spec.json`：路线列表响应 schema 增加 `cityName`。
- `love-space-app/docs/openapi.json`：同步。
- 不涉及 admin、web、数据库 schema、依赖、环境变量。
