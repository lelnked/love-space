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
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. 前置：存在一个活动（记其 id 与 title）
3. POST /api/admin/featured-cycle-items，body：phase=MENSTRUAL、type=ACTIVITY、`targetId`（该活动 id）、description「经期慢下来」、note「周末两日」、banner（objectKey）、sortOrder=1
4. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 创建返回 200；详情 phase=MENSTRUAL、type=ACTIVITY、`targetId` 等于该活动 id、relatedTitle 回显该活动标题、description 与 note 原样、banner 为签名 URL（http 开头、非裸 objectKey）、sortOrder=1、online=false（未传时默认下线）；title/subtitle 为 null；响应中不再出现 activityId/routeId/articleId 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-007/`
**最后更新**: 2026-08-28

### TC-featured-IT-008: POST /api/admin/featured-cycle-items 创建路线类周期推荐
**关联需求**: featured/周期推荐条目管理#创建路线类周期推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置：存在一条路线（记其 id 与主标题，路线主标题取值需与下一步手填标题不同）
2. POST /api/admin/featured-cycle-items，body：phase=OVULATION、type=ROUTE、`targetId`（该路线 id）、title「排卵期就该出门」、subtitle「三天两夜」、description「体力最好的几天」、banner（objectKey）
3. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 创建返回 200；详情 type=ROUTE、`targetId` 等于该路线 id、title 与 subtitle 为**手填值**（不等于路线实体的主标题）、description 原样、banner 为签名 URL；note 为 null；响应中不再出现 activityId/routeId/articleId 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-008/`
**最后更新**: 2026-08-28

### TC-featured-IT-009: POST /api/admin/featured-cycle-items 创建文章类周期推荐
**关联需求**: featured/周期推荐条目管理#创建文章类周期推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置：存在一篇文章（记其 id 与 title）
2. POST /api/admin/featured-cycle-items，body：phase=LUTEAL、type=ARTICLE、`targetId`（该文章 id）、title「黄体期生活法」、banner（objectKey）
3. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 创建返回 200；详情 type=ARTICLE、`targetId` 等于该文章 id、relatedTitle 回显该文章标题、title 为提交值、banner 为签名 URL；subtitle/description/note 均为 null；响应中不再出现 activityId/routeId/articleId 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-009/`
**最后更新**: 2026-08-28

### TC-featured-IT-010: POST /api/admin/featured-cycle-items 类型必填项缺失被拒绝
**关联需求**: featured/周期推荐条目管理#缺少类型必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. POST type=ROUTE，body 含合法 `targetId`（路线）/title/description/banner 但缺 subtitle
2. POST type=ACTIVITY，body 含合法 `targetId`（活动）/banner 但缺 description
3. POST type=ARTICLE，body 含合法 `targetId`（文章）/title 但缺 banner
4. POST body 缺 phase（其余合法）
5. POST body 缺 type（其余合法）
**预期结果**: 五次均返回 400，响应 `message` 为中文业务错误；条目均未创建（后续 GET page 计数不变）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-010/`
**最后更新**: 2026-08-28

### TC-featured-IT-011: POST /api/admin/featured-cycle-items 关联实体不存在被拒绝
**关联需求**: featured/周期推荐条目管理#关联实体不存在被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. POST type=ACTIVITY、`targetId` 为不存在的 UUID（其余合法）
2. POST type=ROUTE、`targetId` 为不存在的 UUID（其余合法）
3. POST type=ARTICLE、`targetId` 为不存在的 UUID（其余合法）
4. POST type=ACTIVITY、`targetId` 传一篇**已存在文章**的 id（即 id 存在但不属于该 type 对应的实体表）
**预期结果**: 四次均返回 400，响应 `message` 为中文业务错误并按类型区分「关联活动/路线/文章不存在」；条目均未创建（`targetId` 按 `type` 分派到对应实体表校验，不跨表命中）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-011/`
**最后更新**: 2026-08-28

### TC-featured-IT-012: PUT /api/admin/featured-cycle-items/{id} 周期与类型创建后不可变
**关联需求**: featured/周期推荐条目管理#周期与类型创建后不可变
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 phase=MENSTRUAL、type=ACTIVITY 的条目（记其关联活动 id）
2. PUT /api/admin/featured-cycle-items/{id}，body 传 phase=LUTEAL、type=ARTICLE、title「改名」，同时改 description；`targetId` 与 banner 等必填字段仍按**持久化类型**（ACTIVITY）提供（`targetId` 传合法活动 id）
3. GET /api/admin/featured-cycle-items/{id}
4. 再 PUT 一次，`targetId` 改传一篇合法文章的 id（type 仍传 ARTICLE）
**预期结果**: 步骤 2 更新返回 200；步骤 3 详情 phase 仍为 MENSTRUAL、type 仍为 ACTIVITY（传入值被忽略）、`targetId` 仍为该活动 id、description 已按提交值更新、title 未被写入（仍为 null）；步骤 4 返回 400 中文业务错误「关联活动不存在」——`targetId` 的存在性校验按持久化类型 ACTIVITY 分派，与请求体传入的 type 无关
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-012/`
**最后更新**: 2026-08-28

