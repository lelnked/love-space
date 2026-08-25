# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/city/{it,web}.md`（city 域「端」列为 web）。

## 新增用例

- TC-city-IT-013: DELETE /api/admin/cities/{id} 删除地图并连带下架 Banner 与商户（ADDED Scenario: city/地图删除#删除地图 + #删除地图连带下架 Banner 与商户）— P0 / happy+state
- TC-city-IT-014: DELETE /api/admin/cities/{id} 存在路线时地图仍可直接删除（ADDED Scenario: city/地图删除#有路线的地图可以直接删除）— P1 / state

> 说明：本 change 不改生产代码，两条用例登记的是自 `route-remove-city-id` 起就已生效、但从未被 living specs 描述的既有行为。

## 修改用例

- TC-city-IT-008: 城市下架后 app 端路线仍可见（不再级联）（MODIFIED: 「关联需求」原指向已不存在的 Requirement「地图下架对活动级联生效」造成矩阵悬空，改指 `city/地图下架对路线与活动均不级联#下架城市后 app 端路线仍可见`；步骤中过期的 `?cityId=` 查询参数改为 `?cityName=`，与 route-remove-city-id 后的实现一致）
- TC-city-IT-006: 城市下架后 app 端活动仍可见（不再级联）（MODIFIED: 原用例「关联需求」指向已不存在的 Requirement「地图下架对活动级联生效」，预期结果与现行 living spec 方向相反且前置不可构造；改为锚定 `city/地图下架对路线与活动均不级联#下架城市后 app 端活动仍可见`——该 Scenario 此前零用例覆盖）

## 删除用例

- TC-city-IT-009: DELETE /api/admin/cities/{id} 城市下存在路线时拒绝删除（REMOVED: `city/城市下存在路线时禁止删除#有路线的城市不能删除` 已删除，约束前提随 route-remove-city-id 消失）
- TC-city-IT-010: DELETE /api/admin/cities/{id} 路线清空后可正常删除城市（REMOVED: 同上，`#路线清空后可删除城市` 已删除）

## 需重测用例

- TC-city-IT-001 ~ TC-city-IT-008、TC-city-IT-011 及之后：本次不改生产代码，理论上不受影响；交付轮跑 city 域全量 IT 确认删除相关用例后无遗留断链即可。

## 执行汇总

**IT（2026-08-25，api-test-runner，admin `http://localhost:8080` / app `http://localhost:8081`）**：city 域执行 10 条，✅ 10 / ❌ 0 / 未执行 0。
- 新增 TC-city-IT-013（删除地图 + 级联下架 Banner 与商户，19 条断言）、TC-city-IT-014（存在路线时地图仍可直接删除）均通过；后者实测确认路线创建响应已无 `cityId` 字段。
- 修订后的 TC-city-IT-006 通过（9/9 断言）：下架全部 38 个城市后 app 端活动列表条数不变、详情仍 200，验证「不再级联」。runner 已将 38 城恢复上架并核验一致。
- 回归 TC-city-IT-001/002/003/004/005/007/008/011/012 全通过。

**UT**：admin 全量 **112/112 通过**（本次前 111 跑 1 失败）。`CityServiceTest` 由 7 个测试变为 8 个：删 2 个锚定已 REMOVED Scenario 的失效测试，增 3 个锚定新 Requirement「地图删除」三个 Scenario 的测试。

**追溯矩阵**：change 内矩阵 **无 ⚠**，6/6 ✅，正反向覆盖完整、无悬空用例。

**遗留（不在本 change 范围，建议另开 route 域 change）**：`contracts/api-spec.json` 的 `GET /api/app/routes` 仍声明 `cityId` 查询参数、`POST /api/admin/routes` 仍把 `cityId` 列为 required，实际实现均已是 `cityName`——route-remove-city-id 的契约侧遗留。
