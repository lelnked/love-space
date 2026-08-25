# 追溯矩阵（交付核对）：recommend-list-align-spec-to-merchant-ids

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change recommend-list-align-spec-to-merchant-ids`

## 需求与场景
- **recommend-list/web 端推荐清单管理页面**: 清单列表与筛选 / 维护清单商户 / 删除清单需确认
- **recommend-list/推荐清单管理**: 创建清单 / 缺少必填项被拒绝 / 修改所属城市需清单内商户同属新城市 / 人工恢复清单 / 删除清单 / 清单列表按排序号升序
- **recommend-list/清单内商户维护**: 添加本城市商户 / 拒绝跨城市商户 / 重复添加同一商户被拒绝 / 拒绝已下架商户 / 从清单移除商户

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-recommend-list-IT-001 | POST /api/admin/recommend-lists 创建清单成功 | recommend-list/推荐清单管理#创建清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-001/` | ✅ |
| TC-recommend-list-IT-002 | POST /api/admin/recommend-lists 缺少必填项被拒绝 | recommend-list/推荐清单管理#缺少必填项被拒绝 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-002/` | ✅ |
| TC-recommend-list-IT-003 | POST /api/admin/recommend-lists 不传 sortOrder 默认 0 | recommend-list/推荐清单管理#创建清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-003/` | ✅ |
| TC-recommend-list-IT-004 | PUT /api/admin/recommend-lists/{id} 修改所属城市需清单内商户同属新城市 | recommend-list/推荐清单管理#修改所属城市需清单内商户同属新城市 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-004/` | ✅ |
| TC-recommend-list-IT-005 | DELETE /api/admin/recommend-lists/{id} 物理删除含商户关联的清单 | recommend-list/推荐清单管理#删除清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/delete | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-005/` | ✅ |
| TC-recommend-list-IT-006 | GET /api/admin/recommend-lists/page 按 sortOrder 升序并支持过滤 | recommend-list/推荐清单管理#清单列表按排序号升序 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1page/get | map-and-recommend-list | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-006/` | ✅ |
| TC-recommend-list-IT-007 | PUT /api/admin/recommend-lists/{id} merchantIds 整体替换本城市商户并按数组顺序回显 | recommend-list/清单内商户维护#添加本城市商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-007/` | ✅ |
| TC-recommend-list-IT-008 | PUT /api/admin/recommend-lists/{id} merchantIds 含跨城市商户被拒绝 | recommend-list/清单内商户维护#拒绝跨城市商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-008/` | ✅ |
| TC-recommend-list-IT-009 | PUT /api/admin/recommend-lists/{id} merchantIds 重复商户被拒绝 | recommend-list/清单内商户维护#重复添加同一商户被拒绝 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-009/` | ✅ |
| TC-recommend-list-IT-010 | PUT /api/admin/recommend-lists/{id} merchantIds 去掉商户即移除且不影响商户本身 | recommend-list/清单内商户维护#从清单移除商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-010/` | ✅ |
| TC-recommend-list-IT-016 | PUT /api/admin/recommend-lists/{id} merchantIds 含已下架商户被拒绝 | recommend-list/清单内商户维护#拒绝已下架商户 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-016/` | ✅ |
| TC-recommend-list-IT-017 | POST /api/admin/recommend-lists status 默认 ONLINE 且可带 status/merchantIds 创建 | recommend-list/推荐清单管理#创建清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists/post | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-017/` | ✅ |
| TC-recommend-list-IT-018 | POST /api/admin/recommend-lists/{id}/online 人工恢复清单（含下架商户拒绝、成功、幂等） | recommend-list/推荐清单管理#人工恢复清单 | api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1online/post | recommend-list-align-spec-to-merchant-ids | IT | `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-018/` | ✅ |
| TC-recommend-list-WEB-001 | 推荐清单列表与城市筛选 | recommend-list/web 端推荐清单管理页面#清单列表与筛选 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-001/` | ✅ |
| TC-recommend-list-WEB-002 | 清单编辑界面维护商户（仅本城市可选） | recommend-list/web 端推荐清单管理页面#维护清单商户 | - | recommend-list-align-spec-to-merchant-ids | WEB | `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-002/` | ✅ |
| TC-recommend-list-WEB-003 | 删除清单需确认（确认删除、取消保留） | recommend-list/web 端推荐清单管理页面#删除清单需确认 | - | map-and-recommend-list | WEB | `test-evidence/map-and-recommend-list/TC-recommend-list-WEB-003/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：16
- ✅ 通过：16 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
