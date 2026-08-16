# 追溯矩阵（交付核对）：map-and-recommend-list

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change map-and-recommend-list`

## 需求与场景
- **city/后台入口更名为地图管理**: 侧栏与页面标题展示地图管理
- **city/地图下架对推荐清单级联生效**: 下架城市后 app 端清单不可见 / web 下架确认提示包含清单
- **city/地图编辑说**: admin 保存编辑说 / 编辑说超长被拒绝 / app 端城市数据返回编辑说
- **merchant/商户编辑推荐理由**: admin 创建/更新商户时保存推荐理由 / 推荐理由超长被拒绝 / 推荐理由可为空 / app 端商户详情返回推荐理由 / web 商户表单录入推荐理由
- **recommend-list/App 端清单查询**: 查询上架城市的清单 / 清单详情返回商户明细 / 下架城市清单不可见
- **recommend-list/web 端推荐清单管理页面**: 清单列表与筛选 / 维护清单商户 / 删除清单需确认
- **recommend-list/推荐清单管理**: 创建清单 / 缺少必填项被拒绝 / 删除清单 / 清单列表按排序号升序
- **recommend-list/清单内商户维护**: 添加本城市商户 / 拒绝跨城市商户 / 重复添加同一商户被拒绝 / 从清单移除商户

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-city-IT-001 | POST /api/admin/cities 创建城市保存编辑说 | city/地图编辑说#admin 保存编辑说 | api-spec.json#/paths/~1api~1admin~1cities/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-city-IT-001/` | ✅ |
| TC-city-IT-002 | PUT /api/admin/cities/{id} 编辑说 200 字边界通过 | city/地图编辑说#admin 保存编辑说 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-city-IT-002/` | ✅ |
| TC-city-IT-003 | PUT /api/admin/cities/{id} 编辑说 201 字被拒绝 | city/地图编辑说#编辑说超长被拒绝 | api-spec.json#/paths/~1api~1admin~1cities~1{id}/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-city-IT-003/` | ✅ |
| TC-city-IT-004 | GET /api/app/cities app 端城市列表返回编辑说 | city/地图编辑说#app 端城市数据返回编辑说 | api-spec.json#/paths/~1api~1app~1cities/get | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-city-IT-004/` | ✅ |
| TC-city-IT-005 | 城市下架后 app 端推荐清单不可见（级联） | city/地图下架对推荐清单级联生效#下架城市后 app 端清单不可见 | api-spec.json#/paths/~1api~1app~1recommend-lists/get | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-city-IT-005/` | ✅ |
| TC-city-WEB-001 | 侧栏与页面标题展示「地图管理」 | city/后台入口更名为地图管理#侧栏与页面标题展示地图管理 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-city-WEB-001/` | ✅ |
| TC-city-WEB-002 | 城市下架确认提示包含推荐清单级联说明 | city/地图下架对推荐清单级联生效#web 下架确认提示包含清单 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-city-WEB-002/` | ✅ |
| TC-merchant-IT-001 | POST /api/admin/merchants 创建商户保存推荐理由 | merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由 | api-spec.json#/paths/~1api~1admin~1merchants/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-001/` | ✅ |
| TC-merchant-IT-002 | PUT /api/admin/merchants/{id} 更新推荐理由 | merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由 | api-spec.json#/paths/~1api~1admin~1merchants~1{id}/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-002/` | ✅ |
| TC-merchant-IT-003 | POST /api/admin/merchants 推荐理由 2000 字边界通过 | merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由 | api-spec.json#/paths/~1api~1admin~1merchants/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-003/` | ✅ |
| TC-merchant-IT-004 | POST /api/admin/merchants 推荐理由 2001 字被拒绝 | merchant/商户编辑推荐理由#推荐理由超长被拒绝 | api-spec.json#/paths/~1api~1admin~1merchants/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-004/` | ✅ |
| TC-merchant-IT-005 | POST /api/admin/merchants 不填推荐理由创建成功 | merchant/商户编辑推荐理由#推荐理由可为空 | api-spec.json#/paths/~1api~1admin~1merchants/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-005/` | ✅ |
| TC-merchant-IT-006 | GET /api/app/merchants/{id} app 端详情返回推荐理由 | merchant/商户编辑推荐理由#app 端商户详情返回推荐理由 | api-spec.json#/paths/~1api~1app~1merchants~1{id}/get | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-006/` | ✅ |
| TC-merchant-WEB-001 | 商户表单录入推荐理由并回显 | merchant/商户编辑推荐理由#web 商户表单录入推荐理由 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-merchant-WEB-001/` | ✅ |
| TC-merchant-WEB-002 | 推荐理由超长表单校验提示 | merchant/商户编辑推荐理由#web 商户表单录入推荐理由 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-merchant-WEB-002/` | ✅ |
| TC-recommend-list-IT-001 | POST /api/admin/recommend-lists 创建清单成功 | recommend-list/推荐清单管理#创建清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-001/` | ✅ |
| TC-recommend-list-IT-002 | POST /api/admin/recommend-lists 缺少必填项被拒绝 | recommend-list/推荐清单管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-002/` | ✅ |
| TC-recommend-list-IT-003 | POST /api/admin/recommend-lists 不传 sortOrder 默认 0 | recommend-list/推荐清单管理#创建清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-003/` | ✅ |
| TC-recommend-list-IT-004 | PUT /api/admin/recommend-lists/{id} 更新清单且 cityId 不可变 | recommend-list/推荐清单管理#创建清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-004/` | ✅ |
| TC-recommend-list-IT-005 | DELETE /api/admin/recommend-lists/{id} 物理删除含商户关联的清单 | recommend-list/推荐清单管理#删除清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/delete | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-005/` | ✅ |
| TC-recommend-list-IT-006 | GET /api/admin/recommend-lists/page 按 sortOrder 升序并支持过滤 | recommend-list/推荐清单管理#清单列表按排序号升序 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1page/get | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-006/` | ✅ |
| TC-recommend-list-IT-007 | PUT /api/admin/recommend-lists/{id}/merchants 全量替换本城市商户 | recommend-list/清单内商户维护#添加本城市商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1merchants/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-007/` | ✅ |
| TC-recommend-list-IT-008 | PUT /api/admin/recommend-lists/{id}/merchants 跨城市商户被拒绝 | recommend-list/清单内商户维护#拒绝跨城市商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1merchants/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-008/` | ✅ |
| TC-recommend-list-IT-009 | PUT /api/admin/recommend-lists/{id}/merchants 重复商户被拒绝 | recommend-list/清单内商户维护#重复添加同一商户被拒绝 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1merchants/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-009/` | ✅ |
| TC-recommend-list-IT-010 | PUT /api/admin/recommend-lists/{id}/merchants 移除商户不影响商户本身 | recommend-list/清单内商户维护#从清单移除商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1merchants/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-010/` | ✅ |
| TC-recommend-list-IT-011 | GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序 | recommend-list/App 端清单查询#查询上架城市的清单 | api-spec.json#/paths/~1api~1app~1recommend-lists/get | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-011/` | ✅ |
| TC-recommend-list-IT-012 | GET /api/app/recommend-lists/{id} 详情返回商户明细按排序升序 | recommend-list/App 端清单查询#清单详情返回商户明细 | api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-012/` | ✅ |
| TC-recommend-list-IT-013 | GET /api/app/recommend-lists 下架城市清单不可见、详情 404 | recommend-list/App 端清单查询#下架城市清单不可见 | api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-recommend-list-IT-013/` | ✅ |
| TC-recommend-list-WEB-001 | 推荐清单列表与城市筛选 | recommend-list/web 端推荐清单管理页面#清单列表与筛选 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-001/` | ✅ |
| TC-recommend-list-WEB-002 | 清单编辑界面维护商户（仅本城市可选） | recommend-list/web 端推荐清单管理页面#维护清单商户 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-002/` | ✅ |
| TC-recommend-list-WEB-003 | 删除清单需确认（确认删除、取消保留） | recommend-list/web 端推荐清单管理页面#删除清单需确认 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-003/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：31
- ✅ 通过：31 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