### TC-featured-IT-013: GET /api/admin/featured-cycle-items/page 按周期过滤并按排序号升序
**关联需求**: featured/周期推荐条目管理#按周期过滤列表
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1page/get
**来源**: featured-cycle-feed
**优先级**: P1
**测试步骤**:
1. 前置：FOLLICULAR 下建 sortOrder 为 2、1、3 的三个条目；MENSTRUAL 下建 1 个条目
2. GET /api/admin/featured-cycle-items/page?phase=FOLLICULAR
3. GET /api/admin/featured-cycle-items/page（不带 phase）
**预期结果**: 第 2 步返回 200，content 仅含 FOLLICULAR 的 3 条且 sortOrder 依次为 1、2、3；第 3 步返回全部 4 条
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-feed/TC-featured-IT-013/`
**最后更新**: 2026-08-20

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
**执行存证**: `test-evidence/featured-cycle-feed/TC-featured-IT-014/`
**最后更新**: 2026-08-20

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
**执行存证**: `test-evidence/featured-cycle-feed/TC-featured-IT-015/`
**最后更新**: 2026-08-20

### TC-featured-IT-016: GET /api/app/featured-cycle-items 扁平数组带 period 周期数组且只含上线条目
**关联需求**: featured/App 端周期推荐查询#查询四个周期的推荐列表
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据）：MENSTRUAL 下 1 个上线 ACTIVITY 条目（活动上线、其城市上架）、OVULATION 下 1 个上线 ARTICLE 条目（文章上线）、LUTEAL 下 1 个**下线**条目、FOLLICULAR 下无条目
2. GET http://localhost:8081/api/app/featured-cycle-items（不带参数，请求头带 X-API-Key）
**预期结果**: 返回 200；响应顶层为 JSON 数组（不是按周期分组的对象，无 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL 键）；数组恰含 2 条：一条 `period` 为数组 `["MENSTRUAL"]`、type=ACTIVITY、`targetId` 等于该活动 id，一条 `period` 为数组 `["OVULATION"]`、type=ARTICLE、`targetId` 等于该文章 id（`period` 为 JSON 数组而非字符串；响应中不再出现 activityId/routeId/articleId 字段）；每条含 banner 签名 URL（http 开头、非裸 objectKey）；不含 LUTEAL 的下线条目
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-016/`
**最后更新**: 2026-08-28

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
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-017/`
**最后更新**: 2026-08-28

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
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-018/`
**最后更新**: 2026-08-28

### TC-featured-IT-019: GET /api/app/featured-cycle-items 按排序号升序
**关联需求**: featured/App 端周期推荐查询#组内按排序号升序
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P1
**测试步骤**:
1. 前置：MENSTRUAL 下按 sortOrder 2、1、3 的顺序创建三个上线条目（关联实体均可见），另建两个 sortOrder 同为 1 的条目
2. GET http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL（请求头带 X-API-Key）
**预期结果**: 返回 200；数组内 5 条条目 `period` 均为 `["MENSTRUAL"]`（各条目只配在经期），按 sortOrder 1、1、1、2、3 升序排列；三个 sortOrder=1 的条目按 createdAt 倒序（后创建的在前）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-019/`
**最后更新**: 2026-08-28

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
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-020/`
**最后更新**: 2026-08-28

