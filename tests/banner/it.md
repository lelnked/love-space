# banner IT 用例

### TC-banner-IT-001: POST /api/admin/banners 创建成功且默认下架
**关联需求**: banner/Banner 管理#创建后默认下架
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. POST http://localhost:21423/api/admin/auth/login，body `{"username":"admin","password":"8@y2eoRLyStM*UVU"}`，取 JWT token
2. POST http://localhost:21423/api/admin/cities 创建一个城市，记录 cityId
3. POST http://localhost:21423/api/admin/banners，body `{"name":"IT首页顶部-<随机后缀>","positionCode":"HOME_TOP","type":"CITY","imageUrls":["images/<uuid>.png"],"link":"<cityId>","sortOrder":1,"online":true}`
4. GET http://localhost:21423/api/admin/banners/{id} 查询详情
**预期结果**: 创建返回 **200**（非 201），响应含新建 banner id；详情 `online` 为 false（请求中的 `online:true` 被忽略），`imageUrls` 为对象数组且每项含 `id`（objectKey）与 `url`（签名地址），`linkedCityName` 为关联城市中文名
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-002: POST /api/admin/banners sortOrder=0 边界可创建
**关联需求**: banner/Banner 管理#创建后默认下架
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. 登录取 token；已存在一个城市 cityId
2. POST http://localhost:21423/api/admin/banners，`sortOrder` 为 0，其余字段合法
3. GET http://localhost:21423/api/admin/banners/page?page=0&size=10 查看列表
**预期结果**: 创建返回 200（sortOrder 非负边界值 0 通过校验）；详情 `sortOrder` 为 0、`online` 为 false；分页列表中可检索到该 Banner
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-003: POST /api/admin/banners 名称重复返回 400
**关联需求**: banner/Banner 管理#名称重复被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 登录取 token；已存在城市 cityId
2. POST http://localhost:21423/api/admin/banners，`name` 为「首页顶部」，其余字段合法（首次应 200）
3. 再次 POST http://localhost:21423/api/admin/banners，`name` 仍为「首页顶部」，`positionCode` 换成另一个值
**预期结果**: 第二次返回 400，响应 `message` 为「Banner 名称已存在：首页顶部」（逐字一致）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-004: PUT /api/admin/banners/{id} 携带 online 字段返回 400
**关联需求**: banner/Banner 管理#更新时携带上下架字段被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners~1{id}/put
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 登录取 token；创建一个 Banner，记录 bannerId
2. PUT http://localhost:21423/api/admin/banners/{bannerId}，body 为全字段覆盖且额外携带 `"online": true`
3. GET http://localhost:21423/api/admin/banners/{bannerId} 确认未变更
**预期结果**: 返回 400，`message` 为「更新 banner 时不可修改上下架状态，请使用上下架操作」；详情 `online` 仍为 false，其余字段保持原值
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-005: POST /api/admin/banners 非法 objectKey 返回 400
**关联需求**: banner/Banner 管理#图片 objectKey 格式非法被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. 登录取 token；已存在城市 cityId
2. POST http://localhost:21423/api/admin/banners，`imageUrls` 为 `["other/abc.exe"]`，其余字段合法
**预期结果**: 返回 400，`message` 为 objectKey 格式相关的中文业务错误（非堆栈/英文异常）；Banner 未被创建
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-006: POST /api/admin/banners/{id}/online 关联城市已下架时上架被拒
**关联需求**: banner/Banner 上架前置校验#关联城市下架时无法上架 Banner
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners~1{id}~1online/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 登录取 token；创建城市 cityId 并确保其为下架状态
2. 创建关联该城市的 Banner，记录 bannerId
3. POST http://localhost:21423/api/admin/banners/{bannerId}/online，body 为上架意图 `{"online": true}`
**预期结果**: 返回 400，`message` 为「关联城市已下架，无法上架」；Banner 详情 `online` 仍为 false
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-007: POST /api/admin/banners/{id}/online 关联城市上架时可上架
**关联需求**: banner/Banner 上架前置校验#关联城市上架时可正常上架
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners~1{id}~1online/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 登录取 token；创建城市 cityId 并将其上架
2. 创建关联该城市的 Banner，记录 bannerId
3. POST http://localhost:21423/api/admin/banners/{bannerId}/online（上架）
4. GET http://localhost:21423/api/admin/banners/{bannerId}
**预期结果**: 上架返回 200；详情 `online` 为 true
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-008: POST /api/admin/banners/{id}/online 下架无前置条件
**关联需求**: banner/Banner 上架前置校验#下架无前置条件
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners~1{id}~1online/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. 登录取 token；创建上架城市 cityId 与关联 Banner（bannerId），并将 Banner 上架，确认 `online=true`
2. 将城市 cityId 下架（级联异步，短时间内 Banner 可能仍为 `online=true`）
3. 立即 POST http://localhost:21423/api/admin/banners/{bannerId}/online，body 为下架意图 `{"online": false}`
4. GET http://localhost:21423/api/admin/banners/{bannerId}
**预期结果**: 下架请求返回 200——关联城市已下架也不阻断下架操作（下架无任何前置校验）；详情 `online` 为 false
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-009: 城市下架级联使关联 Banner 下架
**关联需求**: banner/城市状态变更对 Banner 级联生效#城市下架连带 Banner 下架
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities~1{id}~1online/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 登录取 token
2. 创建城市 cityId 并上架
3. 创建关联该城市的 Banner（bannerId），并通过 POST /api/admin/banners/{bannerId}/online 上架，确认 `online=true`
4. 对城市执行下架操作
5. 级联为**事务提交后异步执行**：轮询 GET http://localhost:21423/api/admin/banners/{bannerId}，每 500ms 一次，最多 10s，直到 `online` 变为 false
**预期结果**: 城市下架返回 200；轮询窗口内 Banner 详情 `online` 变为 false，Banner 记录仍存在
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-010: 城市重新上架级联恢复关联 Banner（含手动下架的）
**关联需求**: banner/城市状态变更对 Banner 级联生效#城市重新上架连带 Banner 上架
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities~1{id}~1online/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 登录取 token
2. 创建城市 cityId 并上架；创建关联 Banner（bannerId）并上架
3. 对 Banner 执行手动下架 POST /api/admin/banners/{bannerId}/online（`online=false`），确认 `online=false`
4. 将城市下架，再将城市**重新上架**
5. 轮询 GET http://localhost:21423/api/admin/banners/{bannerId}，每 500ms 一次，最多 10s，直到 `online` 变为 true
**预期结果**: 城市重新上架返回 200；轮询窗口内 Banner `online` 恢复为 true——包括此前被运营**手动下架**的 Banner 也一并恢复上架
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-011: 删除城市只下架不删除关联 Banner
**关联需求**: banner/城市状态变更对 Banner 级联生效#删除城市只下架不删除 Banner
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities~1{id}/delete
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 登录取 token
2. 创建城市 cityId 并上架；创建关联 Banner（bannerId）并上架，确认 `online=true`
3. DELETE http://localhost:21423/api/admin/cities/{cityId}
4. 轮询 GET http://localhost:21423/api/admin/banners/{bannerId}，每 500ms 一次，最多 10s（级联异步）
**预期结果**: 删除城市返回 200；Banner **仍可查询到**（未被删除），`online` 变为 false，`linkedCityName` 为 null
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-banner-IT-012: GET /api/app/banners 按展示位返回上架 Banner 并按排序号升序
**关联需求**: banner/App 端 Banner 查询#按展示位查询上架 Banner
**关联契约**: api-spec.json#/paths/~1api~1app~1banners/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. admin 侧（http://localhost:21423）登录，创建上架城市 cityId
2. 在同一 `positionCode`（如 `APP_HOME_TOP`）下创建两个 Banner：A 的 `sortOrder=1`、B 的 `sortOrder=0`，并分别上架
3. GET http://localhost:8081/api/app/banners?positionCode=APP_HOME_TOP，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200 且 body 为**数组**（非分页对象）；顺序为 B(sortOrder=0)、A(sortOrder=1)；每项字段为 `{id, name, type, image, data}`——图片字段名为 **`image`**（数组），`data` 含城市 id、中英文名称、省份；响应中**不含** `positionCode` / `online` / `sortOrder` / `link` / 时间戳字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-banner-IT-012/`
**最后更新**: 2026-08-26

