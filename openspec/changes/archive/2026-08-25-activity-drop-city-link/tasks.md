## 1. 数据库迁移（admin 端统一管理 schema）

- [x] 1.1 新增 `love-space-admin/src/main/resources/db/changelog/changes/020-remove-activity-city-id.sql`：`ALTER TABLE loves_activity DROP COLUMN IF EXISTS city_id;` + `DROP INDEX IF EXISTS ix_loves_activity_city;`，rollback 恢复列与索引结构（comment 中写明数据不可恢复）。照 `016-remove-route-city-id.sql` 的格式
- [x] 1.2 在 `db.changelog-master.yaml` include 该 changeset

## 2. admin 后端去 cityId

- [x] 2.1 `modules/activity/entity/Activity.java` 删除 `cityId` 字段与 `@Column(name = "city_id")`
- [x] 2.2 `modules/activity/dto/ActivityUpsertRequest.java` 删除 `cityId` record 组件、`@NotNull` 校验与 javadoc 中的「所属地图（城市）」
- [x] 2.3 `modules/activity/dto/ActivityItemResponse.java`、`ActivityDetailResponse.java` 删除 `cityId` 组件
- [x] 2.4 `modules/activity/service/ActivityService.java`：删 `CityRepository` 导入与构造注入、`create()` 中城市存在性校验、`page()` 的 `cityId` 参数与谓词、`update()` 中「cityId 不可变」逻辑与注释、`toItem/toDetail` 的 cityId 映射
- [x] 2.5 `modules/activity/controller/ActivityController.java`：`page()` 删 `@RequestParam(required=false) UUID cityId` 及相关 javadoc，更新方法注释
- [x] 2.6 补/改 admin 端 UT，标注 `@scenario activity/活动管理#请求体携带 cityId 不影响创建` 与 `@scenario activity/活动管理#活动列表不按城市过滤`

## 3. app 后端改为全局活动列表

- [x] 3.1 `modules/activity/entity/Activity.java` 删除 `cityId` 字段
- [x] 3.2 `modules/activity/repository/ActivityRepository.java`：`findAllByCityIdAndOnlineTrueOrderByCreatedAtDesc` → `findAllByOnlineTrueOrderByCreatedAtDesc`
- [x] 3.3 `modules/activity/service/ActivityQueryService.java`：删 `CityRepository` 导入/字段/构造参数、`listByCity(UUID)` → `listAll()` 且去掉城市上架校验、`detail()` 去掉城市上架校验与 cityId 映射
- [x] 3.4 `modules/activity/dto/ActivityDetailResponse.java` 删除 `cityId` 组件
- [x] 3.5 `modules/activity/controller/ActivityController.java`：`GET /api/app/activities` 删 `@RequestParam UUID cityId`，改调 `listAll()`，更新类级 javadoc
- [x] 3.6 重写 `ActivityQueryServiceTest`，标注 `@scenario activity/App 端活动查询#查询上架城市的活动`、`#下线活动不可见`、`#城市上架状态不影响活动详情可见性`
- [x] 3.7 `modules/featuredcycle/service/FeaturedCycleItemQueryService.java`：活动可见性去掉 `onlineCityIds` 过滤与 `CityRepository` 依赖；同步改 `FeaturedCycleItemQueryServiceTest` 中依赖城市上架的活动用例，标注 `@scenario city/地图下架对路线与活动均不级联#下架城市后 app 端活动仍可见`

## 4. web 前端去掉地图（城市）相关 UI

- [x] 4.1 `pages/Activities/List.tsx` 筛选区：删 `name: "cityId"` 筛选项定义与 `filters.cityId` 的 query 组装
- [x] 4.2 `pages/Activities/List.tsx` 列表：删 `key: "cityId"` 的「所属城市」列、`cityName` useMemo 及为它拉取城市列表的逻辑与相关 import
- [x] 4.3 `pages/Activities/Form.tsx` 表单：删 `cityId` state、编辑回显（`setCityId` 与 `getCity` 调用）、`!cityId` 必填校验与错误文案「请选择所属地图」、提交体中的 `cityId`、「所属地图（城市）」下拉整块渲染与其错误提示节点
- [x] 4.4 清理 `types`/API client 中活动相关类型的 `cityId` 字段，`npm run build` 类型检查通过
- [x] 4.5 `pages/Cities/List.tsx` 下架确认文案删去「活动」，口径改为商户、Banner、推荐清单、精选推荐

## 5. 契约与文档同步

- [x] 5.1 `contracts/api-spec.json`：`/api/admin/activities/page` 删 cityId parameter；`ActivityUpsertRequest` schema 删 cityId 属性与 required 项；admin 详情/列表响应 schema 删 cityId
- [x] 5.2 `contracts/api-spec.json`：`/api/app/activities` get 删必填 cityId parameter 并更新 summary；`/api/app/activities/{id}` get 响应 schema 删 cityId
- [x] 5.3 上述 operation 的 `x-requirement` 反链校对为 `activity/活动管理`、`activity/App 端活动查询`
- [x] 5.4 `二期需求开发文档.md`：删活动章节字段 0「所属地图（城市）」行、第 132 行「活动关联地图（城市）（字段 0）」、验收摘要中活动条目的「关联地图（城市）」表述

## 6. 验证

- [x] 6.1 admin 后端起服务跑迁移，确认 `loves_activity` 无 city_id 列
- [x] 6.2 `./mvnw -Dtest='*IT' test`（admin 与 app 分开跑，不并行）
- [x] 6.3 web 端 `npm run dev -- --host`，人工过一遍活动新增/编辑/列表