### TC-featured-IT-021: GET /api/app/featured-cycle-items?type= 按内容类型过滤
**关联需求**: featured/App 端周期推荐查询#按内容类型过滤
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据）：MENSTRUAL 下各建 1 个上线条目，类型分别为 ACTIVITY（活动上线、城市上架）、ROUTE（大使上线）、ARTICLE（文章上线）
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ARTICLE（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/featured-cycle-items（不带参数，同一批数据）
**预期结果**: 步骤 2 返回 200，数组仅含该 ARTICLE 条目（`period` 为 `["MENSTRUAL"]`、type=ARTICLE、`targetId` 等于该文章 id，响应无 articleId 字段），不含 ACTIVITY/ROUTE 条目；步骤 3 返回 200 且数组含全部 3 条（不传 type 行为不变）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-021/`
**最后更新**: 2026-08-28

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
**执行存证**: `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-022/`
**最后更新**: 2026-08-25

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
**执行存证**: `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-023/`
**最后更新**: 2026-08-25

### TC-featured-IT-024: GET /api/app/featured-cycle-items?period= 按周期过滤
**关联需求**: featured/App 端周期推荐查询#按周期过滤
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据）：MENSTRUAL 下建 2 个上线条目（ACTIVITY 活动上线且城市上架、ARTICLE 文章上线），FOLLICULAR 下建 1 个上线 ARTICLE 条目；三条各关联不同 target，且每个 target 只被配在这一个周期
2. GET http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/featured-cycle-items?period=FOLLICULAR（请求头带 X-API-Key）
**预期结果**: 步骤 2 返回 200，数组恰含 2 条且每条 `period` 为 `["MENSTRUAL"]`（三个 target 各自只配在一个周期），不含 FOLLICULAR 条目；步骤 3 返回 200，数组恰含 1 条且 `period` 为 `["FOLLICULAR"]`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-024/`
**最后更新**: 2026-08-28

### TC-featured-IT-025: GET /api/app/featured-cycle-items?period=&type= 周期与类型同时过滤
**关联需求**: featured/App 端周期推荐查询#周期与类型同时过滤
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据）：MENSTRUAL 下建 ACTIVITY（活动上线且城市上架）、ARTICLE（文章上线）各 1 个上线条目；FOLLICULAR 下建 1 个上线 ARTICLE 条目
2. GET http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL&type=ARTICLE（请求头带 X-API-Key）
**预期结果**: 返回 200，数组恰含 1 条：`period` 为 `["MENSTRUAL"]`（该文章只配在经期）、type=ARTICLE、`targetId` 等于该文章 id（响应无 articleId 字段）；不含 MENSTRUAL 的 ACTIVITY 条目，也不含 FOLLICULAR 的 ARTICLE 条目
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-025/`
**最后更新**: 2026-08-28

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
**执行存证**: `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-026/`
**最后更新**: 2026-08-25

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
**执行存证**: `test-evidence/app-article-optional-category-and-featured-period-filter/TC-featured-IT-027/`
**最后更新**: 2026-08-25

### TC-featured-IT-028: GET /api/app/featured-cycle-items 同一 target 跨周期时两条均下发全部周期
**关联需求**: featured/App 端周期推荐查询#同一 target 跨周期时下发全部周期
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据）：创建 1 个上线活动 A；在 MENSTRUAL 与 LUTEAL 下各建 1 个上线 ACTIVITY 条目，均关联活动 A，两条的 banner 与主标题填不同值
2. GET http://localhost:8081/api/app/featured-cycle-items（不带参数，请求头带 X-API-Key）
**预期结果**: 返回 200；数组含这两条条目（不去重、不合并，条目粒度不变）；两条的 `period` 均为数组 `["MENSTRUAL","LUTEAL"]`（元素顺序按 Period 枚举声明顺序 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL，去重无重复项）；两条的 banner 签名 URL 与主标题分别等于各自条目的配置（互不串写）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-028/`
**最后更新**: 2026-08-28

### TC-featured-IT-029: GET /api/app/featured-cycle-items?period= 过滤后 period 数组仍含其他周期
**关联需求**: featured/App 端周期推荐查询#按周期过滤时 period 数组仍含其他周期
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置：同 TC-featured-IT-028（活动 A 在 MENSTRUAL 与 LUTEAL 下各 1 个上线条目）
2. GET http://localhost:8081/api/app/featured-cycle-items?period=LUTEAL（请求头带 X-API-Key）
**预期结果**: 返回 200；数组恰含 1 条（黄体期那条，过滤仍按条目自身持久化的所属周期，不按 `period` 数组）；该条 `period` 为 `["MENSTRUAL","LUTEAL"]`，未被查询参数收窄为 `["LUTEAL"]`
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-029/`
**最后更新**: 2026-08-28

### TC-featured-IT-030: GET /api/app/featured-cycle-items?type=&period= 类型过滤不影响 period 数组
**关联需求**: featured/App 端周期推荐查询#类型过滤不影响 period 数组
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P1
**测试步骤**:
1. 前置：同 TC-featured-IT-028（活动 A 在 MENSTRUAL 与 LUTEAL 下各 1 个上线条目）
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ACTIVITY&period=MENSTRUAL（请求头带 X-API-Key）
**预期结果**: 返回 200；数组恰含 1 条（经期那条）；该条 `period` 为 `["MENSTRUAL","LUTEAL"]`，`type=ACTIVITY`；聚合范围不受 `type` / `period` 参数影响
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-030/`
**最后更新**: 2026-08-28

