# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/featured/it.md`（App 端接口变更只产出 IT 用例）。

## 新增用例

- TC-featured-IT-021: GET /api/app/featured-cycle-items?type= 按内容类型过滤（ADDED Scenario: featured/App 端周期推荐查询#按内容类型过滤）
- TC-featured-IT-022: 过滤后周期为空仍返回空数组（ADDED Scenario: featured/App 端周期推荐查询#类型过滤后周期为空仍返回空数组）
- TC-featured-IT-023: 非法类型值返回 400（ADDED Scenario: featured/App 端周期推荐查询#非法类型值被拒绝）

## 修改用例

（无——不传 `type` 时行为逐字不变，既有用例定义无需改动）

## 需重测用例

- TC-featured-IT-016: 四周期分组齐全且只含上线条目（确认不传 type 行为不变）
- TC-featured-IT-017: 关联实体不可见时条目不下发
- TC-featured-IT-018: 大使下线连带隐藏路线类条目
- TC-featured-IT-019: 组内按排序号升序
- TC-featured-IT-020: 城市未上架不影响路线类条目

## 执行汇总

总数 8 / ✅ 8 / ❌ 0 / 未执行 0（2026-08-24，api-test-runner，admin `http://localhost:8080` + app `http://localhost:8081`）
存证：`test-evidence/app-featured-cycle-type-filter/TC-featured-IT-{016..023}/`
追溯矩阵：`openspec/changes/app-featured-cycle-type-filter/traceability-matrix.md`（正反向覆盖完整，无 ⚠）
⚠️ 契约缺口（既有，非本次引入）：`/api/app/featured-cycle-items` 未声明 `responses` schema，字段级契约断言跳过
⚠️ 契约漂移（route 域遗留）：`/api/admin/routes` POST 仍声明 `cityId`，实现已是 `cityName`
