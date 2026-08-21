## Context

路线（`loves_route`）通过 `cityId` 归属恰好一个城市，表上无外键，关联一致性由 service 层保证。当前「路线可配置/可见」与「城市是否上架」在三个位置被硬绑：

```
                 ┌──────────────┐
   admin 路线 ──▶ │ loves_route  │ ── cityId（必填、创建后不可变、仅 existsById 校验）
                 └──────┬───────┘ ── ambassadorId（必填）
                        │
  ❶ web Routes/Form.tsx      listOnlineCities()            ← 只能选上架城市
  ❷ app RouteQueryService    findByIdAndOnlineTrue(city)   ← 列表空 / 详情 404
  ❸ app FeaturedCycleItem…   onlineCityIds.contains(...)   ← ROUTE 条目被过滤
```

关键事实：**admin 后端本身从未限制城市上架状态**（`RouteService.create` 只做 `cityRepository.existsById`），所以本次改动集中在 ❶❷❸ 三处查询/取数口径，不触碰写入路径，也没有数据库迁移。

反向依赖「城市上架 → 地图详情页列出该城市全部路线」由 `GET /api/app/routes?cityId=` 承载，逻辑已存在且不需改动。

## Goals / Non-Goals

**Goals:**

- 城市记录存在但处于下架时，运营仍可为其配置路线，且该路线可经周期精选推荐对外投放。
- app 端路线详情提供 `cityName` 文字字段，供 App 展示「xx 城市」。
- 城市上架后，其路线自动出现在地图详情页——无需运营二次搬运。
- 消除 `cityId` 悬空路线的产生途径。

**Non-Goals:**

- 不把路线改成「只存自由文本城市名、不关联城市记录」。那样城市上线后无法自动挂回地图详情页，只能靠名字匹配，脆弱。代价是运营需先建一条（可下架的）城市记录。
- 不改动活动（`ACTIVITY`）的级联口径——活动仍要求所属城市上架。
- 不改动 admin 端路线 CRUD 的写入校验（`cityId` 仍必填、创建后不可变）。
- 不做数据迁移，不加表、不加列（`cityName` 是查询期拼装，不落库）。

## Decisions

### D1：保留 `cityId` 关联，只放开可见性口径

**选择**：路线继续外键式引用城市记录（`cityId`），仅去掉查询期的「城市 online」过滤。

**替代方案**：路线新增 `cityNameText` 自由文本列，彻底断开与城市表的关联。**否决理由**：城市上架后无法把历史路线自动挂回地图详情页，只能按名字模糊匹配；且同名城市、改名场景会静默错配。当前方案的成本仅是「运营先建城市记录」这一步，可接受。

**已定决策（用户 2026-08-20 确认）**：「城市记录先建后下架」的前提对运营不构成障碍。

### D2：`cityName` 在查询期拼装，不落库

`RouteQueryService.detail` 已注入 `CityRepository`，直接 `findById(route.getCityId()).map(City::getChineseName).orElse(null)` 即可。

**为何不落库冗余列**：城市改名后冗余列会失真，需要额外的同步事件；一次主键查询的成本远低于维护一致性的复杂度。列表接口不带 `cityName`——App 的列表入口本来就是「进某个城市的地图详情」，城市名已在上下文里；周期精选信息流走的是 `featured` 自己的 DTO，不复用路线列表项。

### D3：城市删除改为「有路线则拒绝」，而非级联删路线

**选择**：`CityService.delete` 前置校验 `routeRepository.existsByCityId(id)`，有则抛 `IllegalArgumentException`（现有全局异常处理会转 400 + 中文消息）。

**替代方案 A**：监听 `CityDeletedEvent` 级联物理删除该城市下路线。**否决理由**：不可逆——运营误删城市会连带丢掉配好的路线及其地点明细。

