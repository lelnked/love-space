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
**执行存证**: `test-evidence/map-and-recommend-list/TC-city-IT-001/`
**最后更新**: 2026-08-16

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
**执行存证**: `test-evidence/map-and-recommend-list/TC-city-IT-002/`
**最后更新**: 2026-08-16

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
**执行存证**: `test-evidence/map-and-recommend-list/TC-city-IT-003/`
**最后更新**: 2026-08-16

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
**执行存证**: `test-evidence/map-and-recommend-list/TC-city-IT-004/`
**最后更新**: 2026-08-16

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
**执行存证**: `test-evidence/ambassador-route-activity/TC-city-IT-005/`
**最后更新**: 2026-08-16

### TC-city-IT-006: 城市下架后 app 端路线与活动不可见（级联）
**关联需求**: city/地图下架对路线与活动级联生效#下架城市后 app 端路线与活动不可见
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1activities/get
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：某上架城市下有可见路线（大使上线）与上线活动，app 端列表均能查到
2. admin 侧通过既有城市下架接口将该城市下架
3. GET http://localhost:8081/api/app/routes?cityId={cityId}（请求头带 X-API-Key）
4. GET http://localhost:8081/api/app/routes/{routeId}（请求头带 X-API-Key）
5. GET http://localhost:8081/api/app/activities?cityId={cityId}（请求头带 X-API-Key）
6. GET http://localhost:8081/api/app/activities/{activityId}（请求头带 X-API-Key）
**预期结果**: 路线列表与活动列表均返回空数据；路线详情与活动详情均返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-city-IT-006/`
**最后更新**: 2026-08-16
