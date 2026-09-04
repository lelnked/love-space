## Context

路线地点存于 `loves_route.spots` jsonb 数组，admin 与 app 各有一个只含 `name/image/introduction` 的 `RouteSpot` record 映射同一份 json。新增 `address` 只是给 record 加一个组件并在 DTO/表单/契约上透传，横跨三端 + 两份契约，故写 design 记录已定决策。

## Goals / Non-Goals

**Goals:**
- 地点可录入、修改、清空地址；admin 详情与 app 路线详情的地点项均返回 `address`

**Non-Goals:**
- 不做经纬度、地图选点、地址格式校验、按地址搜索
- 不改路线列表（列表本就不带地点）

## Decisions

1. **无迁移**：jsonb 加 key，`RouteSpot` record 加 `String address` 组件；既有数据缺该 key，Jackson 反序列化为 null。备选「拆地点子表」被否：spec 已明确地点为 jsonb 内联，且本次只加一个字段。
2. **可选、不校验**：`RouteSpotRequest.address` 不加 bean validation。用户仅要求「支持配置文本」；既有地点数据都没地址，若设必填会让所有旧路线一改就 400。与活动 `subtitle` 的口径一致。
3. **空值口径**：空白 → null 的归一在 web 表单做（`address.trim() || null`，沿用既有惯例）；后端原样保存、原样下发，不回落为名称或介绍。
4. **字段位置**：record `RouteSpot(name, image, introduction, address)` 末尾追加，两端构造调用处最少改动；web 表单地址输入框放在「介绍」上方（名称 → 图片 → 地址 → 介绍），单行 `<Input>`。
5. **契约**：`contracts/api-spec.json` 的 `RouteSpot` schema 与 app 面地点响应 schema、`love-space-app/docs/openapi.json` 的 `RouteSpotItemResponse` 各增 `address`（string, nullable），apply 阶段随代码一并改。

## Risks / Trade-offs

- [app 端 `RouteSpot` 漏加组件 → app 侧 `address` 永远缺失且无编译错误] → IT 用例覆盖「admin 写入后 app 详情读到」的跨端链路。
- [两端 record 手工同步] → 本就如此（`introduction` 同样两处），本次不引入共享模块。

## Migration Plan

纯加法，无迁移，无回填；两后端部署顺序不敏感。

## Open Questions

无。
