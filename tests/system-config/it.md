# system-config IT 用例

> admin 后端 baseUrl `http://localhost:21423`（test profile），JWT 认证，登录 fixture：`admin` / `8@y2eoRLyStM*UVU`；app 后端 baseUrl `http://localhost:8081`，请求头 `X-API-Key` 认证。
> `<s>` 为每条用例的随机小写字母数字后缀，保证 key 全局唯一、互不干扰；造数一律经 admin 接口，用例末尾收尾删除。

### TC-system-config-IT-001: POST /api/admin/system-configs 新增成功
**关联需求**: system-config/后台新增系统配置#新增成功
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs/post
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. POST /api/admin/system-configs，body `{"key":"home.notice_<s>","name":"首页公告","value":{"text":"hi"}}`（`<s>` 为小写字母数字随机后缀，下同）
3. 收尾：DELETE /api/admin/system-configs/{id}
**预期结果**: 返回 201；响应含非空 `id`、`key=home.notice_<s>`、`name=首页公告`、`value` 为对象 `{"text":"hi"}`、`description=null`、`createdAt` 非空
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-001/`
**最后更新**: 2026-11-10

### TC-system-config-IT-002: value 以 JSON 原值往返（对象）
**关联需求**: system-config/系统配置数据模型#value 以 JSON 原值往返
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. POST /api/admin/system-configs，body `{"key":"home.notice_<s>","name":"首页公告","value":{"text":"放假通知","show":true}}`
3. GET /api/admin/system-configs/{id}
4. 收尾：DELETE 该配置
**预期结果**: 步骤 2 返回 201；步骤 3 返回 200，`value` 的 JSON 类型为 object，等于 `{"text":"放假通知","show":true}`（`show` 为布尔 true），不是字符串 `"{\"text\":...}"`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-002/`
**最后更新**: 2026-11-10

### TC-system-config-IT-003: 标量与数组均可作为 value
**关联需求**: system-config/系统配置数据模型#标量与数组均可作为 value
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs/post
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 依次 POST /api/admin/system-configs 四个配置，key 分别 `v.num_<s>`、`v.str_<s>`、`v.bool_<s>`、`v.arr_<s>`，value 分别为 `28`、`"hello"`、`false`、`[1,2,3]`
3. 分别 GET /api/admin/system-configs/{id}
4. 收尾：DELETE 四个配置
**预期结果**: 四次新增均 201；详情 `value` 依次为 number 28、string "hello"、boolean false、array [1,2,3]，类型与提交一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-003/`
**最后更新**: 2026-11-10

### TC-system-config-IT-004: POST key 重复返回 409
**关联需求**: system-config/后台新增系统配置#key 重复返回 409
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs/post
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：新增 key `home.notice_<s>`
3. 再次 POST，body `{"key":"home.notice_<s>","name":"重复","value":1}`
4. GET /api/admin/system-configs/page?keyword=home.notice_<s>&page=1&size=20
5. 收尾：DELETE 前置配置
**预期结果**: 步骤 3 返回 409，`message` 为「配置 key 已存在」，body 为 `{status,error,message,path}`；步骤 4 `totalElements=1`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-004/`
**最后更新**: 2026-11-10

### TC-system-config-IT-005: POST key 格式与长度边界
**关联需求**: system-config/后台新增系统配置#key 格式非法返回 400
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs/post
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 以下请求 name=`x`、value=`1`，仅 key 不同：
3. key `Home-Notice`（大写与连字符）
4. key `1abc`（数字开头）
5. key `a`（1 位）
6. key 65 位（`k` + 随机后缀补足至 65 位小写字母数字）
7. 不含 key 字段
8. key 2 位（`k` + 1 位随机小写字母/数字，若已存在换一位）
9. key 恰 64 位（`k` + 随机后缀补足至 64 位，可含 `_` 与 `.`）
10. 收尾：DELETE 步骤 8/9 创建的配置
**预期结果**: 步骤 3/4/5/6/7 均返回 400，配置未创建；步骤 8（2 位）与步骤 9（64 位）返回 201，响应 key 与提交一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-005/`
**最后更新**: 2026-11-10

### TC-system-config-IT-006: POST value 缺失或为 null 返回 400
**关联需求**: system-config/后台新增系统配置#value 为 null 返回 400
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs/post
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. POST body `{"key":"a.b_<s>","name":"x","value":null}`
3. POST body `{"key":"a.b_<s>","name":"x"}`（不含 value）
4. POST 原始 body `{"key":"a.b_<s>","name":"x","value":{text: hi}}`（body 本身非法 JSON）
5. GET /api/admin/system-configs/page?keyword=a.b_<s>&page=1&size=20
**预期结果**: 步骤 2/3 返回 400 且 `message` 为「value 不能为空」；步骤 4 返回 400（框架拒绝）；步骤 5 `totalElements=0`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-006/`
**最后更新**: 2026-11-10

