# article IT 用例

### TC-article-IT-001: POST /api/admin/article-categories 创建栏目
**关联需求**: article/文章栏目管理#创建栏目
**关联契约**: api-spec.json#/paths/~1api~1admin~1article-categories/post
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. POST /api/admin/article-categories，body：name「行程攻略」、icon（images/ 前缀 objectKey）、sortOrder=1
3. GET /api/admin/article-categories 确认列表含该栏目
**预期结果**: 创建返回 200，详情含 name、icon（签名 URL，http 开头、非裸 objectKey）、sortOrder=1；列表能查到该栏目
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-001/`
**最后更新**: 2026-08-25

### TC-article-IT-002: POST /api/admin/article-categories 缺必填被拒绝
**关联需求**: article/文章栏目管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1article-categories/post
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. POST /api/admin/article-categories，body 缺 name（icon、sortOrder 合法）
2. POST /api/admin/article-categories，body 缺 icon（name、sortOrder 合法）
**预期结果**: 两次均返回 400，响应 `message` 为中文业务错误；栏目均未创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-002/`
**最后更新**: 2026-08-25

### TC-article-IT-003: PUT /api/admin/article-categories/{id} 更新栏目
**关联需求**: article/文章栏目管理#创建栏目
**关联契约**: api-spec.json#/paths/~1api~1admin~1article-categories~1{id}/put
**来源**: article-and-featured-feed
**优先级**: P1
**测试步骤**:
1. 前置：已存在一个栏目（name「行程攻略」、sortOrder=1）
2. PUT /api/admin/article-categories/{id}，body：name 改「美食攻略」、icon 换新 objectKey、sortOrder=5
3. GET /api/admin/article-categories
**预期结果**: 更新返回 200；列表中该栏目 name=「美食攻略」、sortOrder=5，icon 为新图的签名 URL
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-003/`
**最后更新**: 2026-08-25

### TC-article-IT-004: DELETE /api/admin/article-categories/{id} 删除栏目不影响文章数据
**关联需求**: article/文章栏目管理#删除栏目不影响文章数据
**关联契约**: api-spec.json#/paths/~1api~1admin~1article-categories~1{id}/delete
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在栏目 A、B，一篇文章同时关联 A、B
2. DELETE /api/admin/article-categories/{A 的 id}
3. GET /api/admin/article-categories 确认栏目 A 已不在列表
4. GET /api/admin/articles/{articleId} 查文章详情
**预期结果**: 删除返回 200（物理删除）；文章记录仍存在，其关联栏目列表仅剩 B（不再含 A）；文章存储的 categoryIds 不被回写（详情按「仍存在的栏目」过滤展示）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-004/`
**最后更新**: 2026-08-25

### TC-article-IT-005: POST /api/admin/articles 创建关联多栏目的完整文章
**关联需求**: article/文章管理#创建文章
**关联契约**: api-spec.json#/paths/~1api~1admin~1articles/post
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在栏目 A、B
2. POST /api/admin/articles，body：image（objectKey）、title「海岛两日游」、subtitle「附完整行程」、contentHtml（含段落文本的 HTML）、sortOrder=1、categoryIds=[A,B]、online=true
3. GET /api/admin/articles/{id}
**预期结果**: 创建返回 200；详情 image 为签名 URL、title/subtitle/sortOrder/online 与提交一致、关联栏目为 A、B 两个、contentHtml 文本内容原样保存
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-005/`
**最后更新**: 2026-08-25

### TC-article-IT-006: POST /api/admin/articles 缺必填或栏目不存在被拒绝
**关联需求**: article/文章管理#缺少必填项被拒绝
**关联契约**: api-spec.json#/paths/~1api~1admin~1articles/post
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. POST /api/admin/articles，body 缺 title（其余合法）
2. POST /api/admin/articles，body 缺 image
3. POST /api/admin/articles，body categoryIds 含不存在的 UUID
**预期结果**: 三次均返回 400，响应 `message` 为中文业务错误；文章均未创建
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-006/`
**最后更新**: 2026-08-25

