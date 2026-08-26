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
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-001/`
**最后更新**: 2026-08-25

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
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-002/`
**最后更新**: 2026-08-25

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
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-003/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-004: PUT /api/admin/recommend-lists/{id} 修改所属城市需清单内商户同属新城市
**关联需求**: recommend-list/推荐清单管理#修改所属城市需清单内商户同属新城市
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put
**来源**: recommend-list-align-spec-to-merchant-ids
**优先级**: P1
**测试步骤**:
1. 前置：城市 A、城市 B 均存在；城市 A 下清单 L1 含城市 A 的商户 M1（创建时 `merchantIds: [<M1>]`）；城市 A 下另有清单 L2 无商户
2. PUT /api/admin/recommend-lists/{L1}，body：`{"title": "改名后的清单", "introduction": "新介绍", "cityId": <城市 B 的 id>, "sortOrder": 9}`（不带 merchantIds）
3. GET /api/admin/recommend-lists/{L1}
4. PUT /api/admin/recommend-lists/{L2}，body：`{"title": "换城市的清单", "cityId": <城市 B 的 id>, "sortOrder": 9}`
5. GET /api/admin/recommend-lists/{L2}
**预期结果**: 步骤 2 返回 400，响应 `message` 为中文业务错误信息；步骤 3 详情 cityId 仍为城市 A、merchants 仍为 [M1]；步骤 4 返回 200；步骤 5 详情 cityId 为城市 B、title="换城市的清单"、sortOrder=9
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-004/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-005: DELETE /api/admin/recommend-lists/{id} 物理删除含商户关联的清单
**关联需求**: recommend-list/推荐清单管理#删除清单
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/delete
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：清单已存在且通过创建/更新请求的 `merchantIds` 关联了至少一个商户
2. DELETE /api/admin/recommend-lists/{id}
3. GET /api/admin/recommend-lists/{id}
4. GET /api/admin/merchants/{merchantId} 查询被关联过的商户
**预期结果**: 删除返回 200；再查详情返回 400 及中文业务错误（admin 端「资源不存在」全局口径，IllegalArgumentException→400）；商户本身仍存在、字段不受影响
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-005/`
**最后更新**: 2026-08-25

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
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-006/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-007: PUT /api/admin/recommend-lists/{id} merchantIds 整体替换本城市商户并按数组顺序回显
**关联需求**: recommend-list/清单内商户维护#添加本城市商户
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put
**来源**: recommend-list-align-spec-to-merchant-ids
**优先级**: P0
**测试步骤**:
1. 前置：清单所属城市下存在上架商户 M1、M2
2. PUT /api/admin/recommend-lists/{id}，body：`{"title": <原标题>, "merchantIds": [<M2>, <M1>]}`
3. GET /api/admin/recommend-lists/{id}
**预期结果**: 更新返回 200；详情 `merchants` 顺序为 M2、M1（数组顺序即清单保存顺序）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-007/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-008: PUT /api/admin/recommend-lists/{id} merchantIds 含跨城市商户被拒绝
**关联需求**: recommend-list/清单内商户维护#拒绝跨城市商户
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put
**来源**: recommend-list-align-spec-to-merchant-ids
**优先级**: P0
**测试步骤**:
1. 前置：清单属于城市 A，商户 Mx 属于城市 B
2. PUT /api/admin/recommend-lists/{id}，body：`{"title": <原标题>, "merchantIds": [<Mx>]}`
3. GET /api/admin/recommend-lists/{id}
**预期结果**: 返回 400，响应 `message` 为中文业务错误信息；详情 `merchants` 保持更新前状态（关联未建立）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-008/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-009: PUT /api/admin/recommend-lists/{id} merchantIds 重复商户被拒绝
**关联需求**: recommend-list/清单内商户维护#重复添加同一商户被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put
**来源**: recommend-list-align-spec-to-merchant-ids
**优先级**: P0
**测试步骤**:
1. 前置：清单所属城市下存在上架商户 M1
2. PUT /api/admin/recommend-lists/{id}，body 含同一商户两次：`{"title": <原标题>, "merchantIds": [<M1>, <M1>]}`
3. GET /api/admin/recommend-lists/{id}
**预期结果**: 返回 400，响应 `message` 为中文业务错误信息；详情 `merchants` 保持更新前状态（关联不变）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-009/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-010: PUT /api/admin/recommend-lists/{id} merchantIds 去掉商户即移除且不影响商户本身
**关联需求**: recommend-list/清单内商户维护#从清单移除商户
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put
**来源**: recommend-list-align-spec-to-merchant-ids
**优先级**: P1
**测试步骤**:
1. 前置：清单所属城市下存在上架商户 M1、M2
2. PUT /api/admin/recommend-lists/{id}，body：`{"title": <原标题>, "merchantIds": [<M1>, <M2>]}`
3. PUT /api/admin/recommend-lists/{id}，body 仅保留 M2：`{"title": <原标题>, "merchantIds": [<M2>]}`
4. GET /api/admin/recommend-lists/{id}
5. GET /api/admin/merchants/{M1}
**预期结果**: 步骤 2、3 均返回 200；步骤 4 详情 `merchants` 仅含 M2；步骤 5 商户 M1 本身仍存在且字段不受影响
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-010/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-011: GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序
**关联需求**: recommend-list/App 端清单与清单内商户查询#查询上架城市的清单
**关联契约**: api-spec.json#/paths/~1api~1app~1recommend-lists/get
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：某上架城市下存在多个清单（sortOrder 5、1、3）
2. GET http://localhost:8081/api/app/recommend-lists?cityId={cityId}（请求头带 API-key）
**预期结果**: 返回 200，包含该城市全部清单，按 sortOrder 1→3→5 升序排列
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-011/`
**最后更新**: 2026-08-26

