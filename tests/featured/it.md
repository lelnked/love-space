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
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-001/`
**最后更新**: 2026-08-16

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
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-002/`
**最后更新**: 2026-08-16

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
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-003/`
**最后更新**: 2026-08-16

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
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-004/`
**最后更新**: 2026-08-16

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
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-005/`
**最后更新**: 2026-08-16

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
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-IT-006/`
**最后更新**: 2026-08-16

### TC-featured-IT-007: POST /api/admin/featured-cycle-items 创建活动类周期推荐
**关联需求**: featured/周期推荐条目管理#创建活动类周期推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. 前置：存在一个活动（记其 id 与 title）
3. POST /api/admin/featured-cycle-items，body：phase=MENSTRUAL、type=ACTIVITY、activityId（该活动）、description「经期慢下来」、note「周末两日」、banner（objectKey）、sortOrder=1
4. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 创建返回 200；详情 phase=MENSTRUAL、type=ACTIVITY、activityId 为该活动、关联活动标题回显、description 与 note 原样、banner 为签名 URL（http 开头、非裸 objectKey）、sortOrder=1、online=false（未传时默认下线）；routeId/articleId/title/subtitle 均为 null
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-007/`
**最后更新**: -

### TC-featured-IT-008: POST /api/admin/featured-cycle-items 创建路线类周期推荐
**关联需求**: featured/周期推荐条目管理#创建路线类周期推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在一条路线（记其 id 与主标题，路线主标题取值需与下一步手填标题不同）
2. POST /api/admin/featured-cycle-items，body：phase=OVULATION、type=ROUTE、routeId（该路线）、title「排卵期就该出门」、subtitle「三天两夜」、description「体力最好的几天」、banner（objectKey）
3. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 创建返回 200；详情 type=ROUTE、routeId 为该路线、title 与 subtitle 为**手填值**（不等于路线实体的主标题）、description 原样、banner 为签名 URL；activityId/articleId/note 均为 null
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-008/`
**最后更新**: -

### TC-featured-IT-009: POST /api/admin/featured-cycle-items 创建文章类周期推荐
**关联需求**: featured/周期推荐条目管理#创建文章类周期推荐
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在一篇文章（记其 id 与 title）
2. POST /api/admin/featured-cycle-items，body：phase=LUTEAL、type=ARTICLE、articleId（该文章）、title「黄体期生活法」、banner（objectKey）
3. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 创建返回 200；详情 type=ARTICLE、articleId 为该文章、关联文章标题回显、title 为提交值、banner 为签名 URL；activityId/routeId/subtitle/description/note 均为 null
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-009/`
**最后更新**: -

### TC-featured-IT-010: POST /api/admin/featured-cycle-items 类型必填项缺失被拒绝
**关联需求**: featured/周期推荐条目管理#缺少类型必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. POST type=ROUTE，body 含合法 routeId/title/description/banner 但缺 subtitle
2. POST type=ACTIVITY，body 含合法 activityId/banner 但缺 description
3. POST type=ARTICLE，body 含合法 articleId/title 但缺 banner
4. POST body 缺 phase（其余合法）
5. POST body 缺 type（其余合法）
**预期结果**: 五次均返回 400，响应 `message` 为中文业务错误；条目均未创建（后续 GET page 计数不变）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-010/`
**最后更新**: -

