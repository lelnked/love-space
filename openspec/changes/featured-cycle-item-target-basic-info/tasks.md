## 1. App 端 DTO

- [x] 1.1 新增 `love-space-app/.../modules/featuredcycle/dto/FeaturedCycleItemTargetResponse.java`：sealed interface，permits 下述三个 record
- [x] 1.2 在同一文件内定义三个 record 实现：`ActivityTarget(UUID id, String title, ImageResponse cover, String level)`、`RouteTarget(UUID id, String title, ImageResponse thumbnail, String cityName, String ambassadorName)`、`ArticleTarget(UUID id, String title, String coverTitle, ImageResponse image)`，javadoc 写明形状按 type 判别
- [x] 1.3 `FeaturedCycleItemResponse` 新增 `FeaturedCycleItemTargetResponse target` 字段（置于 `targetId` 之后），补 @param 说明「非 null；形状按 type 判别」

## 2. 查询服务装配

- [x] 2.1 `FeaturedCycleItemQueryService.feed`：三处 `Set<UUID> visibleXxxIds` 改为 `Map<UUID, Xxx> visibleXxxById`，`isVisible` 改用 `containsKey`；ambassador 同样保留为 `Map<UUID, Ambassador>` 以取名称（查询次数不变，仍 4 次 findAll）
- [x] 2.2 新增按 type 装配 `target` 的私有方法：ACTIVITY 取首图签名（images 为空 → cover=null）、ROUTE 取 thumbnail 签名 + 路线自身 cityName + 大使名称、ARTICLE 取 image 签名 + coverTitle
- [x] 2.3 `toResponse` 接入 `target`；确认签名 URL 统一走 `ImageResponses.from(..., imageUrlSigner)`

## 3. 测试

- [x] 3.1 UT/IT 覆盖 `featured/App 端周期推荐查询#活动类条目下发活动基础信息`
- [x] 3.2 UT/IT 覆盖 `featured/App 端周期推荐查询#路线类条目下发路线基础信息且不覆盖手填文案`
- [x] 3.3 UT/IT 覆盖 `featured/App 端周期推荐查询#文章类条目下发文章基础信息`
- [x] 3.4 UT/IT 覆盖 `featured/App 端周期推荐查询#活动无图片时 cover 为 null`
- [x] 3.5 跑既有 featured 周期推荐 IT 确认 period 数组/过滤/可见性行为未回归

## 4. 契约与文档

- [x] 4.1 确认 `contracts/api-spec.json` 的 `/api/app/featured-cycle-items` summary 已含 target 说明（design 阶段已改，apply 时复核）
- [x] 4.2 同步 `love-space-app/docs/openapi.json` 中该接口的响应描述（若该文件登记了此接口）
