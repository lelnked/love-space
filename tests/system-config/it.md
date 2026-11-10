# system-config IT 用例

> admin 后端 baseUrl `http://localhost:21423`（test profile），JWT 认证，登录 fixture：`admin` / `8@y2eoRLyStM*UVU`；app 后端 baseUrl `http://localhost:8081`，请求头 `X-API-Key` 认证。
> 数据库迁移预置 `ios_llm_popup`（id `019794b6-b400-7000-8000-000000000031`，名称「iOS LLM 弹窗开关」，默认 value `{"enabled": true}`）；修改预置行的用例末尾恢复初始值。

### TC-system-config-IT-001: 预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回
**关联需求**: system-config/系统配置数据模型#预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. GET /api/admin/system-configs/page?keyword=ios_llm_popup&page=1&size=20，记录该项 `id`
3. GET /api/admin/system-configs/{id}
**预期结果**: 步骤 2 返回 200，`content` 含 `key=ios_llm_popup`、`name=iOS LLM 弹窗开关`；步骤 3 返回 200，`value` 的 JSON 类型为 object，等于 `{"enabled":true}`（`enabled` 为布尔 true，不是字符串）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-001/`
**最后更新**: -

### TC-system-config-IT-002: GET /page 按 keyword 匹配 key 或名称（不区分大小写）
**关联需求**: system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1page/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. GET /api/admin/system-configs/page?keyword=llm_popup&page=1&size=20
3. GET /api/admin/system-configs/page?keyword=LLM_POPUP&page=1&size=20
4. GET /api/admin/system-configs/page?keyword=弹窗开关&page=1&size=20
5. GET /api/admin/system-configs/page?keyword=zz_nomatch&page=1&size=20
**预期结果**: 步骤 2/3/4 均返回 200，`content` 含 `ios_llm_popup`；步骤 5 返回 200、`content=[]`、`totalElements=0`；每项含 `id,key,name,value,description,createdAt,updatedAt`
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-002/`
**最后更新**: -

### TC-system-config-IT-003: GET /{id} 详情不存在返回 404
**关联需求**: system-config/后台系统配置分页查询#详情不存在返回 404
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/get
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token
2. GET /api/admin/system-configs/00000000-0000-0000-0000-000000000000
**预期结果**: 返回 404，`message` 为「配置不存在」
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-003/`
**最后更新**: -

### TC-system-config-IT-004: 未登录访问后台系统配置接口返回 401
**关联需求**: system-config/后台系统配置分页查询#未登录被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1page/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. 不带 Authorization 请求 GET /api/admin/system-configs/page?page=1&size=20
2. 带非法 token `Bearer invalid` 请求 GET /api/admin/system-configs/page?page=1&size=20
**预期结果**: 两次均返回 401
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-004/`
**最后更新**: -

### TC-system-config-IT-005: PUT /{id} 编辑成功且忽略请求体 key
**关联需求**: system-config/后台编辑系统配置#编辑不能修改 key
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token，查询 `ios_llm_popup` 的 id
2. PUT /api/admin/system-configs/{id}，body `{"key":"other_key","name":"iOS LLM 弹窗开关-改","value":{"enabled":false},"description":"说明-改"}`
3. GET /api/admin/system-configs/{id}
4. 收尾：PUT 恢复 `{"name":"iOS LLM 弹窗开关","value":{"enabled":true},"description":"控制 iOS 系统调用 LLM 时是否需要弹窗"}`
**预期结果**: 步骤 2 返回 200，响应 `key` 仍为 `ios_llm_popup`，`name` 为「iOS LLM 弹窗开关-改」，`value` 为 `{"enabled":false}`，`description` 为「说明-改」；步骤 3 与响应一致
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-005/`
**最后更新**: -

### TC-system-config-IT-006: PUT /{id} value 为 null 或缺失返回 400
**关联需求**: system-config/后台编辑系统配置#value 为 null 返回 400
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token，查询 `ios_llm_popup` 的 id
2. PUT /api/admin/system-configs/{id}，body `{"name":"iOS LLM 弹窗开关","value":null}`
3. PUT /api/admin/system-configs/{id}，body `{"name":"iOS LLM 弹窗开关"}`
**预期结果**: 步骤 2/3 均返回 400 且 `message` 为「value 不能为空」
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-006/`
**最后更新**: -

