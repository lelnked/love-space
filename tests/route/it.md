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

### TC-route-IT-007: POST /api/admin/routes 缺必填或大使不存在被拒绝（城市名不校验）
**关联需求**: route/路线管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1routes/post
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. POST /api/admin/routes，body 缺 title（其余合法）
2. POST /api/admin/routes，body ambassadorId 为不存在的 UUID
3. POST /api/admin/routes，body cityName 为城市表中不存在的「不存在城」（其余合法）
**预期结果**: 步骤 1、2 返回 400，响应 `message` 为中文错误信息且路线未创建；步骤 3 返回 200，详情 `cityName` 原样为「不存在城」
**状态**: ⏳ 待执行
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

### TC-route-IT-012: GET /api/app/routes?cityName= 按城市名查路线列表并按 sortOrder 升序
**关联需求**: route/App 端路线查询#查询上架城市的路线
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: app-route-query-filters
**优先级**: P0
**测试步骤**:
1. 前置：上架城市下存在多条路线（sortOrder 5、1、3），关联大使均上线
2. GET http://localhost:8081/api/app/routes?cityName={cityName}（请求头带 X-API-Key）
**预期结果**: 返回 200，含该城市全部可见路线，按 sortOrder 1→3→5 升序，每项含缩略图（签名 URL）、主标题与大使名称
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-route-IT-012/`
**最后更新**: 2026-08-26

### TC-route-IT-013: GET /api/app/routes 大使下线后路线隐藏、详情 404
**关联需求**: route/App 端路线查询#大使下线后路线隐藏
**关联契约**: api-spec.json#/paths/~1api~1app~1routes~1{id}/get
**来源**: app-route-query-filters
**优先级**: P0
**测试步骤**:
1. 前置：上架城市下一条路线可见（app 列表能查到）
2. admin 侧 PUT /api/admin/ambassadors/{ambassadorId}/online 将其关联大使下线
3. GET http://localhost:8081/api/app/routes?cityName={cityName}（请求头带 X-API-Key）
4. GET http://localhost:8081/api/app/routes/{routeId}（请求头带 X-API-Key）
**预期结果**: 下线后列表不含该路线；详情返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-route-query-filters/TC-route-IT-013/`
**最后更新**: 2026-08-24

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
**执行存证**: `test-evidence/app-route-query-filters/TC-route-IT-014/`
**最后更新**: 2026-08-24

### TC-route-IT-015: GET /api/app/routes 未上架城市的路线仍可见且详情返回 cityName
**关联需求**: route/App 端路线查询#未上架城市的路线仍可见
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get、api-spec.json#/paths/~1api~1app~1routes~1{id}/get
**来源**: app-route-query-filters
**优先级**: P0
**测试步骤**:
1. 前置：admin 侧创建一个城市（中文名「未上线城」）并保持**下架**状态；在该城市下创建一条路线，其关联大使 online=true
2. GET http://localhost:8081/api/app/routes?cityName={cityName}（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/routes/{routeId}（请求头带 X-API-Key）
4. admin 侧将该城市上架，重复步骤 2、3
**预期结果**: 步骤 2 返回 200 且列表包含该路线（城市下架不再过滤）；步骤 3 返回 200，`cityName` = "未上线城"，其余字段（图片、地点、大使信息）与既有口径一致；步骤 4 城市上架后结果不变（可见性与城市状态无关）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-route-query-filters/TC-route-IT-015/`
**最后更新**: 2026-08-24

### TC-route-IT-016: GET /api/app/routes 不带任何参数返回全部可见路线
**关联需求**: route/App 端路线查询#不传任何过滤参数返回全部可见路线
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: app-route-query-filters
**优先级**: P0
**测试步骤**:
1. 前置：城市甲、城市乙下各有一条路线（sortOrder 分别为 2、1），关联大使均上线
2. GET http://localhost:8081/api/app/routes（不带任何查询参数，请求头带 X-API-Key）
**预期结果**: 返回 200，同时包含两个城市的路线，按 sortOrder 1→2 升序；每项的 `city` 对象对应各自所属城市（id 与中文名）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-route-query-filters/TC-route-IT-016/`
**最后更新**: 2026-08-24

