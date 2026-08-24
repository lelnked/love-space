## 1. app 后端：周期推荐按类型过滤

- [x] 1.1 `FeaturedCycleItemQueryService.feed()` 改签名为 `feed(FeaturedCycleItemType type)`：`type` 为 null 时不过滤，否则在既有可见性过滤链上追加 `item.getType() == type`；四周期键预置逻辑不变
- [x] 1.2 `FeaturedCycleItemController.feed`：新增 `@RequestParam(required = false) FeaturedCycleItemType type` 并透传，更新类与方法 javadoc

## 2. 单元测试（带 @scenario 注释）

- [x] 2.1 `FeaturedCycleItemQueryServiceTest`：既有用例更新到新签名（传 null）
- [x] 2.2 新增：按 type 过滤只返回该类型条目（@scenario featured/App 端周期推荐查询#按内容类型过滤）
- [x] 2.3 新增：过滤后四周期键仍齐全且为空数组（@scenario ...#类型过滤后周期为空仍返回空数组）

## 3. 契约与文档

- [x] 3.1 `contracts/api-spec.json`：`/api/app/featured-cycle-items` GET 新增可选 `type`（design 阶段已完成）
- [x] 3.2 `tests/featured/it.md` 新增 TC-featured-IT-021~023

## 4. 交付验证

- [x] 4.1 `./mvnw test`（love-space-app）全绿
- [x] 4.2 `/run-api-test --change app-featured-cycle-type-filter`
- [x] 4.3 刷新追溯矩阵 + `.quality-gate.yml` + `/opsx:verify`