### TC-banner-IT-013: GET /api/app/banners 排序号并列时按创建时间倒序
**关联需求**: banner/App 端 Banner 查询#同排序号 Banner 按创建时间倒序
**关联契约**: api-spec.json#/paths/~1api~1app~1banners/get
**来源**: app-list-sort-tiebreak
**优先级**: P1
**测试步骤**:
1. admin 侧创建上架城市；在同一 `positionCode`（如 `APP_TIE_ORDER`）下**先后**创建 Banner C、Banner D，二者 `sortOrder` **同为 5**，并依次上架
2. GET http://localhost:8081/api/app/banners?positionCode=APP_TIE_ORDER，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200 数组；排序号并列时按创建时间**倒序**，即后创建的 D 排在 C 之前
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-banner-IT-013/`
**最后更新**: 2026-08-26

### TC-banner-IT-014: GET /api/app/banners 下架 Banner 不下发
**关联需求**: banner/App 端 Banner 查询#下架 Banner 不下发
**关联契约**: api-spec.json#/paths/~1api~1app~1banners/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. admin 侧创建上架城市与关联 Banner（`positionCode=APP_OFFLINE_CASE`），**保持下架**（创建后默认 `online=false`）
2. GET http://localhost:8081/api/app/banners?positionCode=APP_OFFLINE_CASE，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200；结果数组中**不含**该 Banner（可为空数组）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-banner-IT-014/`
**最后更新**: 2026-08-26

