## Why

两个积压问题一起解决：

1. 同一个 target（同一个活动 / 路线 / 文章）常被运营投放在多个周期下，DB 里是多条 `loves_featured_cycle_item` 记录。当前 app 端每条只下发自身所属的那一个周期，客户端无从知道这个内容还被推荐在别的周期，无法在卡片上打出「黄体期·经期」这类跨周期标签。
2. 关联实体 id 被拆成 `activity_id` / `route_id` / `article_id` 三列，任一条目恒有两列为 null。`type` 已经是判别列，三列并不带来额外信息，却让实体、两端 DTO、web 表单都要写三遍同构的分派逻辑；本次要按 target 聚合周期，三列会让分组 key 逻辑再复杂一层。

两处都是 App 端响应的破坏性变更，合并为一次交付，客户端只需适配一次。

## What Changes

### App 端 `period` 字段改为周期集合

- **BREAKING** `GET /api/app/featured-cycle-items` 响应中的 `period` 字段由单值枚举改为枚举**数组**：不再是「本条目所属周期」，而是「该 target 在全部可下发条目中被投放到的周期集合」，去重后按 `Period` 枚举声明顺序（`MENSTRUAL` / `FOLLICULAR` / `OVULATION` / `LUTEAL`）排序。
- 条目粒度**不变**：同一 target 配了两个周期就仍返回两条，不去重、不合并。两条各自的 `period` 数组内容相同，其余字段（banner / 文案 / 排序位置）仍是各条目自己的配置。
- 查询参数 `period=X` 的过滤语义**不变**：仍按条目自身持久化的周期过滤。因此带 `?period=LUTEAL` 时，返回的条目其 `period` 数组可能是 `["MENSTRUAL","LUTEAL"]`——过滤看的是归属，下发的是标签。
- 排序规则、可见性规则（条目 `online=true` 且关联实体可见）、400 与空数组行为均不变。

### 三个关联 id 列合并为单列 `target_id`

- **BREAKING** 表 `loves_featured_cycle_item` 的 `activity_id` / `route_id` / `article_id` 三列合并为 `target_id uuid NOT NULL`，指向哪张表由既有的 `type` 判别。含数据回填迁移。
- **BREAKING** admin 写接口 `POST/PUT /api/admin/featured-cycle-items` 请求体的 `activityId` / `routeId` / `articleId` 合并为必填的 `targetId`；admin 读接口响应同样合并（`relatedTitle` 保留不变）。
- **BREAKING** app 读接口响应的三个 id 合并为 `targetId`。
- 关联实体存在性校验行为不变：仍按 `type` 分派到对应 repository 校验，错误文案保持「关联活动/路线/文章不存在」口径。
- web 后台表单的交互**不变**（仍是先选类型、再按类型展示对应实体下拉），仅内部字段绑定收敛到单个 `targetId`。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `featured`：
  - 「App 端周期推荐查询」——`period` 从「所属周期」单值改为「该 target 覆盖的周期集合」数组；下发的关联实体 id 收敛为 `targetId`。受影响的既有 Scenario 断言随之修改，并新增跨周期聚合相关 Scenario。
  - 「周期推荐条目管理」——按内容类型的字段约束表中，「关联活动 id / 关联路线 id / 关联文章 id」统一为「关联实体 id（`targetId`）」，存在性校验仍按 `type` 分派。
  - 「web 端周期推荐页面」不变：页面行为与表单交互没有变化，字段绑定收敛属实现细节，无 spec delta。

## Impact

- **数据库**：`loves_featured_cycle_item` 加 `target_id`、回填 `COALESCE(activity_id, route_id, article_id)`、置 NOT NULL、删三列。Liquibase changeset 由 admin 端统一管理（app 端 liquibase 关闭）。索引 `ix_loves_featured_cycle_item_phase (phase, sort_order)` 不受影响。迁移可逆（rollback 加回三列并按 `type` 回填）。
- **接口契约**：`contracts/api-spec.json` 中 `FeaturedCycleItemUpsertRequest` schema 三字段合并为 `targetId`；`/api/app/featured-cycle-items` 的 summary 与 `period` 参数描述改写。
- **love-space-admin**：`FeaturedCycleItem` 实体、`FeaturedCycleItemUpsertRequest`、`FeaturedCycleItemResponse`、`FeaturedCycleItemService` 的类型分派校验。
- **love-space-app**：`FeaturedCycleItem` 实体、`FeaturedCycleItemResponse`（`period` 与 `targetId` 两处）、`FeaturedCycleItemQueryService.feed` 增加按 target 聚合周期的步骤（聚合范围是全部可下发条目，先于 `period`/`type` 过滤计算）。
- **love-space-web**：`src/api/featuredCycleItems.ts` 类型、`src/pages/FeaturedCycleItems/Form.tsx` 的 state 与校验 key 收敛为 `targetId`。
- **App 客户端**（不在本仓库）：需同时适配 `period` 变数组与关联 id 变 `targetId`，一次发版。
- **测试**：`tests/featured/it.md` 新增跨周期聚合用例；既有断言 `period` 单值或三个 id 字段的用例需改写；web 用例行为未变但需回归。
