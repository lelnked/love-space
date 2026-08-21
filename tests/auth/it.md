# auth IT 用例

### TC-auth-IT-001: POST /api/admin/auth/login 内置管理员登录成功
**关联需求**: auth/运营账号登录#内置管理员登录成功
**关联契约**: api-spec.json#/paths/~1api~1admin~1auth~1login/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. POST http://localhost:21423/api/admin/auth/login，body `{"username":"admin","password":"8@y2eoRLyStM*UVU"}`
2. 断言响应状态码与 body 结构
**预期结果**: 返回 200；`token` 为非空字符串；顶层字段为 `manager`（不存在 `user` 字段）；`manager.username` = `admin`，`manager.role` = `ADMIN`，`manager.id` 非空
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-IT-002: POST /api/admin/auth/login 密码错误返回 401
**关联需求**: auth/运营账号登录#密码错误被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1auth~1login/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. POST http://localhost:21423/api/admin/auth/login，body `{"username":"admin","password":"wrong-password-123"}`
**预期结果**: 返回 401；body `message` 为「用户名或密码错误，或账号已停用」；无 `token` 字段
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-IT-003: 停用账号以正确密码登录仍返回 401
**关联需求**: auth/运营账号登录#停用账号无法登录
**关联契约**: api-spec.json#/paths/~1api~1admin~1auth~1login/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 admin 登录取得 token
2. POST /api/admin/managers 创建账号 `{"username":"it_auth_disabled","password":"Passw0rd!23"}`，记录返回 id
3. PUT /api/admin/managers/{id}/disable
4. POST /api/admin/auth/login，body `{"username":"it_auth_disabled","password":"Passw0rd!23"}`（密码正确）
**预期结果**: 停用返回 204；登录返回 401 且 `message` 为「用户名或密码错误，或账号已停用」，与密码错误场景的消息完全一致
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-IT-004: 用户名不存在与密码错误、账号停用消息不可区分
**关联需求**: auth/运营账号登录#用户名不存在与密码错误不可区分
**关联契约**: api-spec.json#/paths/~1api~1admin~1auth~1login/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login，body `{"username":"no_such_manager_zzz","password":"whatever123"}`
2. 与 TC-auth-IT-002（密码错误）、TC-auth-IT-003（账号停用）的响应逐字比对
**预期结果**: 返回 401；`message` 为「用户名或密码错误，或账号已停用」；三种失败原因的状态码与消息三者完全一致，无法区分
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-IT-005: 无 token 访问受保护接口返回 401
**关联需求**: auth/JWT 会话与授权链#无 token 访问受保护接口
**关联契约**: api-spec.json#/paths/~1api~1admin~1auth~1me/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. GET http://localhost:21423/api/admin/auth/me，不带 Authorization 头
**预期结果**: 返回 401；body 为 `{status, error, message, path}` 四字段结构，`message` 为「未登录或登录已过期」，`status` = 401
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-IT-006: 非法 token 不影响登录接口
**关联需求**: auth/JWT 会话与授权链#非法 token 不影响免认证路径
**关联契约**: api-spec.json#/paths/~1api~1admin~1auth~1login/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. POST http://localhost:21423/api/admin/auth/login，带 `Authorization: Bearer not-a-valid-jwt`，body `{"username":"admin","password":"8@y2eoRLyStM*UVU"}`
**预期结果**: 返回 200，`token` 非空，`manager.username` = `admin`——非法 token 被过滤器忽略（清空 SecurityContext 后放行），不导致 401/500
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-IT-007: MEMBER 角色访问管理员接口返回 403
**关联需求**: auth/JWT 会话与授权链#角色不足返回 403
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 admin 登录，POST /api/admin/managers 创建账号 `{"username":"it_auth_member","password":"Passw0rd!23"}`
2. 以 `it_auth_member` / `Passw0rd!23` 登录取得 MEMBER token
3. GET /api/admin/managers/page?page=1&size=20，携带 MEMBER token
**预期结果**: 步骤 2 返回 200 且 `manager.role` = `MEMBER`；步骤 3 返回 403，body `{status, error, message, path}`，`message` 为「权限不足」
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-IT-008: GET /api/admin/auth/me 返回当前登录人
**关联需求**: auth/当前登录人查询与登出#查询当前登录人
**关联契约**: api-spec.json#/paths/~1api~1admin~1auth~1me/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 admin / 8@y2eoRLyStM*UVU 登录取得 token
2. GET http://localhost:21423/api/admin/auth/me，携带 `Authorization: Bearer {token}`
**预期结果**: 返回 200；body 含 `id`、`username`、`nickname`、`role` 四个字段，`username` = `admin`、`role` = `ADMIN`；不含 `enable` 与 `createdAt` 字段
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-IT-009: 登出返回 204 且同一 token 仍可用
**关联需求**: auth/当前登录人查询与登出#登出返回 204 且 token 仍然有效
**关联契约**: api-spec.json#/paths/~1api~1admin~1auth~1logout/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. 以 admin 登录取得 token
2. POST http://localhost:21423/api/admin/auth/logout，携带该 token
3. 用**同一 token** 再次 GET /api/admin/auth/me
**预期结果**: 步骤 2 返回 204 且无响应体；步骤 3 返回 200 且 `username` = `admin`——无状态 JWT 不被吊销
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: 
**最后更新**: 2026-08-21
