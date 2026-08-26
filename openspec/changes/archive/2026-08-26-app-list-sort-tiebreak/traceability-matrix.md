# 追溯矩阵（交付核对）：app-list-sort-tiebreak

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change app-list-sort-tiebreak`

## 需求与场景
- **banner/App 端 Banner 查询**: 按展示位查询上架 Banner / 同排序号 Banner 按创建时间倒序 / 下架 Banner 不下发 / 关联城市下架时条目被剔除 / 缺少 API-key 返回 401
- **merchant/App 端带排序号列表的排序口径**: 分类列表同序号按创建时间倒序 / 商户评价同序号按创建时间倒序 / 排序号不同时以排序号为准 / weight 型排序号维持降序且已符合口径
- **recommend-list/App 端清单与清单内商户查询**: 查询上架城市的清单 / 同排序号清单按创建时间倒序 / 清单详情返回商户明细 / 商户列表不受清单影响 / 下架城市清单不可见
- **route/App 端路线查询**: 查询上架城市的路线 / 同排序号路线按创建时间倒序 / 不传任何过滤参数返回全部可见路线 / 按大使 ID 过滤路线 / 城市名与大使 ID 组合过滤 / 城市表中无同名城市时仍返回路线且 city 为 null / 未上架城市的路线仍可见 / 大使下线后路线隐藏 / 路线详情返回地点明细

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-banner-IT-012 | GET /api/app/banners 按展示位返回上架 Banner 并按排序号升序 | banner/App 端 Banner 查询#按展示位查询上架 Banner | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-012/` | ✅ |
| TC-banner-IT-013 | GET /api/app/banners 排序号并列时按创建时间倒序 | banner/App 端 Banner 查询#同排序号 Banner 按创建时间倒序 | api-spec.json#/paths/~1api~1app~1banners/get | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-013/` | ✅ |
| TC-banner-IT-014 | GET /api/app/banners 下架 Banner 不下发 | banner/App 端 Banner 查询#下架 Banner 不下发 | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-014/` | ✅ |
| TC-banner-IT-015 | GET /api/app/banners 关联城市下架时条目被剔除 | banner/App 端 Banner 查询#关联城市下架时条目被剔除 | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-015/` | ✅ |
| TC-banner-IT-016 | GET /api/app/banners 缺少 API-key 返回 401 | banner/App 端 Banner 查询#缺少 API-key 返回 401 | api-spec.json#/paths/~1api~1app~1banners/get | baseline-auth-manager-banner-log-file | IT | `test-evidence/app-list-sort-tiebreak/TC-banner-IT-016/` | ✅ |
| TC-merchant-IT-007 | GET /api/app/categories/page 同排序号分类按创建时间倒序 | merchant/App 端带排序号列表的排序口径#分类列表同序号按创建时间倒序 | ⚠️ 待补契约（api-spec.json 中缺 `/api/app/categories/page` 条目） | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-merchant-IT-007/` | ✅ |
| TC-merchant-IT-008 | GET /api/app/categories/page 排序号优先于创建时间 | merchant/App 端带排序号列表的排序口径#排序号不同时以排序号为准 | ⚠️ 待补契约（api-spec.json 中缺 `/api/app/categories/page` 条目） | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-merchant-IT-008/` | ✅ |
| TC-merchant-IT-009 | GET /api/app/merchants/{merchantId}/reviews 同排序号评价按创建时间倒序 | merchant/App 端带排序号列表的排序口径#商户评价同序号按创建时间倒序 | ⚠️ 待补契约（api-spec.json 中缺 `/api/app/merchants/{merchantId}/reviews` 条目） | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-merchant-IT-009/` | ✅ |
| TC-recommend-list-IT-011 | GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序 | recommend-list/App 端清单与清单内商户查询#查询上架城市的清单 | api-spec.json#/paths/~1api~1app~1recommend-lists/get | map-and-recommend-list | IT | `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-011/` | ✅ |
| TC-recommend-list-IT-012 | GET /api/app/recommend-lists/{id} 详情按清单保存顺序返回上架商户四字段 | recommend-list/App 端清单与清单内商户查询#清单详情返回商户明细 | api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get | app-recommend-list-owns-merchant-order | IT | `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-012/` | ✅ |
| TC-recommend-list-IT-013 | GET /api/app/recommend-lists 下架城市清单不可见、详情 404 | recommend-list/App 端清单与清单内商户查询#下架城市清单不可见 | api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get | map-and-recommend-list | IT | `test-evidence/app-recommend-list-owns-merchant-order/TC-recommend-list-IT-013/` | ✅ |
| TC-recommend-list-IT-015 | GET /api/app/merchants/page 商户列表不受清单影响 | recommend-list/App 端清单与清单内商户查询#商户列表不受清单影响 | api-spec.json#/paths/~1api~1app~1merchants~1page/get | app-recommend-list-owns-merchant-order | IT | `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-015/` | ✅ |
| TC-recommend-list-IT-019 | GET /api/app/recommend-lists 同排序号清单按创建时间倒序 | recommend-list/App 端清单与清单内商户查询#同排序号清单按创建时间倒序 | api-spec.json#/paths/~1api~1app~1recommend-lists/get | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-019/` | ✅ |
| TC-route-IT-012 | GET /api/app/routes?cityName= 按城市名查路线列表并按 sortOrder 升序 | route/App 端路线查询#查询上架城市的路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-list-sort-tiebreak/TC-route-IT-012/` | ✅ |
| TC-route-IT-013 | GET /api/app/routes 大使下线后路线隐藏、详情 404 | route/App 端路线查询#大使下线后路线隐藏 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-013/` | ✅ |
| TC-route-IT-014 | GET /api/app/routes/{id} 路线详情返回地点明细与大使信息 | route/App 端路线查询#路线详情返回地点明细 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | ambassador-route-activity | IT | `test-evidence/app-route-query-filters/TC-route-IT-014/` | ✅ |
| TC-route-IT-015 | GET /api/app/routes 未上架城市的路线仍可见且详情返回 cityName | route/App 端路线查询#未上架城市的路线仍可见 | api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-015/` | ✅ |
| TC-route-IT-016 | GET /api/app/routes 不带任何参数返回全部可见路线 | route/App 端路线查询#不传任何过滤参数返回全部可见路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-016/` | ✅ |
| TC-route-IT-017 | GET /api/app/routes?ambassadorId= 按大使过滤路线 | route/App 端路线查询#按大使 ID 过滤路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-017/` | ✅ |
| TC-route-IT-018 | GET /api/app/routes?cityName=&ambassadorId= 组合过滤取交集 | route/App 端路线查询#城市名与大使 ID 组合过滤 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-018/` | ✅ |
| TC-route-IT-019 | GET /api/app/routes?cityName= 城市表无同名城市时仍返回路线且 city 为 null | route/App 端路线查询#城市表中无同名城市时仍返回路线且 city 为 null | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-list-sort-tiebreak/TC-route-IT-019/` | ✅ |
| TC-route-IT-024 | GET /api/app/routes 同排序号路线按创建时间倒序 | route/App 端路线查询#同排序号路线按创建时间倒序 | api-spec.json#/paths/~1api~1app~1routes/get | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-route-IT-024/` | ✅ |

## 覆盖核对

- ⚠ 未覆盖：banner/App 端 Banner 查询#下架 Banner 不下发 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/App 端 Banner 查询#关联城市下架时条目被剔除 无 WEB/APP 用例且无 UT(@scenario) 覆盖
- ⚠ 未覆盖：banner/App 端 Banner 查询#缺少 API-key 返回 401 无 WEB/APP 用例且无 UT(@scenario) 覆盖

## 测试统计
- 总数：22
- ✅ 通过：22 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
