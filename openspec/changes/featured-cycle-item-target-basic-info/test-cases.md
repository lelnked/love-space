# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/featured/it.md`（featured 域「端」列为 web + IT；本 change 只动 app 后端接口，无 web 行为变化，故只落 IT）。

## 新增用例

- TC-featured-IT-034: GET /api/app/featured-cycle-items 活动类条目下发活动基础信息（ADDED Scenario: featured/App 端周期推荐查询#活动类条目下发活动基础信息）
- TC-featured-IT-035: GET /api/app/featured-cycle-items 路线类条目下发路线基础信息且不覆盖手填文案（ADDED Scenario: featured/App 端周期推荐查询#路线类条目下发路线基础信息且不覆盖手填文案）
- TC-featured-IT-036: GET /api/app/featured-cycle-items 文章类条目下发文章基础信息（ADDED Scenario: featured/App 端周期推荐查询#文章类条目下发文章基础信息）
- TC-featured-IT-037: GET /api/app/featured-cycle-items 活动无图片时 target.cover 为 null（ADDED Scenario: featured/App 端周期推荐查询#活动无图片时 cover 为 null）

## 修改用例

（无：既有 Scenario 行为未变，仅新增下发字段）

## 需重测用例

- TC-featured-IT-028 ~ TC-featured-IT-032: 周期数组/过滤/可见性行为，受 `feed` 内部由 Set 改 Map 的实现调整影响，需回归确认未漂移

## 执行汇总

<!-- runner 跑完后由编排 skill 填写：总数 / ✅ / ❌ / 未执行 -->
