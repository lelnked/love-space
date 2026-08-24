# recommend-list IT 用例

### TC-recommend-list-IT-001: POST /api/admin/recommend-lists 创建清单成功
**关联需求**: recommend-list/推荐清单管理#创建清单
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists/post
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/recommend-lists，body：`{"title": "江畔约会精选", "introduction": "沿江十家小店", "cityId": <已存在城市 id>, "sortOrder": 3}`
3. GET /api/admin/recommend-lists/{id}
**预期结果**: 创建返回 200；详情返回 title="江畔约会精选"、introduction="沿江十家小店"、cityId 与提交一致、sortOrder=3
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-001/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-002: POST /api/admin/recommend-lists 缺少必填项被拒绝
**关联需求**: recommend-list/推荐清单管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists/post
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. POST /api/admin/recommend-lists，body 缺 title：`{"cityId": <城市 id>}`
2. POST /api/admin/recommend-lists，body 缺 cityId：`{"title": "无城市清单"}`
**预期结果**: 两次均返回 400，响应 `message` 为中文校验错误信息；清单均未创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-002/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-003: POST /api/admin/recommend-lists 不传 sortOrder 默认 0
**关联需求**: recommend-list/推荐清单管理#创建清单
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists/post
**来源**: map-and-recommend-list
**优先级**: P1
**测试步骤**:
1. POST /api/admin/recommend-lists，body：`{"title": "默认排序清单", "cityId": <城市 id>}`（不含 sortOrder）
2. GET /api/admin/recommend-lists/{id}
**预期结果**: 创建返回 200；详情 sortOrder=0
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-003/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-004: PUT /api/admin/recommend-lists/{id} 更新清单且 cityId 不可变
**关联需求**: recommend-list/推荐清单管理#创建清单
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put
**来源**: map-and-recommend-list
**优先级**: P1
**测试步骤**:
1. 前置：城市 A 下已存在清单
2. PUT /api/admin/recommend-lists/{id}，body：`{"title": "改名后的清单", "introduction": "新介绍", "cityId": <城市 B 的 id>, "sortOrder": 9}`
3. GET /api/admin/recommend-lists/{id}
**预期结果**: title/introduction/sortOrder 更新生效；cityId 仍为城市 A（不可变——被忽略或返回 400 中文业务错误，按实现口径断言其一并记录）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-004/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-005: DELETE /api/admin/recommend-lists/{id} 物理删除含商户关联的清单
**关联需求**: recommend-list/推荐清单管理#删除清单
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/delete
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：清单已存在且通过 PUT /{id}/merchants 关联了至少一个商户
2. DELETE /api/admin/recommend-lists/{id}
3. GET /api/admin/recommend-lists/{id}
4. GET /api/admin/merchants/{merchantId} 查询被关联过的商户
**预期结果**: 删除返回 200；再查详情返回 400 及中文业务错误（admin 端「资源不存在」全局口径，IllegalArgumentException→400）；商户本身仍存在、字段不受影响
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-005/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-006: GET /api/admin/recommend-lists/page 按 sortOrder 升序并支持过滤
**关联需求**: recommend-list/推荐清单管理#清单列表按排序号升序
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1page/get
**来源**: map-and-recommend-list
**优先级**: P1
**测试步骤**:
1. 前置：同一城市下创建 3 个清单，sortOrder 分别为 5、1、3，其中一个标题含关键字「精选」
2. GET /api/admin/recommend-lists/page?cityId={cityId}&page=0&size=10
3. GET /api/admin/recommend-lists/page?cityId={cityId}&keyword=精选
**预期结果**: 步骤 2 返回 3 条且按 sortOrder 1→3→5 排列；步骤 3 仅返回标题含「精选」的清单
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-006/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-007: PUT /api/admin/recommend-lists/{id}/merchants 全量替换本城市商户
**关联需求**: recommend-list/清单内商户维护#添加本城市商户
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1merchants/put
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：清单所属城市下存在商户 M1、M2
2. PUT /api/admin/recommend-lists/{id}/merchants，body：`[{"merchantId": <M2>, "sortOrder": 1}, {"merchantId": <M1>, "sortOrder": 2}]`
3. GET /api/admin/recommend-lists/{id}
**预期结果**: 替换返回 200；详情商户列表按 sortOrder 升序为 M2、M1
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-007/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-008: PUT /api/admin/recommend-lists/{id}/merchants 跨城市商户被拒绝
**关联需求**: recommend-list/清单内商户维护#拒绝跨城市商户
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1merchants/put
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：清单属于城市 A，商户 Mx 属于城市 B
2. PUT /api/admin/recommend-lists/{id}/merchants，body：`[{"merchantId": <Mx>, "sortOrder": 1}]`
3. GET /api/admin/recommend-lists/{id}
**预期结果**: 返回 400，响应 `message` 为中文业务错误信息；详情商户列表保持替换前状态（关联未建立）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-008/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-009: PUT /api/admin/recommend-lists/{id}/merchants 重复商户被拒绝
**关联需求**: recommend-list/清单内商户维护#重复添加同一商户被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1merchants/put
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. PUT /api/admin/recommend-lists/{id}/merchants，body 含同一商户两次：`[{"merchantId": <M1>, "sortOrder": 1}, {"merchantId": <M1>, "sortOrder": 2}]`
**预期结果**: 返回 400，响应 `message` 为中文业务错误信息；清单商户关联不发生变化
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-009/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-010: PUT /api/admin/recommend-lists/{id}/merchants 移除商户不影响商户本身
**关联需求**: recommend-list/清单内商户维护#从清单移除商户
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1merchants/put
**来源**: map-and-recommend-list
**优先级**: P1
**测试步骤**:
1. 前置：清单已含商户 M1、M2
2. PUT /api/admin/recommend-lists/{id}/merchants，body 仅保留 M2：`[{"merchantId": <M2>, "sortOrder": 1}]`
3. GET /api/admin/recommend-lists/{id}
4. GET /api/admin/merchants/{M1}
**预期结果**: 替换返回 200；详情商户列表仅含 M2；商户 M1 本身仍存在且字段不受影响
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-010/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-011: GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序
**关联需求**: recommend-list/App 端清单查询#查询上架城市的清单
**关联契约**: api-spec.json#/paths/~1api~1app~1recommend-lists/get
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：某上架城市下存在多个清单（sortOrder 5、1、3）
2. GET http://localhost:8081/api/app/recommend-lists?cityId={cityId}（请求头带 API-key）
**预期结果**: 返回 200，包含该城市全部清单，按 sortOrder 1→3→5 升序排列
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-011/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-012: GET /api/app/recommend-lists/{id} 详情返回商户明细按排序升序
**关联需求**: recommend-list/App 端清单查询#清单详情返回商户明细
**关联契约**: api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：清单含多个商户，关联 sortOrder 各异，商户配有名称/图片/推荐理由
2. GET http://localhost:8081/api/app/recommend-lists/{id}（请求头带 API-key）
**预期结果**: 返回 200；含清单字段（title、introduction、sortOrder）与商户列表，商户按关联 sortOrder 升序，每项含名称、图片、recommendReason 等展示字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-012/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-013: GET /api/app/recommend-lists 下架城市清单不可见、详情 404
**关联需求**: recommend-list/App 端清单查询#下架城市清单不可见
**关联契约**: api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：某城市配有清单后被下架
2. GET http://localhost:8081/api/app/recommend-lists?cityId={cityId}
3. GET http://localhost:8081/api/app/recommend-lists/{listId}
**预期结果**: 列表请求返回空数据（不含该城市任何清单）；详情请求返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-recommend-list-IT-013/`
**最后更新**: 2026-08-16

### TC-recommend-list-IT-014: GET /api/app/merchants/page?recommendListId= 按推荐清单过滤商户
**关联需求**: recommend-list/App 端清单查询#按推荐清单过滤商户列表
**关联契约**: api-spec.json#/paths/~1api~1app~1merchants~1page/get
**来源**: app-recommend-list-merchant-filter
**优先级**: P1
**测试步骤**:
1. 前置：某上架城市下有 3 个上架商户；其中商户甲（weight 低）、商户乙（weight 高）加入清单 L，清单内 sortOrder 分别为 1、2；商户丙不在清单内
2. GET http://localhost:8081/api/app/merchants/page?cityId={cityId}（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/merchants/page?cityId={cityId}&recommendListId={listId}（请求头带 X-API-Key）
**预期结果**: 步骤 2 返回 200，totalElements=3，按 weight 降序（乙在前），每项 `recommendSortOrder` 为 null；步骤 3 返回 200，totalElements=2，顺序为甲→乙（清单内 sortOrder 升序），`recommendSortOrder` 分别为 1、2，商户丙不出现
**状态**: ⏳ 待执行
**执行方式**: api-test-runner
**执行存证**: -
**最后更新**: 2026-08-24