### TC-featured-IT-011: POST /api/admin/featured-cycle-items 关联实体不存在被拒绝
**关联需求**: featured/周期推荐条目管理#关联实体不存在被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items/post
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. POST type=ACTIVITY、activityId 为不存在的 UUID（其余合法）
2. POST type=ROUTE、routeId 为不存在的 UUID（其余合法）
3. POST type=ARTICLE、articleId 为不存在的 UUID（其余合法）
**预期结果**: 三次均返回 400，响应 `message` 为中文业务错误并指出关联实体不存在；条目均未创建
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-011/`
**最后更新**: -

### TC-featured-IT-012: PUT /api/admin/featured-cycle-items/{id} 周期与类型创建后不可变
**关联需求**: featured/周期推荐条目管理#周期与类型创建后不可变
**关联契约**: api-spec.json#/paths/~1api~1admin~1featured-cycle-items~1{id}/put
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在一个 phase=MENSTRUAL、type=ACTIVITY 的条目
2. PUT /api/admin/featured-cycle-items/{id}，body 传 phase=LUTEAL、type=ARTICLE、articleId（合法文章）、title「改名」，同时改 description
3. GET /api/admin/featured-cycle-items/{id}
**预期结果**: 更新返回 200；详情 phase 仍为 MENSTRUAL、type 仍为 ACTIVITY（传入值被忽略）；description 已按提交值更新；articleId 与 title 未被写入（仍为 null）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-012/`
**最后更新**: -

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
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-013/`
**最后更新**: -

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
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-014/`
**最后更新**: -

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
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-015/`
**最后更新**: -

### TC-featured-IT-016: GET /api/app/featured-cycle-items 四周期分组齐全且只含上线条目
**关联需求**: featured/App 端周期推荐查询#查询四个周期的推荐列表
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据）：MENSTRUAL 下 1 个上线 ACTIVITY 条目（活动上线、其城市上架）、OVULATION 下 1 个上线 ARTICLE 条目（文章上线）、LUTEAL 下 1 个**下线**条目、FOLLICULAR 下无条目
2. GET /api/app/featured-cycle-items（带 API-key 请求头）
**预期结果**: 返回 200；响应含 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL 四个键（FOLLICULAR 为空数组，不缺键）；MENSTRUAL、OVULATION 各 1 条且含 type、banner 签名 URL 与关联实体 id；LUTEAL 为空数组（下线条目不下发）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-016/`
**最后更新**: -

### TC-featured-IT-017: GET /api/app/featured-cycle-items 关联实体不可见时条目不下发
**关联需求**: featured/App 端周期推荐查询#关联实体不可见时条目不下发
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. 前置：MENSTRUAL 下 1 个上线 ACTIVITY 条目（活动上线、城市上架）、1 个上线 ARTICLE 条目（文章上线）；GET app 接口确认两条均在
2. admin 端将该活动下线，GET app 接口
3. 恢复活动上线、改将其所属城市下架，GET app 接口
4. 恢复城市上架、改将该文章下线，GET app 接口
5. 恢复文章上线，admin 端删除该文章，GET app 接口
**预期结果**: 步骤 2、3 该 ACTIVITY 条目从 MENSTRUAL 分组消失；步骤 4、5 该 ARTICLE 条目消失；每步中未受影响的另一条仍在；接口全程返回 200，不因关联实体缺失报 500
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-017/`
**最后更新**: -

### TC-featured-IT-018: GET /api/app/featured-cycle-items 大使下线连带隐藏路线类条目
**关联需求**: featured/App 端周期推荐查询#大使下线连带隐藏路线类条目
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在上架城市下的一条路线，其爱女大使 online=true；OVULATION 下建 1 个上线 ROUTE 条目关联该路线
2. GET /api/app/featured-cycle-items，确认该条目在 OVULATION 分组
3. admin 端将该大使下线，GET /api/app/featured-cycle-items
4. 恢复大使上线、改将路线所属城市下架，GET /api/app/featured-cycle-items
**预期结果**: 步骤 2 该条目存在；步骤 3、4 该条目均从 OVULATION 分组消失；接口返回 200
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-018/`
**最后更新**: -

### TC-featured-IT-019: GET /api/app/featured-cycle-items 组内按排序号升序
**关联需求**: featured/App 端周期推荐查询#组内按排序号升序
**关联契约**: api-spec.json#/paths/~1api~1app~1featured-cycle-items/get
**来源**: featured-cycle-feed
**优先级**: P1
**测试步骤**:
1. 前置：MENSTRUAL 下按 sortOrder 2、1、3 的顺序创建三个上线条目（关联实体均可见），另建两个 sortOrder 同为 1 的条目
2. GET /api/app/featured-cycle-items
**预期结果**: 返回 200；MENSTRUAL 分组内条目按 sortOrder 1、1、2、3 升序排列；两个 sortOrder=1 的条目按 createdAt 倒序（后创建的在前）
**状态**: ⬜ 未测试
**执行方式**: api-test-runner
**执行存证**: `test-evidence/regression/featured/TC-featured-IT-019/`
**最后更新**: -
