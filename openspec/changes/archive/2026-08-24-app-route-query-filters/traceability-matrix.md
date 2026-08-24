# 追溯矩阵（交付核对）：app-route-query-filters

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change app-route-query-filters`

## 需求与场景
- **route/App 端路线查询**: 查询上架城市的路线 / 不传任何过滤参数返回全部可见路线 / 按大使 ID 过滤路线 / 城市名与大使 ID 组合过滤 / 城市名不存在返回空数组 / 未上架城市的路线仍可见 / 大使下线后路线隐藏 / 路线详情返回地点明细

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-route-IT-012 | GET /api/app/routes?cityName= 按城市名查路线列表并按 sortOrder 升序 | route/App 端路线查询#查询上架城市的路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-012/` | ✅ |
| TC-route-IT-013 | GET /api/app/routes 大使下线后路线隐藏、详情 404 | route/App 端路线查询#大使下线后路线隐藏 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-013/` | ✅ |
| TC-route-IT-014 | GET /api/app/routes/{id} 路线详情返回地点明细与大使信息 | route/App 端路线查询#路线详情返回地点明细 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | ambassador-route-activity | IT | `test-evidence/app-route-query-filters/TC-route-IT-014/` | ✅ |
| TC-route-IT-015 | GET /api/app/routes 未上架城市的路线仍可见且详情返回 cityName | route/App 端路线查询#未上架城市的路线仍可见 | api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-015/` | ✅ |
| TC-route-IT-016 | GET /api/app/routes 不带任何参数返回全部可见路线 | route/App 端路线查询#不传任何过滤参数返回全部可见路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-016/` | ✅ |
| TC-route-IT-017 | GET /api/app/routes?ambassadorId= 按大使过滤路线 | route/App 端路线查询#按大使 ID 过滤路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-017/` | ✅ |
| TC-route-IT-018 | GET /api/app/routes?cityName=&ambassadorId= 组合过滤取交集 | route/App 端路线查询#城市名与大使 ID 组合过滤 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-018/` | ✅ |
| TC-route-IT-019 | GET /api/app/routes?cityName= 城市不存在返回空数组 | route/App 端路线查询#城市名不存在返回空数组 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-019/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：8
- ✅ 通过：8 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
