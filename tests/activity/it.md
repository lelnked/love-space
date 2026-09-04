# activity IT 用例

### TC-activity-IT-001: POST /api/admin/activities 创建完整活动
**关联需求**: activity/活动管理#创建活动
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities/post
**来源**: ambassador-route-activity → activity-drop-city-link
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/activities，body（**不含 cityId**）：images 2 张、title「海岛露营节」、tags 2 条、periods `["FOLLICULAR","OVULATION"]`、level="L2"、introduction/editorNote/gatheringPlace/dismissalPlace/transportation/visa、itinerary 2 条（title+content，顺序 I1、I2）、detailHtml（纯文本段落）、online=true
3. GET /api/admin/activities/{id}
**预期结果**: 创建返回 200；详情各字段与提交一致且**响应不含 cityId 字段**，itinerary 按 I1→I2 顺序返回，periods/level 枚举值一致，detailHtml 文本内容原样保存，images 为签名 URL，online=true
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/activity-subtitle/TC-activity-IT-001/`
**最后更新**: 2026-09-02

### TC-activity-IT-002: POST /api/admin/activities 缺必填被拒绝
**关联需求**: activity/活动管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities/post
**来源**: ambassador-route-activity → activity-drop-city-link
**优先级**: P0
**测试步骤**:
1. POST /api/admin/activities，body 缺 title（其余合法）
2. POST /api/admin/activities，body images 为 `[]`
**预期结果**: 两次均返回 400，响应 `message` 为中文错误信息；活动均未创建
**状态**: ⬜ 未测试（activity-drop-city-link 变更后需重测）
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-002/`
**最后更新**: 2026-08-16

### TC-activity-IT-003: PUT /api/admin/activities/{id}/online 活动上下线切换
**关联需求**: activity/活动管理#活动上下线切换
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities~1{id}~1online/put
**来源**: ambassador-route-activity
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 online=true 的活动
2. PUT /api/admin/activities/{id}/online，body：`{"online": false}`
3. GET /api/admin/activities/{id}
4. PUT /api/admin/activities/{id}/online，body：`{"online": true}`
**预期结果**: 步骤 2 返回 200，步骤 3 详情 online=false；步骤 4 后再查详情 online=true（可往返切换）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-003/`
**最后更新**: 2026-08-16

### TC-activity-IT-004: PUT /api/admin/activities/{id} 更新活动，请求体 cityId 被忽略
**关联需求**: activity/活动管理#请求体携带 cityId 不影响创建
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities~1{id}/put
**来源**: ambassador-route-activity → activity-drop-city-link
**优先级**: P1
**测试步骤**:
1. 前置：已存在一个活动
2. PUT /api/admin/activities/{id}，body：title 改名、level 改为 "L3"、periods 改为 `["MENSTRUAL"]`、itinerary 改为 1 条新条目，并额外携带一个任意 `cityId`
3. GET /api/admin/activities/{id}
**预期结果**: 返回 200，title/level/periods/itinerary 更新生效；多余的 cityId 被静默忽略（不报 400），详情响应中不含 cityId 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/activity-subtitle/TC-activity-IT-004/`
**最后更新**: 2026-09-02

### TC-activity-IT-005: DELETE /api/admin/activities/{id} 物理删除活动
**关联需求**: activity/活动管理#创建活动
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities~1{id}/delete
**来源**: ambassador-route-activity → activity-drop-city-link
**优先级**: P1
**测试步骤**:
1. 前置：存在一个活动
2. DELETE /api/admin/activities/{id}
3. GET /api/admin/activities/{id}
4. GET /api/admin/activities/page 确认列表不含该活动
**预期结果**: 删除返回 200；再查详情返回 400 及中文业务错误（admin 端「资源不存在」全局口径）；分页列表不再出现该活动
**状态**: ⬜ 未测试（activity-drop-city-link 变更后需重测）
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-005/`
**最后更新**: 2026-08-16

### TC-activity-IT-006: POST /api/admin/activities 富文本 img src 存 objectKey、admin 读时替换签名 URL
**关联需求**: activity/活动管理#创建活动
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities/post
**来源**: ambassador-route-activity → rich-text-gif-and-inline-sticker
**优先级**: P0
**测试步骤**:
1. POST /api/admin/activities，detailHtml 含 2 个 `<img src="<images/ 前缀 objectKey>">` 与段落文本
2. GET /api/admin/activities/{id}
3. PUT /api/admin/activities/{id}，detailHtml 改为不含 img 的纯文本后再 GET 详情
**预期结果**: 创建返回 200；步骤 2 详情 detailHtml 文本部分与提交一致，2 个 img 的 src 均被替换为签名 URL（http 开头、非裸 objectKey），说明存储层保存的是 bound objectKey；步骤 3 无 img 的 HTML 原样往返不报错
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-006/`
**最后更新**: 2026-09-04

