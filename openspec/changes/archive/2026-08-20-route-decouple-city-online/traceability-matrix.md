# 追溯矩阵（交付核对）：route-decouple-city-online

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change route-decouple-city-online`

## 需求与场景
- **city/地图下架对活动级联生效**: 下架城市后 app 端活动不可见 / 下架城市后 app 端路线仍可见 / web 下架确认提示不含路线
- **city/地图下架对精选推荐级联生效**: 下架城市后 app 端精选推荐不可见 / web 下架确认提示包含精选推荐
- **city/城市下存在路线时禁止删除**: 有路线的城市不能删除 / 路线清空后可删除城市
- **featured/App 端周期推荐查询**: 查询四个周期的推荐列表 / 关联实体不可见时条目不下发 / 城市未上架不影响路线类条目 / 大使下线连带隐藏路线类条目 / 组内按排序号升序
- **route/App 端路线查询**: 查询上架城市的路线 / 未上架城市的路线仍可见 / 大使下线后路线隐藏 / 路线详情返回地点明细
- **route/web 端大使与路线管理页面**: 大使列表与上下线 / 路线表单可选未上架城市 / 路线表单维护地点 / 删除路线需确认

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-city-IT-006 | 城市下架后 app 端活动不可见（级联），路线不受影响 | city/地图下架对活动级联生效#下架城市后 app 端活动不可见 | api-spec.json#/paths/~1api~1app~1activities/get | route-decouple-city-online | IT | `test-evidence/route-decouple-city-online/TC-city-IT-006/` | ✅ |
| TC-city-IT-007 | 城市下架后 app 端精选推荐不可见（级联） | city/地图下架对精选推荐级联生效#下架城市后 app 端精选推荐不可见 | api-spec.json#/paths/~1api~1app~1featured-items/get | article-and-featured-feed | IT | `test-evidence/route-decouple-city-online/TC-city-IT-007/` | ✅ |
| TC-city-IT-008 | 城市下架后 app 端路线仍可见（不再级联） | city/地图下架对活动级联生效#下架城市后 app 端路线仍可见 | api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get | route-decouple-city-online | IT | `test-evidence/route-decouple-city-online/TC-city-IT-008/` | ✅ |
| TC-city-IT-009 | DELETE /api/admin/cities/{id} 城市下存在路线时拒绝删除 | city/城市下存在路线时禁止删除#有路线的城市不能删除 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete | route-decouple-city-online | IT | `test-evidence/route-decouple-city-online/TC-city-IT-009/` | ✅ |
| TC-city-IT-010 | DELETE /api/admin/cities/{id} 路线清空后可正常删除城市 | city/城市下存在路线时禁止删除#路线清空后可删除城市 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete | route-decouple-city-online | IT | `test-evidence/route-decouple-city-online/TC-city-IT-010/` | ✅ |
| TC-city-WEB-003 | 城市下架确认提示包含活动级联说明且不含路线 | city/地图下架对活动级联生效#web 下架确认提示不含路线 | - | route-decouple-city-online | WEB | `test-evidence/route-decouple-city-online/TC-city-WEB-003/` | ⬜ |
| TC-city-WEB-004 | 城市下架确认提示包含精选推荐级联说明 | city/地图下架对精选推荐级联生效#web 下架确认提示包含精选推荐 | - | route-decouple-city-online | WEB | `test-evidence/route-decouple-city-online/TC-city-WEB-004/` | ⬜ |
| TC-featured-IT-016 | GET /api/app/featured-cycle-items 四周期分组齐全且只含上线条目 | featured/App 端周期推荐查询#查询四个周期的推荐列表 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/route-decouple-city-online/TC-featured-IT-016/` | ✅ |
| TC-featured-IT-017 | GET /api/app/featured-cycle-items 关联实体不可见时条目不下发 | featured/App 端周期推荐查询#关联实体不可见时条目不下发 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/route-decouple-city-online/TC-featured-IT-017/` | ✅ |
| TC-featured-IT-018 | GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目 | featured/App 端周期推荐查询#大使下线连带隐藏路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | route-decouple-city-online | IT | `test-evidence/route-decouple-city-online/TC-featured-IT-018/` | ✅ |
| TC-featured-IT-019 | GET /api/app/featured-cycle-items 组内按排序号升序 | featured/App 端周期推荐查询#组内按排序号升序 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | featured-cycle-feed | IT | `test-evidence/route-decouple-city-online/TC-featured-IT-019/` | ✅ |
| TC-featured-IT-020 | GET /api/app/featured-cycle-items 城市未上架不影响路线类条目 | featured/App 端周期推荐查询#城市未上架不影响路线类条目 | api-spec.json#/paths/~1api~1app~1featured-cycle-items/get | route-decouple-city-online | IT | `test-evidence/route-decouple-city-online/TC-featured-IT-020/` | ✅ |
| TC-route-IT-012 | GET /api/app/routes 上架城市路线列表按 sortOrder 升序 | route/App 端路线查询#查询上架城市的路线 | api-spec.json#/paths/~1api~1app~1routes/get | ambassador-route-activity | IT | `test-evidence/route-decouple-city-online/TC-route-IT-012/` | ✅ |
| TC-route-IT-013 | GET /api/app/routes 大使下线后路线隐藏、详情 404 | route/App 端路线查询#大使下线后路线隐藏 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | ambassador-route-activity | IT | `test-evidence/route-decouple-city-online/TC-route-IT-013/` | ✅ |
| TC-route-IT-014 | GET /api/app/routes/{id} 路线详情返回地点明细与大使信息 | route/App 端路线查询#路线详情返回地点明细 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | ambassador-route-activity | IT | `test-evidence/route-decouple-city-online/TC-route-IT-014/` | ✅ |
| TC-route-IT-015 | GET /api/app/routes 未上架城市的路线仍可见且详情返回 cityName | route/App 端路线查询#未上架城市的路线仍可见 | api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get | route-decouple-city-online | IT | `test-evidence/route-decouple-city-online/TC-route-IT-015/` | ✅ |
| TC-route-WEB-001 | 大使列表展示与上下线开关 | route/web 端大使与路线管理页面#大使列表与上下线 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-route-WEB-001/` | ✅ |
| TC-route-WEB-002 | 路线表单维护地点子列表并按添加顺序回显 | route/web 端大使与路线管理页面#路线表单维护地点 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-route-WEB-002/` | ✅ |
| TC-route-WEB-003 | 删除路线需确认（确认删除、取消保留） | route/web 端大使与路线管理页面#删除路线需确认 | - | ambassador-route-activity | WEB | `test-evidence/ambassador-route-activity/TC-route-WEB-003/` | ✅ |
| TC-route-WEB-004 | 路线表单所属城市下拉列出全部城市（下架带「（已下架）」）并可保存 | route/web 端大使与路线管理页面#路线表单可选未上架城市 | - | route-decouple-city-online | WEB | `test-evidence/route-decouple-city-online/TC-route-WEB-004/` | ⬜ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：20
- ✅ 通过：17 (85.0%)
- ❌ 失败：0
- ⬜ 未测：3
