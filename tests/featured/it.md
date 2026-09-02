# featured IT 用例

### TC-featured-IT-001: POST /api/admin/featured-items 创建精选推荐
**关联需求**: featured/精选推荐管理#创建精选推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items/post
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. 前置：存在一个上架城市
3. POST /api/admin/featured-items，body：cityId（该城市）、banner（objectKey）、description「地图上新」、online=true
4. GET /api/admin/featured-items/{id}
**预期结果**: 创建返回 200；详情含关联城市 cityId（城市名称由 web 端经城市列表映射展示，admin 响应不冗余，与 Activity/Banner 口径一致）、banner 签名 URL（http 开头、非裸 objectKey）、description 与 online=true
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-feed/TC-featured-IT-001/`
**最后更新**: 2026-08-20

### TC-featured-IT-002: POST /api/admin/featured-items 缺 banner 或城市不存在被拒绝
**关联需求**: featured/精选推荐管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items/post
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. POST /api/admin/featured-items，body 缺 banner（cityId 合法）
2. POST /api/admin/featured-items，body cityId 为不存在的 UUID（banner 合法）
3. POST /api/admin/featured-items，body 缺 cityId
**预期结果**: 三次均返回 400，响应 `message` 为中文业务错误；条目均未创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-feed/TC-featured-IT-002/`
**最后更新**: 2026-08-20

### TC-featured-IT-003: PUT /api/admin/featured-items/{id}/online 上下线切换
**关联需求**: featured/精选推荐管理#精选推荐上下线切换
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items~1{id}~1online/put
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 online=true 的推荐条目
2. PUT /api/admin/featured-items/{id}/online，body：`{"online": false}`
3. GET /api/admin/featured-items/{id}
4. PUT /api/admin/featured-items/{id}/online，body：`{"online": true}` 后再查详情
**预期结果**: 步骤 2 返回 200，步骤 3 详情 online=false；步骤 4 后详情 online=true（可往返切换）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-feed/TC-featured-IT-003/`
**最后更新**: 2026-08-20

### TC-featured-IT-004: PUT /api/admin/featured-items/{id} 更新条目且 cityId 不可变
**关联需求**: featured/精选推荐管理#创建精选推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items~1{id}/put
**来源**: article-and-featured-feed
**优先级**: P1
**测试步骤**:
1. 前置：城市 A 下已存在推荐条目；另存在城市 B
2. PUT /api/admin/featured-items/{id}，body：description 改写、banner 换新 objectKey、cityId 传城市 B 的 id
3. GET /api/admin/featured-items/{id}
**预期结果**: 更新返回 200；description 与 banner 更新生效（banner 为新图签名 URL）；关联城市仍为城市 A（cityId 变更被忽略，不返回错误）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-feed/TC-featured-IT-004/`
**最后更新**: 2026-08-20

### TC-featured-IT-005: DELETE /api/admin/featured-items/{id} 物理删除
**关联需求**: featured/精选推荐管理#创建精选推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-items~1{id}/delete
**来源**: article-and-featured-feed
**优先级**: P1
**测试步骤**:
1. 前置：存在一个推荐条目
2. DELETE /api/admin/featured-items/{id}
3. GET /api/admin/featured-items/{id}
4. GET /api/admin/featured-items/page 确认分页列表不含该条目
**预期结果**: 删除返回 200；再查详情返回 400 及中文业务错误（admin 端「资源不存在」全局口径）；分页列表不再出现该条目
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-feed/TC-featured-IT-005/`
**最后更新**: 2026-08-20

### TC-featured-IT-006: GET /api/app/featured-items 信息流仅含上线条目且按创建时间倒序
**关联需求**: featured/App 端精选推荐查询#查询精选推荐信息流
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-items/get
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：某上架城市下先后创建两条上线推荐（条目 1 先建、条目 2 后建）与一条下线推荐
2. GET http://localhost:8081/api/app/featured-items（请求头带 X-API-Key）
**预期结果**: 返回 200；列表仅含两条上线条目，不含下线条目；按创建时间倒序（条目 2 在前）；每项含 banner 签名 URL、推荐说明与关联城市数据（id 与名称）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-feed/TC-featured-IT-006/`
**最后更新**: 2026-08-20

### TC-featured-IT-007: POST /api/admin/featured-cycle-items 创建活动类周期推荐
**关联需求**: featured/周期推荐条目管理#创建活动类周期推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. 前置：存在一个**尚未被任何周期推荐引用**的活动（记其 id 与 title）
3. POST /api/admin/featured-cycle-items，body：`phases=["MENSTRUAL"]`、type=ACTIVITY、`targetId`（该活动 id）、description「经期慢下来」、note「周末两日」、banner（objectKey）、sortOrder=1
4. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 创建返回 200；详情 `phases` 为 JSON 数组 `["MENSTRUAL"]`（不是字符串，响应中不再出现单值 `phase` 字段）、type=ACTIVITY、`targetId` 等于该活动 id、relatedTitle 回显该活动标题、description 与 note 原样、banner 为签名 URL（http 开头、非裸 objectKey）、sortOrder=1、online=false（未传时默认下线）；title/subtitle 为 null；响应中不再出现 activityId/routeId/articleId 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-007/`
**最后更新**: 2026-09-02

### TC-featured-IT-008: POST /api/admin/featured-cycle-items 创建路线类周期推荐
**关联需求**: featured/周期推荐条目管理#创建路线类周期推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置：存在一条**尚未被任何周期推荐引用**的路线（记其 id 与主标题，路线主标题取值需与下一步手填标题不同）
2. POST /api/admin/featured-cycle-items，body：`phases=["OVULATION"]`、type=ROUTE、`targetId`（该路线 id）、title「排卵期就该出门」、subtitle「三天两夜」、description「体力最好的几天」、banner（objectKey）
3. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 创建返回 200；详情 `phases` 为 `["OVULATION"]`、type=ROUTE、`targetId` 等于该路线 id、title 与 subtitle 为**手填值**（不等于路线实体的主标题）、description 原样、banner 为签名 URL；note 为 null；响应中不再出现 activityId/routeId/articleId 与单值 `phase` 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-008/`
**最后更新**: 2026-09-02

