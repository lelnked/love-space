## Why

路线归属某个地图（城市），当前 web 表单只允许选**已上架**城市，且 app 端仅在城市上架时路线才可见。运营的实际诉求是：城市地图尚未上线时，就要先把该城市的路线内容配好并能对外投放（经由周期精选推荐），路线内容里只需一个文字字段展示「xx 城市」。现行口径把「路线可配置/可见」和「城市是否上架」硬绑在一起，卡住了内容先行的运营节奏。

## What Changes

- **BREAKING（行为口径）**：app 端路线可见性不再要求所属城市上架，只保留「关联爱女大使 `online=true`」这一条。城市下架/未上架不再隐藏路线。
- app 端周期精选推荐信息流中，`ROUTE` 类条目的可见性同步去掉「所属城市上架」条件（`ACTIVITY` 类保持不变，仍要求活动上线且所属城市上架）。
- app 端路线详情响应新增 `cityName` 文字字段（该路线所属城市的中文名），供 App 展示「xx 城市」；城市已被删除时为 `null`。
- web 端路线表单的城市下拉从「仅上架城市」放开为「全部城市」，下架城市带「（已下架）」后缀（组件已支持该后缀渲染）。
- web 端地图下架确认弹窗文案去掉「路线」（下架不再影响路线可见性），保留商户、Banner、推荐清单、活动、精选推荐。
- admin 端删除城市前校验该城市下是否仍有路线，有则拒绝（400 中文业务错误），杜绝 `cityId` 悬空的路线。

**不变的部分**：路线仍归属恰好一个城市（`cityId` 必填、创建后不可变），地图详情页按 `cityId` 列出该城市全部路线的行为不变；admin 端路线 CRUD 本就未限制城市上架状态，无需改动。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `route`：「App 端路线查询」的可见性条件去掉「城市上架」，仅保留「大使上线」；路线详情新增 `cityName` 字段；「web 端大使与路线管理页面」的城市下拉口径改为全部城市。
- `city`：「地图下架对路线与活动级联生效」改为只级联活动，不再级联路线；web 下架确认提示文案相应调整；新增「城市下存在路线时禁止删除」。
- `featured`：「App 端周期推荐查询」中 `ROUTE` 类条目的可见性条件去掉「所属城市上架」。

## Impact

**代码**

- `love-space-app`：`RouteQueryService`（`listByCity` / `detail` 去掉城市上架校验，详情补 `cityName`）、`RouteDetailResponse`（+`cityName`）、`FeaturedCycleItemQueryService`（ROUTE 可见性去掉 `onlineCityIds` 过滤）。
- `love-space-admin`：`CityService.delete`（新增路线引用校验）、`RouteRepository`（+`existsByCityId`）。
- `love-space-web`：`pages/Routes/Form.tsx`（`listOnlineCities` → `listCities`）、`pages/Cities/List.tsx`（下架确认文案）。

**契约**：`contracts/api-spec.json` — app 端路线详情 schema 新增 `cityName`；admin 删除城市接口补 400 语义说明。

**数据库**：无迁移、无数据变更。存量路线全部引用既有城市记录，改动只影响查询期过滤。

**运营前提**：城市地图未上线时，运营需先在地图管理建好该城市记录（可保持下架），再配置其路线。这样城市上架后路线自动挂回地图详情页，无需二次搬运。

**测试**：`tests/route/{it,web}.md`、`tests/city/{it,web}.md`、`tests/featured/it.md` 相关用例需更新；admin/app 两端 UT 跟改。
