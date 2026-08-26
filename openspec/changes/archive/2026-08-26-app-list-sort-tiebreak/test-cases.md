# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web}.md`（living 文件，runner 独占回写状态）。
> 本 change 为 app 后端行为变更，无 web 端影响，故只产 IT 用例。

## 新增用例

- TC-merchant-IT-007: GET /api/app/categories/page 同排序号分类按创建时间倒序（ADDED Scenario: merchant/App 端带排序号列表的排序口径#分类列表同序号按创建时间倒序）
- TC-merchant-IT-008: GET /api/app/categories/page 排序号优先于创建时间（ADDED Scenario: merchant/App 端带排序号列表的排序口径#排序号不同时以排序号为准）
- TC-merchant-IT-009: GET /api/app/merchants/{merchantId}/reviews 同排序号评价按创建时间倒序（ADDED Scenario: merchant/App 端带排序号列表的排序口径#商户评价同序号按创建时间倒序）
- TC-recommend-list-IT-019: GET /api/app/recommend-lists 同排序号清单按创建时间倒序（ADDED Scenario: recommend-list/App 端清单与清单内商户查询#同排序号清单按创建时间倒序）
- TC-route-IT-024: GET /api/app/routes 同排序号路线按创建时间倒序（ADDED Scenario: route/App 端路线查询#同排序号路线按创建时间倒序）

## 修改用例

- TC-banner-IT-013: GET /api/app/banners 排序号并列时按创建时间倒序（MODIFIED: 原用例断言「先创建的 C 在前」，本 change 把 tie-break 由 `createdAt ASC` 翻成 `DESC`，改断言为「后创建的 D 在前」；标题、关联需求、来源同步更新，TC ID 保留）

## 需重测用例

行为未变，但落在本 change 修改的同一查询路径上，需回归确认排序号主序与其余断言未被 tie-break 改动带偏：

- TC-banner-IT-012: GET /api/app/banners 按展示位返回上架 Banner 并按排序号升序
- TC-recommend-list-IT-011: GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序
- TC-recommend-list-IT-012: GET /api/app/recommend-lists/{id} 详情按清单保存顺序返回上架商户四字段
- TC-recommend-list-IT-015: GET /api/app/merchants/page 商户列表不受清单影响（锚定 `weight DESC, createdAt DESC` 口径，本次零改动，仅验证未回退）
- TC-route-IT-012: GET /api/app/routes?cityName= 按城市名查路线列表并按 sortOrder 升序

## 执行汇总

2026-08-26 由 api-test-runner 执行（app=http://localhost:8081，admin=http://localhost:21423）：

- 总数 15 / ✅ 15 / ❌ 0 / 未执行 0
- 存证：`test-evidence/app-list-sort-tiebreak/<TC完整ID>/{exchange.md,assertions.md}`（15 个目录）
- 追溯矩阵：22 条 100% 通过，0 失败 0 未测

清单原列 11 条，追加跑了 4 条被 MODIFIED 全文复制带入本 change requirement 的既有用例
（TC-banner-IT-014/015/016、TC-route-IT-019），使矩阵内本 change 涉及的场景全部有绿色用例支撑。

Java 侧同批跑绿：UT 64 + IT 19（`./mvnw test` 与 `./mvnw test -Dtest='*IT'`）。

### 执行中发现的契约缺口（均不判失败，非本 change 引入）

1. `contracts/api-spec.json` 缺 `/api/app/categories/page` 与 `/api/app/merchants/{merchantId}/reviews` 条目
   （已记于 design.md Open Questions）
2. `#/components/schemas/RouteUpsertRequest` 声明必填 `cityId`(uuid)，admin 实现实际字段是 `cityName`(string)，
   用 `cityId` 提交被 400 拒绝——只影响 admin 写接口的契约文档，建议后续开小 change 修正
3. `#/paths/~1api~1app~1banners/get` 未声明 401 响应形状（实现返回 RFC7807 `application/problem+json`）

### 遗留告警

追溯矩阵仍有 3 条 ⚠「无 WEB/APP 用例且无 UT(@scenario) 覆盖」：banner 的「下架 Banner 不下发」
「关联城市下架时条目被剔除」「缺少 API-key 返回 401」。三者均有已通过的 IT 用例
（TC-banner-IT-014/015/016），缺的只是 Java 侧 `@scenario` 注释；它们因 MODIFIED 全文复制才进入本
change 的场景清单，非本次引入，未在本 change 内补。
