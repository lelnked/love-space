# 追溯矩阵（交付核对）：city-drop-route-delete-guard

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change city-drop-route-delete-guard`

## 需求与场景
- **city/地图删除**: 删除地图 / 有路线的地图可以直接删除 / 删除地图连带下架 Banner 与商户

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-city-IT-001 | POST /api/admin/cities 创建城市保存编辑说 | city/地图编辑说#admin 保存编辑说 | api-spec.json#/paths/~1api~1admin~1cities/post | map-and-recommend-list | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-001/` | ✅ |
| TC-city-IT-006 | 城市下架后 app 端活动仍可见（不再级联） | city/地图下架对路线与活动均不级联#下架城市后 app 端活动仍可见 | api-spec.json#/paths/~1api~1app~1activities/get | city-drop-route-delete-guard | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-006/` | ✅ |
| TC-city-IT-008 | 城市下架后 app 端路线仍可见（不再级联） | city/地图下架对路线与活动均不级联#下架城市后 app 端路线仍可见 | api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get | city-drop-route-delete-guard | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-008/` | ✅ |
| TC-city-IT-011 | GET /api/app/cities/{id} 返回上架城市详情 | city/地图编辑说#app 端城市数据返回编辑说 | api-spec.json#/paths/~1api~1app~1cities~1{id}/get | 直接实现（未走 change） | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-011/` | ✅ |
| TC-city-IT-013 | DELETE /api/admin/cities/{id} 删除地图并连带下架 Banner 与商户 | city/地图删除#删除地图 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete | city-drop-route-delete-guard | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-013/` | ✅ |
| TC-city-IT-014 | DELETE /api/admin/cities/{id} 存在路线时地图仍可直接删除 | city/地图删除#有路线的地图可以直接删除 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete | city-drop-route-delete-guard | IT | `test-evidence/city-drop-route-delete-guard/TC-city-IT-014/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：6
- ✅ 通过：6 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