### TC-route-IT-017: GET /api/app/routes?ambassadorId= 按大使过滤路线
**关联需求**: route/App 端路线查询#按大使 ID 过滤路线
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: app-route-query-filters
**优先级**: P0
**测试步骤**:
1. 前置：大使 A 上线且名下 2 条路线，大使 B 上线且名下 1 条路线
2. GET http://localhost:8081/api/app/routes?ambassadorId={ambassadorA}（请求头带 X-API-Key）
3. 将大使 A 下线后重复步骤 2
**预期结果**: 步骤 2 返回 200 且仅含大使 A 的 2 条路线（不含 B 的路线），按 sortOrder 升序；步骤 3 返回 200 且为空数组（大使下线优先于过滤条件）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-route-query-filters/TC-route-IT-017/`
**最后更新**: 2026-08-24

### TC-route-IT-018: GET /api/app/routes?cityName=&ambassadorId= 组合过滤取交集
**关联需求**: route/App 端路线查询#城市名与大使 ID 组合过滤
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: app-route-query-filters
**优先级**: P1
**测试步骤**:
1. 前置：大使 A 上线，在城市甲、城市乙各有 1 条路线；城市甲下另有大使 B 的 1 条路线
2. GET http://localhost:8081/api/app/routes?cityName={城市甲中文名}&ambassadorId={ambassadorA}（请求头带 X-API-Key）
**预期结果**: 返回 200，仅含 1 条路线（城市甲 + 大使 A 的那条），不含城市乙的 A 路线与城市甲的 B 路线
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-route-query-filters/TC-route-IT-018/`
**最后更新**: 2026-08-24

### TC-route-IT-019: GET /api/app/routes?cityName= 城市表无同名城市时仍返回路线且 city 为 null
**关联需求**: route/App 端路线查询#城市表中无同名城市时仍返回路线且 city 为 null
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: app-route-query-filters / app-route-list-city-name
**优先级**: P1
**测试步骤**:
1. 前置：admin 侧创建一条路线，cityName 填「不存在城」（城市表中无同名城市），关联大使 online=true
2. GET http://localhost:8081/api/app/routes?cityName=不存在城（请求头带 X-API-Key）
**预期结果**: 返回 200，body 含该路线，其 `city` 为 `null`，其 `cityName` 为「不存在城」
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/route/TC-route-IT-019/`
**最后更新**: 2026-09-01

### TC-route-IT-020: GET /api/app/ambassadors 默认返回权重最高的 3 位上线大使
**关联需求**: route/爱女大使管理
**关联契约**: api-spec.json#/paths/~1api~1app~1ambassadors/get
**来源**: 直接实现（未走 change）
**优先级**: P0
**测试步骤**:
1. 前置：创建 4 位 online=true 的大使，weight 分别为 30/20/10/1；另创建 1 位 weight=40 但 online=false
2. GET /api/app/ambassadors（不传 limit，请求头带 X-API-Key）
**预期结果**: 返回 200，数组长度 3，顺序为 weight 30 → 20 → 10；下线大使不出现
**状态**: ✅ 通过
**执行方式**: AmbassadorReadIT#listReturnsTop3OnlineByWeightDescWhenLimitAbsent
**执行存证**: `love-space-app/target/surefire-reports/com.space.app.modules.ambassador.controller.AmbassadorReadIT.txt`
**最后更新**: 2026-08-24

### TC-route-IT-021: GET /api/app/ambassadors?limit= 生效且上限 20、非法值回落 3
**关联需求**: route/爱女大使管理
**关联契约**: api-spec.json#/paths/~1api~1app~1ambassadors/get
**来源**: 直接实现（未走 change）
**优先级**: P0
**测试步骤**:
1. 前置：25 位 online=true 大使，weight 依次 0..24
2. GET /api/app/ambassadors?limit=5
3. GET /api/app/ambassadors?limit=100
4. GET /api/app/ambassadors?limit=0
**预期结果**: 步骤 2 返回 5 条且首条为 weight 最大者；步骤 3 收敛为 20 条；步骤 4 回落为 3 条（与 PageQuery 的非法值回落口径一致，不返回 400）
**状态**: ✅ 通过
**执行方式**: AmbassadorReadIT#listHonoursLimitAndClampsAt20
**执行存证**: `love-space-app/target/surefire-reports/com.space.app.modules.ambassador.controller.AmbassadorReadIT.txt`
**最后更新**: 2026-08-24

### TC-route-IT-022: GET /api/app/ambassadors/{id} 详情与 404 口径
**关联需求**: route/爱女大使管理
**关联契约**: api-spec.json#/paths/~1api~1app~1ambassadors~1{id}/get
**来源**: 直接实现（未走 change）
**优先级**: P0
**测试步骤**:
1. GET /api/app/ambassadors/{id}，id 指向 online=true 的大使
2. GET /api/app/ambassadors/{id}，id 指向 online=false 的大使
3. GET /api/app/ambassadors/{随机 UUID}
**预期结果**: 步骤 1 返回 200，含 id/avatar{id,url}/name/tags；步骤 2、3 均返回 404
**状态**: ✅ 通过
**执行方式**: AmbassadorReadIT#detailReturnsOnlineAmbassador、AmbassadorReadIT#detailReturns404WhenOfflineOrMissing
**执行存证**: `love-space-app/target/surefire-reports/com.space.app.modules.ambassador.controller.AmbassadorReadIT.txt`
**最后更新**: 2026-08-24

### TC-route-IT-023: admin 大使创建/更新写入排序权重
**关联需求**: route/爱女大使管理
**关联契约**: api-spec.json#/paths/~1api~1admin~1ambassadors/post
**来源**: 直接实现（未走 change）
**优先级**: P1
**测试步骤**:
1. POST /api/admin/ambassadors，body 含 `"weight": 30`
2. GET /api/admin/ambassadors/{id}
3. PUT /api/admin/ambassadors/{id}，body 不传 weight
**预期结果**: 步骤 1、2 响应 `weight` 为 30；步骤 3 后 `weight` 回落为默认 0
**状态**: ✅ 通过
**执行方式**: AmbassadorServiceTest#weightIsPersistedAndDefaultsToZero
**执行存证**: `love-space-admin/target/surefire-reports/com.loves.space.modules.ambassador.service.AmbassadorServiceTest.txt`
**最后更新**: 2026-08-24

### TC-route-IT-024: GET /api/app/routes 同排序号路线按创建时间倒序
**关联需求**: route/App 端路线查询#同排序号路线按创建时间倒序
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: app-list-sort-tiebreak
**优先级**: P1
**测试步骤**:
1. admin 侧（http://localhost:21423）登录，创建一个上线大使 amb
2. 以同一 `cityName`（如 `排序测试城`）**先后**创建两条路线 A、B，二者 `sortOrder` **同为 0**，均关联 amb
3. GET http://localhost:8081/api/app/routes?cityName=排序测试城，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200 数组；后创建的 B 排在先创建的 A 之前（同序号按 `createdAt` 倒序）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-route-IT-024/`
**最后更新**: 2026-08-26