### TC-featured-IT-009: POST /api/admin/featured-cycle-items 创建文章类周期推荐
**关联需求**: featured/周期推荐条目管理#创建文章类周期推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置：存在一篇**尚未被任何周期推荐引用**的文章（记其 id 与 title）
2. POST /api/admin/featured-cycle-items，body：`phases=["LUTEAL"]`、type=ARTICLE、`targetId`（该文章 id）、title「黄体期生活法」、banner（objectKey）
3. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 创建返回 200；详情 `phases` 为 `["LUTEAL"]`、type=ARTICLE、`targetId` 等于该文章 id、relatedTitle 回显该文章标题、title 为提交值、banner 为签名 URL；subtitle/description/note 均为 null；响应中不再出现 activityId/routeId/articleId 与单值 `phase` 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-009/`
**最后更新**: 2026-09-02

### TC-featured-IT-010: POST /api/admin/featured-cycle-items 类型必填项缺失被拒绝
**关联需求**: featured/周期推荐条目管理#缺少类型必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置：准备三个互不相同且尚未被引用的关联实体（活动、路线、文章各一）
2. POST type=ROUTE，body 含 `phases=["MENSTRUAL"]`、合法 `targetId`（路线）/title/description/banner 但缺 subtitle
3. POST type=ACTIVITY，body 含 `phases=["MENSTRUAL"]`、合法 `targetId`（活动）/banner 但缺 description
4. POST type=ARTICLE，body 含 `phases=["MENSTRUAL"]`、合法 `targetId`（文章）/title 但缺 banner
5. POST body 缺 type（`phases` 与其余字段合法）
**预期结果**: 步骤 2~5 四次均返回 400，响应 `message` 为中文业务错误；条目均未创建（后续 GET page 计数不变）。（`phases` 缺省/为空的校验另见 TC-featured-IT-040）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-010/`
**最后更新**: 2026-09-02

### TC-featured-IT-011: POST /api/admin/featured-cycle-items 关联实体不存在被拒绝
**关联需求**: featured/周期推荐条目管理#关联实体不存在被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. POST type=ACTIVITY、`phases=["MENSTRUAL"]`、`targetId` 为不存在的 UUID（其余合法）
2. POST type=ROUTE、`phases=["OVULATION"]`、`targetId` 为不存在的 UUID（其余合法）
3. POST type=ARTICLE、`phases=["LUTEAL"]`、`targetId` 为不存在的 UUID（其余合法）
4. POST type=ACTIVITY、`phases=["MENSTRUAL"]`、`targetId` 传一篇**已存在文章**的 id（即 id 存在但不属于该 type 对应的实体表）
**预期结果**: 四次均返回 400，响应 `message` 为中文业务错误并按类型区分「关联活动/路线/文章不存在」；条目均未创建（`targetId` 按 `type` 分派到对应实体表校验，不跨表命中）；错误文案为「不存在」口径，不与唯一冲突文案「已存在周期推荐」混淆
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-011/`
**最后更新**: 2026-09-02

### TC-featured-IT-012: PUT /api/admin/featured-cycle-items/{id} phases 可改而 type 创建后不可变
**关联需求**: featured/周期推荐条目管理#周期与类型创建后不可变
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 `phases=["MENSTRUAL"]`、type=ACTIVITY 的条目（记其关联活动 id）
2. PUT /api/admin/featured-cycle-items/{id}，body 传 `phases=["FOLLICULAR","OVULATION"]`、type=ARTICLE、title「改名」，同时改 description；`targetId` 与 banner 等必填字段仍按**持久化类型**（ACTIVITY）提供（`targetId` 传原活动 id）
3. GET /api/admin/featured-cycle-items/{id}
4. 再 PUT 一次，`targetId` 改传一篇合法文章的 id（type 仍传 ARTICLE）
**预期结果**: 步骤 2 更新返回 200；步骤 3 详情 `phases` 已更新为 `["FOLLICULAR","OVULATION"]`（周期由不可变放宽为**可修改**，元素按枚举声明顺序 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL 排列）、type 仍为 ACTIVITY（传入值被忽略）、`targetId` 仍为该活动 id、description 已按提交值更新、title 未被写入（仍为 null）；步骤 4 返回 400 中文业务错误「关联活动不存在」——`targetId` 的存在性校验按持久化类型 ACTIVITY 分派，与请求体传入的 type 无关
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-012/`
**最后更新**: 2026-09-02

