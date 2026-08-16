# 受影响测试用例清单

## 新增用例

### merchant
- TC-merchant-IT-001: POST /api/admin/merchants 创建商户保存推荐理由（ADDED Scenario: merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由）
- TC-merchant-IT-002: PUT /api/admin/merchants/{id} 更新推荐理由（ADDED Scenario: merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由）
- TC-merchant-IT-003: POST /api/admin/merchants 推荐理由 2000 字边界通过（ADDED Scenario: merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由）
- TC-merchant-IT-004: POST /api/admin/merchants 推荐理由 2001 字被拒绝（ADDED Scenario: merchant/商户编辑推荐理由#推荐理由超长被拒绝）
- TC-merchant-IT-005: POST /api/admin/merchants 不填推荐理由创建成功（ADDED Scenario: merchant/商户编辑推荐理由#推荐理由可为空）
- TC-merchant-IT-006: GET /api/app/merchants/{id} app 端详情返回推荐理由（ADDED Scenario: merchant/商户编辑推荐理由#app 端商户详情返回推荐理由）
- TC-merchant-WEB-001: 商户表单录入推荐理由并回显（ADDED Scenario: merchant/商户编辑推荐理由#web 商户表单录入推荐理由）
- TC-merchant-WEB-002: 推荐理由超长表单校验提示（ADDED Scenario: merchant/商户编辑推荐理由#web 商户表单录入推荐理由）

### city
- TC-city-IT-001: POST /api/admin/cities 创建城市保存编辑说（ADDED Scenario: city/地图编辑说#admin 保存编辑说）
- TC-city-IT-002: PUT /api/admin/cities/{id} 编辑说 200 字边界通过（ADDED Scenario: city/地图编辑说#admin 保存编辑说）
- TC-city-IT-003: PUT /api/admin/cities/{id} 编辑说 201 字被拒绝（ADDED Scenario: city/地图编辑说#编辑说超长被拒绝）
- TC-city-IT-004: GET /api/app/cities app 端城市列表返回编辑说（ADDED Scenario: city/地图编辑说#app 端城市数据返回编辑说）
- TC-city-IT-005: 城市下架后 app 端推荐清单不可见（级联）（ADDED Scenario: city/地图下架对推荐清单级联生效#下架城市后 app 端清单不可见）
- TC-city-WEB-001: 侧栏与页面标题展示「地图管理」（ADDED Scenario: city/后台入口更名为地图管理#侧栏与页面标题展示地图管理）
- TC-city-WEB-002: 城市下架确认提示包含推荐清单级联说明（ADDED Scenario: city/地图下架对推荐清单级联生效#web 下架确认提示包含清单）

