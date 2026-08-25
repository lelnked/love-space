## Why

`route-remove-city-id`（2026-08-23 归档）已把路线的城市关联彻底移除——归档 README 原话「No city association is retained」，`016-remove-route-city-id.sql` 删掉了 `loves_route.city_id`，路线现在只有自由文本 `city_name`。但那次变更只留了 README、未走 delta spec 流程，导致 living specs 里的 Requirement「城市下存在路线时禁止删除」被遗漏未清理：其存在理由写的是「防止路线的 `cityId` 悬空」，而该字段已不存在；`CityService.delete()` 里的对应校验也已随之删除。

结果是 spec 与实现长期脱节：`CityServiceTest.deleteRejectedWhenCityHasRoutes` 持续失败（挡住 admin unit-test 门禁），`TC-city-IT-009`/`TC-city-IT-010` 成为锚定已不存在行为的孤儿用例。

## What Changes

- **REMOVED** Requirement「城市下存在路线时禁止删除」及其两个 Scenario——约束的前提（路线持有 `cityId`）已随 `route-remove-city-id` 消失。
- 删除失效的 UT `CityServiceTest.deleteRejectedWhenCityHasRoutes`，及其只服务于该测试的私有夹具 `routeIn(...)`（其 `cityId` 参数早已未被使用）。
- 删除孤儿用例 `TC-city-IT-009`、`TC-city-IT-010`。
- **ADDED** Requirement「地图删除」——补登记删除城市的既有行为：物理删除、无任何前置校验，事务提交后异步下架该城市的 CITY 类型 Banner 与该城市下全部商户（均只下架不删除）。此前该行为从未在 living specs 中被独立描述，仅被上述路线约束的 Scenario 顺带覆盖，删除后会留下真空。补登记不改变任何行为。
- **无用户可见行为变化**：`CityService.delete()` 实现自 `route-remove-city-id` 起就不再校验路线，删除有路线的城市一直返回 200。本次仅让 living specs 与测试追上既有实现。
- 非 BREAKING：不改代码行为、不改接口、不改 schema。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `city`: 移除 Requirement「城市下存在路线时禁止删除」；新增 Requirement「地图删除」照实登记既有删除行为与级联范围。两者合起来是同一件事——让 living specs 与自 `route-remove-city-id` 起就已生效的实现保持一致。

## Impact

- **living specs**：`openspec/specs/city/spec.md` 删除该 Requirement 段落。
- **admin 后端**：`CityServiceTest` 删除 2 个锚定已删 Scenario 的测试方法与 1 个私有夹具方法，新增 1 个不涉及路线的删除测试；`CityService` 与两个事件监听器 **均不改动**（实现已是目标状态）。
- **测试用例**：`tests/city/it.md` 删除 TC-city-IT-009、TC-city-IT-010（git 留史，不加废弃标记）。
- **契约**：`contracts/api-spec.json` 的 `DELETE /api/admin/cities/{id}`——`summary` 原写「该城市下仍有路线时返回 400，拒绝删除」与实测行为相反，`x-requirement` 指向已 REMOVED 的 Requirement，二者一并更正为「物理删除、无前置校验 + 级联下架」与 `city/地图删除`。
- **不影响**：app 端、web 端、DB schema、`tests/modules.md`。
