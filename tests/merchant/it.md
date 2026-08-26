# merchant IT 用例

### TC-merchant-IT-001: POST /api/admin/merchants 创建商户保存推荐理由
**关联需求**: merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由
**关联契约**: api-spec.json#/paths/~1api~1admin~1merchants/post
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/merchants，body 含既有必填字段 + `"recommendReason": "步行五分钟即达江景，适合傍晚约会"`（携带 Bearer token）
3. GET /api/admin/merchants/{id} 查询刚创建的商户详情
**预期结果**: 创建返回 200，详情响应 `recommendReason` 与提交值逐字一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-merchant-IT-001/`
**最后更新**: 2026-08-16

### TC-merchant-IT-002: PUT /api/admin/merchants/{id} 更新推荐理由
**关联需求**: merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由
**关联契约**: api-spec.json#/paths/~1api~1admin~1merchants~1{id}/put
**来源**: map-and-recommend-list
**优先级**: P1
**测试步骤**:
1. 前置：已存在一个商户（recommendReason 为空）
2. PUT /api/admin/merchants/{id}，body 在既有字段基础上加 `"recommendReason": "更新后的推荐理由"`
3. GET /api/admin/merchants/{id}
**预期结果**: 更新返回 200，详情 `recommendReason` = "更新后的推荐理由"
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-merchant-IT-002/`
**最后更新**: 2026-08-16

### TC-merchant-IT-003: POST /api/admin/merchants 推荐理由 2000 字边界通过
**关联需求**: merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由
**关联契约**: api-spec.json#/paths/~1api~1admin~1merchants/post
**来源**: map-and-recommend-list
**优先级**: P1
**测试步骤**:
1. POST /api/admin/merchants，`recommendReason` 为恰好 2000 个中文字符（如 "测" 重复 2000 次）
2. GET /api/admin/merchants/{id}
**预期结果**: 创建返回 200，详情 `recommendReason` 长度为 2000 且内容一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-merchant-IT-003/`
**最后更新**: 2026-08-16

### TC-merchant-IT-004: POST /api/admin/merchants 推荐理由 2001 字被拒绝
**关联需求**: merchant/商户编辑推荐理由#推荐理由超长被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1merchants/post
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. POST /api/admin/merchants，`recommendReason` 为 2001 个字符（"测" 重复 2001 次），其余字段合法
2. 用同名等唯一条件确认商户未落库（列表/详情查不到）
**预期结果**: 返回 400，响应 `message` 为中文校验错误信息；商户未被创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-merchant-IT-004/`
**最后更新**: 2026-08-16

### TC-merchant-IT-005: POST /api/admin/merchants 不填推荐理由创建成功
**关联需求**: merchant/商户编辑推荐理由#推荐理由可为空
**关联契约**: api-spec.json#/paths/~1api~1admin~1merchants/post
**来源**: map-and-recommend-list
**优先级**: P1
**测试步骤**:
1. POST /api/admin/merchants，body 不含 `recommendReason` 字段，其余字段合法
2. GET /api/admin/merchants/{id}
**预期结果**: 创建返回 200；详情 `recommendReason` 为 null 或空字符串
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-merchant-IT-005/`
**最后更新**: 2026-08-16

### TC-merchant-IT-006: GET /api/app/merchants/{id} app 端详情返回推荐理由
**关联需求**: merchant/商户编辑推荐理由#app 端商户详情返回推荐理由
**关联契约**: api-spec.json#/paths/~1api~1app~1merchants~1{id}/get
**来源**: map-and-recommend-list
**优先级**: P0
**测试步骤**:
1. 前置：admin 侧已创建上架商户并配置 `recommendReason: "江景约会首选"`
2. GET http://localhost:8081/api/app/merchants/{id}（请求头带 API-key）
**预期结果**: 返回 200，响应体含 `recommendReason` = "江景约会首选"
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/map-and-recommend-list/TC-merchant-IT-006/`
**最后更新**: 2026-08-16

### TC-merchant-IT-007: GET /api/app/categories/page 同排序号分类按创建时间倒序
**关联需求**: merchant/App 端带排序号列表的排序口径#分类列表同序号按创建时间倒序
**关联契约**: ⚠️ 待补契约（api-spec.json 中缺 `/api/app/categories/page` 条目）
**来源**: app-list-sort-tiebreak
**优先级**: P1
**测试步骤**:
1. admin 侧（http://localhost:21423）登录，**先后**创建两个分类 A、B，二者 `sortOrder` **同为 0**，并均上架
2. GET http://localhost:8081/api/app/categories/page?page=0&size=20，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200 分页对象；后创建的 B 排在先创建的 A 之前（同序号按 `createdAt` 倒序）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-merchant-IT-007/`
**最后更新**: 2026-08-26

### TC-merchant-IT-008: GET /api/app/categories/page 排序号优先于创建时间
**关联需求**: merchant/App 端带排序号列表的排序口径#排序号不同时以排序号为准
**关联契约**: ⚠️ 待补契约（api-spec.json 中缺 `/api/app/categories/page` 条目）
**来源**: app-list-sort-tiebreak
**优先级**: P1
**测试步骤**:
1. admin 侧登录，**先**创建分类 A（`sortOrder=1`），**后**创建分类 B（`sortOrder=0`），均上架
2. GET http://localhost:8081/api/app/categories/page?page=0&size=20，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200；B（sortOrder=0）排在 A（sortOrder=1）之前——排序号优先，创建时间仅作同序号 tie-break
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-merchant-IT-008/`
**最后更新**: 2026-08-26

### TC-merchant-IT-009: GET /api/app/merchants/{merchantId}/reviews 同排序号评价按创建时间倒序
**关联需求**: merchant/App 端带排序号列表的排序口径#商户评价同序号按创建时间倒序
**关联契约**: ⚠️ 待补契约（api-spec.json 中缺 `/api/app/merchants/{merchantId}/reviews` 条目）
**来源**: app-list-sort-tiebreak
**优先级**: P1
**测试步骤**:
1. admin 侧登录，创建上架城市与一个上架商户 M
2. 为 M **先后**创建两条评价 A、B，二者 `sortOrder` **同为 0**
3. GET http://localhost:8081/api/app/merchants/{M}/reviews，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200 数组；后创建的 B 排在先创建的 A 之前（同序号按 `createdAt` 倒序）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-merchant-IT-009/`
**最后更新**: 2026-08-26