### TC-featured-IT-013: GET /api/admin/featured-cycle-items/page phase 参数按「包含」过滤并按排序号升序
**关联需求**: featured/周期推荐条目管理#按周期过滤列表
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1page/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置（每条关联互不相同的实体，满足 `(type,targetId)` 全局唯一）：条目 X1 `phases=["FOLLICULAR"]` sortOrder=2、X2 `phases=["FOLLICULAR","LUTEAL"]` sortOrder=1、X3 `phases=["FOLLICULAR"]` sortOrder=3、Y `phases=["MENSTRUAL"]`
2. GET /api/admin/featured-cycle-items/page?phase=FOLLICULAR
3. GET /api/admin/featured-cycle-items/page?phase=LUTEAL
4. GET /api/admin/featured-cycle-items/page?phase=MENSTRUAL
**预期结果**: 步骤 2 返回 200，content 恰含 X1/X2/X3 三条（`phase` 参数语义为「条目 `phases` **包含**该周期」）且 sortOrder 依次为 1、2、3，每项 `phases` 为数组（X2 为 `["FOLLICULAR","LUTEAL"]`）；步骤 3 恰含 X2 一条（多周期条目在其每个周期的过滤结果中都出现）；步骤 4 恰含 Y 一条，不含 X1/X2/X3
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-013/`
**最后更新**: 2026-09-02

### TC-featured-IT-014: PUT /api/admin/featured-cycle-items/{id}/online 上下线切换
**关联需求**: featured/周期推荐条目管理#周期推荐上下线切换
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}~1online/put
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 online=true 的周期推荐条目
2. PUT /api/admin/featured-cycle-items/{id}/online，body：online=false
3. GET /api/admin/featured-cycle-items/{id}
4. PUT 再置 online=true 并复查
**预期结果**: 两次切换均返回 200；详情 online 分别为 false、true
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-014/`
**最后更新**: 2026-09-02

### TC-featured-IT-015: DELETE /api/admin/featured-cycle-items/{id} 物理删除
**关联需求**: featured/周期推荐条目管理
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/delete
**来源**: featured-cycle-feed
**优先级**: P1
**测试步骤**:
1. 前置：存在一个周期推荐条目
2. DELETE /api/admin/featured-cycle-items/{id}
3. GET /api/admin/featured-cycle-items/{id}
4. DELETE 同一 id 再删一次
**预期结果**: 第 2 步返回 200；第 3 步返回 400/404 且 `message` 为中文业务错误；第 4 步返回 400/404，不产生 500；被关联的活动/路线/文章实体本身不受影响（GET 其详情仍 200）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-015/`
**最后更新**: 2026-09-02

### TC-featured-IT-016: GET /api/app/featured-cycle-items 扁平数组带 period 周期数组且只含上线条目
**关联需求**: featured/App 端周期推荐查询#查询四个周期的推荐列表
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据，各条关联不同实体）：`phases=["MENSTRUAL"]` 的上线 ACTIVITY 条目（活动上线）、`phases=["OVULATION"]` 的上线 ARTICLE 条目（文章上线）、`phases=["LUTEAL"]` 的**下线**条目
2. GET http://localhost:8081/api/app/featured-cycle-items（不带参数，请求头带 X-API-Key）
**预期结果**: 返回 200；响应顶层为 JSON 数组（不是按周期分组的对象，无 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL 键）；数组恰含 2 条：一条 `period` 为数组 `["MENSTRUAL"]`、type=ACTIVITY、`targetId` 等于该活动 id，一条 `period` 为数组 `["OVULATION"]`、type=ARTICLE、`targetId` 等于该文章 id（`period` 直接取自条目自身持久化的 `phases`，不跨条目聚合；字段名与形状不变，仍叫 `period` 且为数组；响应中不再出现 activityId/routeId/articleId 字段）；每条含 banner 签名 URL（http 开头、非裸 objectKey）；不含下线条目
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-016/`
**最后更新**: 2026-09-02

### TC-featured-IT-017: GET /api/app/featured-cycle-items 关联实体不可见时条目不下发
**关联需求**: featured/App 端周期推荐查询#关联实体不可见时条目不下发
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置：MENSTRUAL 下 1 个上线 ACTIVITY 条目（活动上线、城市上架）、1 个上线 ARTICLE 条目（文章上线）；GET app 接口确认数组含两条（`period` 均为 `["MENSTRUAL"]`）
2. admin 端将该活动下线，GET app 接口
3. 恢复活动上线，GET app 接口（活动不关联城市，无城市下架步骤）
4. 恢复城市上架、改将该文章下线，GET app 接口
5. 恢复文章上线，admin 端删除该文章，GET app 接口
**预期结果**: 步骤 2 数组不含该 ACTIVITY 条目、步骤 3 恢复后重新出现；步骤 4、5 数组不含该 ARTICLE 条目；每步中未受影响的另一条仍在数组中；接口全程返回 200，不因关联实体缺失报 500
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-017/`
**最后更新**: 2026-09-02

### TC-featured-IT-018: GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目
**关联需求**: featured/App 端周期推荐查询#大使下线连带隐藏路线类条目
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置：存在上架城市下的一条路线，其爱女大使 online=true；OVULATION 下建 1 个上线 ROUTE 条目关联该路线
2. GET http://localhost:8081/api/app/featured-cycle-items（请求头带 X-API-Key），确认数组含该条目且其 `period` 为 `["OVULATION"]`
3. admin 端将该大使下线，GET /api/app/featured-cycle-items
4. 恢复大使上线，GET /api/app/featured-cycle-items
**预期结果**: 步骤 2 数组含该条目（`period` 为 `["OVULATION"]`、type=ROUTE、`targetId` 等于该路线 id，响应无 routeId 字段）；步骤 3 数组不含该条目；步骤 4 该条目重新出现；接口全程返回 200。（城市下架不再影响 ROUTE 条目，该口径改由 TC-featured-IT-020 断言）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-018/`
**最后更新**: 2026-09-02