### TC-activity-IT-007: GET /api/app/activities 全局上线活动列表
**关联需求**: activity/App 端活动查询#查询上架城市的活动
**关联契约**: api-spec.json#/paths/~1api~1app~1activities/get
**来源**: ambassador-route-activity → activity-drop-city-link → activity-subtitle
**优先级**: P0
**测试步骤**:
1. 前置：存在多个上线活动（含图片、标签、级别、周期，其中至少一个填写了副标题 subtitle），且系统中存在至少一个已下架城市
2. GET http://localhost:8081/api/app/activities（不带任何查询参数，请求头带 X-API-Key）
**预期结果**: 返回 200，列表含全部上线活动（不因任何城市上架状态被筛掉），按创建时间倒序，每项含标题、**副标题 subtitle**、图片（签名 URL）、标签、级别、周期字段；填写了副标题的活动其 `subtitle` 与后台录入一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/activity-subtitle/TC-activity-IT-007/`
**最后更新**: 2026-09-02

### TC-activity-IT-008: GET /api/app/activities 下线活动不可见、详情 404
**关联需求**: activity/App 端活动查询#下线活动不可见
**关联契约**: api-spec.json#/paths/~1api~1app~1activities~1{id}/get
**来源**: ambassador-route-activity → activity-drop-city-link
**优先级**: P0
**测试步骤**:
1. 前置：一个上线活动 app 端可见
2. admin 侧 PUT /api/admin/activities/{id}/online 将其下线
3. GET http://localhost:8081/api/app/activities（请求头带 X-API-Key）
4. GET http://localhost:8081/api/app/activities/{id}（请求头带 X-API-Key）
**预期结果**: 下线后列表不含该活动；详情返回 404
**状态**: ⬜ 未测试（activity-drop-city-link 变更后需重测）
**执行方式**: api-test-runner
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-IT-008/`
**最后更新**: 2026-08-16

### TC-activity-IT-009: GET /api/app/activities/{id} 详情返回富文本且 img src 为签名 URL
**关联需求**: activity/App 端活动查询#活动详情返回富文本
**关联契约**: api-spec.json#/paths/~1api~1app~1activities~1{id}/get
**来源**: ambassador-route-activity → activity-drop-city-link → rich-text-gif-and-inline-sticker
**优先级**: P0
**测试步骤**:
1. 前置：一个可见活动，detailHtml 后台保存为含图片标签与文本的 HTML
2. GET http://localhost:8081/api/app/activities/{id}（请求头带 X-API-Key）
**预期结果**: 返回 200；含全部展示字段（title/images/tags/periods/level/introduction/editorNote/gatheringPlace/dismissalPlace/transportation/visa/itinerary）且**不含 cityId**；detailHtml 文本与后台保存内容一致，img src 已替换为可访问的签名 URL
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-009/`
**最后更新**: 2026-09-04

### TC-activity-IT-020: 活动景观字段贯通 admin 写入与 admin/app 查询
**关联需求**: activity/活动管理#景观字段可写可改可空
**关联契约**: api-spec.json#/components/schemas/ActivityUpsertRequest
**来源**: activity-landscape-field → activity-drop-city-link
**优先级**: P1
**测试步骤**:
1. POST /api/admin/activities，body 含 landscape="海岸线景观"（其余字段合法，online=true）
2. GET /api/admin/activities/{id}
3. PUT /api/admin/activities/{id}，把 landscape 改为「火山地貌」
4. GET http://localhost:8081/api/app/activities/{id}（请求头带 X-API-Key）
**预期结果**: 步骤 1、2 返回 200 且 `landscape`="海岸线景观"；步骤 3 返回 200 且 `landscape`="火山地貌"；步骤 4 返回 200 且 `landscape`="火山地貌"
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/activity-subtitle/TC-activity-IT-020/`
**最后更新**: 2026-09-02

