# Design: recommend-list-city-merchant-cascade

## Context

现有代码约定（沿用，不新造）：admin/app 双后端同栈分离；实体继承 `BaseAuditEntity`，无外键、列名 snake_case；Liquibase formatted-SQL 落在 admin 端 `db/changelog/changes/*.sql`；表前缀 `loves_`；校验错误信息中文；web 端列表用 `DataTable`、删除用自定义确认弹窗、表单 `noValidate` 统一校验风格。

## Goals / Non-Goals

- Goals：推荐清单创建/编辑支持城市-商户级联选择；商户后期下架时级联下架相关推荐清单；app 端仅返回 `ONLINE` 清单。
- Non-Goals：城市/商户基础字段变更（沿用既有 spec）；路线/大使/活动/文章/精选（不属本 change）；清单物理删除口径（沿用既有）。

## Decisions（已定决策）

1. **RecommendList 新增 `status` 枚举**：`ONLINE` / `OFFLINE`，默认 `ONLINE`；列名 `status`，`varchar` 或等效枚举，由 DBA/ Liquibase 口径统一。
2. **cityId 创建后可修改**：与既有 map-and-recommend-list 决策 4 相反；修改城市时已有商户若不属于新城市，保存时返回 400 中文提示，要求运营先清理。
3. **商户下架级联动作**：商户状态变更为“已下架”时，后台同步查询所有 `status = ONLINE` 且包含该商户的推荐清单，批量置为 `OFFLINE`；变更原因写入审计字段或独立备注字段（沿用既有审计口径即可，不新造表）。
4. **恢复清单需人工复核**：商户恢复后不自动恢复清单；运营手动将清单置回 `ONLINE` 时，后端校验该清单当前是否还存在已下架商户，存在则拒绝并返回 400 中文提示。
5. **app 端清单可见性靠 `status = ONLINE` 过滤**：不依赖城市下架状态；若城市本身已下架，清单仍应为 `OFFLINE`（被 city 级联或本 change 级联均可），app 端查询统一加 `WHERE status = 'ONLINE'`。
6. **商户下拉过滤“未下架”**：创建/编辑表单的商户选项仅展示所选城市且 `status = ONLINE`（或 merchant 等价可用状态）的商户；后端保存时仍做兜底校验。
7. **清单与商户关联沿用全量替换**：继续使用 `PUT /{id}/merchants` 提交全量列表，保存前过滤已下架商户并提示；不新增细粒度 add/remove 接口。
8. **无 ui-spec 线框**：界面复用既有 DataTable + 弹窗表单 + 下拉级联模式，不产线框。

## API 设计（同步登记 contracts/api-spec.json）

| 方法+路径 | 说明 | x-requirement |
|---|---|---|
| GET `/api/admin/recommend-lists/page?cityId&keyword&page&size` | 分页列表，新增 `status` 字段 | recommend-list/推荐清单管理 |
| GET `/api/admin/recommend-lists/{id}` | 详情，含商户明细 | recommend-list/推荐清单管理 |
| POST `/api/admin/recommend-lists` | 创建 {title, description?, cityId, sortOrder, status?, merchants[]} | recommend-list/推荐清单管理 |
| PUT `/api/admin/recommend-lists/{id}` | 更新（cityId 可变、status 可变、merchants 全量替换） | recommend-list/推荐清单管理 |
| DELETE `/api/admin/recommend-lists/{id}` | 物理删除（连带关联） | recommend-list/推荐清单管理 |
| PUT `/api/admin/recommend-lists/{id}/merchants` | 全量替换清单商户 `[{merchantId, sortOrder}]`，同城校验、重复校验、已下架商户校验 | recommend-list/推荐清单管理 |
| POST `/api/admin/recommend-lists/{id}/online` | 人工恢复清单，校验当前无已下架商户 | recommend-list/推荐清单管理 |
| GET `/api/app/recommend-lists?cityId=` | 城市清单列表（仅 `status = ONLINE`） | recommend-list/App 端清单查询 |
| GET `/api/app/recommend-lists/{id}` | 清单详情含商户（`status = ONLINE` 才可见） | recommend-list/App 端清单查询 |

## 界面实现映射

- 推荐清单列表：`pages/RecommendLists` 列表页增加 `status` 列；城市筛选下拉保持可用全部城市。
- 新建/编辑弹窗：所属城市下拉可改；商户下拉随城市联动且仅展示未下架商户；状态单选项；已选商户若后续被下架，打开表单时展示提示文案与移除按钮。
- 保存前校验：若所选城市下无可选未下架商户，商户下拉展示空态；若城市变更导致已有商户不属新城市，保存返回 400。

## Migration Plan

单向加列/改表 SQL（Liquibase changes/*.sql，可回滚 DROP）：
- `loves_recommend_list` 新增 `status varchar not null default 'ONLINE'`
- 无数据回填；现有清单默认 `ONLINE`

## Open Questions

无。