### TC-system-config-IT-007: POST value 序列化长度边界 65535 / 65536
**关联需求**: system-config/后台新增系统配置#value 为 null 返回 400
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs/post
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. value 为 JSON 字符串 `"` + 65533 个 `x` + `"`（序列化后恰 65535 字符），key `v.max_<s>`
3. value 为 JSON 字符串 `"` + 65534 个 `x` + `"`（序列化后 65536 字符），key `v.over_<s>`
4. 收尾：DELETE 步骤 2 创建的配置
**预期结果**: 步骤 2 返回 201，详情 `value` 长度 65533 的字符串原样返回；步骤 3 返回 400，`message` 为「value 过长」，配置未创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-007/`
**最后更新**: 2026-11-10

### TC-system-config-IT-008: POST name / description 必填与长度边界
**关联需求**: system-config/后台新增系统配置#新增成功
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs/post
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 以合法 key（每步换新 `<s>`）、value=`1` 新增：
3. 不含 name
4. name 65 字符
5. name 恰 64 字符
6. description 501 字符
7. description 恰 500 字符
8. 收尾：DELETE 步骤 5/7 创建的配置
**预期结果**: 步骤 3/4/6 返回 400，配置未创建；步骤 5/7 返回 201，响应 name / description 原样返回
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-008/`
**最后更新**: 2026-11-10

### TC-system-config-IT-009: GET /page 按 keyword 匹配 key 或名称（不区分大小写）
**关联需求**: system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1page/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：新增 `home.notice_<s>`（名称「首页公告<s>」）与 `cycle_default_days_<s>`（名称「默认周期<s>」）
3. GET /api/admin/system-configs/page?keyword=notice_<s>&page=1&size=20
4. GET ...?keyword=NOTICE_<S大写>&page=1&size=20
5. GET ...?keyword=默认周期<s>&page=1&size=20
6. GET ...?keyword=zz_nomatch_<s>&page=1&size=20
7. 收尾：DELETE 两个前置配置
**预期结果**: 步骤 3/4 均返回 200，`content` 仅含 `home.notice_<s>`；步骤 5 `content` 仅含 `cycle_default_days_<s>`（按名称命中）；步骤 6 返回 200、`content=[]`、`totalElements=0`；每项含 `id,key,name,value,description,createdAt,updatedAt`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-009/`
**最后更新**: 2026-11-10

### TC-system-config-IT-010: GET /page 排序 updated_at DESC 与分页参数口径
**关联需求**: system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1page/get
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：依次新增 `sort_<s>.a`、`sort_<s>.b`、`sort_<s>.c`，再 PUT 编辑 `sort_<s>.a` 的 name
3. GET /api/admin/system-configs/page?keyword=sort_<s>&page=1&size=20
4. GET ...?keyword=sort_<s>&page=1&size=50
5. GET ...?keyword=sort_<s>&page=1&size=200
6. 收尾：DELETE 三个配置
**预期结果**: 步骤 3 返回 200，`page=1`、`size=20`、`totalElements=3`，顺序为 `sort_<s>.a`（最近更新）、`sort_<s>.c`、`sort_<s>.b`；步骤 4 返回 200 且 `size` 校正为 20（沿用现有 size 白名单口径）；步骤 5 返回 200、`size=200`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-010/`
**最后更新**: 2026-11-10

### TC-system-config-IT-011: GET /{id} 详情不存在返回 404
**关联需求**: system-config/后台系统配置分页查询#详情不存在返回 404
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/get
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. GET /api/admin/system-configs/00000000-0000-0000-0000-000000000000
**预期结果**: 返回 404，`message` 为「配置不存在」
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-011/`
**最后更新**: 2026-11-10

### TC-system-config-IT-012: 未登录访问后台系统配置接口返回 401
**关联需求**: system-config/后台系统配置分页查询#未登录被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1page/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. 不带 Authorization 请求 GET /api/admin/system-configs/page?page=1&size=20
2. 不带 Authorization 请求 POST /api/admin/system-configs，body `{"key":"anon.k_<s>","name":"x","value":1}`
3. 带非法 token `Bearer invalid` 请求 GET /api/admin/system-configs/page?page=1&size=20
**预期结果**: 三次均返回 401；步骤 2 未创建配置（以 admin 查询 keyword=anon.k_<s> 为 0 条）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-012/`
**最后更新**: 2026-11-10

