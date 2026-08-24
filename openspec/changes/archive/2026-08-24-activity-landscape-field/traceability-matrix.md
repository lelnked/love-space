# 追溯矩阵（交付核对）：activity-landscape-field

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change activity-landscape-field（归档前生成）`

## 需求与场景
- **activity/App 端活动查询**: 查询上架城市的活动 / 下线活动不可见 / 活动详情返回富文本 / 活动详情返回景观
- **activity/web 端活动管理页面**: 活动列表与上下线 / 活动表单富文本编辑 / 活动表单填写景观并回显
- **activity/活动管理**: 创建活动 / 缺少必填项被拒绝 / 活动上下线切换 / 景观字段可写可改可空

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
| TC-activity-IT-020 | 活动景观字段贯通 admin 写入与 admin/app 查询 | activity/活动管理#景观字段可写可改可空 | api-spec.json#/components/schemas/ActivityUpsertRequest | activity-landscape-field | IT | `test-evidence/activity-landscape-field/TC-activity-IT-020/` | ✅ |
| TC-activity-WEB-001 | 活动列表展示与上下线开关 | activity/web 端活动管理页面#活动列表与上下线 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-activity-WEB-001/` | ✅ |
| TC-activity-WEB-002 | 活动表单富文本编辑并回显 | activity/web 端活动管理页面#活动表单富文本编辑 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-activity-WEB-002/` | ⬜ |
| TC-activity-WEB-003 | 活动表单填写景观并回显 | activity/web 端活动管理页面#活动表单填写景观并回显 | - | activity-landscape-field | WEB | `test-evidence/activity-landscape-field/TC-activity-WEB-003/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：13
- ✅ 通过：12 (92.3%)
- ❌ 失败：0
- ⬜ 未测：1
