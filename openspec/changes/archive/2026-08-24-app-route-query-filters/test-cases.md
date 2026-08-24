# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/route/it.md`（route 域「端」列为 web，App 端接口变更只产出 IT 用例）。

## 新增用例

- TC-route-IT-016: GET /api/app/routes 不带任何参数返回全部可见路线（ADDED Scenario: route/App 端路线查询#不传任何过滤参数返回全部可见路线）
- TC-route-IT-017: GET /api/app/routes?ambassadorId= 按大使过滤路线（ADDED Scenario: route/App 端路线查询#按大使 ID 过滤路线）
- TC-route-IT-018: GET /api/app/routes?cityName=&ambassadorId= 组合过滤取交集（ADDED Scenario: route/App 端路线查询#城市名与大使 ID 组合过滤）
- TC-route-IT-019: GET /api/app/routes?cityName= 城市不存在返回空数组（ADDED Scenario: route/App 端路线查询#城市名不存在返回空数组）

## 修改用例

- TC-route-IT-012: 请求 URL 由 `?cityId=` 改为 `?cityName=`（MODIFIED: 列表过滤参数变更）
- TC-route-IT-013: 请求 URL 由 `?cityId=` 改为 `?cityName=`（MODIFIED: 同上）
- TC-route-IT-015: 请求 URL 由 `?cityId=` 改为 `?cityName=`（MODIFIED: 同上）

## 需重测用例

- TC-route-IT-014: 详情接口未变，但与列表共用 RouteQueryService，重构后回归确认

## 执行汇总

总数 8 / ✅ 8 / ❌ 0 / 未执行 0（2026-08-24，api-test-runner，baseUrl admin `http://localhost:8080` + app `http://localhost:8081`）
存证：`test-evidence/app-route-query-filters/TC-route-IT-{012..019}/`
追溯矩阵：`openspec/changes/app-route-query-filters/traceability-matrix.md`（正反向覆盖完整，无悬空用例）
⚠️ 契约缺口：`/api/app/routes` 与 `/api/app/routes/{id}` 在 api-spec.json 中未声明 `responses` schema，字段级契约断言跳过（非实现漂移）
