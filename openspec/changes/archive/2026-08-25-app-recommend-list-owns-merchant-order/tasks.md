## 1. merchant 域去掉清单依赖（love-space-app）

- [x] 1.1 `MerchantRepository.searchOnlineNative` / `countQuery`：删除对 `loves_recommend_list_merchant` 的 left join 与 `recommendListId` 条件，`order by` 改为 `m.weight desc, m.created_at desc`；`searchOnline` default 方法与 native 方法签名同步去掉 `recommendListId` 参数；更新 javadoc
- [x] 1.2 `MerchantService.page`：签名去掉 `recommendListId`；删除 `recommendSortOrders` 计算与 `RecommendListMerchantRepository` 注入及相关 import；类 javadoc 排序描述改为"weight DESC, createdAt DESC"
- [x] 1.3 `MerchantListItemResponse`：删除 `recommendSortOrder` 组件及 javadoc
- [x] 1.4 `MerchantController.page`：删除 `recommendListId` 请求参数；类/方法 javadoc 同步
- [x] 1.5 确认 `com.space.app.modules.merchant` 包内不再 import `com.space.app.modules.recommendlist.*`（`grep -rn recommendlist src/main/java/com/space/app/modules/merchant` 为空）

## 2. recommend-list 域商户项收敛（love-space-app）

- [x] 2.1 `RecommendListMerchantItemResponse` 改为 `(UUID id, String name, String address, ImageResponse logo)`，javadoc 注明"按清单保存顺序，仅四字段"
- [x] 2.2 `RecommendListQueryService.detail`：按新 DTO 组装（去掉 `recommendReason`、`relation.getSortOrder()`）；类 javadoc"按关联排序号升序"改为"按清单保存顺序"
- [x] 2.3 `RecommendListDetailResponse` / `RecommendListController` javadoc 中"按关联排序号升序"改为"按清单保存顺序"

## 3. 测试（love-space-app）

- [x] 3.1 `MerchantReadIT`：删除"按推荐清单过滤"用例与 `relate` 辅助方法；新增/改写用例锚定 `@scenario recommend-list/App 端清单与清单内商户查询#商户列表不受清单影响`——3 个上架商户其中 2 个入清单（清单顺序与 weight 相反），带 `recommendListId` 与不带均返回 3 个、按 weight 降序、`$.content[*].recommendSortOrder` 不存在
- [x] 3.2 `RecommendListQueryServiceTest`：详情用例改为锚定 `@scenario recommend-list/App 端清单与清单内商户查询#清单详情返回商户明细`——按保存顺序返回、下架商户不出现、每项仅 `id/name/address/logo`（断言 `recommendReason` 相关代码删除）；该文件另两处 `@scenario recommend-list/App 端清单查询#…` 注释（查询上架城市的清单 / 下架城市清单不可见）同步改为新 Requirement 名
- [x] 3.3 `MerchantControllerWebMvcTest` 若构造 `MerchantListItemResponse` 或引用 `recommendListId`，同步修正
- [x] 3.4 `cd love-space-app && env -u SERVER_PORT -u SPRING_DATASOURCE_URL APP_SECURITY_API_KEYS=<AbstractPostgresIntegrationTest.TEST_API_KEY 的 hash> ./mvnw -q test` 与同环境 `./mvnw -q -Dtest='*IT' test` 全绿

## 4. 契约与文档

- [x] 4.1 `contracts/api-spec.json` 已在 design 阶段同步（删 `recommendListId` 参数、改两处 summary）——apply 时核对无遗漏
- [x] 4.2 重新生成 `love-space-app` 的 `openapi.json`（沿用仓库既有生成方式，参考 commit 45985ce），确认 `/api/app/merchants/page` 无 `recommendListId`、`RecommendListMerchantItemResponse` 仅四字段
- [x] 4.3 `tests/recommend-list/it.md` 由 test-cases 工件同步（删 TC-014、改 TC-012）——apply 时核对与 spec 一致
