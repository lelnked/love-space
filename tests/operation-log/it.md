# operation-log IT 用例

> 本域留痕为**横切行为**，所有用例均为两段式：先调用**其它域的写接口**触发留痕，再查 `GET /api/admin/logs/page` 断言。
> 留痕**异步落库**，查询前必须轮询等待（参照既有 `OperationLogAspectIT`：最多 20 次、每次间隔 100ms，命中即停）。
> 后端 baseUrl: `http://localhost:21423`（test profile）；登录 fixture：`admin` / `8@y2eoRLyStM*UVU`。

### TC-operation-log-IT-001: 创建城市后异步产生 city:create 留痕
**关联需求**: operation-log/运营写操作留痕#创建城市后异步留痕
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login，body `{"username":"admin","password":"8@y2eoRLyStM*UVU"}`，取 JWT token
2. POST /api/admin/cities，body `{"chineseName":"留痕城市<随机后缀>","englishName":"LogCity","chineseProvince":"湖北","englishProvince":"Hubei"}`，记录返回的城市 id
3. 轮询 GET /api/admin/logs/page?username=admin&module=city&page=1&size=20（最多 20 次、间隔 100ms），直到出现 `action=create` 的新记录
**预期结果**: 步骤 2 返回 200；轮询在超时前命中，存在一条 `module=city`、`action=create`、`username=admin` 的记录，`createdAt` 非空
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-002: 业务校验失败（400）时不产生留痕
**关联需求**: operation-log/运营写操作留痕#业务方法失败时不留痕
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 登录取 token
2. GET /api/admin/logs/page?module=city&page=1&size=20，记录 `total`（基线值 T0）
3. POST /api/admin/cities，body 缺失必填字段（`{"chineseName":"","englishName":"X","chineseProvince":"X","englishProvince":"X"}`），期望被业务校验拒绝
4. 等待 2s（覆盖异步落库窗口），再次 GET /api/admin/logs/page?module=city&page=1&size=20
**预期结果**: 步骤 3 返回 400 且消息为「中文名不能为空」；步骤 4 的 `total` 仍等于 T0，日志中不新增 `module=city`、`action=create` 记录
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-003: 登录不产生 auth:login 日志
**关联需求**: operation-log/运营写操作留痕#登录不产生日志
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 成功登录取 token
2. 等待 2s（覆盖异步落库窗口）
3. GET /api/admin/logs/page?module=auth&page=1&size=20
**预期结果**: 步骤 1 返回 200；步骤 3 返回 200，`items` 中不存在 `module=auth` 且 `action=login` 的记录（若有 auth 记录，其 `action` 只能是 `logout`）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-004: 创建运营账号的 payload 中 password 被脱敏
**关联需求**: operation-log/留痕字段取值与敏感信息脱敏#密码字段被脱敏
**关联契约**: api-spec.json#/paths/~1api~1admin~1managers/post
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: `GET /api/admin/logs/page` 响应**不含 payload**，本用例无法用 API 断言。执行时需直连 test profile 的测试库查询 `loves_operation_log.payload`（连接串取自 `love-space-admin/src/test` 的 Testcontainers/测试配置）。若无法直连测试库，本用例标记为「未执行」而非失败。
**测试步骤**:
1. 以 admin（ADMIN 角色）登录取 token
2. POST /api/admin/managers，body `{"username":"logredact<随机后缀>","password":"P@ssw0rd123","nickname":"脱敏用例"}`
3. 轮询等待（20×100ms）留痕落库：GET /api/admin/logs/page?module=manager&page=1&size=20 出现 `action=create` 新记录
4. 直连测试库执行 `SELECT payload FROM loves_operation_log WHERE module='manager' AND action='create' ORDER BY created_at DESC LIMIT 1`
**预期结果**: 步骤 2 返回 201；步骤 4 取到的 payload JSON 中 `password` 字段值恰为 `[REDACTED]`，全文不含明文 `P@ssw0rd123`；`username` 字段值保持明文
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-005: 创建类操作的 target 为 null
**关联需求**: operation-log/留痕字段取值与敏感信息脱敏#创建类操作的 target 为空
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. 登录取 token
2. POST /api/admin/cities 创建一个城市（字段同 TC-001）
3. 轮询（20×100ms）GET /api/admin/logs/page?username=admin&module=city&page=1&size=20 直到出现新的 `action=create` 记录
**预期结果**: 该条记录的 `target` 为 `null`（创建类操作无 UUID 入参，属现行为）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-006: 更新类操作的 target 为目标城市 id
**关联需求**: operation-log/留痕字段取值与敏感信息脱敏#更新类操作记录目标 id
**关联契约**: api-spec.json#/paths/~1api~1admin~1cities~1{id}/put
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 登录取 token
2. POST /api/admin/cities 创建城市，记录 id = C
3. PUT /api/admin/cities/C，body 为完整合法更新体（改 `chineseName`）
4. 轮询（20×100ms）GET /api/admin/logs/page?username=admin&module=city&page=1&size=20 直到出现 `action=update` 记录
**预期结果**: 步骤 3 返回 200；日志中最新一条 `module=city`、`action=update` 记录的 `target` 等于城市 id C（字符串形式）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-007: 嵌套资源（商户评价）的 target 取父级商户 id
**关联需求**: operation-log/留痕字段取值与敏感信息脱敏#嵌套资源的 target 取父级 id
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**前置条件**: 已存在一个商户（必要时先按 merchant 域创建流程建一个），记 merchantId = M
**测试步骤**:
1. 登录取 token
2. POST /api/admin/merchants/M/reviews，body `{"nickname":"张三","title":"很好","content":"体验不错","sortOrder":0,"recommended":false}`，记录返回评价 id = R（R ≠ M）
3. PUT /api/admin/merchants/M/reviews/R，body 同上但 `title` 改为「更好」
4. 轮询（20×100ms）GET /api/admin/logs/page?module=merchant-review&page=1&size=20 直到出现 `action=update` 记录
**预期结果**: 步骤 3 返回 200；该 `module=merchant-review`、`action=update` 记录的 `target` 等于**商户** id M，而非评价 id R（现行为：取第一个 UUID 入参）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-008: 按操作人与模块组合过滤
**关联需求**: operation-log/操作日志查询#按操作人与模块组合过滤
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 存在一个上架城市可供 banner 关联，记 cityId = C
**测试步骤**:
1. 登录取 token
2. POST /api/admin/cities 创建城市（产生 city 模块记录）
3. POST /api/admin/banners，body `{"name":"日志用例 banner","positionCode":"home-top","type":"CITY","imageUrls":["images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff0001.png"],"link":"C","sortOrder":0}`（产生 banner 模块记录）
4. 轮询（20×100ms）等待两条记录落库
5. GET /api/admin/logs/page?username=admin&module=city&page=1&size=20
**预期结果**: 步骤 5 返回 200；`items` 全部满足 `username=admin` 且 `module=city`，不含任何 `module=banner` 的条目
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-009: 操作人过滤为模糊匹配
**关联需求**: operation-log/操作日志查询#操作人过滤为模糊匹配
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 admin 登录取 token（ADMIN）
2. POST /api/admin/managers 创建账号 `admin2`，body `{"username":"admin2","password":"P@ssw0rd123"}`
3. PUT /api/admin/managers/{admin2Id}/enable（确保可登录）
4. POST /api/admin/auth/login 以 `admin2` 登录取 token2
5. 以 token2 POST /api/admin/auth/logout（MEMBER 可执行的写接口，产生 `username=admin2` 的留痕）
6. 轮询（20×100ms）等待 admin2 的记录落库
7. 以 admin 的 token GET /api/admin/logs/page?username=admin&page=1&size=30
**预期结果**: 步骤 7 返回 200；结果中同时出现 `username=admin` 与 `username=admin2` 的记录（模糊匹配生效）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-010: username 传空白串视为不传
**关联需求**: operation-log/操作日志查询#操作人过滤为模糊匹配
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P2
**测试步骤**:
1. 登录取 token
2. GET /api/admin/logs/page?page=1&size=20，记录 `total` = T0
3. GET /api/admin/logs/page?username=%20%20&page=1&size=20（两个空格）
4. GET /api/admin/logs/page?username=%20admin%20&page=1&size=20（首尾带空格）
**预期结果**: 步骤 3 返回 200 且 `total` 等于 T0（空白视为不传）；步骤 4 返回 200，结果与 `username=admin` 一致（去除首尾空白后模糊匹配）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-011: 创建时间区间过滤含上下边界
**关联需求**: operation-log/操作日志查询#时间区间含边界
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**测试步骤**:
1. 登录取 token
2. POST /api/admin/cities 创建城市
3. 轮询（20×100ms）GET /api/admin/logs/page?module=city&page=1&size=20，取最新记录的 `createdAt` = T 与 `id` = L
4. GET /api/admin/logs/page?createdAtFrom={T}&createdAtTo={T}&page=1&size=20（T 原样 URL-encode 回传）
**预期结果**: 步骤 4 返回 200；`items` 中包含 id = L 的记录（from 与 to 均含边界）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-012: 响应条目不含 payload，仅六个字段
**关联需求**: operation-log/操作日志查询#响应不含 payload
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 日志中已存在带 payload 的记录（如先创建一个城市）
**测试步骤**:
1. 登录取 token
2. POST /api/admin/cities 创建城市（该操作会写入 payload）
3. 轮询（20×100ms）后 GET /api/admin/logs/page?module=city&page=1&size=20
**预期结果**: 返回 200；每个条目的键集合恰为 `id`、`username`、`module`、`action`、`target`、`createdAt` 六个；响应全文不含 `payload` 字段名，也不含请求体中的城市名称字符串
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-013: MEMBER 角色账号可查询日志（不返回 403）
**关联需求**: operation-log/操作日志查询#非 ADMIN 角色可查询日志
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**测试步骤**:
1. 以 admin 登录取 token（ADMIN）
2. POST /api/admin/managers，body `{"username":"logmember<随机后缀>","password":"P@ssw0rd123"}`（服务端固定写入 role=MEMBER），记录 id
3. 如账号非启用态，PUT /api/admin/managers/{id}/enable
4. POST /api/admin/auth/login 以该 MEMBER 账号登录取 tokenMember
5. 以 tokenMember GET /api/admin/logs/page?page=1&size=20
**预期结果**: 步骤 5 返回 200（不是 403），`items` 为数组，分页字段正常
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21

### TC-operation-log-IT-014: size 非法值校正为 20 且固定按创建时间倒序
**关联需求**: operation-log/操作日志查询#按操作人与模块组合过滤
**关联契约**: api-spec.json#/paths/~1api~1admin~1logs~1page/get
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P2
**前置条件**: 日志总数 > 20（必要时先连续创建若干城市制造记录）
**测试步骤**:
1. 登录取 token
2. GET /api/admin/logs/page?page=1&size=50
3. GET /api/admin/logs/page?page=1&size=30&sort=createdAt,asc
**预期结果**: 步骤 2 返回 200 且实际每页条数为 20（size 仅接受 20/30，其余校正为 20）；步骤 3 返回 200 且 `items` 的 `createdAt` 仍为**倒序**（客户端排序参数无效）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**:
**最后更新**: 2026-08-21