### TC-featured-IT-019: GET /api/app/featured-cycle-items 按排序号升序
**关联需求**: featured/App 端周期推荐查询#组内按排序号升序
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**测试步骤**:
1. 前置：创建 5 个关联**互不相同**实体的上线条目（满足 `(type,targetId)` 唯一），`phases` 均为 `["MENSTRUAL"]`，sortOrder 依次为 2、1、3、1、1（按此顺序创建，关联实体均可见）
2. GET http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL（请求头带 X-API-Key）
**预期结果**: 返回 200；数组内 5 条条目 `period` 均为 `["MENSTRUAL"]`，按 sortOrder 1、1、1、2、3 升序排列；三个 sortOrder=1 的条目按 createdAt 倒序（后创建的在前）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-019/`
**最后更新**: 2026-09-02

### TC-featured-IT-020: GET /api/app/featured-cycle-items 城市未上架不影响路线类条目
**关联需求**: featured/App 端周期推荐查询#城市未上架不影响路线类条目
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置：admin 端创建一个**下架**城市，在其下创建一条路线（关联大使 online=true）；OVULATION 下建 1 个上线 ROUTE 条目关联该路线；同一周期另建 1 个上线 ACTIVITY 条目（活动上线，活动不关联城市）
2. GET http://localhost:8081/api/app/featured-cycle-items（请求头带 X-API-Key）
3. admin 端将该城市上架，再次 GET /api/app/featured-cycle-items
**预期结果**: 步骤 2 返回 200，数组含该 ROUTE 条目（`period` 为 `["OVULATION"]`，城市下架不再过滤），ACTIVITY 条目同样在数组中（活动不关联城市）；步骤 3 城市上架后数组仍同时含两条条目，前后一致
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-020/`
**最后更新**: 2026-09-02

### TC-featured-IT-021: GET /api/app/featured-cycle-items?type= 按内容类型过滤
**关联需求**: featured/App 端周期推荐查询#按内容类型过滤
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据，各关联不同实体）：`phases=["MENSTRUAL"]` 的上线条目各 1 个，类型分别为 ACTIVITY（活动上线）、ROUTE（大使上线）、ARTICLE（文章上线）
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ARTICLE（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/featured-cycle-items（不带参数，同一批数据）
**预期结果**: 步骤 2 返回 200，数组仅含该 ARTICLE 条目（`period` 为 `["MENSTRUAL"]`、type=ARTICLE、`targetId` 等于该文章 id，响应无 articleId 字段），不含 ACTIVITY/ROUTE 条目；步骤 3 返回 200 且数组含全部 3 条（不传 type 行为不变）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-021/`
**最后更新**: 2026-09-02

### TC-featured-IT-022: GET /api/app/featured-cycle-items?type= 类型过滤后无条目返回空数组
**关联需求**: featured/App 端周期推荐查询#类型过滤后周期为空仍返回空数组
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: app-article-optional-category-and-featured-period-filter
**优先级**: P1
**测试步骤**:
1. 前置：库中仅 MENSTRUAL 下有 1 个 ACTIVITY 类上线条目（无 ROUTE 类可见条目，其余周期无条目）
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ROUTE（请求头带 X-API-Key）
**预期结果**: 返回 200 且响应体为 `[]`（空数组，不是四键对象、不返回 404）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-022/`
**最后更新**: 2026-09-02

### TC-featured-IT-023: GET /api/app/featured-cycle-items?type= 非法类型值返回 400
**关联需求**: featured/App 端周期推荐查询#非法类型值被拒绝
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: app-article-optional-category-and-featured-period-filter
**优先级**: P1
**测试步骤**:
1. GET http://localhost:8081/api/app/featured-cycle-items?type=UNKNOWN（请求头带 X-API-Key）
**预期结果**: 返回 400（枚举转换失败），不返回 200 也不静默忽略该参数
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-023/`
**最后更新**: 2026-09-02

### TC-featured-IT-024: GET /api/app/featured-cycle-items?period= 按周期过滤
**关联需求**: featured/App 端周期推荐查询#按周期过滤
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据，三条各关联不同 target）：条目 M1 `phases=["MENSTRUAL"]` 上线 ACTIVITY（活动上线）、条目 M2 `phases=["MENSTRUAL"]` 上线 ARTICLE（文章上线）、条目 F1 `phases=["FOLLICULAR"]` 上线 ARTICLE（文章上线）
2. GET http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/featured-cycle-items?period=FOLLICULAR（请求头带 X-API-Key）
**预期结果**: 步骤 2 返回 200，数组恰含 M1、M2 两条且每条 `period` 为 `["MENSTRUAL"]`（过滤语义为条目 `phases` **包含** MENSTRUAL），不含 F1；步骤 3 返回 200，数组恰含 F1 且 `period` 为 `["FOLLICULAR"]`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-024/`
**最后更新**: 2026-09-02

