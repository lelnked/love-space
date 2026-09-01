## Why

App 端周期推荐信息流目前只下发 `targetId`，App 拿到条目后无法直接展示关联实体（活动/路线/文章）的任何信息，必须按 `targetId` 再逐条打一次对应详情接口才能渲染卡片——一屏推荐位就要 N+1 次请求。把关联实体的基础信息随条目一起下发，App 一次请求即可完成渲染。

## What Changes

- `GET /api/app/featured-cycle-items` 的每个条目新增 `target` 对象，携带关联实体的基础信息；`targetId` 保留不变（不破坏现有客户端）。
- `target` 的形状按 `type` 分为三种，各自只含基础信息，由 App 根据 `type` 自行解析：
  - `ACTIVITY`：`id`、`title`、`cover`（首图签名 URL，无图为 null）、`level`
  - `ROUTE`：`id`、`title`、`thumbnail`（签名 URL）、`cityName`、`ambassadorName`
  - `ARTICLE`：`id`、`title`、`coverTitle`、`image`（签名 URL）
- 条目的可下发条件、排序、`period` 数组语义、`period` / `type` 过滤行为一律不变；不可下发的条目仍不下发，因此 `target` 恒非 null。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `featured`: App 端周期推荐查询的下发内容新增 `target`（按 `type` 分形状的关联实体基础信息）。

## Impact

- `love-space-app`：`FeaturedCycleItemResponse` 新增 `target` 字段；新增三个 target DTO；`FeaturedCycleItemQueryService` 已经全量捞出三类实体做可见性过滤，改为顺带建 id→实体的映射填充 `target`，无额外查询。爱女大使名称需从已捞出的 ambassador 列表取。
- `contracts/api-spec.json`：`FeaturedCycleItemResponse` schema 新增 `target`（oneOf 三形状）。
- admin 端、web 端不受影响。
- 兼容性：纯新增字段，非 BREAKING。
