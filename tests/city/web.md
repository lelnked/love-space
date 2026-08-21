# city WEB 用例

### TC-city-WEB-001: 侧栏与页面标题展示「地图管理」
**关联需求**: city/后台入口更名为地图管理#侧栏与页面标题展示地图管理
**来源**: map-and-recommend-list
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.100.117.79:5173/love-space/signin
**测试步骤**:
1. 登录后台，查看侧栏导航
2. 点击侧栏「地图管理」菜单项
**预期结果**: 侧栏菜单项文案为「地图管理」（不再出现「城市管理」）；跳转后 URL 仍为 /love-space/cities，页面标题显示「地图管理」，DataTable 城市列表正常渲染
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/map-and-recommend-list/TC-city-WEB-001/`
**最后更新**: 2026-08-16

### TC-city-WEB-002: 城市下架确认提示包含推荐清单级联说明
**关联需求**: city/地图下架对推荐清单级联生效#web 下架确认提示包含清单
**来源**: map-and-recommend-list
**优先级**: P1
**前置条件**: Manager 已登录；列表中存在至少一个「上架」状态的城市
**测试步骤**:
1. 进入 /love-space/cities，对某上架城市点击下架操作
2. 读取弹出的确认提示文案（本用例点「取消」，不实际下架）
**预期结果**: 确认弹窗出现，文案说明级联影响范围包含商户、Banner、推荐清单；点取消后城市状态仍为「上架」
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/map-and-recommend-list/TC-city-WEB-002/`
**最后更新**: 2026-08-16

### TC-city-WEB-003: 城市下架确认提示包含活动级联说明且不含路线
**关联需求**: city/地图下架对活动级联生效#web 下架确认提示不含路线
**来源**: route-decouple-city-online
**优先级**: P1
**前置条件**: Manager 已登录；列表中存在至少一个「上架」状态的城市
**测试步骤**:
1. 进入 /love-space/cities，对某上架城市点击下架操作
2. 读取弹出的确认提示文案（本用例点「取消」，不实际下架）
**预期结果**: 确认弹窗出现，文案说明级联影响范围包含商户、Banner、推荐清单、活动，且文案中**不出现「路线」**（路线可见性不再随城市下架变化）；点取消后城市状态仍为「上架」
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/route-decouple-city-online/TC-city-WEB-003/`
**最后更新**: -

### TC-city-WEB-004: 城市下架确认提示包含精选推荐级联说明
**关联需求**: city/地图下架对精选推荐级联生效#web 下架确认提示包含精选推荐
**来源**: route-decouple-city-online
**优先级**: P1
**前置条件**: Manager 已登录；列表中存在至少一个「上架」状态的城市
**测试步骤**:
1. 进入 /love-space/cities，对某上架城市点击下架操作
2. 读取弹出的确认提示文案（本用例点「取消」，不实际下架）
**预期结果**: 确认弹窗出现，文案说明级联影响范围同时包含商户、Banner、推荐清单、活动、精选推荐（不含路线）；点取消后城市状态仍为「上架」
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/route-decouple-city-online/TC-city-WEB-004/`
**最后更新**: -