### TC-recommend-list-IT-012: GET /api/app/recommend-lists/{id} 详情按清单保存顺序返回上架商户四字段
**关联需求**: recommend-list/App 端清单与清单内商户查询#清单详情返回商户明细
**关联契约**: api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get
**来源**: app-recommend-list-owns-merchant-order
**优先级**: P0
**测试步骤**:
1. 前置：某上架城市下清单按顺序保存了上架商户 甲、乙（甲的 weight 低于乙）；另有已下架商户 丙 也在该清单中
2. GET http://localhost:8081/api/app/recommend-lists/{id}（请求头带 X-API-Key）
**预期结果**: 返回 200；含清单字段（title、introduction、sortOrder）与 `merchants` 数组；`merchants` 顺序为 甲→乙（清单保存顺序，与 weight 无关），丙不出现；每项仅含 `id`、`name`、`address`、`logo` 四个字段，不含 `recommendReason`、`sortOrder`、`merchantId`、`recommendSortOrder`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-012/`
**最后更新**: 2026-08-26

### TC-recommend-list-IT-013: GET /api/app/recommend-lists 下架城市清单不可见、详情 404
**关联需求**: recommend-list/App 端清单与清单内商户查询#下架城市清单不可见
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
**执行存证**: `test-evidence/app-recommend-list-owns-merchant-order/TC-recommend-list-IT-013/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-015: GET /api/app/merchants/page 商户列表不受清单影响
**关联需求**: recommend-list/App 端清单与清单内商户查询#商户列表不受清单影响
**关联契约**: api-spec.json#/paths/~1api~1app~1merchants~1page/get
**来源**: app-recommend-list-owns-merchant-order
**优先级**: P1
**测试步骤**:
1. 前置：某上架城市下有 3 个上架商户 甲、乙、丙（weight 各异）；其中 甲、乙 加入清单 L，清单内保存顺序与 weight 排序相反（weight 低者在前）
2. GET http://localhost:8081/api/app/merchants/page?cityId={cityId}（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/merchants/page?cityId={cityId}&recommendListId={L}（请求头带 X-API-Key）
**预期结果**: 两次均返回 200，totalElements=3，`content` 顺序均按 weight 降序（与清单内顺序无关，`recommendListId` 被忽略）；`content[*]` 不含 `recommendSortOrder` 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-015/`
**最后更新**: 2026-08-26

### TC-recommend-list-IT-016: PUT /api/admin/recommend-lists/{id} merchantIds 含已下架商户被拒绝
**关联需求**: recommend-list/清单内商户维护#拒绝已下架商户
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}/put
**来源**: recommend-list-align-spec-to-merchant-ids
**优先级**: P0
**测试步骤**:
1. 前置：清单所属城市下存在商户 Mo；PUT /api/admin/merchants/{Mo}/online，body：`{"online": false}` 将其下架（⚠️ 待补契约：该路径未登记于 api-spec.json，归 merchant 域）
2. PUT /api/admin/recommend-lists/{id}，body：`{"title": <原标题>, "merchantIds": [<Mo>]}`
3. GET /api/admin/recommend-lists/{id}
**预期结果**: 步骤 2 返回 400，响应 `message` 为中文业务错误信息；步骤 3 详情 `merchants` 不含 Mo（关联未建立）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-016/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-017: POST /api/admin/recommend-lists status 默认 ONLINE 且可带 status/merchantIds 创建
**关联需求**: recommend-list/推荐清单管理#创建清单
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists/post
**来源**: recommend-list-align-spec-to-merchant-ids
**优先级**: P1
**测试步骤**:
1. 前置：城市 A 存在，其下有上架商户 M1
2. POST /api/admin/recommend-lists，body：`{"title": "默认上架清单", "cityId": <城市 A id>}`（不传 status）
3. GET /api/admin/recommend-lists/{id1}
4. POST /api/admin/recommend-lists，body：`{"title": "下架带商户清单", "cityId": <城市 A id>, "status": "OFFLINE", "merchantIds": [<M1>]}`
5. GET /api/admin/recommend-lists/{id2}
**预期结果**: 步骤 2、4 均返回 200；步骤 3 详情 status="ONLINE"、merchants 为空数组；步骤 5 详情 status="OFFLINE"、merchants 仅含 M1
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-017/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-018: POST /api/admin/recommend-lists/{id}/online 人工恢复清单（含下架商户拒绝、成功、幂等）
**关联需求**: recommend-list/推荐清单管理#人工恢复清单
**关联契约**: api-spec.json#/paths/~1api~1admin~1recommend-lists~1{id}~1online/post
**来源**: recommend-list-align-spec-to-merchant-ids
**优先级**: P0
**测试步骤**:
1. 前置：城市 A 下清单 L 为 OFFLINE（创建时 `status: "OFFLINE"`）且含上架商户 M1（`merchantIds: [<M1>]`）
2. PUT /api/admin/merchants/{M1}/online，body：`{"online": false}` 将 M1 下架（⚠️ 待补契约：该路径未登记于 api-spec.json，归 merchant 域）
3. POST /api/admin/recommend-lists/{L}/online
4. GET /api/admin/recommend-lists/{L}
5. PUT /api/admin/merchants/{M1}/online，body：`{"online": true}` 将 M1 上架
6. POST /api/admin/recommend-lists/{L}/online
7. POST /api/admin/recommend-lists/{L}/online（再次调用）
**预期结果**: 步骤 3 返回 400，响应 `message` 为中文业务错误信息；步骤 4 详情 status 仍为 "OFFLINE"；步骤 6 返回 200 且响应 status="ONLINE"；步骤 7 返回 200 且 status 仍为 "ONLINE"（幂等，返回详情）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-018/`
**最后更新**: 2026-08-25

### TC-recommend-list-IT-019: GET /api/app/recommend-lists 同排序号清单按创建时间倒序
**关联需求**: recommend-list/App 端清单与清单内商户查询#同排序号清单按创建时间倒序
**关联契约**: api-spec.json#/paths/~1api~1app~1recommend-lists/get
**来源**: app-list-sort-tiebreak
**优先级**: P1
**测试步骤**:
1. admin 侧（http://localhost:21423）登录，创建上架城市 cityId
2. 在该城市下**先后**创建两个 ONLINE 清单 A、B，二者 `sortOrder` **同为 0**
3. GET http://localhost:8081/api/app/recommend-lists?cityId={cityId}，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200；后创建的 B 排在先创建的 A 之前（同序号按 `createdAt` 倒序）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-recommend-list-IT-019/`
**最后更新**: 2026-08-26