### TC-activity-IT-021: GET /api/admin/activities/page 携带 cityId 不收窄结果
**关联需求**: activity/活动管理#活动列表不按城市过滤
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities~1page/get
**来源**: activity-drop-city-link
**优先级**: P1
**测试步骤**:
1. 前置：存在 ≥2 个活动
2. GET /api/admin/activities/page（不带参数），记录 totalElements
3. GET /api/admin/activities/page?cityId={任意 UUID}
**预期结果**: 步骤 3 返回 200 且 totalElements 与步骤 2 相同（未知参数被忽略、不收窄结果）；每条记录均不含 cityId 字段
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/activity/TC-activity-IT-021/`
**最后更新**: -

### TC-activity-IT-022: GET /api/app/activities/{id} 详情不受城市上架状态影响
**关联需求**: activity/App 端活动查询#城市上架状态不影响活动详情可见性
**关联契约**: api-spec.json#/paths/~1api~1app~1activities~1{id}/get
**来源**: activity-drop-city-link
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 online=true 的活动；admin 侧把系统中所有城市（地图）下架
2. GET http://localhost:8081/api/app/activities/{id}（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/activities（请求头带 X-API-Key）
**预期结果**: 步骤 2 返回 200 且字段齐全；步骤 3 列表含该活动——活动可见性与任何城市上架状态无关
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/activity/TC-activity-IT-022/`
**最后更新**: -

### TC-activity-IT-023: 活动副标题可写可改可清空（admin 侧）
**关联需求**: activity/活动管理#副标题可写可改可空
**关联契约**: api-spec.json#/components/schemas/ActivityUpsertRequest
**来源**: activity-subtitle
**优先级**: P1
**测试步骤**:
1. POST /api/admin/activities，body 含 subtitle="一日徒步"（其余字段合法，online=true）
2. GET /api/admin/activities/{id}
3. PUT /api/admin/activities/{id}，把 subtitle 改为「两日徒步」，再 GET 详情
4. PUT /api/admin/activities/{id}，body **不带** subtitle 字段，再 GET 详情
5. PUT /api/admin/activities/{id}，body subtitle=""（空串），再 GET 详情
6. GET /api/admin/activities/page，定位该活动列表项
**预期结果**: 步骤 1、2 返回 200 且 `subtitle`="一日徒步"；步骤 3 返回 200 且 `subtitle`="两日徒步"；步骤 4 返回 200 且 `subtitle` 为 null（不必填、可清空，不报 400）；步骤 5 返回 200 且 `subtitle` 为 ""（后端原样保存，不做 trim 归一——空白→null 由 web 表单负责，与 landscape 等同类字段口径一致）；步骤 6 列表项含 `subtitle` 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/activity-subtitle/TC-activity-IT-023/`
**最后更新**: 2026-09-02

### TC-activity-IT-024: GET /api/app/activities 列表与详情下发 subtitle，未填时为 null
**关联需求**: activity/App 端活动查询#活动副标题下发且未填时为 null
**关联契约**: api-spec.json#/paths/~1api~1app~1activities/get
**来源**: activity-subtitle
**优先级**: P0
**测试步骤**:
1. 前置：admin 侧创建两个上线活动——活动 A 填 subtitle="山野轻装"，活动 B 不填 subtitle
2. GET http://localhost:8081/api/app/activities（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/activities/{A.id}（请求头带 X-API-Key）
4. GET http://localhost:8081/api/app/activities/{B.id}（请求头带 X-API-Key）
**预期结果**: 步骤 2 返回 200，列表中 A 的 `subtitle`="山野轻装"、B 的 `subtitle` 为 null（键存在且为 null，不回落为 title）；步骤 3 详情 `subtitle`="山野轻装"；步骤 4 详情 `subtitle` 为 null——admin 写入到 app 读出的跨端链路贯通
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/activity-subtitle/TC-activity-IT-024/`
**最后更新**: 2026-09-02

