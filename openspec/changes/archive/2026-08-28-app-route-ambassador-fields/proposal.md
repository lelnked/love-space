## Why

App 客户端在路线列表页要展示「爱女大使说」文案，在路线详情页要凭大使 id 跳转大使名下路线列表（`GET /api/app/routes?ambassadorId=`），但当前两个接口都拿不到：列表项没有 `ambassadorNote`，详情的 `ambassador` 对象只有名称、头像、标签，没有 id。两个字段在 admin 后台均已存在（`Route.ambassadorNote`、`RouteDetailResponse.ambassadorId`），App 端属于漏出，非新增数据。

## What Changes

- `GET /api/app/routes` 列表项新增 `ambassadorNote`（取路线自身的 `Route.ambassadorNote`，与详情同源；无值时为 `null`）。
- `GET /api/app/routes/{id}` 详情的 `ambassador` 对象新增 `id`（关联大使 UUID）。
- 路线与大使的可见性规则、排序、过滤参数一律不变；两处均为纯字段扩展，老字段位置与语义不动，非 BREAKING。
- 不在本次范围：`GET /api/app/featured-cycle-items` 中 ACTIVITY 类型的 `title`/`subtitle`。经核对 admin 后台对 ACTIVITY 类型本就不采集这两项（web 表单无输入、admin 服务层显式置 null），App 端返回 `null` 即为与后台对齐的正确行为，无数据源可补。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `route`: 「App 端路线查询」需求补充响应字段约定——列表项含 `ambassadorNote`，详情 `ambassador` 对象含 `id`。

## Impact

- 代码：`love-space-app` 的 `RouteItemResponse`、`AmbassadorView`、`RouteQueryService`（列表映射需要按大使聚合的现有逻辑之外，直接读路线字段；详情处补大使 id）。
- 契约：`contracts/api-spec.json` 与 `love-space-app/docs/openapi.json` 中两个路线响应 schema。
- 测试：`tests/route/it.md` 增补两条 IT 场景；app 端既有单测 `RouteQueryServiceTest` 断言需同步。
- 无 DB schema 变更、无依赖变更、无环境变量变更；web 与 admin 端不改。
