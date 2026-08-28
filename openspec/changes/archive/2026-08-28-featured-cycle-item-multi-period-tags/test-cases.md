# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web,app}.md`（按 modules.md「端」列裁决落点；living 文件，runner 独占回写状态）。

域裁决：本次行为全部落在 `featured` 域（已注册，接口路径前缀已含 `/api/admin/featured-cycle-items/*` 与
`/api/app/featured-cycle-items`）。注册表 `tests/modules.md` 无需修改。

本 change 含两批变更，用例分两部分登记：

- **A. `period` 改周期集合数组**（app 只读接口）——IT 新增 028~032、改写 016~021/024/025。
- **B. 三列合并为 `target_id`**（DB 迁移 + admin 写/读接口 + app 读接口 + web 表单内部绑定）——IT 新增 033、
  改写 007~012 与 016/018/021/025/032 的 id 断言；web 端无 spec delta，不新增用例，仅回归。

web 端裁决：「web 端周期推荐页面」Requirement 无 delta（页面与表单交互行为未变，仅内部字段绑定由三个
state 收敛为单个 `targetId`），故**不新增 web 用例**；但 `Form.tsx` 与 `api/featuredCycleItems.ts` 有代码改动，
既有 web 用例需回归（见「需重测用例」）。

⚠️ 待补契约（两处）：

1. `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 response schema，
   `period` 的数组类型与 `targetId` 均无契约可断言，IT 用例按 delta spec 断言实际响应体。
2. `api-spec.json#/components/schemas/FeaturedCycleItemUpsertRequest` 目前仍是
   `activityId` / `routeId` / `articleId` 三字段（tasks 6.1 待做），`targetId` 及其必填约束进契约前，
   admin 写接口用例只做响应体断言，不做 schema 断言。admin 读接口 `FeaturedCycleItemResponse`
   在契约中同样未声明 schema。

## 新增用例

| TC ID | 标题 | 关联 Scenario | 优先级 | 象限 | 批次 |
|---|---|---|---|---|---|
| TC-featured-IT-028 | 同一 target 跨周期时两条均下发全部周期 | 同一 target 跨周期时下发全部周期 | P0 | happy | A |
| TC-featured-IT-029 | `?period=` 过滤后 period 数组仍含其他周期 | 按周期过滤时 period 数组仍含其他周期 | P0 | happy | A |
| TC-featured-IT-030 | `?type=&period=` 类型过滤不影响 period 数组 | 类型过滤不影响 period 数组 | P1 | boundary | A |
| TC-featured-IT-031 | 不可下发条目不贡献周期 | 不可下发条目不贡献周期 | P0 | state | A |
| TC-featured-IT-032 | 不同 target 的周期集合互不影响 | 不同 target 的周期集合互不影响 | P1 | boundary | A |
| TC-featured-IT-033 | POST/PUT 缺 `targetId` 被拒绝 | 缺少 targetId 被拒绝（ADDED Scenario） | P0 | error | B |

批次 A 的 error 象限沿用既有 TC-featured-IT-023 / TC-featured-IT-027（非法 `type` / `period` 返回 400，行为未变）。

批次 B 的写接口（`POST` / `PUT /api/admin/featured-cycle-items`）四象限覆盖：
happy = 007/008/009（三类型各一条 `targetId` 落库与回显）、error = 010/011/033、
state = 012（更新按持久化类型分派 `targetId` 校验）、boundary = 011 步骤 4（`targetId` 存在但属于别的实体表）。

## 修改用例

原地改断言、保留原 TC ID、`来源` 改为 `featured-cycle-item-multi-period-tags`：

