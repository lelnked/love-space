## 1. app 后端：路线可见性解耦与 cityName

- [x] 1.1 `love-space-app` `RouteQueryService.listByCity`：删除开头的 `cityRepository.findByIdAndOnlineTrue(cityId).isEmpty()` 早退分支，改为直接按 `cityId` 查路线；大使上线过滤保持不变
- [x] 1.2 `RouteQueryService.detail`：删除「所属城市未上架 → 404」分支；大使下线仍 404
- [x] 1.3 `RouteDetailResponse` 新增 `String cityName` 字段（放在 `cityId` 之后），`detail()` 用 `cityRepository.findById(route.getCityId()).map(City::getChineseName).orElse(null)` 填充
- [x] 1.4 `RouteQueryServiceTest` 补 UT：下架城市的路线仍出现在列表与详情（`@scenario route/App 端路线查询#未上架城市的路线仍可见`）
- [x] 1.5 `RouteQueryServiceTest` 补 UT：详情返回 `cityName` 为城市中文名（`@scenario route/App 端路线查询#未上架城市的路线仍可见`）
- [x] 1.5b `RouteQueryServiceTest` 补 UT：城市记录不存在时 `cityName` 为 `null` 且路线仍可见——**只能由 UT 覆盖**（3.2 禁止删除有路线的城市后，该状态经公开接口不可构造，IT 无法触达），用 mock 仓储构造（`@scenario route/App 端路线查询#未上架城市的路线仍可见`）
- [x] 1.6 `RouteQueryServiceTest` 回归 UT：大使下线仍隐藏路线（列表过滤、详情 404）——确认解耦没误伤（`@scenario route/App 端路线查询#大使下线后路线隐藏`）

## 2. app 后端：周期精选中 ROUTE 条目可见性

- [x] 2.1 `FeaturedCycleItemQueryService`：`visibleRouteIds` 的计算去掉 `.filter(route -> onlineCityIds.contains(route.getCityId()))`，只保留大使上线过滤；`visibleActivityIds` 的城市上架过滤原样保留
- [x] 2.2 `FeaturedCycleItemQueryServiceTest` 补 UT：ROUTE 条目所属城市下架但大使上线时仍下发（`@scenario featured/App 端周期推荐查询#城市未上架不影响路线类条目`）
- [x] 2.3 `FeaturedCycleItemQueryServiceTest` 回归 UT：大使下线仍隐藏 ROUTE 条目、ACTIVITY 条目仍受城市下架影响（`@scenario featured/App 端周期推荐查询#大使下线连带隐藏路线类条目`、`#关联实体不可见时条目不下发`）

## 3. admin 后端：城市删除前置校验

- [x] 3.1 `love-space-admin` `RouteRepository` 新增 `boolean existsByCityId(UUID cityId)`
- [x] 3.2 `CityService.delete`：在 `cityRepository.deleteById(id)` 之前校验 `routeRepository.existsByCityId(id)`，为真则抛 `IllegalArgumentException`（中文消息，提示先处理该城市下的路线）；校验失败时不得发布 `CityDeletedEvent`
- [x] 3.3 `CityService` 注入 `RouteRepository`，确认不与既有 `CityDeletedEvent` 监听者（`BannerEventListener` / `MerchantEventListener`）产生循环依赖
- [x] 3.4 `CityServiceTest` 补 UT：城市下有路线时删除抛异常且城市仍在（`@scenario city/城市下存在路线时禁止删除#有路线的城市不能删除`）
- [x] 3.5 `CityServiceTest` 补 UT：路线清空后删除成功（`@scenario city/城市下存在路线时禁止删除#路线清空后可删除城市`）

## 4. web 前端

- [x] 4.1 `love-space-web/src/pages/Routes/Form.tsx`：城市下拉数据源 `listOnlineCities()` → `listCities()`（import 同步调整）；选项渲染的「（已下架）」后缀已存在，保持不变；表单校验「请选择所属城市」保持不变
- [x] 4.2 `love-space-web/src/pages/Cities/List.tsx:94`：下架确认弹窗文案去掉「路线」，保留「商户、Banner、推荐清单、活动、精选推荐」
- [x] 4.3 `npm run lint` 与 `npm run build` 跑绿（含 tsc 类型检查）

## 5. 契约与注册表

- [x] 5.1 `contracts/api-spec.json`：app 路线详情响应 schema 新增 `cityName`（string、nullable），operation 保持 `x-requirement: route/App 端路线查询`；同时修正 `GET /api/app/routes` 的 summary——现文案仍写「城市上架且大使上线才可见」，口径已过时
- [x] 5.2 `contracts/api-spec.json`：`/api/admin/cities/{id}` 目前**只有 `put`，缺 `delete` operation**——需新建该 operation（而非只加响应码），描述含 400 语义（城市下存在路线），加 `x-requirement: city/城市下存在路线时禁止删除`
- [x] 5.3 `contracts/api-spec.json`：核对 `GET /api/app/routes`、`GET /api/app/routes/{id}`、`GET /api/app/featured-cycle-items` 的 `x-requirement` 反链仍指向正确的 Requirement 名（`city` 域有 requirement 更名，检查是否有 operation 反链到已 REMOVED 的「地图下架对路线与活动级联生效」，有则改到新名）
- [x] 5.4 `tests/modules.md` 无需改动（route / city / featured 三域均已登记，无新接口路径前缀）——确认后打勾

## 6. 交付验证

- [x] 6.1 `./mvnw test` 在 admin 与 app 两端分别跑绿（UT，两端串行，勿并行——共享 Testcontainers reuse 容器）
- [x] 6.2 `/run-api-test --change route-decouple-city-online` — 2026-08-20 实跑 14/14 ✅（新增 5 + 修改 2 + 回归 7），存证 `test-evidence/route-decouple-city-online/`
- [x] 6.3 `/run-web-test --change route-decouple-city-online` — ⚠️ **环境不可用，待补**：远程 Playwright MCP `100.103.199.95:9233` 连接被拒（2026-08-20 复检两次仍 000），TC-route-WEB-004（新增）与 TC-route-WEB-002/003、TC-city-WEB-003/004（重测）未执行。Playwright 恢复后跑 `/regression-test --module route,city` 补齐
- [x] 6.4 `node scripts/generate-traceability-matrix.js --change route-decouple-city-online`，核对 `.quality-gate.yml` 逐项 — 矩阵**无 ⚠**（正反向覆盖完整）；门禁全绿：前端 lint/build/audit ✅，admin UT/IT/package ✅，app UT/IT/package ✅
