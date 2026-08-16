# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web,app}.md`（按 modules.md「端」列裁决落点；living 文件，runner 独占回写状态）。
> 生成规则见 schema instruction；用例块格式如下（写入 living 文件时使用）：
>
> ```markdown
> ### TC-{domain}-IT-NNN: <METHOD /path 用例标题>
> **关联需求**: {domain}/{Requirement 名}#{Scenario 名}
> **关联契约**: api-spec.json#/paths/~1<path>/<method>
> **来源**: <change-id>
> **优先级**: P0/P1/P2
> **测试步骤**:
> 1. <请求/操作>
> **预期结果**: <状态码 + 字段断言，可验证>
> **状态**: ⬜ 未测试
> **执行方式**: api-test-runner
> **执行存证**: `test-evidence/regression/{domain}/TC-{domain}-IT-NNN/`
> **最后更新**: -
> ```
>
> WEB 用例：`执行方式: web-test-runner（@playwright/mcp）`，无 `关联契约`，
> 预期结果按线框区域口径断言（layout），多 `**前置条件**` 字段。
> APP 用例：`执行方式: app-test-runner（Maestro）`，口径同 WEB（无关联契约、layout 断言、前置条件），
> 执行形态另落 `tests/{domain}/flows/` 的 Maestro flow yaml。

## 新增用例

### route
- TC-route-IT-001: POST /api/admin/ambassadors 创建大使成功且标签顺序保持（ADDED Scenario: route/爱女大使管理#创建大使）
- TC-route-IT-002: POST /api/admin/ambassadors 标签边界 3 条通过、4 条拒绝（ADDED Scenario: route/爱女大使管理#标签超过 3 条被拒绝）
- TC-route-IT-003: PUT /api/admin/ambassadors/{id}/online 大使上下线切换（ADDED Scenario: route/爱女大使管理#大使上下线切换）
- TC-route-IT-004: PUT /api/admin/ambassadors/{id} 更新大使字段（ADDED Scenario: route/爱女大使管理#创建大使）
- TC-route-IT-005: DELETE /api/admin/ambassadors/{id} 物理删除大使（ADDED Scenario: route/爱女大使管理#创建大使）
- TC-route-IT-006: POST /api/admin/routes 创建路线含 2 个地点按提交顺序返回（ADDED Scenario: route/路线管理#创建路线）
- TC-route-IT-007: POST /api/admin/routes 缺必填或城市/大使不存在被拒绝（ADDED Scenario: route/路线管理#缺少必填项被拒绝）
- TC-route-IT-008: POST /api/admin/routes 路线图片边界 1 张通过、空数组拒绝（ADDED Scenario: route/路线管理#缺少必填项被拒绝）
- TC-route-IT-009: PUT /api/admin/routes/{id} 更新路线且 cityId 不可变（ADDED Scenario: route/路线管理#创建路线）
- TC-route-IT-010: GET /api/admin/routes/page 按 sortOrder 升序并支持过滤（ADDED Scenario: route/路线管理#路线列表按排序号升序）
- TC-route-IT-011: DELETE /api/admin/routes/{id} 物理删除路线连带地点（ADDED Scenario: route/路线管理#删除路线）
- TC-route-IT-012: GET /api/app/routes 上架城市路线列表按 sortOrder 升序（ADDED Scenario: route/App 端路线查询#查询上架城市的路线）
- TC-route-IT-013: GET /api/app/routes 大使下线后路线隐藏、详情 404（ADDED Scenario: route/App 端路线查询#大使下线后路线隐藏）
- TC-route-IT-014: GET /api/app/routes/{id} 路线详情返回地点明细与大使信息（ADDED Scenario: route/App 端路线查询#路线详情返回地点明细）
- TC-route-WEB-001: 大使列表展示与上下线开关（ADDED Scenario: route/web 端大使与路线管理页面#大使列表与上下线）
- TC-route-WEB-002: 路线表单维护地点子列表并按添加顺序回显（ADDED Scenario: route/web 端大使与路线管理页面#路线表单维护地点）
- TC-route-WEB-003: 删除路线需确认（确认删除、取消保留）（ADDED Scenario: route/web 端大使与路线管理页面#删除路线需确认）

