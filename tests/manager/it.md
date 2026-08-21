# manager IT 用例

### TC-manager-IT-001: POST /api/admin/managers 创建账号强制 MEMBER 角色
**关联需求**: manager/运营账号管理#创建账号强制为 MEMBER 角色
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. POST http://localhost:21423/api/admin/auth/login，`{"username":"admin","password":"8@y2eoRLyStM*UVU"}` 取得 ADMIN token
2. POST /api/admin/managers，body `{"username":"it_mgr_role","password":"Passw0rd!23","nickname":"角色测试","role":"ADMIN"}`（故意多带 `role`）
3. GET /api/admin/managers/{id} 查询详情
**预期结果**: 创建返回 **201**；详情 `role` = `MEMBER`（多带的 `role=ADMIN` 被忽略），`enable` = true，`nickname` = `角色测试`；响应不回传明文密码
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-002: 用户名重复创建返回 400
**关联需求**: manager/运营账号管理#用户名重复被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 ADMIN token POST /api/admin/managers，body `{"username":"ops01","password":"Passw0rd!23"}`（若已存在可跳过）
2. 再次 POST /api/admin/managers，body `{"username":"ops01","password":"Passw0rd!23"}`
**预期结果**: 第二次返回 400，`message` 为「用户名已存在：ops01」
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-003: 密码 7 位创建返回 400
**关联需求**: manager/运营账号管理#密码长度不足被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 ADMIN token POST /api/admin/managers，body `{"username":"it_mgr_shortpwd","password":"Pass1!2"}`（7 位）
**预期结果**: 返回 400，`message` 为「密码长度需为 8~128 个字符」；账号未落库（后续 page 查询查不到该用户名）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-004: 重置密码后旧密码失效、新密码可登录
**关联需求**: manager/运营账号管理#重置密码后旧密码失效
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers~1{id}~1password/put
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 ADMIN token 创建账号 `{"username":"it_mgr_pwd","password":"OldPass!23"}`，记录 id
2. POST /api/admin/auth/login 用旧密码登录，确认 200
3. PUT /api/admin/managers/{id}/password，body `{"password":"NewPass!456"}`（不提供旧密码）
4. 以旧密码 `OldPass!23` 登录
5. 以新密码 `NewPass!456` 登录
**预期结果**: 步骤 3 返回 **204**（不校验旧密码）；步骤 4 返回 401 且 `message` 为「用户名或密码错误，或账号已停用」；步骤 5 返回 200 且 `token` 非空
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-005: 停用后该账号无法登录
**关联需求**: manager/账号启停与内置管理员保护#停用后无法登录
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers~1{id}~1disable/put
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 ADMIN token 创建启用账号 `{"username":"it_mgr_disable","password":"Passw0rd!23"}`，记录 id
2. PUT /api/admin/managers/{id}/disable
3. GET /api/admin/managers/{id} 查看 `enable`
4. POST /api/admin/auth/login 以正确密码登录该账号
**预期结果**: 停用返回 **204**；详情 `enable` = false；登录返回 401，`message` 为「用户名或密码错误，或账号已停用」
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-006: 内置 admin 不可停用但可重置密码
**关联需求**: manager/账号启停与内置管理员保护#内置 admin 不可停用
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers~1{id}~1disable/put
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 ADMIN token GET /api/admin/managers/page?username=admin 取得内置 admin 的 id
2. PUT /api/admin/managers/{adminId}/disable
3. GET /api/admin/managers/{adminId} 确认状态
4. PUT /api/admin/managers/{adminId}/password，body `{"password":"8@y2eoRLyStM*UVU"}`（重置为原密码，验证保护仅覆盖停用）
**预期结果**: 步骤 2 返回 400，`message` 为「内置管理员 admin 账号不可停用」；步骤 3 详情 `enable` 仍为 true；步骤 4 返回 204，且随后以 `admin` / `8@y2eoRLyStM*UVU` 登录仍返回 200
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-007: 启停往复切换最终为启用
**关联需求**: manager/账号启停与内置管理员保护#启停可往复切换
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers~1{id}~1enable/put
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. 以 ADMIN token 创建启用账号 `{"username":"it_mgr_toggle","password":"Passw0rd!23"}`，记录 id
2. PUT /api/admin/managers/{id}/disable
3. PUT /api/admin/managers/{id}/enable
4. GET /api/admin/managers/{id}
5. 以该账号密码登录
**预期结果**: 步骤 2、3 均返回 **204**；详情 `enable` = true；登录返回 200
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-008: 按用户名模糊过滤
**关联需求**: manager/运营账号分页查询#按用户名模糊过滤
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 ADMIN token 创建账号 `ops01`、`ops02`（已存在则复用）
2. GET /api/admin/managers/page?username=ops&page=1&size=20
**预期结果**: 返回 200；`content` 中同时包含 `ops01` 与 `ops02`（模糊匹配生效）；响应含 `content, page, size, totalElements, totalPages` 五个字段
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-009: 页大小非白名单值 size=25 被校正为 20
**关联需求**: manager/运营账号分页查询#页大小非白名单值被校正
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 ADMIN token 批量创建账号使总数多于 30（如 `it_mgr_p001`~`it_mgr_p031`）
2. GET /api/admin/managers/page?page=1&size=25
3. GET /api/admin/managers/page?page=1&size=30（白名单值对照）
4. GET /api/admin/managers/page?page=0&size=20（page 归一对照）
**预期结果**: 步骤 2 返回 200，响应 `size` = 20 且 `content` 长度为 20；步骤 3 响应 `size` = 30；步骤 4 响应 `page` = 1（以 1 为基回传）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-010: 列表按创建时间倒序
**关联需求**: manager/运营账号分页查询#列表按创建时间倒序
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. 以 ADMIN token 依次创建账号 `it_mgr_ordA`、`it_mgr_ordB`、`it_mgr_ordC`（保证先后顺序）
2. GET /api/admin/managers/page?username=it_mgr_ord&page=1&size=20
**预期结果**: 返回 200；`content` 顺序为 `it_mgr_ordC`、`it_mgr_ordB`、`it_mgr_ordA`（创建时间倒序，客户端不可指定排序）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-IT-011: 查询不存在的账号返回 400
**关联需求**: manager/运营账号分页查询#查询不存在的账号返回 400
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers~1{id}/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 ADMIN token GET /api/admin/managers/00000000-0000-0000-0000-000000000000
**预期结果**: 返回 **400**（非 404），`message` 为「管理员不存在：00000000-0000-0000-0000-000000000000」
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21
