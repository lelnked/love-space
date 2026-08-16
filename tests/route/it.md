# route IT 用例

### TC-route-IT-001: POST /api/admin/ambassadors 创建大使成功且标签顺序保持
**关联需求**: route/爱女大使管理#创建大使
**关联契约**: api-spec.json#/paths/~1api~1admin~1ambassadors/post
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/ambassadors，body：`{"avatar": "<objectKey>", "name": "小满", "tags": ["古着", "咖啡"], "online": true}`
3. GET /api/admin/ambassadors/{id}
**预期结果**: 创建返回 200；详情 name="小满"、tags 为 ["古着","咖啡"] 且顺序与提交一致、online=true、avatar 返回可访问的签名 URL
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-001/`
**最后更新**: 2026-08-16

### TC-route-IT-002: POST /api/admin/ambassadors 标签边界 3 条通过、4 条拒绝
**关联需求**: route/爱女大使管理#标签超过 3 条被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1ambassadors/post
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. POST /api/admin/ambassadors，body 含恰好 3 条标签：`{"avatar": "<objectKey>", "name": "三标大使", "tags": ["a", "b", "c"]}`
2. POST /api/admin/ambassadors，body 含 4 条标签：`{"avatar": "<objectKey>", "name": "四标大使", "tags": ["a", "b", "c", "d"]}`
**预期结果**: 步骤 1 返回 200 且详情 tags 为 3 条；步骤 2 返回 400，响应 `message` 为中文校验错误信息，大使未创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-002/`
**最后更新**: 2026-08-16

### TC-route-IT-003: PUT /api/admin/ambassadors/{id}/online 大使上下线切换
**关联需求**: route/爱女大使管理#大使上下线切换
**关联契约**: api-spec.json#/paths/~1api~1admin~1ambassadors~1{id}~1online/put
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 online=true 的大使
2. PUT /api/admin/ambassadors/{id}/online，body：`{"online": false}`
3. GET /api/admin/ambassadors/{id}
4. PUT /api/admin/ambassadors/{id}/online，body：`{"online": true}`
**预期结果**: 步骤 2 返回 200，步骤 3 详情 online=false；步骤 4 后再查详情 online=true（可往返切换）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-003/`
**最后更新**: 2026-08-16

### TC-route-IT-004: PUT /api/admin/ambassadors/{id} 更新大使字段
**关联需求**: route/爱女大使管理#创建大使
**关联契约**: api-spec.json#/paths/~1api~1admin~1ambassadors~1{id}/put
**来源**: ambassador-route-activity
**优先级**: P1
**测试步骤**:
1. 前置：已存在大使（name「小满」、2 条标签）
2. PUT /api/admin/ambassadors/{id}，body：`{"avatar": "<新 objectKey>", "name": "小满改", "tags": ["旅拍"]}`
3. GET /api/admin/ambassadors/{id}
**预期结果**: 更新返回 200；详情 name="小满改"、tags 仅剩 ["旅拍"]、头像为新图签名 URL
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-004/`
**最后更新**: 2026-08-16

### TC-route-IT-005: DELETE /api/admin/ambassadors/{id} 物理删除大使
**关联需求**: route/爱女大使管理#创建大使
**关联契约**: api-spec.json#/paths/~1api~1admin~1ambassadors~1{id}/delete
**来源**: ambassador-route-activity
**优先级**: P1
**测试步骤**:
1. 前置：存在一个未被路线引用的大使
2. DELETE /api/admin/ambassadors/{id}
3. GET /api/admin/ambassadors/{id}
**预期结果**: 删除返回 200；再查详情返回 400 及中文业务错误（admin 端「资源不存在」全局口径）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-005/`
**最后更新**: 2026-08-16

### TC-route-IT-006: POST /api/admin/routes 创建路线含 2 个地点按提交顺序返回
**关联需求**: route/路线管理#创建路线
**关联契约**: api-spec.json#/paths/~1api~1admin~1routes/post
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：存在上架城市与上线大使
2. POST /api/admin/routes，body 含全部字段：cityId、sortOrder=2、title「江畔一日线」、ambassadorNote、thumbnail、images 2 张、travelTime/season/travelStatus、ambassadorId、spots 2 个（各含 name/image/intro，顺序为 S1、S2）
3. GET /api/admin/routes/{id}
**预期结果**: 创建返回 200；详情字段与提交一致，spots 按 S1→S2 顺序返回且每个含 name/image/intro，thumbnail/images/地点图片均为签名 URL
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-006/`
**最后更新**: 2026-08-16

