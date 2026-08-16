# featured IT 用例

### TC-featured-IT-001: POST /api/admin/featured-items 创建精选推荐
**关联需求**: featured/精选推荐管理#创建精选推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items/post
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. 前置：存在一个上架城市
3. POST /api/admin/featured-items，body：cityId（该城市）、banner（objectKey）、description「地图上新」、online=true
4. GET /api/admin/featured-items/{id}
**预期结果**: 创建返回 200；详情含关联城市 cityId（城市名称由 web 端经城市列表映射展示，admin 响应不冗余，与 Activity/Banner 口径一致）、banner 签名 URL（http 开头、非裸 objectKey）、description 与 online=true
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-001/`
**最后更新**: 2026-08-16

### TC-featured-IT-002: POST /api/admin/featured-items 缺 banner 或城市不存在被拒绝
**关联需求**: featured/精选推荐管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items/post
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. POST /api/admin/featured-items，body 缺 banner（cityId 合法）
2. POST /api/admin/featured-items，body cityId 为不存在的 UUID（banner 合法）
3. POST /api/admin/featured-items，body 缺 cityId
**预期结果**: 三次均返回 400，响应 `message` 为中文业务错误；条目均未创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-002/`
**最后更新**: 2026-08-16

### TC-featured-IT-003: PUT /api/admin/featured-items/{id}/online 上下线切换
**关联需求**: featured/精选推荐管理#精选推荐上下线切换
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items~1{id}~1online/put
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 online=true 的推荐条目
2. PUT /api/admin/featured-items/{id}/online，body：`{"online": false}`
3. GET /api/admin/featured-items/{id}
4. PUT /api/admin/featured-items/{id}/online，body：`{"online": true}` 后再查详情
**预期结果**: 步骤 2 返回 200，步骤 3 详情 online=false；步骤 4 后详情 online=true（可往返切换）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-003/`
**最后更新**: 2026-08-16

### TC-featured-IT-004: PUT /api/admin/featured-items/{id} 更新条目且 cityId 不可变
**关联需求**: featured/精选推荐管理#创建精选推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items~1{id}/put
**来源**: article-and-featured-feed
**优先级**: P1
**测试步骤**:
1. 前置：城市 A 下已存在推荐条目；另存在城市 B
2. PUT /api/admin/featured-items/{id}，body：description 改写、banner 换新 objectKey、cityId 传城市 B 的 id
3. GET /api/admin/featured-items/{id}
**预期结果**: 更新返回 200；description 与 banner 更新生效（banner 为新图签名 URL）；关联城市仍为城市 A（cityId 变更被忽略，不返回错误）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-004/`
**最后更新**: 2026-08-16

### TC-featured-IT-005: DELETE /api/admin/featured-items/{id} 物理删除
**关联需求**: featured/精选推荐管理#创建精选推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items~1{id}/delete
**来源**: article-and-featured-feed
**优先级**: P1
**测试步骤**:
1. 前置：存在一个推荐条目
2. DELETE /api/admin/featured-items/{id}
3. GET /api/admin/featured-items/{id}
4. GET /api/admin/featured-items/page 确认分页列表不含该条目
**预期结果**: 删除返回 200；再查详情返回 400 及中文业务错误（admin 端「资源不存在」全局口径）；分页列表不再出现该条目
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-005/`
**最后更新**: 2026-08-16

### TC-featured-IT-006: GET /api/app/featured-items 信息流仅含上线条目且按创建时间倒序
**关联需求**: featured/App 端精选推荐查询#查询精选推荐信息流
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-items/get
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：某上架城市下先后创建两条上线推荐（条目 1 先建、条目 2 后建）与一条下线推荐
2. GET http://localhost:8081/api/app/featured-items（请求头带 X-API-Key）
**预期结果**: 返回 200；列表仅含两条上线条目，不含下线条目；按创建时间倒序（条目 2 在前）；每项含 banner 签名 URL、推荐说明与关联城市数据（id 与名称）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-006/`
**最后更新**: 2026-08-16