### TC-activity-IT-025: POST/PUT /api/admin/activities 富文本内联小图放行，admin/app 读取原样透传
**关联需求**: file/objectKey 两段式生命周期与绑定校验#富文本内联小图放行
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities/post
**来源**: rich-text-gif-and-inline-sticker
**优先级**: P0
**前置条件**: admin `http://localhost:21423`（test profile）、app `http://localhost:8081`；准备 data URL fixture `D1` = `data:image/gif;base64,<2048 字节内容的 base64>`（内容任意，如 `head -c 2048 /dev/urandom | base64 -w0`；后端只校验 MIME 前缀与解码字节数）
**测试步骤**:
1. POST /api/admin/activities，其余字段合法、online=true，detailHtml 为 `<p>表情</p><img src="D1"><img src="images/0199aaaa-bbbb-7ccc-8ddd-eeeeffff2501.png"><p>结束</p>`（一个内联小图 + 一个 objectKey 图混排）
2. GET /api/admin/activities/{id}
3. GET http://localhost:8081/api/app/activities/{id}（请求头带 X-API-Key）
4. PUT /api/admin/activities/{id}，detailHtml 改为 `<p>改</p><img src="D1">`（内联图经编辑回传），再 GET admin 详情
**预期结果**: 步骤 1 返回 200；步骤 2、3 返回 200，detailHtml 中第一个 img 的 src **与 D1 逐字符相等**（未被替换为签名地址、未被当作 objectKey 改写），第二个 img 的 src 为签名 URL（http 开头），段落文本原样；步骤 4 返回 200 且 src 仍与 D1 相等——内联小图可反复回传保存
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-025/`
**最后更新**: 2026-09-04

### TC-activity-IT-026: POST /api/admin/activities 富文本内联图超限被拒绝（3 KB 边界）
**关联需求**: file/objectKey 两段式生命周期与绑定校验#富文本内联图超限被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities/post
**来源**: rich-text-gif-and-inline-sticker
**优先级**: P0
**前置条件**: 准备三个 png data URL fixture：`D4K` 解码后 4096 字节、`D3073` 解码后 3073 字节、`D3072` 解码后恰 3072 字节（`head -c N /dev/urandom | base64 -w0`）
**测试步骤**:
1. POST /api/admin/activities，其余字段合法，detailHtml 为 `<p>x</p><img src="D4K">`
2. POST /api/admin/activities，detailHtml 为 `<p>x</p><img src="D3073">`
3. POST /api/admin/activities，detailHtml 为 `<p>x</p><img src="D3072">`，再 GET admin 详情
4. GET /api/admin/activities/page 核对数量
**预期结果**: 步骤 1、2 返回 400 且 `message` 为「图片对象不可用」，活动未创建；步骤 3 返回 200（边界值 3072 字节放行），详情中 src 与 D3072 相等；步骤 4 列表仅多出步骤 3 的一条
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-026/`
**最后更新**: 2026-09-04

### TC-activity-IT-027: POST /api/admin/activities 富文本内联图类型不符被拒绝
**关联需求**: file/objectKey 两段式生命周期与绑定校验#富文本内联图类型不符被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1activities/post
**来源**: rich-text-gif-and-inline-sticker
**优先级**: P0
**前置条件**: 准备 `DSVG` = `data:image/svg+xml;base64,<1024 字节内容的 base64>`；`DTXT` = `data:text/plain;base64,aGVsbG8=`；`DNB` = `data:image/png,rawbytes`（非 base64 形态）
**测试步骤**:
1. POST /api/admin/activities，其余字段合法，detailHtml 为 `<p>x</p><img src="DSVG">`
2. POST /api/admin/activities，detailHtml 为 `<p>x</p><img src="DTXT">`
3. POST /api/admin/activities，detailHtml 为 `<p>x</p><img src="DNB">`
4. PUT /api/admin/activities/{既有活动 id}，detailHtml 为 `<p>x</p><img src="DSVG">`，再 GET 详情
**预期结果**: 步骤 1~3 均返回 400 且 `message` 为「图片对象不可用」（不区分类型/大小原因），活动未创建；步骤 4 返回 400 且详情 detailHtml 保持更新前内容（事务回滚，svg 未落库）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/rich-text-gif-and-inline-sticker/TC-activity-IT-027/`
**最后更新**: 2026-09-04
