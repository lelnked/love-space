# 追溯矩阵（交付核对）：route-spot-address

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change route-spot-address`

## 需求与场景
- **route/App 端路线查询**: 查询上架城市的路线 / 同排序号路线按创建时间倒序 / 不传任何过滤参数返回全部可见路线 / 按大使 ID 过滤路线 / 城市名与大使 ID 组合过滤 / 城市表中无同名城市时仍返回路线且 city 为 null / 列表项返回路线自身城市名 / 未上架城市的路线仍可见 / 大使下线后路线隐藏 / 路线详情返回地点明细 / 路线列表返回爱女大使说 / 路线详情返回大使 id / 地点地址下发且未填时为 null
- **route/web 端大使与路线管理页面**: 大使列表与上下线 / 路线表单可选未上架城市 / 路线表单维护地点 / 路线表单填写地点地址并回显 / 删除路线需确认
- **route/路线管理**: 创建路线 / 缺少必填项被拒绝 / 路线列表按排序号升序 / 删除路线 / 地点地址可写可改可空

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-route-IT-006 | POST /api/admin/routes 创建路线含 2 个地点按提交顺序返回 | route/路线管理#创建路线 | api-spec.json#/paths/~1api~1admin~1routes/post | ambassador-route-activity | IT | `test-evidence/route-spot-address/TC-route-IT-006/` | ✅ |
| TC-route-IT-007 | POST /api/admin/routes 缺必填或大使不存在被拒绝（城市名不校验） | route/路线管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1routes/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-007/` | ⬜ |
| TC-route-IT-008 | POST /api/admin/routes 路线图片边界 1 张通过、空数组拒绝 | route/路线管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1routes/post | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-008/` | ✅ |
| TC-route-IT-009 | PUT /api/admin/routes/{id} 更新路线且 cityId 不可变 | route/路线管理#创建路线 | api-spec.json#/paths/~1api~1admin~1routes~1{id}/put | ambassador-route-activity | IT | `test-evidence/route-spot-address/TC-route-IT-009/` | ✅ |
| TC-route-IT-010 | GET /api/admin/routes/page 按 sortOrder 升序并支持过滤 | route/路线管理#路线列表按排序号升序 | api-spec.json#/paths/~1api~1admin~1routes~1page/get | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-010/` | ✅ |
| TC-route-IT-011 | DELETE /api/admin/routes/{id} 物理删除路线连带地点 | route/路线管理#删除路线 | api-spec.json#/paths/~1api~1admin~1routes~1{id}/delete | ambassador-route-activity | IT | `test-evidence/ambassador-route-activity/TC-route-IT-011/` | ✅ |
| TC-route-IT-012 | GET /api/app/routes?cityName= 按城市名查路线列表并按 sortOrder 升序 | route/App 端路线查询#查询上架城市的路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-list-sort-tiebreak/TC-route-IT-012/` | ✅ |
| TC-route-IT-013 | GET /api/app/routes 大使下线后路线隐藏、详情 404 | route/App 端路线查询#大使下线后路线隐藏 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-013/` | ✅ |
| TC-route-IT-014 | GET /api/app/routes/{id} 路线详情返回地点明细与大使信息 | route/App 端路线查询#路线详情返回地点明细 | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | route-spot-address | IT | `test-evidence/route-spot-address/TC-route-IT-014/` | ✅ |
| TC-route-IT-015 | GET /api/app/routes 未上架城市的路线仍可见且详情返回 cityName | route/App 端路线查询#未上架城市的路线仍可见 | api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-015/` | ✅ |
| TC-route-IT-016 | GET /api/app/routes 不带任何参数返回全部可见路线 | route/App 端路线查询#不传任何过滤参数返回全部可见路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-016/` | ✅ |
| TC-route-IT-017 | GET /api/app/routes?ambassadorId= 按大使过滤路线 | route/App 端路线查询#按大使 ID 过滤路线 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-017/` | ✅ |
| TC-route-IT-018 | GET /api/app/routes?cityName=&ambassadorId= 组合过滤取交集 | route/App 端路线查询#城市名与大使 ID 组合过滤 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters | IT | `test-evidence/app-route-query-filters/TC-route-IT-018/` | ✅ |
| TC-route-IT-019 | GET /api/app/routes?cityName= 城市表无同名城市时仍返回路线且 city 为 null | route/App 端路线查询#城市表中无同名城市时仍返回路线且 city 为 null | api-spec.json#/paths/~1api~1app~1routes/get | app-route-query-filters / app-route-list-city-name | IT | `test-evidence/regression/route/TC-route-IT-019/` | ✅ |
| TC-route-IT-024 | GET /api/app/routes 同排序号路线按创建时间倒序 | route/App 端路线查询#同排序号路线按创建时间倒序 | api-spec.json#/paths/~1api~1app~1routes/get | app-list-sort-tiebreak | IT | `test-evidence/app-list-sort-tiebreak/TC-route-IT-024/` | ✅ |
| TC-route-IT-025 | GET /api/app/routes 列表项返回 ambassadorNote | route/App 端路线查询#路线列表返回爱女大使说 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-ambassador-fields | IT | `test-evidence/regression/route/TC-route-IT-025/` | ✅ |
| TC-route-IT-026 | GET /api/app/routes/{id} 详情 ambassador 含 id | route/App 端路线查询#路线详情返回大使 id | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | app-route-ambassador-fields | IT | `test-evidence/regression/route/TC-route-IT-026/` | ✅ |
| TC-route-IT-027 | GET /api/app/routes 列表项返回路线自身城市名 cityName | route/App 端路线查询#列表项返回路线自身城市名 | api-spec.json#/paths/~1api~1app~1routes/get | app-route-list-city-name | IT | `test-evidence/regression/route/TC-route-IT-027/` | ✅ |
| TC-route-IT-028 | POST/PUT /api/admin/routes 地点地址可写可改可空 | route/路线管理#地点地址可写可改可空 | api-spec.json#/paths/~1api~1admin~1routes/post、api-spec.json#/paths/~1api~1admin~1routes~1{id}/put、api-spec.json#/paths/~1api~1admin~1routes~1{id}/get | route-spot-address | IT | `test-evidence/route-spot-address/TC-route-IT-028/` | ✅ |
| TC-route-IT-029 | GET /api/app/routes/{id} 地点地址下发且未填时为 null | route/App 端路线查询#地点地址下发且未填时为 null | api-spec.json#/paths/~1api~1app~1routes~1{id}/get | route-spot-address | IT | `test-evidence/route-spot-address/TC-route-IT-029/` | ✅ |
| TC-route-WEB-001 | 大使列表展示与上下线开关 | route/web 端大使与路线管理页面#大使列表与上下线 | - | ambassador-route-activity | WEB | `test-evidence/regression/route/TC-route-WEB-001/` | ✅ |
| TC-route-WEB-002 | 路线表单维护地点子列表并按添加顺序回显 | route/web 端大使与路线管理页面#路线表单维护地点 | - | ambassador-route-activity | WEB | `test-evidence/regression/route/TC-route-WEB-002/` | ✅ |
| TC-route-WEB-003 | 删除路线需确认（确认删除、取消保留） | route/web 端大使与路线管理页面#删除路线需确认 | - | ambassador-route-activity | WEB | `test-evidence/regression/route/TC-route-WEB-003/` | ✅ |
| TC-route-WEB-004 | 路线表单所属城市下拉列出全部城市（下架带「（已下架）」）并可保存 | route/web 端大使与路线管理页面#路线表单可选未上架城市 | - | route-decouple-city-online | WEB | `test-evidence/regression/route/TC-route-WEB-004/` | ✅ |
| TC-route-WEB-005 | 路线表单填写地点地址并回显 | route/web 端大使与路线管理页面#路线表单填写地点地址并回显 | - | route-spot-address | WEB | `test-evidence/regression/route/TC-route-WEB-005/` | ⬜ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：25
- ✅ 通过：23 (92.0%)
- ❌ 失败：0
- ⬜ 未测：2