### TC-route-IT-007: POST /api/admin/routes 缺必填或城市/大使不存在被拒绝
**关联需求**: route/路线管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1routes/post
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. POST /api/admin/routes，body 缺 title（其余合法）
2. POST /api/admin/routes，body cityId 为不存在的 UUID
3. POST /api/admin/routes，body ambassadorId 为不存在的 UUID
**预期结果**: 三次均返回 400，响应 `message` 为中文错误信息；路线均未创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-007/`
**最后更新**: 2026-08-16

### TC-route-IT-008: POST /api/admin/routes 路线图片边界 1 张通过、空数组拒绝
**关联需求**: route/路线管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1routes/post
**来源**: ambassador-route-activity
**优先级**: P1
**测试步骤**:
1. POST /api/admin/routes，body 合法且 images 恰好 1 张
2. POST /api/admin/routes，body 合法但 images 为 `[]`
**预期结果**: 步骤 1 返回 200；步骤 2 返回 400，响应 `message` 为中文校验错误信息
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-008/`
**最后更新**: 2026-08-16

### TC-route-IT-009: PUT /api/admin/routes/{id} 更新路线且 cityId 不可变
**关联需求**: route/路线管理#创建路线
**关联契约**: api-spec.json#/paths/~1api~1admin~1routes~1{id}/put
**来源**: ambassador-route-activity
**优先级**: P1
**测试步骤**:
1. 前置：城市 A 下已存在路线（2 个地点）
2. PUT /api/admin/routes/{id}，body：title 改名、sortOrder=9、spots 改为 1 个新地点、cityId 传城市 B 的 id
3. GET /api/admin/routes/{id}
**预期结果**: title/sortOrder/spots 更新生效（spots 仅剩新地点）；cityId 仍为城市 A（不可变——被忽略或返回 400 中文业务错误，按实现口径断言其一并记录）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-009/`
**最后更新**: 2026-08-16

### TC-route-IT-010: GET /api/admin/routes/page 按 sortOrder 升序并支持过滤
**关联需求**: route/路线管理#路线列表按排序号升序
**关联契约**: api-spec.json#/paths/~1api~1admin~1routes~1page/get
**来源**: ambassador-route-activity
**优先级**: P1
**测试步骤**:
1. 前置：同一城市下创建 3 条路线，sortOrder 分别为 5、1、3，其中一条标题含关键字「江畔」
2. GET /api/admin/routes/page?cityId={cityId}&page=0&size=10
3. GET /api/admin/routes/page?cityId={cityId}&keyword=江畔
**预期结果**: 步骤 2 返回 3 条且按 sortOrder 1→3→5 排列；步骤 3 仅返回标题含「江畔」的路线
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-010/`
**最后更新**: 2026-08-16

### TC-route-IT-011: DELETE /api/admin/routes/{id} 物理删除路线连带地点
**关联需求**: route/路线管理#删除路线
**关联契约**: api-spec.json#/paths/~1api~1admin~1routes~1{id}/delete
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：存在一条含 2 个地点的路线
2. DELETE /api/admin/routes/{id}
3. GET /api/admin/routes/{id}
4. GET /api/admin/routes/page?cityId={cityId} 确认列表不含该路线
**预期结果**: 删除返回 200；再查详情返回 400 及中文业务错误（地点为路线 jsonb 内嵌数据，随路线一并删除）；分页列表不再出现该路线
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-011/`
**最后更新**: 2026-08-16

### TC-route-IT-012: GET /api/app/routes 上架城市路线列表按 sortOrder 升序
**关联需求**: route/App 端路线查询#查询上架城市的路线
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：上架城市下存在多条路线（sortOrder 5、1、3），关联大使均上线
2. GET http://localhost:8081/api/app/routes?cityId={cityId}（请求头带 X-API-Key）
**预期结果**: 返回 200，含该城市全部可见路线，按 sortOrder 1→3→5 升序，每项含缩略图（签名 URL）、主标题与大使名称
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-012/`
**最后更新**: 2026-08-16

### TC-route-IT-013: GET /api/app/routes 大使下线后路线隐藏、详情 404
**关联需求**: route/App 端路线查询#大使下线后路线隐藏
**关联契约**: api-spec.json#/paths/~1api~1app~1routes~1{id}/get
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：上架城市下一条路线可见（app 列表能查到）
2. admin 侧 PUT /api/admin/ambassadors/{ambassadorId}/online 将其关联大使下线
3. GET http://localhost:8081/api/app/routes?cityId={cityId}（请求头带 X-API-Key）
4. GET http://localhost:8081/api/app/routes/{routeId}（请求头带 X-API-Key）
**预期结果**: 下线后列表不含该路线；详情返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-013/`
**最后更新**: 2026-08-16

### TC-route-IT-014: GET /api/app/routes/{id} 路线详情返回地点明细与大使信息
**关联需求**: route/App 端路线查询#路线详情返回地点明细
**关联契约**: api-spec.json#/paths/~1api~1app~1routes~1{id}/get
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：一条可见路线含 2 个地点（顺序 S1、S2），关联大使配有头像/名称/标签
2. GET http://localhost:8081/api/app/routes/{id}（请求头带 X-API-Key）
**预期结果**: 返回 200；含路线图片列表（签名 URL）、地点按 S1→S2 顺序返回且每个含名称/图片/介绍；含大使信息（名称、头像签名 URL）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-IT-014/`
**最后更新**: 2026-08-16
