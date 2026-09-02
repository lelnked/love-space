## Why

活动目前只有一个标题字段，卡片位（尤其周期推荐位）想在标题下补一行短说明时无处可取，只能挪用简介（introduction，长文，不适合卡片）。文章已有 `subtitle` 承担这个角色，活动缺同类字段。

## What Changes

- 活动新增可选文本字段 `subtitle`（副标题），存 `loves_activity.subtitle`，可空、可改、可清空。与 `landscape` 同构，无长度以外的校验。
- admin：`ActivityUpsertRequest` 接受 `subtitle`（非必填），`ActivityDetailResponse` / `ActivityItemResponse` 返回该字段。
- web：活动表单在「活动标题」下方新增「副标题」单行输入框，可留空，编辑时回显。
- app：`GET /api/app/activities` 列表项与 `GET /api/app/activities/{id}` 详情返回 `subtitle`。
- app：`GET /api/app/featured-cycle-items` 的 `target`（`type=ACTIVITY` 形状 `ActivityTarget`）新增 `subtitle`，取自活动实体本身，未填写时为 null（不回落为 title）。
- 非 BREAKING：新增可选字段，既有请求体不带 `subtitle` 仍合法，既有响应字段一个不减。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `activity`: 活动管理、App 端活动查询、web 端活动管理页面三条 requirement 的字段集合各增 `subtitle`
- `featured`: 周期推荐条目 `target` 的 `ACTIVITY` 形状字段集合增 `subtitle`

## Impact

- 数据库：admin 端 Liquibase 新增 changeset，`loves_activity` 加 `subtitle` 列（nullable）
- admin：`Activity` 实体、`ActivityUpsertRequest`、`ActivityDetailResponse`、`ActivityItemResponse`、`ActivityService`
- app：`Activity` 实体、`ActivityItemResponse`、`ActivityDetailResponse`、`ActivityQueryService`、`FeaturedCycleItemTargetResponse.ActivityTarget`、`FeaturedCycleItemQueryService`
- web：`src/pages/Activities/Form.tsx`、`src/api/activities.ts` 类型
- 契约：`contracts/api-spec.json`、`love-space-app/docs/openapi.json`
- 测试：`tests/activity/{it,web}.md`、`tests/featured/it.md`
