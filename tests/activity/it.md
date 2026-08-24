# activity IT 用例

### TC-activity-IT-001: POST /api/admin/activities 创建完整活动
**关联需求**: activity/活动管理#创建活动
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities/post
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/activities，body：cityId（上架城市）、images 2 张、title「海岛露营节」、tags 2 条、periods `["FOLLICULAR","OVULATION"]`、level="L2"、introduction/editorNote/gatheringPlace/dismissalPlace/transportation/visa、itinerary 2 条（title+content，顺序 I1、I2）、detailHtml（纯文本段落）、online=true
3. GET /api/admin/activities/{id}
**预期结果**: 创建返回 200；详情各字段与提交一致，itinerary 按 I1→I2 顺序返回，periods/level 枚举值一致，detailHtml 文本内容原样保存，images 为签名 URL，online=true
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-001/`
**最后更新**: 2026-08-16

### TC-activity-IT-002: POST /api/admin/activities 缺必填或城市不存在被拒绝
**关联需求**: activity/活动管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities/post
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. POST /api/admin/activities，body 缺 title（其余合法）
2. POST /api/admin/activities，body images 为 `[]`
3. POST /api/admin/activities，body cityId 为不存在的 UUID
**预期结果**: 三次均返回 400，响应 `message` 为中文错误信息；活动均未创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-002/`
**最后更新**: 2026-08-16

### TC-activity-IT-003: PUT /api/admin/activities/{id}/online 活动上下线切换
**关联需求**: activity/活动管理#活动上下线切换
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities~1{id}~1online/put
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 online=true 的活动
2. PUT /api/admin/activities/{id}/online，body：`{"online": false}`
3. GET /api/admin/activities/{id}
4. PUT /api/admin/activities/{id}/online，body：`{"online": true}`
**预期结果**: 步骤 2 返回 200，步骤 3 详情 online=false；步骤 4 后再查详情 online=true（可往返切换）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-003/`
**最后更新**: 2026-08-16

### TC-activity-IT-004: PUT /api/admin/activities/{id} 更新活动且 cityId 不可变
**关联需求**: activity/活动管理#创建活动
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities~1{id}/put
**来源**: ambassador-route-activity
**优先级**: P1
**测试步骤**:
1. 前置：城市 A 下已存在活动
2. PUT /api/admin/activities/{id}，body：title 改名、level 改为 "L3"、periods 改为 `["MENSTRUAL"]`、itinerary 改为 1 条新条目、cityId 传城市 B 的 id
3. GET /api/admin/activities/{id}
**预期结果**: title/level/periods/itinerary 更新生效；cityId 仍为城市 A（不可变——被忽略或返回 400 中文业务错误，按实现口径断言其一并记录）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-004/`
**最后更新**: 2026-08-16

### TC-activity-IT-005: DELETE /api/admin/activities/{id} 物理删除活动
**关联需求**: activity/活动管理#创建活动
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities~1{id}/delete
**来源**: ambassador-route-activity
**优先级**: P1
**测试步骤**:
1. 前置：存在一个活动
2. DELETE /api/admin/activities/{id}
3. GET /api/admin/activities/{id}
4. GET /api/admin/activities/page?cityId={cityId} 确认列表不含该活动
**预期结果**: 删除返回 200；再查详情返回 400 及中文业务错误（admin 端「资源不存在」全局口径）；分页列表不再出现该活动
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-005/`
**最后更新**: 2026-08-16

### TC-activity-IT-006: POST /api/admin/activities 富文本 img src 存 objectKey、admin 读时替换签名 URL
**关联需求**: activity/活动管理#创建活动
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities/post
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. POST /api/admin/activities，detailHtml 含 2 个 `<img src="<images/ 前缀 objectKey>">` 与段落文本
2. GET /api/admin/activities/{id}
3. PUT /api/admin/activities/{id}，detailHtml 改为不含 img 的纯文本后再 GET 详情
**预期结果**: 创建返回 200；步骤 2 详情 detailHtml 文本部分与提交一致，2 个 img 的 src 均被替换为签名 URL（http 开头、非裸 objectKey），说明存储层保存的是 bound objectKey；步骤 3 无 img 的 HTML 原样往返不报错
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-006/`
**最后更新**: 2026-08-16

### TC-activity-IT-007: GET /api/app/activities 上架城市活动列表
**关联需求**: activity/App 端活动查询#查询上架城市的活动
**关联契约**: api-spec.json#/paths/~1api~1app~1activities/get
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：上架城市下存在上线活动（含图片、标签、级别、周期）
2. GET http://localhost:8081/api/app/activities?cityId={cityId}（请求头带 X-API-Key）
**预期结果**: 返回 200，列表含该活动，每项含标题、图片（签名 URL）、标签、级别、周期字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-007/`
**最后更新**: 2026-08-16

### TC-activity-IT-008: GET /api/app/activities 下线活动不可见、详情 404
**关联需求**: activity/App 端活动查询#下线活动不可见
**关联契约**: api-spec.json#/paths/~1api~1app~1activities~1{id}/get
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：上架城市下一个上线活动 app 端可见
2. admin 侧 PUT /api/admin/activities/{id}/online 将其下线
3. GET http://localhost:8081/api/app/activities?cityId={cityId}（请求头带 X-API-Key）
4. GET http://localhost:8081/api/app/activities/{id}（请求头带 X-API-Key）
**预期结果**: 下线后列表不含该活动；详情返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-008/`
**最后更新**: 2026-08-16

### TC-activity-IT-009: GET /api/app/activities/{id} 详情返回富文本且 img src 为签名 URL
**关联需求**: activity/App 端活动查询#活动详情返回富文本
**关联契约**: api-spec.json#/paths/~1api~1app~1activities~1{id}/get
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：一个可见活动，detailHtml 后台保存为含图片标签与文本的 HTML
2. GET http://localhost:8081/api/app/activities/{id}（请求头带 X-API-Key）
**预期结果**: 返回 200；含全部展示字段（title/images/tags/periods/level/introduction/editorNote/gatheringPlace/dismissalPlace/transportation/visa/itinerary）；detailHtml 文本与后台保存内容一致，img src 已替换为可访问的签名 URL
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-009/`
**最后更新**: 2026-08-16

### TC-activity-IT-020: 活动景观字段贯通 admin 写入与 admin/app 查询
**关联需求**: activity/活动管理#景观字段可写可改可空
**关联契约**: api-spec.json#/components/schemas/ActivityUpsertRequest
**来源**: activity-landscape-field
**优先级**: P1
**测试步骤**:
1. POST /api/admin/activities，body 含 landscape="海岸线景观"（其余字段合法，online=true，所属城市上架）
2. GET /api/admin/activities/{id}
3. PUT /api/admin/activities/{id}，把 landscape 改为「火山地貌」
4. GET http://localhost:8081/api/app/activities/{id}（请求头带 X-API-Key）
**预期结果**: 步骤 1、2 返回 200 且 `landscape`="海岸线景观"；步骤 3 返回 200 且 `landscape`="火山地貌"；步骤 4 返回 200 且 `landscape`="火山地貌"
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/activity-landscape-field/TC-activity-IT-020/`
**最后更新**: 2026-08-24