### TC-article-IT-007: PUT /api/admin/articles/{id} 更新文章与栏目关联
**关联需求**: article/文章管理#创建文章
**关联契约**: api-spec.json#/paths/~1api~1admin~1articles~1{id}/put
**来源**: article-and-featured-feed
**优先级**: P1
**测试步骤**:
1. 前置：存在栏目 A、B，一篇文章关联 [A]
2. PUT /api/admin/articles/{id}，body：title 改名、subtitle 改写、sortOrder=9、categoryIds 改为 [B]
3. GET /api/admin/articles/{id}
**预期结果**: 更新返回 200；详情 title/subtitle/sortOrder 更新生效，关联栏目变为仅 B
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-007/`
**最后更新**: 2026-08-25

### TC-article-IT-008: PUT /api/admin/articles/{id}/online 文章上下线切换
**关联需求**: article/文章管理#文章上下线切换
**关联契约**: api-spec.json#/paths/~1api~1admin~1articles~1{id}~1online/put
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：存在一篇 online=true 的文章
2. PUT /api/admin/articles/{id}/online，body：`{"online": false}`
3. GET /api/admin/articles/{id}
4. PUT /api/admin/articles/{id}/online，body：`{"online": true}` 后再查详情
**预期结果**: 步骤 2 返回 200，步骤 3 详情 online=false；步骤 4 后详情 online=true（可往返切换）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-008/`
**最后更新**: 2026-08-25

### TC-article-IT-009: DELETE /api/admin/articles/{id} 物理删除文章
**关联需求**: article/文章管理#创建文章
**关联契约**: api-spec.json#/paths/~1api~1admin~1articles~1{id}/delete
**来源**: article-and-featured-feed
**优先级**: P1
**测试步骤**:
1. 前置：存在一篇文章
2. DELETE /api/admin/articles/{id}
3. GET /api/admin/articles/{id}
4. GET /api/admin/articles/page 确认分页列表不含该文章
**预期结果**: 删除返回 200；再查详情返回 400 及中文业务错误（admin 端「资源不存在」全局口径）；分页列表不再出现该文章
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-009/`
**最后更新**: 2026-08-25

### TC-article-IT-010: POST /api/admin/articles 富文本 img src 存 objectKey、admin 读时替换签名 URL
**关联需求**: article/文章管理#创建文章
**关联契约**: api-spec.json#/paths/~1api~1admin~1articles/post
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. POST /api/admin/articles，contentHtml 含 2 个 `<img src="<images/ 前缀 objectKey>">` 与段落文本
2. GET /api/admin/articles/{id}
3. PUT /api/admin/articles/{id}，contentHtml 改为不含 img 的纯文本后再 GET 详情
**预期结果**: 创建返回 200；步骤 2 详情 contentHtml 文本部分与提交一致，2 个 img 的 src 均被替换为签名 URL（http 开头、非裸 objectKey），说明存储层保存的是 bound objectKey；步骤 3 无 img 的 HTML 原样往返不报错
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-010/`
**最后更新**: 2026-08-25

### TC-article-IT-011: GET /api/app/article-categories 与 /api/app/articles 均按权重升序
**关联需求**: article/App 端文章查询#查询栏目与文章列表
**关联契约**: api-spec.json#/paths/~1api~1app~1article-categories/get
**来源**: article-cover-title-intro-tags
**优先级**: P0
**测试步骤**:
1. 前置：存在栏目 A（sortOrder=2）、B（sortOrder=1）；栏目 B 下有两篇上线文章（sortOrder=3 与 1）
2. GET http://localhost:8081/api/app/article-categories（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/articles?categoryId={B 的 id}（请求头带 X-API-Key）
**预期结果**: 栏目列表返回 200，按 sortOrder 升序（B 在前），每项含名称与 icon 签名 URL；文章列表返回 200，按 sortOrder 升序（权重 1 在前），每项含图片（签名 URL）、封面标题 coverTitle、标题、副标题、标签 tags
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-011/`
**最后更新**: 2026-08-25

### TC-article-IT-012: GET /api/app/articles 下线文章不可见、详情 404
**关联需求**: article/App 端文章查询#下线文章不可见
**关联契约**: api-spec.json#/paths/~1api~1app~1articles~1{id}/get
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：某栏目下一篇上线文章 app 端列表可见
2. admin 侧 PUT /api/admin/articles/{id}/online 将其下线
3. GET http://localhost:8081/api/app/articles?categoryId={categoryId}（请求头带 X-API-Key）
4. GET http://localhost:8081/api/app/articles/{id}（请求头带 X-API-Key）
**预期结果**: 下线后列表不含该文章；详情返回 404
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-012/`
**最后更新**: 2026-08-25