### TC-banner-IT-015: GET /api/app/banners 关联城市下架时条目被剔除
**关联需求**: banner/App 端 Banner 查询#关联城市下架时条目被剔除
**关联契约**: api-spec.json#/paths/~1api~1app~1banners/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. admin 侧创建上架城市 cityId；创建关联 Banner（`positionCode=APP_CITY_OFF`）并上架
2. 直接在数据库/或通过城市下架后确认 Banner 仍为 `online=true` 的时间窗内（级联异步），构造「Banner `online=true` 但关联城市已下架」的状态：将城市下架后**立即**执行下一步
3. GET http://localhost:8081/api/app/banners?positionCode=APP_CITY_OFF，请求头 `X-API-Key: test-api-key`
**预期结果**: 返回 200；结果数组中**不含**该 Banner——即便 Banner 自身 `online` 尚未被异步级联改写，app 端查询也会按关联城市状态整条剔除（第三重防线）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-banner-IT-015/`
**最后更新**: 2026-08-26

### TC-banner-IT-016: GET /api/app/banners 缺少 API-key 返回 401
**关联需求**: banner/App 端 Banner 查询#缺少 API-key 返回 401
**关联契约**: api-spec.json#/paths/~1api~1app~1banners/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. GET http://localhost:8081/api/app/banners?positionCode=APP_HOME_TOP，**不携带** `X-API-Key` 请求头
2. GET 同一地址，携带错误的 `X-API-Key: wrong-key`
**预期结果**: 两次均返回 401，且两种情况响应不作区分（不泄漏 key 是否存在）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-list-sort-tiebreak/TC-banner-IT-016/`
**最后更新**: 2026-08-26

### TC-banner-IT-017: DELETE /api/admin/banners/{id} 物理删除 Banner
**关联需求**: banner/Banner 管理#创建后默认下架
**关联契约**: api-spec.json#/paths/~1api~1admin~1banners~1{id}/delete
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. POST http://localhost:21423/api/admin/auth/login（admin / 8@y2eoRLyStM*UVU）取 JWT
2. 创建一个城市，再 POST /api/admin/banners 创建一个关联该城市的 Banner，记下 `id`
3. DELETE /api/admin/banners/{id}
4. GET /api/admin/banners/{id}
5. 再次 DELETE /api/admin/banners/{id}（重复删除）
**预期结果**: 第 3 步返回 200 且 body 为空（非 204）；第 4 步返回 400 及消息「banner 不存在：{id}」——为物理删除，非软删；第 5 步同样返回 400「banner 不存在：{id}」
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21
