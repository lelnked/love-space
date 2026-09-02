# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web}.md`（activity 与 featured 两域按 `tests/modules.md`「端」列均为 `web`，
> 未启用 APP 端，故不产出 APP 用例；app 后端接口的断言落在 IT 用例里）。

## 新增用例

- TC-activity-IT-023: 活动副标题可写可改可清空（admin 侧）（ADDED Scenario: activity/活动管理#副标题可写可改可空）
- TC-activity-IT-024: GET /api/app/activities 列表与详情下发 subtitle，未填时为 null（ADDED Scenario: activity/App 端活动查询#活动副标题下发且未填时为 null）
- TC-activity-WEB-005: 活动表单填写副标题并回显（ADDED Scenario: activity/web 端活动管理页面#活动表单填写副标题并回显）
- TC-featured-IT-038: GET /api/app/featured-cycle-items 活动未填副标题时 target.subtitle 为 null（ADDED Scenario: featured/App 端周期推荐查询#活动未填副标题时 target.subtitle 为 null）

## 修改用例

- TC-activity-IT-007: GET /api/app/activities 全局上线活动列表（MODIFIED: Scenario「查询上架城市的活动」列表项字段集合增 `subtitle`，前置与断言补副标题）
- TC-featured-IT-034: GET /api/app/featured-cycle-items 活动类条目下发活动基础信息（MODIFIED: Scenario「活动类条目下发活动基础信息」的 `ActivityTarget` 增 `subtitle`，断言 target.subtitle 取自活动实体）

## 需重测用例

行为未变但受本 change 实现影响（`loves_activity` 加列 + admin/app 两侧 Activity 实体与 DTO 改动 + web 活动表单改动），需回归确认：

- TC-activity-IT-001: 创建完整活动（不带 subtitle 的既有请求体仍合法、响应字段不减）
- TC-activity-IT-004: 更新活动（不带 subtitle 的 PUT 仍合法）
- TC-activity-IT-009: app 活动详情返回富文本（详情字段集合扩容后既有字段不受影响）
- TC-activity-IT-020: 活动景观字段贯通（同类可空文本字段，验证新列未干扰）
- TC-activity-WEB-004: 活动表单无地图选项即可保存（表单新增输入框后保存链路回归）
- TC-featured-IT-037: 活动无图片时 target.cover 为 null（ActivityTarget 形状变更后回归）

## 执行汇总

**IT（api-test-runner，2026-09-02）**：10 条全部 ✅ 通过。
环境：admin `http://localhost:21423`（test profile；本机 8080 被其他项目占用）、app `http://localhost:8081`，共库 `localhost:25432/love_space`。
存证：`test-evidence/activity-subtitle/<TC ID>/`。
过程中 TC-featured-IT-038 首轮 ❌，根因是用例与既有契约冲突——ACTIVITY 类条目本就不持有 `subtitle` 文案（`FeaturedCycleItemService#applyByType` 对非 ROUTE 类型强制置 null）。已订正该用例与 featured delta spec 的 Scenario（改用 `description` 表达「条目手填文案不被 target 覆盖」），重跑 ✅。

**UT**：admin 130 项、app 98 项全绿（含新增 `subtitleIsWritableUpdatableAndNullable`、`listAndDetailCarrySubtitleAndNullWhenAbsent`、`activityTargetSubtitleIsNullWhenActivityHasNoSubtitle`）。

**WEB（web-test-runner）**：⏸ 未执行——`playwright-company` MCP 本会话连接超时（CONNECT_TIMEOUT），远程浏览器不可用。待执行：TC-activity-WEB-005（新增）、TC-activity-WEB-004（需重测）。
