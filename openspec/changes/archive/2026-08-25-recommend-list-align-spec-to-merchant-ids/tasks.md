## 1. admin 死代码清理（love-space-admin）

- [x] 1.1 删除 `modules/recommendlist/dto/RecommendListMerchantItemRequest.java`
- [x] 1.2 `RecommendListService`：删除 `replaceMerchants` 方法及其独有 import；类 javadoc "CRUD + 清单商户全量替换" 改为 "CRUD；清单内商户经 create/update 的 merchantIds 整体替换"；`detail` / `toDetail` javadoc "按关联 sortOrder 升序" → "按清单保存顺序"
- [x] 1.3 `RecommendListMerchantResponse` / `RecommendListDetailResponse` javadoc 中"按关联排序号升序" → "按清单保存顺序"（字段不动）
- [x] 1.4 `grep -rn "replaceMerchants\|RecommendListMerchantItemRequest" love-space-admin/src` 为空

## 2. admin 单测对齐（love-space-admin）

- [x] 2.1 `RecommendListServiceTest`：4 个 `replaceMerchants*` 用例改为经 `update(id, new RecommendListUpdateRequest(title, null, null, 0, null, merchantIds))` 验证，方法名去 `replaceMerchants` 前缀，`@scenario` 锚点不变：`recommend-list/清单内商户维护#添加本城市商户`（`[m2, m1]` → 详情顺序 m2、m1）、`#拒绝跨城市商户`、`#重复添加同一商户被拒绝`、`#从清单移除商户`；`deleteRemovesListAndRelationsButKeepsMerchant` 的前置改为 create 时带 `merchantIds`
- [x] 2.2 新增 UT `@scenario recommend-list/清单内商户维护#拒绝已下架商户`：下架商户入 `merchantIds` → `IllegalArgumentException` 含"已下架"，关联不建立
- [x] 2.3 新增 UT `@scenario recommend-list/推荐清单管理#修改所属城市需清单内商户同属新城市`：清单含城市 A 商户，update cityId=B → 异常含"不属于新城市"且 cityId 仍为 A；清单无商户时 update cityId=B 成功
- [x] 2.4 新增 UT `@scenario recommend-list/推荐清单管理#人工恢复清单`：OFFLINE 清单含下架商户 → `online` 抛异常含"未上架商户"、status 仍 OFFLINE；无下架商户 → status 变 ONLINE；ONLINE 清单调用幂等
- [x] 2.5 `cd love-space-admin && env -u SERVER_PORT SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:25432/love_space SPRING_DATASOURCE_USERNAME=iris SPRING_DATASOURCE_PASSWORD=iris ./mvnw -q -Dtest=RecommendListServiceTest test` 全绿，再同环境 `./mvnw -q test` 全量 UT 全绿（默认 5432/love_space 是被 create-drop 清过的坏库；admin IT 与 app IT 不并行）

## 3. 契约与用例

- [x] 3.1 `contracts/api-spec.json` 已在 design 阶段同步（删 `PUT /{id}/merchants` 与 `RecommendListMerchantItem`；Create 补 status/merchantIds；新增 `RecommendListUpdateRequest`、`POST /{id}/online`；summary 更新）——apply 时核对
- [x] 3.2 `tests/recommend-list/it.md` / `web.md` 由 test-cases 工件同步（TC-004、007～010、WEB-002 改口径；新增 016～018）——apply 时核对与 delta spec 一致
- [x] 3.3 admin `docs/` 若有生成式 openapi（对照 `scripts/` 与 love-space-admin/docs），按既有方式重新生成；没有则跳过并记录
