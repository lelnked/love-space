# 追溯矩阵（交付核对）：ambassador-route-activity

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change ambassador-route-activity`

## 需求与场景
- **activity/App 端活动查询**: 查询上架城市的活动 / 下线活动不可见 / 活动详情返回富文本
- **activity/web 端活动管理页面**: 活动列表与上下线 / 活动表单富文本编辑
- **activity/活动管理**: 创建活动 / 缺少必填项被拒绝 / 活动上下线切换
- **city/地图下架对路线与活动级联生效**: 下架城市后 app 端路线与活动不可见 / web 下架确认提示包含路线与活动
- **route/App 端路线查询**: 查询上架城市的路线 / 大使下线后路线隐藏 / 路线详情返回地点明细
- **route/web 端大使与路线管理页面**: 大使列表与上下线 / 路线表单维护地点 / 删除路线需确认
- **route/爱女大使管理**: 创建大使 / 标签超过 3 条被拒绝 / 大使上下线切换
- **route/路线管理**: 创建路线 / 缺少必填项被拒绝 / 路线列表按排序号升序 / 删除路线

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-activity-IT-001 | POST /api/admin/activities 创建完整活动 | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-001/` | ✅ |
| TC-activity-IT-002 | POST /api/admin/activities 缺必填或城市不存在被拒绝 | activity/活动管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-002/` | ✅ |
| TC-activity-IT-003 | PUT /api/admin/activities/{id}/online 活动上下线切换 | activity/活动管理#活动上下线切换 | api-spec.json#/paths/~1api~1admin~1activities~1{id}~1online/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-003/` | ✅ |
| TC-activity-IT-004 | PUT /api/admin/activities/{id} 更新活动且 cityId 不可变 | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities~1{id}/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-004/` | ✅ |
| TC-activity-IT-005 | DELETE /api/admin/activities/{id} 物理删除活动 | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities~1{id}/delete | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-005/` | ✅ |
| TC-activity-IT-006 | POST /api/admin/activities 富文本 img src 存 objectKey、admin 读时替换签名 URL | activity/活动管理#创建活动 | api-spec.json#/paths/~1api~1admin~1activities/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-006/` | ✅ |
| TC-activity-IT-007 | GET /api/app/activities 上架城市活动列表 | activity/App 端活动查询#查询上架城市的活动 | api-spec.json#/paths/~1api~1app~1activities/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-007/` | ✅ |
| TC-activity-IT-008 | GET /api/app/activities 下线活动不可见、详情 404 | activity/App 端活动查询#下线活动不可见 | api-spec.json#/paths/~1api~1app~1activities~1{id}/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-008/` | ✅ |
| TC-activity-IT-009 | GET /api/app/activities/{id} 详情返回富文本且 img src 为签名 URL | activity/App 端活动查询#活动详情返回富文本 | api-spec.json#/paths/~1api~1app~1activities~1{id}/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-activity-IT-009/` | ✅ |
| TC-activity-WEB-001 | 活动列表展示与上下线开关 | activity/web 端活动管理页面#活动列表与上下线 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-activity-WEB-001/` | ✅ |
| TC-activity-WEB-002 | 活动表单富文本编辑并回显 | activity/web 端活动管理页面#活动表单富文本编辑 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-activity-WEB-002/` | ✅ |
| TC-city-IT-005 | 城市下架后 app 端推荐清单不可见（级联） | city/地图下架对推荐清单级联生效#下架城市后 app 端清单不可见 | api-spec.json#/paths/~1api~1app~1recommend-lists/get | map-and-recommend-list | IT | `test-evidence/ambassador-route-activity/TC-city-IT-005/` | ✅ |
| TC-city-IT-006 | 城市下架后 app 端路线与活动不可见（级联） | city/地图下架对路线与活动级联生效#下架城市后 app 端路线与活动不可见 | api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1activities/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-city-IT-006/` | ✅ |
| TC-city-WEB-002 | 城市下架确认提示包含推荐清单级联说明 | city/地图下架对推荐清单级联生效#web 下架确认提示包含清单 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-city-WEB-002/` | ✅ |
| TC-city-WEB-003 | 城市下架确认提示包含路线与活动级联说明 | city/地图下架对路线与活动级联生效#web 下架确认提示包含路线与活动 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-city-WEB-003/` | ✅ |
| TC-route-IT-001 | POST /api/admin/ambassadors 创建大使成功且标签顺序保持 | route/爱女大使管理#创建大使 | api-spec.json#/paths/~1api~1admin~1ambassadors/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-001/` | ✅ |
| TC-route-IT-002 | POST /api/admin/ambassadors 标签边界 3 条通过、4 条拒绝 | route/爱女大使管理#标签超过 3 条被拒绝 | api-spec.json#/paths/~1api~1admin~1ambassadors/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-002/` | ✅ |
| TC-route-IT-003 | PUT /api/admin/ambassadors/{id}/online 大使上下线切换 | route/爱女大使管理#大使上下线切换 | api-spec.json#/paths/~1api~1admin~1ambassadors~1{id}~1online/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-003/` | ✅ |
| TC-route-IT-004 | PUT /api/admin/ambassadors/{id} 更新大使字段 | route/爱女大使管理#创建大使 | api-spec.json#/paths/~1api~1admin~1ambassadors~1{id}/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-004/` | ✅ |
| TC-route-IT-005 | DELETE /api/admin/ambassadors/{id} 物理删除大使 | route/爱女大使管理#创建大使 | api-spec.json#/paths/~1api~1admin~1ambassadors~1{id}/delete | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-005/` | ✅ |
| TC-route-IT-006 | POST /api/admin/routes 创建路线含 2 个地点按提交顺序返回 | route/路线管理#创建路线 | api-spec.json#/paths/~1api~1admin~1routes/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-006/` | ✅ |
| TC-route-IT-007 | POST /api/admin/routes 缺必填或城市/大使不存在被拒绝 | route/路线管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1routes/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-007/` | ✅ |
| TC-route-IT-008 | POST /api/admin/routes 路线图片边界 1 张通过、空数组拒绝 | route/路线管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1routes/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-008/` | ✅ |
| TC-route-IT-009 | PUT /api/admin/routes/{id} 更新路线且 cityId 不可变 | route/路线管理#创建路线 | api-spec.json#/paths/~1api~1admin~1routes~1{id}/put | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-009/` | ✅ |
| TC-route-IT-010 | GET /api/admin/routes/page 按 sortOrder 升序并支持过滤 | route/路线管理#路线列表按排序号升序 | api-spec.json#/paths/~1api~1admin~1routes~1page/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-010/` | ✅ |
| TC-route-IT-011 | DELETE /api/admin/routes/{id} 物理删除路线连带地点 | route/路线管理#删除路线 | api-spec.json#/paths/~1api~1admin~1routes~1{id}/delete | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-011/` | ✅ |
| TC-route-IT-012 | GET /api/app/routes 上架城市路线列表按 sortOrder 升序 | route/App 端路线查询#查询上架城市的路线 | api-spec.json#/paths/~1api~1app~1routes/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-012/` | ✅ |
| TC-route-IT-013 | GET /api/app/routes 大使下线后路线隐藏、详情 404 | route/App 端路线查询#大使下线后路线隐藏 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-013/` | ✅ |
| TC-route-IT-014 | GET /api/app/routes/{id} 路线详情返回地点明细与大使信息 | route/App 端路线查询#路线详情返回地点明细 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-014/` | ✅ |
| TC-route-WEB-001 | 大使列表展示与上下线开关 | route/web 端大使与路线管理页面#大使列表与上下线 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-route-WEB-001/` | ✅ |
| TC-route-WEB-002 | 路线表单维护地点子列表并按添加顺序回显 | route/web 端大使与路线管理页面#路线表单维护地点 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-route-WEB-002/` | ✅ |
| TC-route-WEB-003 | 删除路线需确认（确认删除、取消保留） | route/web 端大使与路线管理页面#删除路线需确认 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-route-WEB-003/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：32
- ✅ 通过：32 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
