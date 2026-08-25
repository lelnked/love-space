# city IT 用例

### TC-city-IT-001: POST /api/admin/cities 创建城市保存编辑说
**关联需求**: city/地图编辑说#admin 保存编辑说
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities/post
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/cities，body 含既有必填字段 + `"editorNote": "江城夜景是这座城市的灵魂"`
3. 查询该城市详情
**预期结果**: 创建返回 200，详情响应 `editorNote` 与提交值逐字一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-001/`
**最后更新**: 2026-08-25

### TC-city-IT-002: PUT /api/admin/cities/{id} 编辑说 200 字边界通过
**关联需求**: city/地图编辑说#admin 保存编辑说
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities~1{id}/put
**来源**: map-and-recommend-list
**优先级**: P1
**测试步骤**:
1. 前置：已存在一个城市
2. PUT /api/admin/cities/{id}，`editorNote` 为恰好 200 个中文字符（"编" 重复 200 次）
3. 查询该城市详情
**预期结果**: 更新返回 200，详情 `editorNote` 长度为 200 且内容一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-002/`
**最后更新**: 2026-08-25

### TC-city-IT-003: PUT /api/admin/cities/{id} 编辑说 201 字被拒绝
**关联需求**: city/地图编辑说#编辑说超长被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities~1{id}/put
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. PUT /api/admin/cities/{id}，`editorNote` 为 201 个字符（"编" 重复 201 次），其余字段合法
2. 查询该城市详情确认未变更
**预期结果**: 返回 400，响应 `message` 为中文校验错误信息；城市 `editorNote` 保持原值
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-003/`
**最后更新**: 2026-08-25

### TC-city-IT-004: GET /api/app/cities app 端城市列表返回编辑说
**关联需求**: city/地图编辑说#app 端城市数据返回编辑说
**关联契约**: api-spec.json#/paths/~1api~1app~1cities/get
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：admin 侧存在一个上架城市，`editorNote` 已配置为「山与湖之间的浪漫」
2. GET http://localhost:8081/api/app/cities（请求头带 API-key）
**预期结果**: 返回 200，列表中该城市项含 `editorNote` = "山与湖之间的浪漫"
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-004/`
**最后更新**: 2026-08-25

### TC-city-IT-005: 城市下架后 app 端推荐清单不可见（级联）
**关联需求**: city/地图下架对推荐清单级联生效#下架城市后 app 端清单不可见
**关联契约**: api-spec.json#/paths/~1api~1app~1recommend-lists/get
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：某上架城市下已创建至少一个推荐清单，GET http://localhost:8081/api/app/recommend-lists?cityId={cityId} 能返回该清单
2. admin 侧通过既有城市下架接口将该城市下架
3. 再次 GET http://localhost:8081/api/app/recommend-lists?cityId={cityId}
4. GET http://localhost:8081/api/app/recommend-lists/{listId}
**预期结果**: 下架后清单列表返回空数组（或城市不可见口径的空结果）；清单详情返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-005/`
**最后更新**: 2026-08-25

### TC-city-IT-006: 城市下架后 app 端活动仍可见（不再级联）
**关联需求**: city/地图下架对路线与活动均不级联#下架城市后 app 端活动仍可见
**关联契约**: api-spec.json#/paths/~1api~1app~1activities/get
**来源**: city-drop-route-delete-guard
**优先级**: P0
**测试步骤**:
1. 前置：存在至少 1 个上线活动（活动自 activity-drop-city-link 起已无城市关联）；存在至少 1 个上架城市
2. GET http://localhost:8081/api/app/activities（请求头带 X-API-Key），确认列表含该活动
3. admin 侧将系统中全部城市下架
4. GET http://localhost:8081/api/app/activities（请求头带 X-API-Key）
5. GET http://localhost:8081/api/app/activities/{activityId}（请求头带 X-API-Key）
**预期结果**: 步骤 4 列表仍包含该活动；步骤 5 详情返回 200——活动可见性只取决于活动自身上线状态，与城市上下架无关。路线侧行为由 TC-city-IT-008 单独断言。
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-006/`
**最后更新**: 2026-08-25