### TC-system-config-IT-013: MEMBER 角色可完整使用系统配置接口
**关联需求**: system-config/后台系统配置分页查询#未登录被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs/post
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. 以 admin 登录，POST /api/admin/managers 创建 MEMBER 账号 `it_sc_member_<s>` / `Passw0rd!23`，再以其登录取 MEMBER token
2. 以 MEMBER token 依次：GET /page、POST 新增 `member.k_<s>`、GET /{id}、PUT /{id}、DELETE /{id}
3. 收尾：admin 禁用该 MEMBER 账号（系统无删除账号接口）
**预期结果**: MEMBER 各请求依次返回 200 / 201 / 200 / 200 / 204，无 403
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-013/`
**最后更新**: 2026-11-10

### TC-system-config-IT-014: PUT /{id} 编辑成功且忽略请求体 key
**关联需求**: system-config/后台编辑系统配置#编辑不能修改 key
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：新增 `home.notice_<s>`，value `{"text":"旧"}`，记录 `updatedAt`=T0
3. PUT /api/admin/system-configs/{id}，body `{"key":"other.key_<s>","name":"首页公告-改","value":{"text":"新"},"description":"顶部"}`
4. GET /api/admin/system-configs/{id}
5. GET /page?keyword=other.key_<s>&page=1&size=20
6. 收尾：DELETE 该配置
**预期结果**: 步骤 3 返回 200，响应 `key` 仍为 `home.notice_<s>`，`name`/`value`/`description` 为新值，`updatedAt` 晚于 T0；步骤 4 与响应一致；步骤 5 `totalElements=0`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-014/`
**最后更新**: 2026-11-10

### TC-system-config-IT-015: PUT /{id} 不存在与校验失败
**关联需求**: system-config/后台编辑系统配置#编辑不能修改 key
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. PUT /api/admin/system-configs/00000000-0000-0000-0000-000000000000，body `{"name":"x","value":1}`
3. 前置：新增 `edit.k_<s>`，value=`1`
4. PUT 该 id，body `{"name":"x","value":null}`
5. PUT 该 id，body `{"name":"x"}`
6. PUT 该 id，name 65 字符、value=`2`
7. PUT 该 id，value 为序列化 65536 字符的 JSON 字符串
8. GET 该 id
9. 收尾：DELETE 该配置
**预期结果**: 步骤 2 返回 404「配置不存在」；步骤 4/5 返回 400「value 不能为空」；步骤 6 返回 400；步骤 7 返回 400「value 过长」；步骤 8 `value` 仍为 1、`name` 未变
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-015/`
**最后更新**: 2026-11-10

### TC-system-config-IT-016: DELETE /{id} 物理删除与重复删除
**关联需求**: system-config/后台删除系统配置#删除后 App 查不到
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/delete
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：新增 `del.k_<s>`
3. DELETE /api/admin/system-configs/{id}
4. GET /api/admin/system-configs/{id}
5. 再次 DELETE 同一 id
6. POST 重新以 key `del.k_<s>` 新增
7. 收尾：DELETE 步骤 6 的配置
**预期结果**: 步骤 3 返回 204；步骤 4 返回 404「配置不存在」；步骤 5 返回 404「配置不存在」；步骤 6 返回 201（物理删除后 key 可复用）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-016/`
**最后更新**: 2026-11-10

### TC-system-config-IT-017: 编辑 value 后 App 立即读到新值
**关联需求**: system-config/后台编辑系统配置#编辑 value 后 App 立即读到新值
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/put
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：admin 新增 `home.notice_<s>`，value `{"text":"旧"}`
3. GET http://localhost:8081/api/app/system-configs/home.notice_<s>，请求头 `X-API-Key: <有效 key>`
4. admin PUT 该配置，body `{"name":"首页公告","value":{"text":"新"}}`
5. 立即再次 GET http://localhost:8081/api/app/system-configs/home.notice_<s>
6. 收尾：DELETE 该配置
**预期结果**: 步骤 3 返回 value `{"text":"旧"}`；步骤 4 返回 200；步骤 5 返回 200，value 为 `{"text":"新"}`（服务端无缓存）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-017/`
**最后更新**: 2026-11-10

### TC-system-config-IT-018: 删除后 App 查询返回 404
**关联需求**: system-config/后台删除系统配置#删除后 App 查不到
**关联契约**: api-spec.json#/paths/~1api~1admin~1system-configs~1{id}/delete
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）
2. 前置：admin 新增 `home.notice_<s>`；App GET http://localhost:8081/api/app/system-configs/home.notice_<s>（`X-API-Key: <有效 key>`）确认 200
3. admin DELETE 该配置
4. App 再次 GET 同一路径
5. App GET http://localhost:8081/api/app/system-configs?keys=home.notice_<s>
**预期结果**: 步骤 3 返回 204；步骤 4 返回 404，`message` 为「配置不存在」；步骤 5 返回 200 且为空数组 `[]`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-018/`
**最后更新**: 2026-11-10