### TC-system-config-IT-007: PUT /{id} ios_llm_popup 的 enabled 非布尔值返回 400
**关联需求**: system-config/后台编辑系统配置#ios_llm_popup 的 enabled 非布尔值返回 400
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token，查询 `ios_llm_popup` 的 id
2. PUT /api/admin/system-configs/{id}，body `{"name":"iOS LLM 弹窗开关","value":{}}`
3. PUT /api/admin/system-configs/{id}，body `{"name":"iOS LLM 弹窗开关","value":{"enabled":"yes"}}`
4. PUT /api/admin/system-configs/{id}，body `{"name":"iOS LLM 弹窗开关","value":true}`
**预期结果**: 步骤 2/3/4 均返回 400 且 `message` 为「ios_llm_popup 的 enabled 必须为布尔值」
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-007/`
**最后更新**: -

### TC-system-config-IT-008: 编辑 value 后 App 立即读到新值
**关联需求**: system-config/后台编辑系统配置#编辑 value 后 App 立即读到新值
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token，查询 `ios_llm_popup` 的 id
2. admin PUT 该配置，body `{"name":"iOS LLM 弹窗开关","value":{"enabled":false}}`
3. GET http://localhost:8081/api/app/system-configs/ios_llm_popup，请求头 `X-API-Key: *** key>`
4. 收尾：admin PUT 恢复 `{"name":"iOS LLM 弹窗开关","value":{"enabled":true},"description":"控制 iOS 系统调用 LLM 时是否需要弹窗"}`
**预期结果**: 步骤 2 返回 200；步骤 3 返回 200，value 为 `{"enabled":false}`（服务端无缓存）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-008/`
**最后更新**: -

### TC-system-config-IT-009: GET /api/app/system-configs/{key} 单个查询
**关联需求**: system-config/App 查询系统配置#单个 key 查询
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. GET http://localhost:8081/api/app/system-configs/ios_llm_popup，请求头 `X-API-Key: *** key>`
**预期结果**: 返回 200，body 恰为 `{"key":"ios_llm_popup","value":{"enabled":true}}`；不含 `name`、`description`、`id`、时间字段
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-009/`
**最后更新**: -

### TC-system-config-IT-010: GET /api/app/system-configs/{key} 不存在返回 404
**关联需求**: system-config/App 查询系统配置#单个 key 不存在返回 404
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. GET http://localhost:8081/api/app/system-configs/no.such，请求头 `X-API-Key: *** key>`
**预期结果**: 返回 404，`message` 为「配置不存在」
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-010/`
**最后更新**: -

### TC-system-config-IT-011: GET /api/app/system-configs?keys= 批量查询忽略不存在 key 并保持顺序
**关联需求**: system-config/App 查询系统配置#批量查询忽略不存在的 key 并保持顺序
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. GET http://localhost:8081/api/app/system-configs?keys=no.such,ios_llm_popup,ios_llm_popup，请求头 `X-API-Key: *** key>`
**预期结果**: 返回 200，数组共 1 项，为 `{key:"ios_llm_popup",value:{"enabled":true}}`
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-011/`
**最后更新**: -

### TC-system-config-IT-012: 批量查询 keys 缺失、为空与 50/51 边界
**关联需求**: system-config/App 查询系统配置#批量查询 keys 缺失或超限返回 400
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. 以下请求均带 `X-API-Key: *** key>`，baseUrl http://localhost:8081
2. GET /api/app/system-configs（不带 keys）
3. GET /api/app/system-configs?keys=%20,%20,
4. GET ?keys= 50 个互不相同的 key（`k1`…`k50`）
5. GET ?keys= 51 个互不相同的 key（`k1`…`k51`）
**预期结果**: 步骤 2/3 返回 400；步骤 4 返回 200；步骤 5 返回 400
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-012/`
**最后更新**: -

### TC-system-config-IT-013: App 系统配置接口 API-key 缺失或非法返回 401
**关联需求**: system-config/App 查询系统配置#缺少 API-key 返回 401
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. GET http://localhost:8081/api/app/system-configs/ios_llm_popup（不带 X-API-Key）
2. 同路径，请求头 `X-API-Key: ***`
**预期结果**: 两次均返回 401
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-013/`
**最后更新**: -