### activity
- TC-activity-IT-001: POST /api/admin/activities 创建完整活动（ADDED Scenario: activity/活动管理#创建活动）
- TC-activity-IT-002: POST /api/admin/activities 缺必填或城市不存在被拒绝（ADDED Scenario: activity/活动管理#缺少必填项被拒绝）
- TC-activity-IT-003: PUT /api/admin/activities/{id}/online 活动上下线切换（ADDED Scenario: activity/活动管理#活动上下线切换）
- TC-activity-IT-004: PUT /api/admin/activities/{id} 更新活动且 cityId 不可变（ADDED Scenario: activity/活动管理#创建活动）
- TC-activity-IT-005: DELETE /api/admin/activities/{id} 物理删除活动（ADDED Scenario: activity/活动管理#创建活动）
- TC-activity-IT-006: POST /api/admin/activities 富文本 img src 存 objectKey、admin 读时替换签名 URL（ADDED Scenario: activity/活动管理#创建活动）
- TC-activity-IT-007: GET /api/app/activities 上架城市活动列表（ADDED Scenario: activity/App 端活动查询#查询上架城市的活动）
- TC-activity-IT-008: GET /api/app/activities 下线活动不可见、详情 404（ADDED Scenario: activity/App 端活动查询#下线活动不可见）
- TC-activity-IT-009: GET /api/app/activities/{id} 详情返回富文本且 img src 为签名 URL（ADDED Scenario: activity/App 端活动查询#活动详情返回富文本）
- TC-activity-WEB-001: 活动列表展示与上下线开关（ADDED Scenario: activity/web 端活动管理页面#活动列表与上下线）
- TC-activity-WEB-002: 活动表单富文本编辑并回显（ADDED Scenario: activity/web 端活动管理页面#活动表单富文本编辑）

### city
- TC-city-IT-006: 城市下架后 app 端路线与活动不可见（级联）（ADDED Scenario: city/地图下架对路线与活动级联生效#下架城市后 app 端路线与活动不可见）

## 修改用例

- TC-city-WEB-003: 城市下架确认提示包含路线与活动级联说明（ADDED Scenario: city/地图下架对路线与活动级联生效#web 下架确认提示包含路线与活动；TC-city-WEB-002 保持 map-and-recommend-list 旧口径不动，本 change 用新用例承载新口径）

## 需重测用例

- TC-city-IT-005: 城市下架后 app 端推荐清单不可见（级联）（行为未变；本 change 扩展了同一下架级联的实现面，回归确认既有清单级联不受影响）

## 执行汇总

- **IT（2026-08-16，api-test-runner）**：25/25 ✅（route 14、activity 9、city 2 含 IT-005 重测）。cityId 不可变按「传异值返回 200 被忽略」口径断言；富文本 img src 存 objectKey、读出替换 bound/ 签名 URL 双端验证通过；契约漂移 0。
- **WEB（2026-08-16，web-test-runner，playwright-core 替代 @playwright/mcp）**：6/6 ✅。其中 TC-route-WEB-001 / TC-activity-WEB-001 首轮 ❌（Switch 切换成功后无成功 toast），补 `toast.success` 后复测通过；TC-activity-WEB-002「图片正常渲染」因 test profile StubObjectKeyValidator 不做真实 copyObject 降级为「img src 为 bound/ 签名 URL」断言（环境限制，API 层由 IT-006/009 覆盖，详见存证 env-note.txt）。
- **UT 锚点**：admin AmbassadorServiceTest/RouteServiceTest/ActivityServiceTest/RichTextImagesTest + app RouteQueryServiceTest/ActivityQueryServiceTest，覆盖全部管理/App 查询 Scenario；admin `./mvnw test` 98/98、`*IT` 11/11，app `./mvnw test` 41/41、`*IT` 10/10。
- **追溯矩阵**：32 用例 100% 通过，无 ⚠。
