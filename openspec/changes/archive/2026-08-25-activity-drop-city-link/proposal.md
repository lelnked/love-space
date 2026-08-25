## Why

二期需求把活动定义为「关联地图（城市）」，实现上 `loves_activity.city_id` 成了必填且创建后不可变的维度，App 端也只能按地图查活动。产品侧现已确认活动不属于任何单一地图，是全局内容——这层关联既限制了运营（一个活动只能挂一个地图），又让 App 端无法展示全量活动。现在把它拆掉。

## What Changes

- **BREAKING** admin 端活动创建/更新请求移除 `cityId`：不再必填、不再校验城市存在性、不再「创建后不可变」。
- **BREAKING** admin 端活动列表 `GET /api/admin/activities/page` 移除 `cityId` 过滤参数；活动详情/列表响应不再返回 `cityId`。
- **BREAKING** app 端 `GET /api/app/activities` 移除必填的 `cityId` 查询参数，改为返回**全部上线活动**（创建时间倒序）；活动可见性只看活动自身 `online`，不再受所属城市上架状态影响。
- **BREAKING** app 端活动详情 `GET /api/app/activities/{id}` 移除响应中的 `cityId`，并去掉「所属城市未上架 → 404」这条判定。
- web 端活动管理页移除「所属地图（城市）」表单项与列表筛选、列表「所属城市」列。
- **BREAKING（数据不可逆）** Liquibase 迁移直接 `DROP COLUMN loves_activity.city_id`（连同索引 `ix_loves_activity_city`），已有活动的地图归属永久丢失。此为用户明确确认的决策。
- **BREAKING** 精选信息流（app 端 `FeaturedCycleItemQueryService`）中活动条目的可见性去掉「所属城市上架」这一条，只看活动自身 `online`。
- **BREAKING** 城市（地图）下架的级联口径去掉活动：living spec 中 city 域的「地图下架对活动级联生效」需求作废，web 端下架确认弹窗文案删去「活动」。
- 二期需求开发文档中活动章节（字段 0「所属地图（城市）」及验收摘要相应条目）同步修订。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `city`: 「地图下架对活动级联生效」整条 REMOVED（其中仍成立的「路线不级联」由新需求「地图下架对路线与活动均不级联」承接）；「地图下架对精选推荐级联生效」补充「活动条目不受城市下架影响」，确认弹窗文案口径去掉活动。
- `activity`: 三条需求全部变更——「活动管理」去掉所属地图字段及其必填/不可变约束；「App 端活动查询」由「按城市查」改为「查全部上线活动」，可见性判定去掉城市上架条件；「web 端活动管理页面」去掉城市表单项、筛选与列表列。

## Impact

**admin 后端** `love-space-admin/src/main/java/com/loves/space/modules/activity/`
- `entity/Activity.java`（删 `cityId` 字段）、`dto/ActivityUpsertRequest.java`、`dto/ActivityItemResponse.java`、`dto/ActivityDetailResponse.java`
- `service/ActivityService.java`（删 `CityRepository` 依赖、城市存在性校验、`page()` 的 cityId 谓词、更新时的不可变逻辑）、`controller/ActivityController.java`

**app 后端** `love-space-app/src/main/java/com/space/app/modules/activity/`
- `entity/Activity.java`、`repository/ActivityRepository.java`（`findAllByCityIdAndOnlineTrue...` → `findAllByOnlineTrueOrderByCreatedAtDesc`）
- `service/ActivityQueryService.java`（删 `CityRepository` 依赖与两处城市上架校验）、`controller/ActivityController.java`、`dto/ActivityDetailResponse.java`
- 现有 UT `ActivityQueryServiceTest` 需同步改写

**web 前端** `love-space-web/src/pages/Activities/List.tsx`（筛选项 + 列 + `cityName` 查表）、`Form.tsx`（`cityId` state / 校验 / 下拉 / 提交体）

**数据库** `love-space-admin/src/main/resources/db/changelog/changes/` 新增迁移，drop `loves_activity.city_id` 及其索引

**契约** `contracts/api-spec.json`：`/api/admin/activities/page`、`/api/admin/activities`、`/api/admin/activities/{id}`、`/api/app/activities`、`/api/app/activities/{id}`、`ActivityUpsertRequest` schema

**文档** `二期需求开发文档.md`（活动章节字段 0、第 132 行、验收摘要第 201 行）

**app 后端（精选）** `love-space-app/.../modules/featuredcycle/service/FeaturedCycleItemQueryService.java`：活动可见性去掉 `onlineCityIds` 过滤（连同 `CityRepository` 依赖）

**web 前端（地图）** `love-space-web/src/pages/Cities/List.tsx` 下架确认文案删去「活动」

**不受影响**：`loves_featured_cycle_item.activity_id` 关联本身（只引用活动 id，无需改动）；城市删除时的路线校验。

> 注：精选可见性与 city 域级联口径这两项，是实现阶段发现的连带影响，已在 apply 过程中补入本提案与 `specs/city/spec.md`。