### TC-featured-IT-025: GET /api/app/featured-cycle-items?period=&type= 周期与类型同时过滤
**关联需求**: featured/App 端周期推荐查询#周期与类型同时过滤
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据，各关联不同 target）：`phases` 含 MENSTRUAL 的上线 ACTIVITY 条目（活动上线）与上线 ARTICLE 条目（文章上线）各 1 个；另建 1 个 `phases=["FOLLICULAR"]` 的上线 ARTICLE 条目
2. GET http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL&type=ARTICLE（请求头带 X-API-Key）
**预期结果**: 返回 200，数组恰含 1 条：`period` 含 MENSTRUAL、type=ARTICLE、`targetId` 等于该文章 id（响应无 articleId 字段）；不含 MENSTRUAL 的 ACTIVITY 条目，也不含 FOLLICULAR 的 ARTICLE 条目
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-025/`
**最后更新**: 2026-09-02

### TC-featured-IT-026: GET /api/app/featured-cycle-items?period= 周期过滤后无条目返回空数组
**关联需求**: featured/App 端周期推荐查询#周期过滤后无条目返回空数组
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: app-article-optional-category-and-featured-period-filter
**优先级**: P1
**测试步骤**:
1. 前置：库中仅 MENSTRUAL 下有 1 个 ACTIVITY 类上线条目，其余周期无条目
2. GET http://localhost:8081/api/app/featured-cycle-items?period=LUTEAL（请求头带 X-API-Key）
**预期结果**: 返回 200 且响应体为 `[]`（空数组，不是空对象、不返回 404）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-026/`
**最后更新**: 2026-09-02

### TC-featured-IT-027: GET /api/app/featured-cycle-items?period= 非法周期值返回 400
**关联需求**: featured/App 端周期推荐查询#非法周期值被拒绝
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: app-article-optional-category-and-featured-period-filter
**优先级**: P1
**测试步骤**:
1. GET http://localhost:8081/api/app/featured-cycle-items?period=UNKNOWN（请求头带 X-API-Key）
2. GET http://localhost:8081/api/app/featured-cycle-items?period=menstrual（小写，请求头带 X-API-Key）
**预期结果**: 两次均返回 400（枚举转换失败），不返回 200 也不静默忽略该参数
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-027/`
**最后更新**: 2026-09-02

### TC-featured-IT-028: GET /api/app/featured-cycle-items 多周期条目在 period 数组中下发全部周期
**关联需求**: featured/App 端周期推荐查询#同一 target 跨周期时下发全部周期
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据）：创建 1 个上线活动 A；创建**一条** `phases=["MENSTRUAL","LUTEAL"]` 的上线 ACTIVITY 条目关联活动 A（因 `(type,targetId)` 全局唯一，A 只能有这一条条目）
2. GET http://localhost:8081/api/app/featured-cycle-items（不带参数，请求头带 X-API-Key）
**预期结果**: 返回 200；数组含关联 A 的条目**恰好一次**（一个 target 至多出现一次，不再出现同 target 多条）；其 `period` 为 `["MENSTRUAL","LUTEAL"]`（元素顺序按 Period 枚举声明顺序 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL，去重无重复项，值直接取自条目自身的 `phases`）；banner 签名 URL 与主标题等于该条目的配置
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-028/`
**最后更新**: 2026-09-02

### TC-featured-IT-029: GET /api/app/featured-cycle-items?period= 过滤后 period 数组仍含其他周期
**关联需求**: featured/App 端周期推荐查询#按周期过滤时 period 数组仍含其他周期
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置：同 TC-featured-IT-028（活动 A 的唯一条目 `phases=["MENSTRUAL","LUTEAL"]`，上线）
2. GET http://localhost:8081/api/app/featured-cycle-items?period=LUTEAL（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL（请求头带 X-API-Key）
**预期结果**: 步骤 2、3 均返回 200 且数组均含该条目（`period` 查询参数语义为条目 `phases` **包含**该值，故两个周期都能命中同一条）；两次该条的 `period` 均为 `["MENSTRUAL","LUTEAL"]`，未被查询参数收窄为 `["LUTEAL"]` 或 `["MENSTRUAL"]`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-029/`
**最后更新**: 2026-09-02

### TC-featured-IT-030: GET /api/app/featured-cycle-items?type=&period= 类型过滤不影响 period 数组
**关联需求**: featured/App 端周期推荐查询#类型过滤不影响 period 数组
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**测试步骤**:
1. 前置：同 TC-featured-IT-028（活动 A 的唯一条目 `phases=["MENSTRUAL","LUTEAL"]`，type=ACTIVITY，上线）
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY&period=MENSTRUAL（请求头带 X-API-Key）
**预期结果**: 返回 200；数组含该条目；其 `period` 为 `["MENSTRUAL","LUTEAL"]`、`type=ACTIVITY`；`period` 取值只由条目自身 `phases` 决定，不受 `type` / `period` 查询参数影响
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-030/`
**最后更新**: 2026-09-02