### TC-featured-IT-031: GET /api/app/featured-cycle-items 不可下发条目不贡献周期
**关联需求**: featured/App 端周期推荐查询#不可下发条目不贡献周期
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. 前置：创建 1 个上线活动 A；MENSTRUAL 下建 1 个**上线** ACTIVITY 条目关联 A，LUTEAL 下建 1 个**下线** ACTIVITY 条目关联 A
2. GET http://localhost:8081/api/app/featured-cycle-items（不带参数，请求头带 X-API-Key）
3. admin 端将 LUTEAL 那条上线，再次 GET（同一请求）
4. 将活动 A 下线，再次 GET
**预期结果**: 步骤 2 返回 200，数组仅含经期那条，其 `period` 为 `["MENSTRUAL"]`（下线条目所属的 LUTEAL 不计入）；步骤 3 数组含两条且两条 `period` 均为 `["MENSTRUAL","LUTEAL"]`（标签随可见性变化）；步骤 4 数组不含 A 的任何条目，接口仍返回 200
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-031/`
**最后更新**: 2026-08-28

### TC-featured-IT-032: GET /api/app/featured-cycle-items?period= 不同 target 的周期集合互不影响
**关联需求**: featured/App 端周期推荐查询#不同 target 的周期集合互不影响
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P1
**测试步骤**:
1. 前置：创建上线活动 A、上线活动 B；A 在 MENSTRUAL 与 LUTEAL 下各 1 个上线条目，B 仅在 MENSTRUAL 下 1 个上线条目
2. GET http://localhost:8081/api/app/featured-cycle-items?period=MENSTRUAL（请求头带 X-API-Key）
**预期结果**: 返回 200；数组恰含 2 条；关联 A 的条目 `period` 为 `["MENSTRUAL","LUTEAL"]`，关联 B 的条目 `period` 为 `["MENSTRUAL"]`（按 `(type, targetId)` 二元组分组，A 的周期不外溢到 B）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-032/`
**最后更新**: 2026-08-28

### TC-featured-IT-033: POST /api/admin/featured-cycle-items 缺 targetId 被拒绝
**关联需求**: featured/周期推荐条目管理#缺少 targetId 被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post（⚠️ 待补契约：`FeaturedCycleItemUpsertRequest` schema 仍为 activityId/routeId/articleId 三字段，`targetId` 与其必填约束需随本 change 同步进契约后才可做 schema 断言）
**来源**: featured-cycle-item-multi-period-tags
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/featured-cycle-items，body：phase=MENSTRUAL、type=ACTIVITY、description、banner，**不带 `targetId`**
3. POST /api/admin/featured-cycle-items，body：phase=OVULATION、type=ROUTE、title、subtitle、description、banner，**不带 `targetId`**
4. POST /api/admin/featured-cycle-items，body：phase=LUTEAL、type=ARTICLE、title、banner，**不带 `targetId`**
5. PUT /api/admin/featured-cycle-items/{id}（已存在条目），body 其余合法但 `targetId` 传 null
6. GET /api/admin/featured-cycle-items/page 核对总数与被更新条目详情
**预期结果**: 步骤 2~5 均返回 400，响应 `message` 为中文业务错误（关联实体不能为空口径），不返回 500；步骤 6 分页总数不变（三次创建均未落库），被 PUT 的条目 `targetId` 与其余字段保持原值未被清空
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/featured-cycle-item-multi-period-tags/TC-featured-IT-033/`
**最后更新**: 2026-08-28

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
**执行存证**: `test-evidence/activity-subtitle/TC-featured-IT-034/`
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
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-035/`
**最后更新**: -

### TC-featured-IT-036: GET /api/app/featured-cycle-items 文章类条目下发文章基础信息
**关联需求**: featured/App 端周期推荐查询#文章类条目下发文章基础信息
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-item-target-basic-info
**优先级**: P0
**测试步骤**:
1. 前置：创建上线文章（含标题、封面标题 coverTitle、封面图 image），在 LUTEAL 下创建 1 个上线 ARTICLE 条目
2. GET http://localhost:8081/api/app/featured-cycle-items?type=ARTICLE（请求头带 X-API-Key）
**预期结果**: 返回 200；`target.id` = 文章 id，`target.title` = 文章标题，`target.coverTitle` = 文章封面标题，`target.image` 为签名 URL 对象
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-036/`
**最后更新**: -

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
**执行存证**: `test-evidence/activity-subtitle/TC-featured-IT-037/`
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
**执行存证**: `test-evidence/activity-subtitle/TC-featured-IT-038/`
**最后更新**: 2026-09-02
