## Context

- `GET /api/app/articles`：`ArticleController.list(@RequestParam UUID categoryId)` 必填，`ArticleQueryService.listByCategory` 走 native 查询 `findVisibleByCategory`（jsonb `@>` 判断）。可见性 = online ∧ 至少一个仍存在的栏目；按栏目查时栏目存在性由 `existsById` 前置保证。
- `GET /api/app/featured-cycle-items`：`FeaturedCycleItemQueryService.feed(type)` 返回 `Map<Period, List<...>>`，四键预置；条目实体字段名为 `phase`（admin 端参数也叫 `phase`）。待归档 change `app-featured-cycle-type-filter` 已加 `type` 参数，本 change 以其为基线。
- 移动端不在本仓库，响应结构变化需线下通知。

## Goals / Non-Goals

**Goals:**
- `categoryId` 可选；不传返回全部可见文章，排序口径与按栏目查一致。
- 周期推荐新增 `period` 可选过滤；响应改扁平数组，条目带 `period`。
- 契约、living spec、IT 用例同步。

**Non-Goals:**
- 不改 admin 端接口与 web 端页面。
- 不做分页（运营配置级数据量）。
- 不保留旧的分组响应形态做兼容（客户端尚未上线，直接切换）。

## Decisions

1. **app 端参数与响应字段统一命名 `period`，不沿用 admin 端的 `phase`。** 用户明确要求参数名 `period`；同一接口内响应字段与参数同名，避免客户端一处叫 period 一处叫 phase。admin 端 `phase` 不动（跨端 API 面本就分离）。
2. **周期推荐响应改 `List<FeaturedCycleItemResponse>`，record 新增首字段 `period`。** 删除四键预置的 `LinkedHashMap` 逻辑；过滤链上追加 `period == null || item.getPhase() == period`。排序沿用仓储既有 `sortOrder asc, createdAt desc`——不传 `period` 时跨周期混排，客户端按 `period` 字段自行分组即可，spec 未承诺跨周期的次序。
3. **全部可见文章用一条新的 native 查询，可见性在 SQL 里判。** 新增 `ArticleRepository.findAllVisible()`：`online = true AND EXISTS (SELECT 1 FROM loves_article_category c WHERE category_ids @> jsonb_build_array(cast(c.id AS text)))`，`ORDER BY sort_order ASC, created_at DESC`。替代方案（捞全部 online 文章 + 内存按 `categoryRepository` 过滤）多一次全表读，且与既有"jsonb 在 SQL 判"的口径不一致，不取。
4. **Controller 层：`categoryId == null` 分派到 `listAll()`，否则走原 `listByCategory`。** 两方法共用同一 `toItem` 映射，抽成私有方法消除重复。
5. **契约更新放在 apply 首个任务，不在 propose 阶段直改 `contracts/api-spec.json`。** proposal 未批准前不动 living 契约（硬门禁语义）。
6. **既有 IT 用例 TC-featured-IT-016~022 改写断言为扁平口径**（"四周期键齐全"→"数组含/不含 X"），来源字段追加本 change；不新开重复用例。

## Risks / Trade-offs

- [周期推荐响应形态 BREAKING，移动端若已按 Map 解析会挂] → 已在 proposal 标 BREAKING；`period` 字段让客户端可无损重建分组。
- [不传 `period` 时跨周期混排，客户端若期望按周期顺序排列会不满足] → spec 明确只承诺 `sortOrder` 升序；客户端按 `period` 分组后组内顺序仍成立。
- [全部文章查询无分页] → 文章为运营配置级数据量，与既有列表接口口径一致；需要时再加分页。

## Migration Plan

- 无 schema 变更，无数据迁移。部署 app 后端后契约生效；回滚即回退 jar。
- 归档顺序：先归档 `app-featured-cycle-type-filter`，再归档本 change（delta spec 以前者合入后的 living spec 为基线）。

## Open Questions

（无）