### recommend-list
- TC-recommend-list-IT-001: POST /api/admin/recommend-lists 创建清单成功（ADDED Scenario: recommend-list/推荐清单管理#创建清单）
- TC-recommend-list-IT-002: POST /api/admin/recommend-lists 缺少必填项被拒绝（ADDED Scenario: recommend-list/推荐清单管理#缺少必填项被拒绝）
- TC-recommend-list-IT-003: POST /api/admin/recommend-lists 不传 sortOrder 默认 0（ADDED Scenario: recommend-list/推荐清单管理#创建清单）
- TC-recommend-list-IT-004: PUT /api/admin/recommend-lists/{id} 更新清单且 cityId 不可变（ADDED Scenario: recommend-list/推荐清单管理#创建清单）
- TC-recommend-list-IT-005: DELETE /api/admin/recommend-lists/{id} 物理删除含商户关联的清单（ADDED Scenario: recommend-list/推荐清单管理#删除清单）
- TC-recommend-list-IT-006: GET /api/admin/recommend-lists/page 按 sortOrder 升序并支持过滤（ADDED Scenario: recommend-list/推荐清单管理#清单列表按排序号升序）
- TC-recommend-list-IT-007: PUT /api/admin/recommend-lists/{id}/merchants 全量替换本城市商户（ADDED Scenario: recommend-list/清单内商户维护#添加本城市商户）
- TC-recommend-list-IT-008: PUT /api/admin/recommend-lists/{id}/merchants 跨城市商户被拒绝（ADDED Scenario: recommend-list/清单内商户维护#拒绝跨城市商户）
- TC-recommend-list-IT-009: PUT /api/admin/recommend-lists/{id}/merchants 重复商户被拒绝（ADDED Scenario: recommend-list/清单内商户维护#重复添加同一商户被拒绝）
- TC-recommend-list-IT-010: PUT /api/admin/recommend-lists/{id}/merchants 移除商户不影响商户本身（ADDED Scenario: recommend-list/清单内商户维护#从清单移除商户）
- TC-recommend-list-IT-011: GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序（ADDED Scenario: recommend-list/App 端清单查询#查询上架城市的清单）
- TC-recommend-list-IT-012: GET /api/app/recommend-lists/{id} 详情返回商户明细按排序升序（ADDED Scenario: recommend-list/App 端清单查询#清单详情返回商户明细）
- TC-recommend-list-IT-013: GET /api/app/recommend-lists 下架城市清单不可见、详情 404（ADDED Scenario: recommend-list/App 端清单查询#下架城市清单不可见）
- TC-recommend-list-WEB-001: 推荐清单列表与城市筛选（ADDED Scenario: recommend-list/web 端推荐清单管理页面#清单列表与筛选）
- TC-recommend-list-WEB-002: 清单编辑界面维护商户（仅本城市可选）（ADDED Scenario: recommend-list/web 端推荐清单管理页面#维护清单商户）
- TC-recommend-list-WEB-003: 删除清单需确认（确认删除、取消保留）（ADDED Scenario: recommend-list/web 端推荐清单管理页面#删除清单需确认）

## 修改用例
（无）

## 需重测用例
（无——三域 living 用例均为首建）

## 执行汇总

### IT（2026-08-16）

- **结果：24/24 ✅**（merchant 6 / city 5 / recommend-list 13）
- baseUrl：admin `http://localhost:21423`（test profile）、app `http://localhost:8081`（`X-API-Key: test-api-key`）——均在 tests/modules.md 白名单内
- 存证：`test-evidence/map-and-recommend-list/<TC-ID>/`（request/response/assertions）
- 状态已回写 `tests/{merchant,city,recommend-list}/it.md`
- 判定说明：
  - TC-recommend-list-IT-005 预期由 404 调整为 400（admin 端「资源不存在」全局口径 IllegalArgumentException→400，与商户/城市一致，design.md 决策 9）
  - 测试实例无真实 OSS，启用 `@Profile("test")` 的 `StubObjectKeyValidator`（design.md 决策 10），生产行为不变

### WEB（2026-08-16）

- **结果：7/7 ✅**（city 2 / merchant 2 / recommend-list 3）
- 前端 `http://100.100.117.79:5173/love-space/`（vite `--mode test`）、后端 `http://100.100.117.79:21423`——均在白名单内
- 存证：`test-evidence/map-and-recommend-list/<TC-ID>/`（关键步骤截图 + run-summary.json）
- 状态已回写 `tests/{city,merchant,recommend-list}/web.md`
- 判定说明：
  - TC-city-WEB-002 按本 change 口径（商户/Banner/推荐清单）通过；ambassador-route-activity 追加的「路线、活动」文案口径拆为新用例 TC-city-WEB-003，归属该 change 交付轮执行
  - TC-merchant-WEB-001 保存成功判定采用「跳回商户列表 + 重开回显」（该表单既有口径，无独立成功 toast）
  - 执行方式：会话无 @playwright/mcp 工具，改用 playwright-core 驱动本机 chromium，导航基址/清 localStorage/存证要求等效执行

### 追溯矩阵（2026-08-16）

- `traceability-matrix.md` 无 ⚠：全部 Scenario 除 IT 外均有 WEB 用例或带 `@scenario` 锚点的 UT 覆盖（本轮补 6 条 UT/锚点，admin+app 相关测试类全绿）
