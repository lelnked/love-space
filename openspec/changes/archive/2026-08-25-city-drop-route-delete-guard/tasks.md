## 1. 清理失效测试

- [x] 1.1 删除 `love-space-admin/src/test/java/com/loves/space/modules/city/service/CityServiceTest.java` 中的 `deleteRejectedWhenCityHasRoutes` 与 `deleteSucceedsAfterRoutesRemoved`（两者分别锚定 `#有路线的城市不能删除`、`#路线清空后可删除城市`，本次 REMOVED）
- [x] 1.2 删除只服务于这两个测试的私有夹具 `routeIn(UUID cityId)`，及随之不再使用的字段与 import（`RouteRepository`、`AmbassadorRepository`、`Route`、`Ambassador`——已确认无其他测试引用）
- [x] 1.3 新增测试 `deleteRemovesCity`，锚定 `city/地图删除#删除地图`：建城市 → delete → 再 get 抛异常（不涉及路线）
- [x] 1.5 新增测试 `deleteSucceedsEvenWhenRoutesExist`，锚定 `city/地图删除#有路线的地图可以直接删除`（追溯矩阵要求每个 Scenario 有 UT/WEB/APP 覆盖，IT 不计入）
- [x] 1.6 新增测试 `deleteOfflinesLinkedBannerAndMerchants`，锚定 `city/地图删除#删除地图连带下架 Banner 与商户`：断言两者 online=false、记录仍在、商户 cityId 未清空（照抄 `BannerServiceTest.deletingCityOfflinesItsBanners` 的既有模式）
- [x] 1.4 `./mvnw -Dtest=CityServiceTest test` 通过（6 个测试全绿）

## 2. 清理孤儿用例

- [x] 2.1 `tests/city/it.md` 删除 `TC-city-IT-009`（城市下存在路线时拒绝删除）整块
- [x] 2.2 `tests/city/it.md` 删除 `TC-city-IT-010`（路线清空后可正常删除城市）整块
- [x] 2.3 确认 `tests/city/web.md` 与其他域用例无「关联需求」指向被删 Requirement 的残留
- [x] 2.4 修订 `TC-city-IT-006`：关联需求改指 `city/地图下架对路线与活动均不级联#下架城市后 app 端活动仍可见`，预期结果反转为「活动仍可见、详情 200」，去掉不可构造的 `?cityId=` 前置（交付轮回归暴露的同类遗留，该 Scenario 此前零覆盖）

## 3. 契约

- [x] 3.0 `contracts/api-spec.json` 的 `DELETE /api/admin/cities/{id}`：`summary` 改为「物理删除，无前置校验；提交后异步下架该城市的 CITY Banner 与其下全部商户，只下架不删除」，`x-requirement` 由 `city/城市下存在路线时禁止删除` 改为 `city/地图删除`（交付轮 IT 实测发现契约滞后，proposal 初版误判为「契约不变」，已回写修正）

## 4. 验证

- [x] 4.1 admin 全量 `./mvnw test` 通过：**112/112**（本次前为 111 跑 1 失败；删 2 个失效测试、增 3 个新 Scenario 测试）
- [x] 4.2 归档时同步 living specs：`openspec/specs/city/spec.md` 移除「城市下存在路线时禁止删除」段落、加入「地图删除」Requirement
- [x] 4.3 刷新追溯矩阵：change 内矩阵 **无 ⚠**（6/6 ✅，正反向覆盖完整、无悬空用例）
