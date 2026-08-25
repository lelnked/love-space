## 1. 契约

- [x] 1.1 `contracts/api-spec.json`：`/api/app/articles` GET 的 `categoryId` 改 `required: false` 并补 description（不传返回全部可见文章）；`/api/app/featured-cycle-items` GET 新增可选 `period`（`$ref Period`，非法值 400）、summary 改为"扁平数组、条目带 period"；两处 `x-requirement` 不变

## 2. app 后端：文章列表 categoryId 可选

- [x] 2.1 `ArticleRepository` 新增 `findAllVisible()` native 查询：`online = true AND EXISTS (SELECT 1 FROM loves_article_category c WHERE category_ids @> jsonb_build_array(cast(c.id AS text)))`，`ORDER BY sort_order ASC, created_at DESC`
- [x] 2.2 `ArticleQueryService` 新增 `listAll()`；`listByCategory` 与 `listAll` 共用私有 `toItem(Article)` 映射
- [x] 2.3 `ArticleController.list`：`@RequestParam(required = false) UUID categoryId`，null → `listAll()`，否则 `listByCategory`；更新类 javadoc

## 3. app 后端：周期推荐 period 过滤 + 扁平响应

- [x] 3.1 `FeaturedCycleItemResponse` 新增首字段 `Period period`（取自实体 `phase`）
- [x] 3.2 `FeaturedCycleItemQueryService.feed(Period period, FeaturedCycleItemType type)` 返回 `List<FeaturedCycleItemResponse>`：删除四键预置 Map，过滤链追加 `period == null || item.getPhase() == period`，顺序沿用仓储排序
- [x] 3.3 `FeaturedCycleItemController.feed` 新增 `@RequestParam(required = false) Period period` 并透传，返回类型改 `List`；更新类与方法 javadoc

## 4. 单元测试（带 @scenario 注释）

- [x] 4.1 `ArticleQueryServiceTest` 新增：不传栏目返回全部可见文章，不含下线与失去栏目的文章（@scenario article/App 端文章查询#不传栏目返回全部可见文章）
- [x] 4.2 `FeaturedCycleItemQueryServiceTest`：既有用例改为新签名与扁平断言（@scenario featured/App 端周期推荐查询#查询四个周期的推荐列表、#组内按排序号升序 等）；新增按 period 过滤（#按周期过滤）、period+type 同时过滤（#周期与类型同时过滤）、过滤后空数组（#类型过滤后周期为空仍返回空数组、#周期过滤后无条目返回空数组）
- [x] 4.3 `FeaturedCycleItemControllerWebMvcTest` 新增：`period=UNKNOWN` → 400（@scenario featured/App 端周期推荐查询#非法周期值被拒绝）；合法 `period` 透传

## 5. 交付验证

- [x] 5.1 `./mvnw test`（love-space-app）全绿
- [x] 5.2 `/run-api-test --change app-article-optional-category-and-featured-period-filter`（范围见 test-cases.md）
- [x] 5.3 `node scripts/generate-traceability-matrix.js --change app-article-optional-category-and-featured-period-filter` + `.quality-gate.yml` backend-app 项 + `/opsx:verify`
