## Context

`GET /api/app/featured-cycle-items` 目前只下发 `targetId`，App 渲染卡片需按 id 再逐条查活动/路线/文章详情。服务端 `FeaturedCycleItemQueryService.feed` 为做可见性过滤，已经把三类实体全量 `findAll()` 捞进内存（运营配置级数据量），只是过滤完只留了 id 集合——实体对象本身随即被丢弃。所以填充 `target` 不需要任何新增查询。

## Goals / Non-Goals

**Goals:**
- 条目下发时带上关联实体的基础信息，App 一次请求即可渲染推荐卡片。
- `target` 按 `type` 分三种形状，各自只含卡片所需字段，App 按 `type` 分支解析。
- 不改动条目可见性、排序、`period` 数组语义与过滤行为。

**Non-Goals:**
- 不下发详情内容（活动行程/文章正文/路线打卡点）——那是详情接口的事。
- 不动 admin 端与 web 端。
- 不引入统一同构的 target 结构（用户已明确按 type 分 DTO）。

## Decisions

### 1. 按 type 分三个独立 DTO，而非复用各端列表项 DTO
新增 `FeaturedCycleItemTargetResponse` 密封接口 + 三个 record 实现（`ActivityTarget` / `RouteTarget` / `ArticleTarget`），放在 `modules/featuredcycle/dto/`。
- 为什么不复用 `ActivityItemResponse` / `RouteItemResponse` / `ArticleItemResponse`：那三个是各自列表页的契约，字段比推荐卡片需要的多（活动 tags/periods/introduction、路线 sortOrder/city 反查对象、文章 tags），复用会把推荐流的契约绑死在别人的列表页上——那边加字段这边就跟着变。基础信息子集独立成 DTO，边界清楚。
- 为什么不统一同构结构：用户已定「按 type 分不同 DTO，App 根据 type 自己解析」。

### 2. 各 DTO 字段（默认拍板，已定决策）
| type | 字段 | 取值来源 |
|---|---|---|
| `ACTIVITY` | `id`, `title`, `cover`, `level` | `Activity.images` 首张签名后作 `cover`（空列表 → null），其余直取 |
| `ROUTE` | `id`, `title`, `thumbnail`, `cityName`, `ambassadorName` | `Route` 直取；`ambassadorName` 由已捞出的 ambassador 列表按 `route.ambassadorId` 取 |
| `ARTICLE` | `id`, `title`, `coverTitle`, `image` | `Article` 直取，`image` 签名 |
判据是「推荐卡片渲染所需的最小集」：一张图 + 标题 + 一到两个辅助标签。路线的 `cityName` 直取路线自身（与 `RouteItemResponse.cityName` 同源），不做城市表反查——反查只为拿城市详情对象，卡片用不上。

### 3. 复用已在内存中的实体，不新增查询
`feed` 里三个 `findAll()` 的结果从「过滤成 id Set」改为「过滤成 id→实体 Map」，可见性判定改用 `Map.containsKey`，装配 `target` 时从同一个 Map 取实体。查询次数不变（仍是 4 次 findAll），无 N+1。

### 4. JSON 序列化形状
三个 record 实现同一密封接口，Jackson 按运行时具体类型序列化，`target` 天然就是该类型的字段集，不加类型判别字段——`type` 已在条目上，App 用它分支即可。

## Risks / Trade-offs

- [`target` 三形状不同构，App 必须按 `type` 分支解析] → 已是用户明确选择；spec 与 api-spec.json 用 oneOf 把三形状写死，契约无歧义。
- [响应体变大] → 运营配置级数据量（每周期个位数条目），每条多几个字符串字段，可忽略。
- [`ambassadorName` 依赖大使记录存在] → 条目仅在大使 `online=true` 时才下发，大使必然存在，取不到即条目本就不可见。

## Migration Plan

纯新增响应字段，非 BREAKING，老客户端忽略 `target` 即可。无数据库变更、无迁移脚本。回滚 = 回滚 app 端部署。

## Open Questions

无。