### TC-route-IT-025: GET /api/app/routes 列表项返回 ambassadorNote
**关联需求**: route/App 端路线查询#路线列表返回爱女大使说
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: app-route-ambassador-fields
**优先级**: P1
**测试步骤**:
1. admin 侧（http://localhost:21423）登录，创建一个上线大使 amb
2. 以同一 `cityName`（如 `大使说测试城`）创建两条关联 amb 的路线：路线甲 `ambassadorNote` 填「跟着我逛老城区」、`sortOrder=1`；路线乙不填 `ambassadorNote`、`sortOrder=2`
3. GET http://localhost:8081/api/app/routes?cityName=大使说测试城，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200 数组共 2 条；路线甲 `ambassadorNote` == "跟着我逛老城区"，路线乙 `ambassadorNote` 为 null；两条的 `id`/`title`/`thumbnail`/`sortOrder`/`ambassadorName`/`city` 字段仍完整返回
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/route/TC-route-IT-025/`
**最后更新**: 2026-09-01

### TC-route-IT-026: GET /api/app/routes/{id} 详情 ambassador 含 id
**关联需求**: route/App 端路线查询#路线详情返回大使 id
**关联契约**: api-spec.json#/paths/~1api~1app~1routes~1{id}/get
**来源**: app-route-ambassador-fields
**优先级**: P1
**测试步骤**:
1. admin 侧（http://localhost:21423）登录，创建一个上线大使 amb 及其名下一条路线 R
2. GET http://localhost:8081/api/app/routes/{R.id}，请求头 `X-API-Key: test-api-key`
3. 取响应 `ambassador.id`，再 GET http://localhost:8081/api/app/routes?ambassadorId={该 id}
**预期结果**: 步骤 2 返回 200，`ambassador.id` == amb 的 id，且 `ambassador` 的 `name`/`avatar`/`tags` 仍返回；步骤 3 返回 200，结果含路线 R
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/route/TC-route-IT-026/`
**最后更新**: 2026-08-28

### TC-route-IT-027: GET /api/app/routes 列表项返回路线自身城市名 cityName
**关联需求**: route/App 端路线查询#列表项返回路线自身城市名
**关联契约**: api-spec.json#/paths/~1api~1app~1routes/get
**来源**: app-route-list-city-name
**优先级**: P1
**测试步骤**:
1. admin 侧（http://localhost:21423）登录，创建城市「成都」，再创建一条 cityName 为「成都」的路线 R，关联大使 online=true
2. GET http://localhost:8081/api/app/routes，请求头 `X-API-Key: test-api-key`
3. GET http://localhost:8081/api/app/routes/{R.id}，请求头同上
**预期结果**: 步骤 2 返回 200，R 对应列表项 `cityName` == "成都"，`city` 为 `{id, name:"成都"}`；步骤 3 详情 `cityName` == "成都"，与列表项一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/route/TC-route-IT-027/`
**最后更新**: 2026-09-01
