# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/route/{it,web}.md`（route 域按 `tests/modules.md`「端」列为 `web`，未启用 APP 端，
> 故不产出 APP 用例；app 后端接口的断言落在 IT 用例里）。
> 契约：`RouteSpot` schema 的 `address` 字段于 apply 阶段随代码补入 `contracts/api-spec.json`，用例按既有路径关联。

## 新增用例

- TC-route-IT-028: POST/PUT /api/admin/routes 地点地址可写可改可空（ADDED Scenario: route/路线管理#地点地址可写可改可空）
- TC-route-IT-029: GET /api/app/routes/{id} 地点地址下发且未填时为 null（ADDED Scenario: route/App 端路线查询#地点地址下发且未填时为 null；admin 写入 → app 读出的跨端链路）
- TC-route-WEB-005: 路线表单填写地点地址并回显（ADDED Scenario: route/web 端大使与路线管理页面#路线表单填写地点地址并回显）

## 修改用例

- TC-route-IT-014: GET /api/app/routes/{id} 路线详情返回地点明细与大使信息（MODIFIED: Scenario「路线详情返回地点明细」THEN 增「地址」，预期结果补每个地点含 `address`）

## 需重测用例

行为未变但受本 change 实现影响（admin/app 两侧 `RouteSpot` record 与 DTO 改动 + web 路线表单地点子项改动），需回归确认：

- TC-route-IT-006: POST /api/admin/routes 创建路线含 2 个地点（不带 `address` 的既有请求体仍合法、响应字段不减）
- TC-route-IT-009: PUT /api/admin/routes/{id} 更新路线（不带 `address` 的 PUT 仍合法）
- TC-route-WEB-002: 路线表单维护地点子列表并按添加顺序回显（地点子项新增输入框后保存/回显链路回归）

## 执行汇总

<!-- runner 跑完后由编排 skill 填写：总数 / ✅ / ❌ / 未执行 -->

**IT（api-test-runner，2026-09-04）**：5 条全部 ✅（IT-028、IT-029 新增；IT-014 修改；IT-006、IT-009 重测）。
环境：admin `http://localhost:21423`（test profile）、app `http://localhost:8081`，共库 `localhost:25432/love_space`。
存证：`test-evidence/route-spot-address/<TC ID>/`。
IT-009 首轮 ❌：PUT 传异 `cityName` 被写入，违反 spec「创建后不可变」。根因是既有缺陷（b876cb1 把 `setCityName` 放进 create/update 共用的 `apply()`），非本 change 引入；已修（只在 create 写入）并同步 web 表单编辑态禁用城市输入框，重跑 ✅。
⚠️ 契约漂移（非本 change 引入，未判失败）：api-spec.json `RouteUpsertRequest` 仍声明 `cityId`，实现为 `cityName`；路线 operation 未声明 `responses`。

**UT**：admin 全量 `*Test` 与 app 全量共 238 项全绿；两端 `*IT` 共 30 项全绿。

**WEB（web-test-runner）**：⏸ 未执行——`playwright-company` MCP 本会话 CONNECT_TIMEOUT。待执行：TC-route-WEB-005（新增）、TC-route-WEB-002（需重测）。

**质量门禁**：web lint ✅（0 error）；web build ❌ 与基线同样失败（`vite.config.ts` 缺 `@types/node`，环境问题，`tsc -p tsconfig.app.json` ✅）；npm audit 因 registry 503 未出结果。追溯矩阵已刷新，无 ⚠、无悬空用例。