| TC ID | 改动 | 批次 |
|---|---|---|
| TC-featured-IT-007 | 请求体 `activityId` → `targetId`；响应断言 `targetId` 等于活动 id、`relatedTitle` 回显、不再出现三个旧 id 字段 | B |
| TC-featured-IT-008 | 请求体 `routeId` → `targetId`；响应断言同上（路线） | B |
| TC-featured-IT-009 | 请求体 `articleId` → `targetId`；响应断言同上（文章） | B |
| TC-featured-IT-010 | 三个必填项缺失场景的合法 id 字段统一写为 `targetId` | B |
| TC-featured-IT-011 | 三次不存在 id 改传 `targetId`；新增步骤 4：`targetId` 存在但不属于该 `type` 的实体表，仍 400（跨表不命中） | B |
| TC-featured-IT-012 | 更新请求体去掉 `articleId`，改为按持久化类型提供 `targetId`；新增步骤 4 断言存在性校验按持久化类型分派（传文章 id → 400「关联活动不存在」） | B |
| TC-featured-IT-016 | 标题与断言：`period` 单值 → 数组 `["MENSTRUAL"]` / `["OVULATION"]`，显式断言是 JSON 数组；`activityId`/`articleId` → `targetId` | A+B |
| TC-featured-IT-017 | 前置步骤中的 `period 均为 MENSTRUAL` → `["MENSTRUAL"]` | A |
| TC-featured-IT-018 | `period=OVULATION` → `["OVULATION"]`；`routeId` → `targetId` | A+B |
| TC-featured-IT-019 | 5 条条目 `period` → `["MENSTRUAL"]`，补注「各条目只配在经期」 | A |
| TC-featured-IT-020 | `period=OVULATION` → `["OVULATION"]` | A |
| TC-featured-IT-021 | `period=MENSTRUAL` → `["MENSTRUAL"]`；`articleId` → `targetId` | A+B |
| TC-featured-IT-024 | `period` 断言改数组，前置补「每个 target 只配在一个周期」以固定数组内容 | A |
| TC-featured-IT-025 | `period=MENSTRUAL` → `["MENSTRUAL"]`；`articleId` → `targetId` | A+B |
| TC-featured-IT-032 | 分组 key 表述 `(type, 关联 id)` → `(type, targetId)` | B |

## 需重测用例

### IT（20 条）

新增 6 条 + 修改 14 条全部需跑：

TC-featured-IT-007、008、009、010、011、012、016、017、018、019、020、021、024、025、028、029、030、031、032、033

未受影响、不重测：TC-featured-IT-001~006（精选推荐，另一 Requirement）、013、014、015（分页/上下线/删除，不涉及 id 字段）、
022、023、026、027（空数组与 400，不断言 `period` 值也不断言 id）。

### WEB（4 条，回归确认，无新增用例）

`Form.tsx` 三个 state 合并为 `targetId`、`api/featuredCycleItems.ts` 类型收敛，页面行为应完全不变，跑既有用例确认无回归：

| TC ID | 为何重测 |
|---|---|
| TC-featured-WEB-003 | 周期推荐列表页走同一份 api 类型，「关联实体名」列依赖 `relatedTitle`（本次保留不变） |
| TC-featured-WEB-004 | 表单按类型切换字段块 + 切换时清空上一类型内容——`targetId` 单 state 后清空逻辑改写，风险最高 |
| TC-featured-WEB-005 | 文章类型选中后自动带出主标题，依赖 `targetId` 的 onChange |
| TC-featured-WEB-006 | 新增周期推荐端到端（提交载荷字段名改为 `targetId`） |

不重测：TC-featured-WEB-001/002（精选推荐页，另一页面）、TC-featured-WEB-007（上下线与删除，不经表单字段绑定）。

## 执行汇总

| 端 | 用例数 | Runner | 命令 |
|---|---|---|---|
| IT | 20 | api-test-runner | `/run-api-test --change featured-cycle-item-multi-period-tags` |
| WEB | 4（全为回归，无新增） | web-test-runner | `/run-web-test --change featured-cycle-item-multi-period-tags` |
| APP | 0 | — | featured 域未登记 app 端 |
