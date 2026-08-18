## Why

二期需求「【精选】推荐信息流管理」拆成两块：§7.1 地图上新推荐已随 `article-and-featured-feed` 交付，§7.2「你的周期活动」当时因两个问题未澄清而整体搁置（tripperclub活动的实体归属、用户周期数据来源）。2026-08-18 两问已定，本变更补齐 §7.2，让精选信息流完整。

客户端目前无法按用户生理周期推荐内容——四个周期列表全靠写死。本变更把它变成运营可配置。

## What Changes

- 新增**周期推荐条目**：一条条目挂一个周期（`MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL`），列表内按 `sortOrder` 升序，带上线/下线开关。
- 条目有三种内容类型，各自关联既有实体、不新建内容模块：
  - `ACTIVITY`（tripperclub活动）→ 关联 `loves_activity`，额外填 活动说明（选填）/推荐说明/banner 图。
  - `ROUTE`（路线体验）→ 关联 `loves_route` 仅供 App 跳转，主标题/副标题/推荐说明/banner 图全部在推荐位手填。
  - `ARTICLE`（周期生活法）→ 关联 `loves_article`，填 主标题（默认带出文章标题、可改）/banner 图。
- admin 端新增 CRUD 接口与后台页面；app 端新增只读接口，**一次性下发四个周期的全部列表**，由客户端按本地周期自选。
- **不分地图**：周期推荐为全局配置，对所有用户生效（沿用 §7.1「配置后对所有用户生效」语义）。
- banner 图比例由运营自控，CMS 不做尺寸校验（沿用 §7.1 已定决策）。

不做：用户周期数据的采集与存储。app 后端无用户体系，周期判定完全在客户端。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `featured`: 现有 spec 只覆盖「地图上新推荐」。新增「周期推荐条目管理」「App 端周期推荐查询」「web 端周期推荐页面」三条 Requirement，与既有地图上新推荐并列、互不影响。

## Impact

**数据库**（admin 端统一管理 schema）
- 新增 `loves_featured_cycle_item` 单表：`type` 区分三种内容，关联 id（`activity_id`/`route_id`/`article_id`）与文案列平铺可空——不建三张表。

**接口**（`contracts/api-spec.json` 需同步）
- admin：`/api/admin/featured-cycle-items/*`（分页列表按周期过滤、详情、创建、更新、上下线、删除）
- app：`/api/app/featured-cycle-items`（只读，返回四个周期分组）
- admin 端需要给三种内容类型提供可选实体的下拉数据——复用既有 `/api/admin/{activities,routes,articles}` 列表接口，不新增选择器接口。

**前端**
- `love-space-web/src/pages/FeaturedCycleItems/`：周期 Tab 列表 + 按类型切换字段的弹窗表单。

**可见性级联**
- 条目上线 **且** 被关联实体自身可见时才在 app 端下发：活动需 `online=true` 且所属城市上架；路线需所属城市上架且其大使 `online=true`；文章需 `online=true`。关联实体被删除时条目不再下发。

**测试域**
- 归入既有 `featured` 域，不新增域；`tests/modules.md` 的 featured 行补充新路径前缀与页面域。
