# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web,app}.md`（按 modules.md「端」列裁决落点；living 文件，runner 独占回写状态）。
> 本 change 只改 app 端两条 GET 接口，不涉及 web 页面，仅产 IT 用例；
> `period` 参数与扁平响应尚未写入 `contracts/api-spec.json`（apply 任务 1.1 补），用例关联契约仍指向既有路径。

## 新增用例

- TC-article-IT-020: GET /api/app/articles 不传 categoryId 返回全部可见文章（ADDED Scenario: article/App 端文章查询#不传栏目返回全部可见文章）
- TC-featured-IT-024: GET /api/app/featured-cycle-items?period= 按周期过滤（ADDED Scenario: featured/App 端周期推荐查询#按周期过滤）
- TC-featured-IT-025: GET /api/app/featured-cycle-items?period=&type= 周期与类型同时过滤（ADDED Scenario: featured/App 端周期推荐查询#周期与类型同时过滤）
- TC-featured-IT-026: GET /api/app/featured-cycle-items?period= 周期过滤后无条目返回空数组（ADDED Scenario: featured/App 端周期推荐查询#周期过滤后无条目返回空数组）
- TC-featured-IT-027: GET /api/app/featured-cycle-items?period= 非法周期值返回 400（ADDED Scenario: featured/App 端周期推荐查询#非法周期值被拒绝）

## 修改用例

- TC-featured-IT-016: 扁平数组带 period 字段且只含上线条目（MODIFIED: 响应由四周期分组 Map 改为扁平数组，断言改为"顶层为数组、条目 period 字段正确"）
- TC-featured-IT-017: 关联实体不可见时条目不下发（MODIFIED: "从 MENSTRUAL 分组消失"改为"数组不含该条目"）
- TC-featured-IT-018: 大使下线连带隐藏路线类条目（MODIFIED: "OVULATION 分组"断言改为"数组含/不含该条目且 period=OVULATION"）
- TC-featured-IT-019: 按排序号升序（MODIFIED: 改用 period=MENSTRUAL 过滤后断言数组顺序）
- TC-featured-IT-020: 城市未上架不影响路线类条目（MODIFIED: 分组断言改为数组含/不含）
- TC-featured-IT-021: 按内容类型过滤（MODIFIED: 去掉"四周期键齐全"，改为数组仅含该 ARTICLE 条目）
- TC-featured-IT-022: 类型过滤后无条目返回空数组（MODIFIED: "四键齐全且均为空数组"改为响应体为 `[]`）
- TC-featured-IT-023: 非法类型值返回 400（MODIFIED: 仅来源改本 change-id，断言不变）

## 需重测用例

- TC-article-IT-011: GET /api/app/article-categories 与 /api/app/articles 均按权重升序（传 categoryId 路径经 Controller 分派改动）
- TC-article-IT-012: GET /api/app/articles 下线文章不可见、详情 404（传 categoryId 路径）
- TC-article-IT-018: GET /api/app/articles 未设封面标题时回落文章标题（toItem 映射抽为私有方法后共用）

## 执行汇总

2026-08-25 交付轮（api-test-runner，admin=http://localhost:21423 / app=http://localhost:8081）：总数 16 / ✅ 16 / ❌ 0 / 未执行 0。
存证：`test-evidence/app-article-optional-category-and-featured-period-filter/<TC-ID>/`。
⚠️ 已处理：IT-017 步骤 3 与 IT-020 的"活动所属城市下架"断言与现实现（活动不关联城市，commit 22add9b）不符，已随本 change 把 delta spec 与两条用例措辞对齐。
⚠️ 范围外记录：`RouteUpsertRequest` 契约仍写 `cityId`（实现为 `cityName`）；四个 app 只读 operation 无 `responses` schema。
