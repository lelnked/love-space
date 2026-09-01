# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/route/it.md`（route 域「端」列为 web，app 端未启用，故只产 IT 用例；web 页面行为未变，无 WEB 用例增改）。

## 新增用例

- TC-route-IT-027: GET /api/app/routes 列表项返回路线自身城市名 cityName（ADDED Scenario: route/App 端路线查询#列表项返回路线自身城市名）

## 修改用例

- TC-route-IT-019: 原用例只断言 `city` 为 null，本次补断言 `cityName` 仍为「不存在城」（MODIFIED: Scenario 增加了 cityName 断言）

## 需重测用例

- TC-route-IT-025: 列表项 ambassadorNote —— 同一响应结构被改动，回归确认既有字段未丢

## 执行汇总

2026-09-01 实跑（app :8081，admin :21423）：总数 3 / ✅ 3 / ❌ 0 / 未执行 0
- TC-route-IT-019 ✅ 5 条路线 `city` 为 null、`cityName` 为「不存在的城市」
- TC-route-IT-025 ✅ 43 条列表项既有字段完整，`ambassadorNote` 未受影响
- TC-route-IT-027 ✅ 列表 `cityName` == `city.name` == 详情 `cityName`

另：app 端 mvn test 92 UT 全绿（含新增 `listReturnsRouteOwnCityName`），`-Dtest='*IT'` 全绿。
