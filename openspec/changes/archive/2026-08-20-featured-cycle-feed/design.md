## Context

二期需求 §7.2「你的周期活动」，是精选信息流的第二块。§7.1「地图上新推荐」已由 `loves_featured_item` + `FeaturedItemService`/`FeaturedItemQueryService` 交付，本变更沿用同一套骨架（无外键、存在性在 service 校验、图片走 `ObjectKeyValidator` + `ImageUrlSigner`、app 端内存过滤级联可见性）。

当时搁置的两个阻塞点已于 2026-08-18 澄清：
- 「tripperclub活动」= 现有活动管理实体，不新建内容模块；
- 用户周期数据：app 后端**没有用户体系**（`love-space-app/modules/` 下无 user 模块，仅 API-key 认证），后端拿不到用户当前周期。

现有可复用实体：`loves_activity`（已带 `periods` jsonb）、`loves_route`（含 `ambassadorId`、`cityId`）、`loves_article`（含 `online`、`title`）。

## Goals / Non-Goals

**Goals:**
- 运营可为四个周期各自配置一份推荐列表，内容取自已有的活动/路线/文章，不重复录入内容。
- app 端一个接口拿到四个周期的全部可见条目，客户端本地判周期后自行选取。
- 复用 §7.1 已建立的模块骨架与图片链路，不引入新依赖、不新建内容模块。

**Non-Goals:**
- 用户周期数据的采集、存储与判定——完全在客户端，后端不感知用户身份。
- 周期推荐按地图（城市）分维度——本期全局配置。
- 为三种内容类型新建专用的下拉选择器接口——复用 admin 既有列表接口。
- 富文本、图片比例校验。

## Decisions

### D1：单表 + `type` 判别 + 平铺可空列

`loves_featured_cycle_item` 一张表承载三种内容类型：

```
id, phase, type, sort_order, online,
activity_id, route_id, article_id,        -- 三者恰有一个非空，由 type 决定
title, subtitle, description, note,       -- 文案列，按 type 取用
banner, created_at, updated_at
```

按 type 的列使用矩阵：

| type | 关联列 | title | subtitle | description（推荐说明） | note（活动说明） |
|---|---|---|---|---|---|
| `ACTIVITY` | `activity_id` | — | — | 必填 | 选填 |
| `ROUTE` | `route_id` | 必填 | 必填 | 必填 | — |
| `ARTICLE` | `article_id` | 必填 | — | — | — |

**为什么不是三张表**：三张表要三套 Entity/Repository/Service/Controller/前端页面，而三者共享周期、排序、上下线、banner 这四个核心维度，且 app 端必须把它们混在同一个周期列表里按 sortOrder 排序——分表后每次查询都要三查一归并。单表让排序天然成立。

**为什么不是 jsonb 存 payload**：`loves_activity` 用 jsonb 存的是真正的不定长数组（images/tags/itinerary）。这里字段是固定的 7 列、且 `activity_id` 等要参与关联查询与存在性校验，平铺可空列可读、可索引、迁移可逆。

**代价**：类型与列的约束靠应用层保证，DB 层不阻止「type=ARTICLE 却写了 route_id」。接受——项目既有做法就是无外键、约束在 service（见 `FeaturedItemService.create`）。

### D2：类型分派校验放在 service，不用 Bean Validation 分组

`FeaturedCycleItemUpsertRequest` 是一个宽 record，所有字段声明为可空；`@Size` 之类的长度约束仍走注解，**必填性**由 service 的 `switch (type)` 分派校验，同时校验关联实体存在性并把不属于该类型的字段置空后落库。

否决 Bean Validation 分组：分组要在 Controller 上按运行时 type 选组，Spring 的 `@Validated(Group.class)` 是编译期常量，做不到；硬上要写自定义 `ConstraintValidator` + `@GroupSequenceProvider`，比一个 switch 长十倍。既有 `MerchantService`、`RecommendListService` 也都是在 service 里抛 `IllegalArgumentException` 带中文消息。

### D3：周期与类型创建后不可变

与 §7.1 的 `cityId` 不可变同构。更新请求体不含 `phase`/`type`，传了也忽略。理由：改周期等于换列表位置、改类型等于换一套字段，语义上是「删了重建」，允许原地改会让字段残留（如 ACTIVITY→ARTICLE 后 `description` 留着）。

### D4：app 端可见性在内存过滤，不落库、不写 SQL join

沿用 `FeaturedItemQueryService` 的 ponytail 模式：一次性捞可见的活动/路线/文章 id 集合，再过滤条目。

三条级联链：
- `ACTIVITY` → 活动 `online=true` **且** 活动所属城市上架
- `ROUTE` → 路线所属城市上架 **且** 路线的爱女大使 `online=true`（与 `RouteQueryService` 一致）
- `ARTICLE` → 文章 `online=true`