### TC-featured-IT-031: GET /api/app/featured-cycle-items 下线条目整条不下发
**关联需求**: featured/App 端周期推荐查询#不可下发条目不贡献周期
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置：创建 1 个上线活动 A；创建**一条** `phases=["MENSTRUAL","LUTEAL"]` 的 ACTIVITY 条目关联 A，先置为**下线**
2. GET http://localhost:8081/api/app/featured-cycle-items（不带参数，请求头带 X-API-Key）
3. admin 端将该条目上线，再次 GET
4. 将活动 A 下线，再次 GET
**预期结果**: 步骤 2 返回 200，数组**不含**该条目（下线条目整条不下发，其 `phases` 不以任何形式出现）；步骤 3 数组含该条目一次且 `period` 为 `["MENSTRUAL","LUTEAL"]`；步骤 4 数组不含该条目，接口仍返回 200
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-031/`
**最后更新**: 2026-09-02

### TC-featured-IT-032: GET /api/app/featured-cycle-items?period= 不同 target 的周期集合互不影响
**关联需求**: featured/App 端周期推荐查询#不同 target 的周期集合互不影响
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**测试步骤**:
1. 前置：创建上线活动 A、上线活动 B；A 的唯一条目 `phases=["MENSTRUAL","LUTEAL"]` 上线，B 的唯一条目 `phases=["MENSTRUAL"]` 上线
2. GET http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL（请求头带 X-API-Key）
**预期结果**: 返回 200；数组恰含 2 条；关联 A 的条目 `period` 为 `["MENSTRUAL","LUTEAL"]`，关联 B 的条目 `period` 为 `["MENSTRUAL"]`（各取自各自条目的 `phases`，A 的周期不外溢到 B）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-032/`
**最后更新**: 2026-09-02

### TC-featured-IT-033: POST /api/admin/featured-cycle-items 缺 targetId 被拒绝
**关联需求**: featured/周期推荐条目管理#缺少 targetId 被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/featured-cycle-items，body：`phases=["MENSTRUAL"]`、type=ACTIVITY、description、banner，**不带 `targetId`**
3. POST /api/admin/featured-cycle-items，body：`phases=["OVULATION"]`、type=ROUTE、title、subtitle、description、banner，**不带 `targetId`**
4. POST /api/admin/featured-cycle-items，body：`phases=["LUTEAL"]`、type=ARTICLE、title、banner，**不带 `targetId`**
5. PUT /api/admin/featured-cycle-items/{id}（已存在条目），body 其余合法但 `targetId` 传 null
6. GET /api/admin/featured-cycle-items/page 核对总数与被更新条目详情
**预期结果**: 步骤 2~5 均返回 400，响应 `message` 为中文业务错误（关联实体不能为空口径），不返回 500；步骤 6 分页总数不变（三次创建均未落库），被 PUT 的条目 `targetId`、`phases` 与其余字段保持原值未被清空
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-033/`
**最后更新**: 2026-09-02

### TC-featured-IT-034: GET /api/app/featured-cycle-items 活动类条目下发活动基础信息
**关联需求**: featured/App 端周期推荐查询#活动类条目下发活动基础信息
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-target-basic-info → activity-subtitle
**优先级**: P0
**测试步骤**:
1. 前置：创建上线活动（含 ≥1 张图片、标题、副标题 subtitle="山野轻装"、难度等级 level），在 MENSTRUAL 下创建 1 个上线 ACTIVITY 条目并填推荐说明
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY（请求头带 X-API-Key）
**预期结果**: 返回 200；该条目 `target` 非 null，`target.id` = 活动 id，`target.title` = 活动标题，`target.subtitle` = "山野轻装"（取自活动实体），`target.cover` 为首图签名 URL 对象，`target.level` = 活动难度等级；条目自身的 `description` 仍为条目上手填的推荐说明
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-034/`
**最后更新**: 2026-09-02

### TC-featured-IT-035: GET /api/app/featured-cycle-items 路线类条目下发路线基础信息且不覆盖手填文案
**关联需求**: featured/App 端周期推荐查询#路线类条目下发路线基础信息且不覆盖手填文案
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-target-basic-info
**优先级**: P0
**测试步骤**:
1. 前置：创建上线爱女大使，创建关联该大使的路线（含缩略图、cityName、路线标题 T1）；在 OVULATION 下创建 1 个上线 ROUTE 条目，手填主标题 T2（T2 ≠ T1）
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ROUTE（请求头带 X-API-Key）
**预期结果**: 返回 200；`target.id` = 路线 id，`target.title` = T1，`target.thumbnail` 为签名 URL 对象，`target.cityName` = 路线自身城市名，`target.ambassadorName` = 大使名称；条目的 `title` 仍为 T2
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-035/`
**最后更新**: 2026-09-02

### TC-featured-IT-036: GET /api/app/featured-cycle-items 文章类条目下发文章基础信息
**关联需求**: featured/App 端周期推荐查询#文章类条目下发文章基础信息
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-target-basic-info
**优先级**: P0
**测试步骤**:
1. 前置：创建上线文章（含标题、封面标题 coverTitle、封面图 image），在 LUTEAL 下创建 1 个上线 ARTICLE 条目
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ARTICLE（请求头带 X-API-Key）
**预期结果**: 返回 200；`target.id` = 文章 id，`target.title` = 文章标题，`target.coverTitle` = 文章封面标题，`target.image` 为签名 URL 对象
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-036/`
**最后更新**: 2026-09-02