### TC-article-IT-013: GET /api/app/articles/{id} 失去所有栏目的文章不可见
**关联需求**: article/App 端文章查询#失去所有栏目的文章不可见
**关联契约**: api-spec.json#/paths/~1api~1app~1articles~1{id}/get
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：一篇上线文章仅关联栏目 A，app 端详情返回 200
2. admin 侧 DELETE /api/admin/article-categories/{A 的 id}
3. GET http://localhost:8081/api/app/articles/{id}（请求头带 X-API-Key）
**预期结果**: 删除栏目后文章详情返回 404（可见性 = online ∧ 至少关联一个仍存在的栏目）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-013/`
**最后更新**: 2026-08-25

### TC-article-IT-014: GET /api/app/articles/{id} 详情返回富文本且 img src 为签名 URL
**关联需求**: article/App 端文章查询#文章详情返回富文本
**关联契约**: api-spec.json#/paths/~1api~1app~1articles~1{id}/get
**来源**: article-and-featured-feed
**优先级**: P0
**测试步骤**:
1. 前置：一篇可见文章，contentHtml 后台保存为含图片标签与文本的 HTML
2. GET http://localhost:8081/api/app/articles/{id}（请求头带 X-API-Key）
**预期结果**: 返回 200；含图片、标题、副标题字段；contentHtml 文本与后台保存内容一致，img src 已替换为可访问的签名 URL
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-014/`
**最后更新**: 2026-08-25

### TC-article-IT-015: POST /api/admin/articles 创建带封面标题、引言与标签的文章
**关联需求**: article/文章管理#创建带封面标题、引言与标签的文章
**关联契约**: api-spec.json#/paths/~1api~1admin~1articles/post
**来源**: article-cover-title-intro-tags
**优先级**: P0
**测试步骤**:
1. POST /api/admin/auth/login 获取 JWT token
2. 前置：创建一个栏目备用
3. POST /api/admin/articles，body：image、title「详情页标题」、coverTitle「封面标题」、subtitle「副标题」、intro「这是引言」、tags ["约会","周末"]、categoryIds[该栏目]、online=true
4. GET /api/admin/articles/{id}
**预期结果**: 创建返回 200；详情中 title=「详情页标题」、coverTitle=「封面标题」、subtitle=「副标题」、intro=「这是引言」、tags=["约会","周末"]，五个字段互不覆盖
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-015/`
**最后更新**: 2026-08-25

### TC-article-IT-016: POST /api/admin/articles 省略封面标题、引言、标签
**关联需求**: article/文章管理#封面标题、引言、标签均可省略
**关联契约**: api-spec.json#/paths/~1api~1admin~1articles/post
**来源**: article-cover-title-intro-tags
**优先级**: P0
**测试步骤**:
1. POST /api/admin/articles，body 只含 image、title、categoryIds、online，不带 coverTitle/intro/tags
2. GET /api/admin/articles/{id}
3. GET /api/admin/articles/page 查该文章列表项
**预期结果**: 返回 200；详情与列表项中 coverTitle=null、intro=null、tags=[]（空数组而非 null）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-016/`
**最后更新**: 2026-08-25