关联实体已被删除 → id 不在可见集合里 → 自然过滤掉，无需额外处理。

数据量是运营配置级（四个周期各几条），内存过滤足够；条目量级涨到需要 join 时再说。

### D5：响应按周期分组，四个键恒在

app 端返回 `{"MENSTRUAL": [...], "FOLLICULAR": [...], "OVULATION": [...], "LUTEAL": [...]}`，空周期返回空数组而不是缺键——客户端不必写空值判断。

组内排序：`sortOrder` 升序，同序号按 `createdAt` 倒序（与 §7.1 的倒序默认一致）。

条目下发字段随 type 变化，但统一保留 `type` 与关联实体 id，跳转由 App 端自行决定（与 §7.1 已定口径一致）。

### D6：前端「先选类型、再动态渲染字段」

`FeaturedCycleItems/List.tsx`（周期 Tab + DataTable）+ `Form.tsx`（弹窗）。表单以 `type` 为受控开关切换字段块；三个实体下拉分别调既有的 `/api/admin/activities/page`、`/api/admin/routes/page`、`/api/admin/articles/page`，不新增选择器接口。

`ARTICLE` 类型选中文章后把文章标题写入 `title` 输入框（可再改）——仅前端行为，后端只存最终提交值。

### D7：不关联地图（城市）

§7.1 原文「配置后对所有用户生效展示」，§7.2 未提地图维度，故按全局配置实现。将来若要按地图过滤，是 `ALTER TABLE ADD COLUMN city_id`，可加可逆，不构成返工风险。

注意：条目本身不带城市，但被关联的活动/路线**自身**属于某个城市，其城市下架仍会让条目从 app 端消失（见 D4）。

## 接口契约

已同步写入 `contracts/api-spec.json`（operation 均带 `x-requirement` 反链）：

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/featured-cycle-items/page` | 分页，`phase`/`type` 可选过滤，`sortOrder` 升序 |
| GET | `/api/admin/featured-cycle-items/{id}` | 详情 |
| POST | `/api/admin/featured-cycle-items` | 创建（`phase`/`type` 必选） |
| PUT | `/api/admin/featured-cycle-items/{id}` | 更新（`phase`/`type` 忽略） |
| PUT | `/api/admin/featured-cycle-items/{id}/online` | 上下线，复用 `OnlineStatusRequest` |
| DELETE | `/api/admin/featured-cycle-items/{id}` | 物理删除 |
| GET | `/api/app/featured-cycle-items` | 只读，返回四周期分组 |

## 界面实现映射

| 线框区域 | 实现 |
|---|---|
| 顶部四周期 Tab | `pages/FeaturedCycleItems/List.tsx` — 复用 `components/ui/tabs`（若无则本地 `<nav>` + 受控 state），Tab 值写入列表请求的 `phase` 参数 |
| 列表表格（banner/类型/标题/关联实体/排序号/状态/操作） | 同上 — 复用既有 DataTable 与 `Badge`/`Switch` 组件，同 `FeaturedItems/List.tsx` |
| 新增/编辑弹窗 | `pages/FeaturedCycleItems/Form.tsx` — 复用既有 Modal + 表单控件；类型选择器为顶部固定块，其下按 type 渲染字段块 |
| banner 上传 | 复用既有图片上传组件（同 `FeaturedItems`） |
| 删除确认 | 复用既有确认弹窗 |
| 侧栏入口 | `layout/AppSidebar.tsx` 在「精选推荐」下方加「周期推荐」 |

（本变更未跑 `requirement-breakdown-fullstack`，无 `ui-spec.md` ASCII 线框可粘贴；界面结构以本表与 spec 的 web 端 Scenario 为准。）

## Risks / Trade-offs

- **类型与列的一致性靠应用层** → service 的 `apply()` 统一按 type 清空无关列，UT 覆盖「切类型不残留」；DB 无约束是既有项目口径的延续，不单独破例。
- **关联实体被删除后条目变成静默隐身**，运营在后台仍看得到这条配置、但 app 端不显示 → admin 列表的「关联实体」列在实体查不到时显示「已删除」标记，让运营能自查。
- **app 端一次下发四个周期的全部数据**，比按周期查更费带宽 → 运营配置级数据量（预计每周期个位数），换来后端零用户状态依赖，值得。
- **周期以字符串存储**而非 DB 枚举 → 与 `loves_activity.periods` 的 jsonb 字符串数组保持同一套周期取值，避免两处枚举定义漂移。

## Migration Plan

单条 Liquibase changeset `014-create-featured-cycle-item.sql`，纯建表 + 两个索引（`phase`、`sort_order`），`--rollback DROP TABLE`。无数据迁移、无既有表改动，可独立回滚。

## Open Questions

无。三个实体归属决策已于 2026-08-18 确认（见 proposal），其余按 §7.1 已定口径沿用。