### TC-featured-IT-037: GET /api/app/featured-cycle-items 活动无图片时 target.cover 为 null
**关联需求**: featured/App 端周期推荐查询#活动无图片时 cover 为 null
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-target-basic-info
**优先级**: P1
**测试步骤**:
1. 前置：创建上线活动但不上传任何图片，在 FOLLICULAR 下创建 1 个上线 ACTIVITY 条目
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY&period=FOLLICULAR（请求头带 X-API-Key）
**预期结果**: 返回 200；该条目仍被下发，`target` 非 null，`target.cover` 为 null，`target.id` / `target.title` 正常有值
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-037/`
**最后更新**: 2026-09-02

### TC-featured-IT-038: GET /api/app/featured-cycle-items 活动未填副标题时 target.subtitle 为 null
**关联需求**: featured/App 端周期推荐查询#活动未填副标题时 target.subtitle 为 null
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: activity-subtitle
**优先级**: P0
**测试步骤**:
1. 前置：创建上线活动（含标题、≥1 张图片，**不填** subtitle），在 OVULATION 下创建 1 个上线 ACTIVITY 条目，条目自身手填 `description`="限时开团"（ACTIVITY 类条目不持有 subtitle 文案，该字段只适用于 ROUTE）
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY&period=OVULATION（请求头带 X-API-Key）
**预期结果**: 返回 200；该条目 `target` 非 null，`target.subtitle` 为 null——不回落为活动标题；条目自身的 `description` 仍为「限时开团」不被 target 覆盖，`target.title` 为活动标题
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-038/`
**最后更新**: 2026-09-02

### TC-featured-IT-039: POST /api/admin/featured-cycle-items 创建多周期条目
**关联需求**: featured/周期推荐条目管理#创建多周期条目
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. 前置：存在一条尚未被任何周期推荐引用的路线（大使上线）
3. POST /api/admin/featured-cycle-items，body：`phases=["LUTEAL","MENSTRUAL"]`（**乱序传入**）、type=ROUTE、`targetId`（该路线 id）、title、subtitle、description、banner
4. GET /api/admin/featured-cycle-items/{id}
5. GET /api/admin/featured-cycle-items/page?phase=MENSTRUAL
6. GET /api/admin/featured-cycle-items/page?phase=LUTEAL
7. 另 POST 一条 `phases=["MENSTRUAL","MENSTRUAL","LUTEAL"]`（**含重复值**）关联另一条未被引用的路线，并 GET 其详情
**预期结果**: 步骤 3 返回 200；步骤 4 详情 `phases` 为 `["MENSTRUAL","LUTEAL"]`——按 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL 枚举声明顺序归一，而非请求体传入顺序；步骤 5、6 的分页结果中**均**出现该条目（一条条目同时归属多个周期）；步骤 7 详情 `phases` 为 `["MENSTRUAL","LUTEAL"]`（重复值已去重，长度为 2）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-039/`
**最后更新**: 2026-09-02

### TC-featured-IT-040: POST /api/admin/featured-cycle-items phases 为空或缺省被拒绝
**关联需求**: featured/周期推荐条目管理#phases 为空被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/featured-cycle-items，body：`phases=[]`（空数组），type=ACTIVITY 及该类型全部必填字段合法
3. POST /api/admin/featured-cycle-items，body **完全不带 `phases` 字段**，其余同上
4. POST /api/admin/featured-cycle-items，body：`phases=null`，其余同上
5. PUT /api/admin/featured-cycle-items/{id}（已存在的多周期条目），body 传 `phases=[]`，其余合法
6. GET /api/admin/featured-cycle-items/page 与被 PUT 条目的详情
**预期结果**: 步骤 2~5 均返回 400，响应 `message` 为中文业务错误（至少选择一个周期口径），不返回 500 也不静默落库为空数组；步骤 6 分页总数不变（三次创建均未落库），被 PUT 条目的 `phases` 保持原值未被清空
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-040/`
**最后更新**: 2026-09-02