### TC-article-IT-017: PUT /api/admin/articles/{id} 空白值按 null 存、标签空白项剔除
**关联需求**: article/文章管理#创建带封面标题、引言与标签的文章
**关联契约**: api-spec.json#/paths/~1api~1admin~1articles~1{id}/put
**来源**: article-cover-title-intro-tags
**优先级**: P1
**测试步骤**:
1. 前置：已存在一篇设置了 coverTitle、intro、tags 的文章
2. PUT /api/admin/articles/{id}，body：coverTitle="  "（纯空格）、intro=" "、tags=[" 甲 ", "", "乙"]
3. GET /api/admin/articles/{id}
**预期结果**: 返回 200；coverTitle=null、intro=null；tags=["甲","乙"]（空白项被剔除、保留项已 trim）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-017/`
**最后更新**: 2026-08-25

### TC-article-IT-018: GET /api/app/articles 未设封面标题时回落文章标题
**关联需求**: article/App 端文章查询#未设封面标题时列表回落文章标题
**关联契约**: api-spec.json#/paths/~1api~1app~1articles/get
**来源**: article-cover-title-intro-tags
**优先级**: P0
**测试步骤**:
1. 前置：同一栏目下两篇上线文章——甲设 coverTitle「封面甲」+ tags ["约会"]，乙不设 coverTitle（title「文章乙」）
2. GET http://localhost:8081/api/app/articles?categoryId={categoryId}（请求头带 X-API-Key）
**预期结果**: 返回 200；甲的 coverTitle=「封面甲」、tags=["约会"]；乙的 coverTitle 回落为「文章乙」、tags=[]；两项均含 image、title、subtitle 字段
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-018/`
**最后更新**: 2026-08-25

### TC-article-IT-019: GET /api/app/articles/{id} 详情返回引言与标签
**关联需求**: article/App 端文章查询#详情返回引言与标签
**关联契约**: api-spec.json#/paths/~1api~1app~1articles~1{id}/get
**来源**: article-cover-title-intro-tags
**优先级**: P0
**测试步骤**:
1. 前置：一篇可见文章设 intro「这是引言」+ tags ["恋爱","指南"]；另一篇可见文章不设 intro 与 tags
2. GET http://localhost:8081/api/app/articles/{设了的 id}（请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/articles/{未设的 id}（请求头带 X-API-Key）
**预期结果**: 前者返回 200，intro=「这是引言」、tags=["恋爱","指南"]；后者返回 200，intro=null、tags=[]
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/article-cover-title-intro-tags/TC-article-IT-019/`
**最后更新**: 2026-08-25

### TC-article-IT-020: GET /api/app/articles 不传 categoryId 返回全部可见文章
**关联需求**: article/App 端文章查询#不传栏目返回全部可见文章
**关联契约**: api-spec.json#/paths/~1api~1app~1articles/get
**来源**: app-article-optional-category-and-featured-period-filter
**优先级**: P0
**测试步骤**:
1. 前置（admin 端建数据）：栏目 A、B、C；文章甲关联 A（sortOrder=2、online=true）、文章乙关联 B（sortOrder=1、online=true）、文章丙关联 A（online=false）、文章丁仅关联 C（online=true）；随后 DELETE /api/admin/article-categories/{C 的 id}
2. GET http://localhost:8081/api/app/articles（不带 categoryId，请求头带 X-API-Key）
3. GET http://localhost:8081/api/app/articles?categoryId={A 的 id}（请求头带 X-API-Key）
**预期结果**: 步骤 2 返回 200，列表恰含甲、乙两篇且乙在前（sortOrder 升序），不含下线的丙与失去所有栏目的丁；每项含 image 签名 URL、coverTitle、title、subtitle、tags；步骤 3 返回 200 且仅含甲（传 categoryId 行为不变）
**状态**: ✅ 通过
**执行方式**: api-test-runner
**执行存证**: `test-evidence/app-article-optional-category-and-featured-period-filter/TC-article-IT-020/`
**最后更新**: 2026-08-25