**替代方案 B**：什么都不做，允许悬空。**否决理由**：解耦后悬空路线不再因城市不存在而隐身，若被周期精选引用，详情里 `cityName` 会是 `null`，是可见的脏数据。

**已定决策（AI 于 2026-08-20 拍板，按 §4.1「数据迁移优先可加可逆」默认值方向）**：采用「禁止删除」。与既有「大使被路线引用则不能删」（`RouteRepository.existsByAmbassadorId`）完全同构，一行仓储方法 + 一处校验，源头杜绝悬空。用户可随时推翻。

注意：`CityDeletedEvent` 的既有监听者（`BannerEventListener` 下架关联 CITY banner、`MerchantEventListener` 下架该城市商户）不受影响——校验发生在 `deleteById` 之前，拒绝时事件根本不发布。

### D4：web 城市下拉复用既有「（已下架）」渲染

`Routes/Form.tsx` 的下拉选项渲染已写有 `{c.online ? "" : "（已下架）"}`（编辑态回显下架城市时用得上），只需把数据源从 `listOnlineCities()` 换成 `listCities()`。

## 接口与契约映射

| 接口 | 变更 | api-spec.json |
|---|---|---|
| `GET /api/app/routes?cityId=` | 去掉城市上架过滤（响应结构不变） | `x-requirement: route/App 端路线查询` |
| `GET /api/app/routes/{id}` | 响应新增 `cityName`（string, nullable） | 同上，schema 加字段 |
| `GET /api/app/featured-cycle-items` | ROUTE 条目可见性放宽（响应结构不变） | `x-requirement: featured/App 端周期推荐查询` |
| `DELETE /api/admin/cities/{id}` | 新增 400 语义（城市下有路线） | `x-requirement: city/城市下存在路线时禁止删除` |

## 界面实现映射

本次无新增页面与线框，只调整两处既有组件的取数与文案：

| 区域 | 文件 | 变更 |
|---|---|---|
| 路线表单 · 所属城市下拉 | `love-space-web/src/pages/Routes/Form.tsx`（约 L59 数据源、L204 选项渲染） | `listOnlineCities()` → `listCities()`；「（已下架）」后缀渲染已存在，无需改 |
| 地图管理 · 下架确认弹窗 | `love-space-web/src/pages/Cities/List.tsx:94` | 文案去掉「路线」 |

`Routes/Form.tsx` L85 的编辑态兜底（`getCity(d.cityId)` 单查回填）在改用 `listCities()` 后成为冗余——全量列表已含下架城市——但保留无害（应对城市被删的历史数据），本次不动。

## Risks / Trade-offs

- **[存量下架城市的路线突然在 app 端可见]** → 实际入口只有两个：地图详情（城市下架时 App 根本进不去）和周期精选信息流（条目自身有上线开关，运营可控）。不会出现「内容突然冒出来」。
- **[已悬空的历史路线（城市已被删）]** → 改动后 `cityName` 为 `null`，但可见性仍受大使上线约束，且只在被精选条目引用时才可能触达用户。D3 只堵住新增途径，不清理存量。若线上确有此类数据，需单独排查——本 change 不含数据清理脚本。
- **[`city` 域 requirement 改用 REMOVED + ADDED]** → 归档时旧的「地图下架对路线与活动级联生效」整条移出 living specs，新的「地图下架对活动级联生效」加入。`tests/city/` 中引用旧 requirement 名的用例「关联需求」字段必须同步更新，否则追溯矩阵会出现悬空用例。

## Migration Plan

无数据库迁移。部署顺序：admin 与 app 后端可独立发布（互不依赖）；web 依赖 admin 的既有接口，无新接口依赖，可同批或后置发布。

回滚：三处过滤条件与一处删除校验均为纯代码改动，回滚即恢复原行为，不涉及数据形态。

## Open Questions

无。D3 的「禁止删除 vs 级联删除」已按默认值方向拍板并记录在案，用户如不认同可在 apply 前推翻。
