# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/featured/{it,web}.md`（featured 域「端」列为 `web`，故产出 IT + WEB 两端，无 APP 用例）。

## 新增用例

- TC-featured-IT-039: POST /api/admin/featured-cycle-items 创建多周期条目（ADDED Scenario: featured/周期推荐条目管理#创建多周期条目）
- TC-featured-IT-040: POST /api/admin/featured-cycle-items phases 为空或缺省被拒绝（ADDED Scenario: featured/周期推荐条目管理#phases 为空被拒绝）
- TC-featured-IT-041: POST /api/admin/featured-cycle-items 同一关联实体重复创建被拒绝（ADDED Scenario: featured/周期推荐条目管理#同一关联实体重复创建被拒绝）
- TC-featured-IT-042: POST /api/admin/featured-cycle-items 下线条目同样占用唯一位（ADDED Scenario: featured/周期推荐条目管理#下线条目同样占用唯一位）
- TC-featured-IT-043: PUT /api/admin/featured-cycle-items/{id} 更新条目自身不触发唯一冲突（ADDED Scenario: featured/周期推荐条目管理#更新条目自身不触发唯一冲突）
- TC-featured-IT-044: PUT /api/admin/featured-cycle-items/{id} 更新指向已被占用的实体被拒绝（ADDED Scenario: featured/周期推荐条目管理#更新指向已被占用的实体被拒绝）
- TC-featured-IT-045: GET /api/admin/featured-cycle-items/page 不传周期返回全部条目（ADDED Scenario: featured/周期推荐条目管理#不传周期返回全部条目）
- TC-featured-IT-046: 更新关联实体（targetId 可改，原实体唯一位释放）
- TC-featured-WEB-008: 周期筛选下拉过滤列表（ADDED Scenario: featured/web 端周期推荐页面#周期筛选下拉）
- TC-featured-WEB-009: 未勾选周期无法提交（ADDED Scenario: featured/web 端周期推荐页面#未勾选周期无法提交）
- TC-featured-WEB-010: 编辑时修改周期（ADDED Scenario: featured/web 端周期推荐页面#编辑时修改周期）
- TC-featured-WEB-011: 关联实体重复时展示后端中文业务错误（ADDED Scenario: featured/web 端周期推荐页面#关联实体重复时展示错误）

## 修改用例

admin 端（`phase` → `phases` 多值、`(type,targetId)` 全局唯一、`phase` 查询参数改「包含」语义）：

- TC-featured-IT-007: 创建活动类周期推荐（MODIFIED: 请求体与详情 `phase` → `phases` 数组；前置补「未被引用」）
- TC-featured-IT-008: 创建路线类周期推荐（MODIFIED: 同上）
- TC-featured-IT-009: 创建文章类周期推荐（MODIFIED: 同上）
- TC-featured-IT-010: 类型必填项缺失被拒绝（MODIFIED: 拆出「缺 phase」一步——该校验已独立为 TC-featured-IT-040；五步减为四步）
- TC-featured-IT-011: 关联实体不存在被拒绝（MODIFIED: `phases` 化；补断言「不存在」文案不与唯一冲突文案混淆）
- TC-featured-IT-012: phases 可改而 type 创建后不可变（MODIFIED: 周期由不可变放宽为**可修改**，断言方向反转；标题随之更名）
- TC-featured-IT-013: page 的 phase 参数按「包含」过滤（MODIFIED: 过滤语义由「等于」改为「包含」，新增多周期条目同时命中两个周期的断言；优先级 P1 → P0）
- TC-featured-IT-033: 缺 targetId 被拒绝（MODIFIED: `phases` 化；补 `phases` 不被清空的断言）

app 端（`period` 改为直读条目自身 `phases`，不再跨条目聚合；`period` 参数改「包含」语义）：

- TC-featured-IT-016: 扁平数组带 period 且只含上线条目（MODIFIED: `period` 来源改为条目自身 `phases`）
- TC-featured-IT-019: 按排序号升序（MODIFIED: 前置改为 5 个关联互不相同实体的条目，以满足唯一约束）
- TC-featured-IT-021: 按内容类型过滤（MODIFIED: 前置各关联不同实体）
- TC-featured-IT-024: 按周期过滤（MODIFIED: 过滤语义改「包含」）
- TC-featured-IT-025: 周期与类型同时过滤（MODIFIED: 同上）
- TC-featured-IT-028: 多周期条目在 period 数组中下发全部周期（MODIFIED: 由「同 target 两条条目聚合」改为「同 target 唯一一条、含多周期」，断言 target 至多出现一次；标题更名）
- TC-featured-IT-029: 过滤后 period 数组仍含其他周期（MODIFIED: 补「两个周期都能命中同一条」断言）
- TC-featured-IT-030: 类型过滤不影响 period 数组（MODIFIED: 单条多周期口径）
- TC-featured-IT-031: 下线条目整条不下发（MODIFIED: 原「不贡献周期」的聚合语义消失，改为整条不下发；标题更名）
- TC-featured-IT-032: 不同 target 的周期集合互不影响（MODIFIED: 单条多周期口径）