### TC-city-IT-007: 城市下架后 app 端精选推荐不可见（级联）
**关联需求**: city/地图下架对精选推荐级联生效#下架城市后 app 端精选推荐不可见
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-items/get
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：某上架城市关联有上线的精选推荐，GET http://localhost:8081/api/app/featured-items（请求头带 X-API-Key）能返回该条目
2. admin 侧通过既有城市下架接口将该城市下架
3. 再次 GET http://localhost:8081/api/app/featured-items（请求头带 X-API-Key）
**预期结果**: 下架后信息流列表不含该城市的推荐条目（可见性 = 条目上线 ∧ 城市上架）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-007/`
**最后更新**: 2026-08-25

### TC-city-IT-008: 城市下架后 app 端路线仍可见（不再级联）
**关联需求**: city/地图下架对路线与活动均不级联#下架城市后 app 端路线仍可见
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get
**来源**: city-drop-route-delete-guard
**优先级**: P0
**测试步骤**:
1. 前置：存在一个上架城市，并有一条所属地图名（自由文本 cityName）与之同名的路线，其关联大使 online=true；GET http://localhost:8081/api/app/routes?cityName={cityName}（请求头带 X-API-Key）能查到该路线
2. admin 侧通过既有城市下架接口将该城市下架
3. GET http://localhost:8081/api/app/routes?cityName={cityName}（请求头带 X-API-Key）
4. GET http://localhost:8081/api/app/routes/{routeId}（请求头带 X-API-Key）
5. admin 侧将该大使下线，重复步骤 3、4
**预期结果**: 步骤 3 列表仍包含该路线，步骤 4 详情返回 200（城市下架不再隐藏路线）；步骤 5 大使下线后列表不含该路线、详情 404（可见性只由大使上线决定）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-008/`
**最后更新**: 2026-08-25

### TC-city-IT-011: GET /api/app/cities/{id} 返回上架城市详情
**关联需求**: city/地图编辑说#app 端城市数据返回编辑说
**关联契约**: api-spec.json#/paths/~1api~1app~1cities~1{id}/get
**来源**: 直接实现（未走 change）
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 online=true 的城市，含 backgroundImage 与 editorNote
2. GET /api/app/cities/{id}，带 X-API-Key
**预期结果**: 200，响应字段与列表项一致（id/chineseName/englishName/chineseProvince/englishProvince/backgroundImage{id,url}/editorNote）
**状态**: ✅ 通过
**执行方式**: CityReadIT#detailReturnsOnlineCity
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-011/`
**最后更新**: 2026-08-25

### TC-city-IT-012: GET /api/app/cities/{id} 未上架或不存在返回 404
**关联需求**: city/地图编辑说#app 端城市数据返回编辑说
**关联契约**: api-spec.json#/paths/~1api~1app~1cities~1{id}/get
**来源**: 直接实现（未走 change）
**优先级**: P0
**测试步骤**:
1. GET /api/app/cities/{id}，id 指向 online=false 的城市
2. GET /api/app/cities/{随机 UUID}
**预期结果**: 两次均返回 404（app 端 ResourceNotFoundException 全局口径）
**状态**: ✅ 通过
**执行方式**: CityReadIT#detailReturns404WhenOfflineOrMissing
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-012/`
**最后更新**: 2026-08-25

### TC-city-IT-013: DELETE /api/admin/cities/{id} 删除地图并连带下架 Banner 与商户
**关联需求**: city/地图删除#删除地图
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete
**来源**: city-drop-route-delete-guard
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. 前置：创建一个城市并上架；在其下创建 1 个上架商户；创建 1 个指向该城市的上架 CITY 类型 Banner
3. DELETE /api/admin/cities/{cityId}
4. GET /api/admin/cities/{cityId}
5. GET /api/admin/merchants/{merchantId}、GET /api/admin/banners/{bannerId}
**预期结果**: 步骤 3 返回 200；步骤 4 返回 400 及中文业务错误（admin 端「资源不存在」全局口径）；步骤 5 两者记录均仍存在且 online=false（只下架不删除，商户 cityId 不清空）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-013/`
**最后更新**: 2026-08-25

### TC-city-IT-014: DELETE /api/admin/cities/{id} 存在路线时地图仍可直接删除
**关联需求**: city/地图删除#有路线的地图可以直接删除
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete
**来源**: city-drop-route-delete-guard
**优先级**: P1
**测试步骤**:
1. 前置：创建一个城市；创建 1 条路线（路线的所属地图为自由文本，与该城市无关联字段）
2. DELETE /api/admin/cities/{cityId}
3. GET /api/admin/routes/{routeId}
**预期结果**: 步骤 2 返回 200，删除不因存在路线被拒绝（路线自 route-remove-city-id 起不再持有 cityId）；步骤 3 路线仍返回 200，记录不受影响
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/city-drop-route-delete-guard/TC-city-IT-014/`
**最后更新**: 2026-08-25