### TC-featured-IT-041: POST /api/admin/featured-cycle-items 同一关联实体重复创建被拒绝
**关联需求**: featured/周期推荐条目管理#同一关联实体重复创建被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. 前置：对活动 A 创建一条 `phases=["MENSTRUAL"]`、type=ACTIVITY 的条目（返回 200，记 id）
3. 再 POST 一条 type=ACTIVITY、`targetId` 仍为活动 A、`phases=["LUTEAL"]`、其余必填合法
4. 同样对已被引用的路线 R 再 POST type=ROUTE 的条目；对已被引用的文章 T 再 POST type=ARTICLE 的条目
5. GET /api/admin/featured-cycle-items/page 核对总数
**预期结果**: 步骤 3 返回 400，`message` 为中文业务错误「该活动已存在周期推荐」（文案按类型区分，路线为「该路线已存在周期推荐」、文章为「该文章已存在周期推荐」）；步骤 4 两次同样返回 400 且文案分别对应路线/文章；全程不返回 500、不触发数据库唯一约束异常泄漏；步骤 5 分页总数不变，重复条目均未新增
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-041/`
**最后更新**: 2026-09-02

### TC-featured-IT-042: POST /api/admin/featured-cycle-items 下线条目同样占用唯一位
**关联需求**: featured/周期推荐条目管理#下线条目同样占用唯一位
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**测试步骤**:
1. 前置：对文章 T 创建一条 type=ARTICLE 的条目，并 PUT /api/admin/featured-cycle-items/{id}/online 置 online=false
2. POST /api/admin/featured-cycle-items，type=ARTICLE、`targetId` 仍为文章 T、`phases=["FOLLICULAR"]`、其余必填合法
3. DELETE 掉步骤 1 的下线条目后，再次执行步骤 2 的 POST
**预期结果**: 步骤 2 返回 400 及中文业务错误「该文章已存在周期推荐」——唯一约束与上下线状态无关，下线条目同样占位；步骤 3 返回 200（占位条目删除后该实体重新可用），详情 `phases` 为 `["FOLLICULAR"]`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-042/`
**最后更新**: 2026-09-02

### TC-featured-IT-043: PUT /api/admin/featured-cycle-items/{id} 更新条目自身不触发唯一冲突
**关联需求**: featured/周期推荐条目管理#更新条目自身不触发唯一冲突
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置：对活动 A 创建一条 `phases=["MENSTRUAL"]`、type=ACTIVITY 的条目（记 id）
2. PUT /api/admin/featured-cycle-items/{id}，body：`targetId` **仍为活动 A**、`phases=["OVULATION","LUTEAL"]`、description 改为新文案、其余必填合法
3. GET /api/admin/featured-cycle-items/{id}
4. 再 PUT 一次，body 与步骤 2 完全相同（幂等重复提交）
**预期结果**: 步骤 2 返回 200——唯一性校验 SHALL 排除条目自身，不因 `(type,targetId)` 未变而误报冲突；步骤 3 详情 `phases` 为 `["OVULATION","LUTEAL"]`、`targetId` 仍为活动 A、description 为新文案；步骤 4 仍返回 200
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-043/`
**最后更新**: 2026-09-02

### TC-featured-IT-046: PUT /api/admin/featured-cycle-items/{id} 更新关联实体
**关联需求**: featured/周期推荐条目管理#更新关联实体
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**测试步骤**:
1. 前置：存在活动 A 与活动 B，B 未被任何周期推荐引用；对活动 A 创建一条 type=ACTIVITY 的条目
2. PUT /api/admin/featured-cycle-items/{id}，`targetId` 改为活动 B，其余必填按 ACTIVITY 约束提供
3. POST /api/admin/featured-cycle-items，新建一条 type=ACTIVITY、`targetId` 为活动 A 的条目
**预期结果**: 步骤 2 返回 200，详情 `targetId` 为活动 B 且 `relatedTitle` 变为活动 B 的标题；步骤 3 返回 200——活动 A 的唯一位随 targetId 改动而释放
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-046/`
**最后更新**: 2026-09-02

### TC-featured-IT-044: PUT /api/admin/featured-cycle-items/{id} 更新指向已被占用的实体被拒绝
**关联需求**: featured/周期推荐条目管理#更新指向已被占用的实体被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P0
**测试步骤**:
1. 前置：活动 A 有条目 CA，活动 B 有条目 CB（均 type=ACTIVITY，各自唯一）
2. PUT /api/admin/featured-cycle-items/{CA.id}，body：`targetId` 改为活动 B，其余必填合法
3. GET /api/admin/featured-cycle-items/{CA.id} 与 /{CB.id}
**预期结果**: 步骤 2 返回 400 及中文业务错误「该活动已存在周期推荐」，不返回 500；步骤 3 中 CA 的 `targetId` 仍为活动 A、其余字段未被部分写入（更新整体回滚），CB 不受影响
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-044/`
**最后更新**: 2026-09-02

### TC-featured-IT-045: GET /api/admin/featured-cycle-items/page 不传周期返回全部条目
**关联需求**: featured/周期推荐条目管理#不传周期返回全部条目
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1page/get
**来源**: featured-cycle-item-multi-phase-single-target
**优先级**: P1
**测试步骤**:
1. 前置（各关联不同实体）：`phases=["MENSTRUAL"]`、`phases=["FOLLICULAR","LUTEAL"]`、`phases=["OVULATION"]` 的条目各 1 条，sortOrder 分别为 1、0、1
2. GET /api/admin/featured-cycle-items/page（**不带 `phase` 参数**）
**预期结果**: 返回 200；content 含全部 3 条（不做任何周期过滤）；每项均带 `phases` 数组字段（多周期那条为 `["FOLLICULAR","LUTEAL"]`，按枚举声明顺序）；整体按 sortOrder 升序、同 sortOrder 按创建时间倒序排列
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-phase-single-target/TC-featured-IT-045/`
**最后更新**: 2026-09-02