web 端（去 4 个周期 Tab → 单列表 + 周期筛选下拉 + 表单周期多选）：

- TC-featured-WEB-003: 周期推荐页单列表展示与投放周期标签（MODIFIED: 断言由「四个 Tab 切换」反转为「无 Tab、单列表、新增投放周期标签列」；标题更名）
- TC-featured-WEB-004: 新增弹窗按内容类型切换字段块（MODIFIED: 补区域④ 周期勾选在类型切换后保持不变的断言）
- TC-featured-WEB-006: 弹窗表单新增多周期周期推荐（MODIFIED: 去 Tab 前置，改为勾选两个周期并断言列表标签；标题更名）
- TC-featured-WEB-007: 周期推荐上下线切换与删除确认（MODIFIED: 去 Tab 前置与「该周期暂无推荐」空态断言，补上下线开关一步）

## 需重测用例

行为口径未变，但实现（DB 唯一约束、`phases` 字段、app 端查询重写、web 页面重构）大幅改动，需回归确认：

- TC-featured-IT-014: 周期推荐上下线切换
- TC-featured-IT-015: 周期推荐物理删除
- TC-featured-IT-017: 关联实体不可见时条目不下发
- TC-featured-IT-018: 大使下线连带隐藏路线类条目
- TC-featured-IT-020: 城市未上架不影响路线类条目
- TC-featured-IT-022: 类型过滤后无条目返回空数组
- TC-featured-IT-023: 非法类型值返回 400
- TC-featured-IT-026: 周期过滤后无条目返回空数组
- TC-featured-IT-027: 非法周期值返回 400
- TC-featured-IT-034: 活动类条目下发活动基础信息
- TC-featured-IT-035: 路线类条目下发路线基础信息且不覆盖手填文案
- TC-featured-IT-036: 文章类条目下发文章基础信息
- TC-featured-IT-037: 活动无图片时 target.cover 为 null
- TC-featured-IT-038: 活动未填副标题时 target.subtitle 为 null
- TC-featured-WEB-005: 周期生活法选中文章后自动带出主标题

> 契约已在 design 阶段同步：`contracts/api-spec.json` 的 `FeaturedCycleItemUpsertRequest`
> 已改为 `phases: Period[]`（`minItems: 1`），`/api/admin/featured-cycle-items/page` 的
> `phase` 查询参数已标注「包含」语义，IT 用例可直接做 schema 断言。
> app 端 `/api/app/featured-cycle-items` 的 `period` 响应字段名与形状不变，仅描述文案更新。

## 执行汇总

执行日期：2026-09-02

| 端 | 总数 | ✅ | ❌ | ⬜ 未测 |
|---|---|---|---|---|
| IT | 40 | 40 | 0 | 0 |
| WEB | 9 | 0 | 0 | 9 |
| 合计 | 49 | 40 | 0 | 9 |

- **IT 全绿**（api-test-runner，admin `http://localhost:21423` + app `http://localhost:8081`）。
  存证：`test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-0{07..46}/`。
  契约无漂移；`contracts/api-spec.json` 对这些接口只声明 requestBody schema，故契约校验限于请求体自检 + summary 语义。
- **WEB 未执行**：本环境唯一的 Playwright MCP 服务（`playwright-company`）ConnectionRefused，
  web-test-runner 无浏览器可驱动。属环境阻塞，非实现缺陷。
  WEB-003/004/005/006/007 本轮被实质改写（Tab→单列表、弹窗→独立表单页），
  上一轮的 ✅ 已重置为 ⬜ 未测——旧的绿不能证明新行为。
  服务恢复后跑 `/run-web-test --change featured-cycle-item-multi-phase-single-target` 补齐。
- 两端 UT 与 `*IT` 全量绿（admin/app 各自 `./mvnw test` 与 `./mvnw -Dtest='*IT' test`）。