### TC-system-config-IT-019: GET /api/app/system-configs/{key} 单个查询
**关联需求**: system-config/App 查询系统配置#单个 key 查询
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）；admin 新增 `home.notice_<s>`，name「首页公告」、value `{"text":"hi"}`、description「顶部」
2. GET http://localhost:8081/api/app/system-configs/home.notice_<s>，请求头 `X-API-Key: <有效 key>`
3. 收尾：admin DELETE 该配置
**预期结果**: 返回 200，body 恰为 `{"key":"home.notice_<s>","value":{"text":"hi"}}`；不含 `name`、`description`、`id`、时间字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-019/`
**最后更新**: 2026-11-10

### TC-system-config-IT-020: GET /api/app/system-configs/{key} 不存在返回 404
**关联需求**: system-config/App 查询系统配置#单个 key 不存在返回 404
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get
**来源**: add-system-config
**优先级**: P1
**测试步骤**:
1. GET http://localhost:8081/api/app/system-configs/no.such_<s>，请求头 `X-API-Key: <有效 key>`
**预期结果**: 返回 404，`message` 为「配置不存在」
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-020/`
**最后更新**: 2026-11-10

### TC-system-config-IT-021: GET /api/app/system-configs?keys= 批量查询忽略不存在 key 并保持顺序
**关联需求**: system-config/App 查询系统配置#批量查询忽略不存在的 key 并保持顺序
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 取 JWT token（后续 admin 请求均带）；admin 新增 `a.one_<s>`（value 1）与 `b.two_<s>`（value "two"）
2. GET http://localhost:8081/api/app/system-configs?keys=b.two_<s>,c.none_<s>,a.one_<s>,b.two_<s>，请求头 `X-API-Key: <有效 key>`
3. GET ...?keys=%20b.two_<s>%20,,a.one_<s>,（含空白与空项）
4. 收尾：admin DELETE 两个配置
**预期结果**: 步骤 2 返回 200，数组共 2 项，依次为 `{key:"b.two_<s>",value:"two"}`、`{key:"a.one_<s>",value:1}`，每项仅含 key、value；步骤 3 返回 200，同样 2 项且顺序为 b.two、a.one
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-021/`
**最后更新**: 2026-11-10

### TC-system-config-IT-022: 批量查询 keys 缺失、为空与 50/51 边界
**关联需求**: system-config/App 查询系统配置#批量查询 keys 缺失或超限返回 400
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. 以下请求均带 `X-API-Key: <有效 key>`，baseUrl http://localhost:8081
2. GET /api/app/system-configs（不带 keys）
3. GET /api/app/system-configs?keys=
4. GET /api/app/system-configs?keys=%20,%20,
5. GET ?keys= 50 个互不相同的 key（`k1_<s>`…`k50_<s>`）
6. GET ?keys= 51 个互不相同的 key（`k1_<s>`…`k51_<s>`）
7. GET ?keys= 51 项但去重后 50 个（`k1_<s>`…`k50_<s>` 再追加 `k1_<s>`）
**预期结果**: 步骤 2/3/4 返回 400；步骤 5 返回 200（key 均不存在，返回 `[]`）；步骤 6 返回 400；步骤 7 返回 200（按去重后计数）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-022/`
**最后更新**: 2026-11-10

### TC-system-config-IT-023: App 系统配置接口 API-key 缺失或非法返回 401
**关联需求**: system-config/App 查询系统配置#缺少 API-key 返回 401
**关联契约**: api-spec.json#/paths/~1api~1app~1system-configs~1{key}/get
**来源**: add-system-config
**优先级**: P0
**测试步骤**:
1. GET http://localhost:8081/api/app/system-configs/home.notice（不带 X-API-Key）
2. 同路径，请求头 `X-API-Key: wrong-key`
3. GET http://localhost:8081/api/app/system-configs?keys=home.notice（不带 X-API-Key）
**预期结果**: 三次均返回 401
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/add-system-config/TC-system-config-IT-023/`
**最后更新**: 2026-11-10
