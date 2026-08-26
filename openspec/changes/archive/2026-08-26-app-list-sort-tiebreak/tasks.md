## 1. sortOrder 型排序号补齐 createdAt DESC（app 后端）

- [x] 1.1 `love-space-app` `modules/category/service/CategoryService.java`：`Sort.by` 第二项由 `asc("createdAt")` 改为 `desc("createdAt")`，同步更新类注释与方法 javadoc 的排序措辞
- [x] 1.2 `modules/banner/service/BannerQueryService.java`：`Sort.by` 第二项由 `asc(Banner_.CREATED_AT)` 改为 `desc(Banner_.CREATED_AT)`，同步更新类 javadoc 的排序措辞
- [x] 1.3 `modules/merchant/service/MerchantReviewQueryService.java`：`Sort` 由单列 `ASC sortOrder` 改为 `Sort.by(asc(MerchantReview_.SORT_ORDER), desc(MerchantReview_.CREATED_AT))`，同步更新类/方法 javadoc
- [x] 1.4 `modules/recommendlist/repository/RecommendListRepository.java`：方法名 `findByCityIdAndStatusOrderBySortOrderAsc` → `findByCityIdAndStatusOrderBySortOrderAscCreatedAtDesc`，并改 `RecommendListQueryService` 调用点
- [x] 1.5 `modules/recommendlist/repository/RecommendListMerchantRepository.java`：方法名 `findAllByRecommendListIdOrderBySortOrderAsc` → `findAllByRecommendListIdOrderBySortOrderAscCreatedAtDesc`，并改 `RecommendListQueryService` 调用点
- [x] 1.6 `modules/route/repository/RouteRepository.java`：native SQL `order by r.sort_order asc` → `order by r.sort_order asc, r.created_at desc`，同步更新方法 javadoc
- [x] 1.7 `./mvnw -q compile` 通过（1.4/1.5 改名的调用点全部编译干净）

## 2. UT（锚定 Scenario）

- [x] 2.1 `CategoryService` UT：同 sortOrder 两条分类按 createdAt 倒序返回（`@scenario merchant/App 端带排序号列表的排序口径#分类列表同序号按创建时间倒序`）
- [x] 2.2 `CategoryService` UT：sortOrder 不同时以 sortOrder 为准，创建时间不干扰（`@scenario merchant/App 端带排序号列表的排序口径#排序号不同时以排序号为准`）
- [x] 2.3 `MerchantReviewQueryService` UT：同 sortOrder 两条评价按 createdAt 倒序返回（`@scenario merchant/App 端带排序号列表的排序口径#商户评价同序号按创建时间倒序`）
- [x] 2.4 `BannerQueryService` UT：同 sortOrder 两条 Banner 按 createdAt 倒序返回（`@scenario banner/App 端 Banner 查询#同排序号 Banner 按创建时间倒序`）
- [x] 2.5 `RecommendListQueryService` UT：同 sortOrder 两个 ONLINE 清单按 createdAt 倒序返回（`@scenario recommend-list/App 端清单与清单内商户查询#同排序号清单按创建时间倒序`）
- [x] 2.6 `RouteQueryService` UT：同 sortOrder 两条可见路线按 createdAt 倒序返回（`@scenario route/App 端路线查询#同排序号路线按创建时间倒序`）
- [x] 2.7 商户列表 weight 口径回归 UT（现状即应为 `weight DESC, createdAt DESC`，仅锚定不改实现）（`@scenario merchant/App 端带排序号列表的排序口径#weight 型排序号维持降序且已符合口径`）
- [x] 2.8 `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:25432/love_space_app_test APP_SECURITY_API_KEYS=<hash> ./mvnw test` 全绿

## 3. 交付验证

- [x] 3.1 `/run-api-test --change app-list-sort-tiebreak` 跑 test-cases.md 列出的 IT 范围，全绿
- [x] 3.2 `node scripts/generate-traceability-matrix.js --change app-list-sort-tiebreak` 刷新追溯矩阵
- [x] 3.3 `openspec validate app-list-sort-tiebreak` 通过，`.quality-gate.yml` 逐项过
