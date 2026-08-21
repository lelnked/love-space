# featured WEB 用例

### TC-featured-WEB-001: 精选推荐列表展示与上下线开关
**关联需求**: featured/web 端精选推荐页面#精选推荐列表与上下线
**来源**: article-and-featured-feed
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.93.172.18:5173/love-space/signin；已存在至少一个推荐条目（含 banner、关联城市、说明）
**测试步骤**:
1. 登录后台，导航至 /love-space/featured-items
2. 核对 DataTable 列内容
3. 切换某条目的状态开关
**预期结果**: DataTable 展示 banner 图、关联城市、推荐说明、状态开关与操作列；切换后该行状态即时更新且出现成功提示，刷新页面后状态保持
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-WEB-001/`
**最后更新**: 2026-08-19

### TC-featured-WEB-002: 弹窗表单新增精选推荐
**关联需求**: featured/web 端精选推荐页面#新增精选推荐
**来源**: article-and-featured-feed
**优先级**: P1
**前置条件**: Manager 已登录；存在至少一个上架城市
**测试步骤**:
1. 进入 /love-space/featured-items，打开新增弹窗表单
2. 单选一个城市、上传 banner 图片、填写推荐说明后提交
**预期结果**: 保存成功有提示；列表即时刷新出现新条目，展示所选城市与填写的说明
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-WEB-002/`
**最后更新**: 2026-08-19

### TC-featured-WEB-003: 周期推荐页四周期 Tab 切换与列表展示
**关联需求**: featured/web 端周期推荐页面#周期 Tab 切换与列表展示
**来源**: featured-cycle-feed
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.93.172.18:5173/love-space/signin；经期下有 2 个条目、卵泡期下有 1 个条目（sortOrder 不同）、排卵期下无条目
**测试步骤**:
1. 登录后台，导航至 /love-space/featured-cycle-items
2. 核对默认 Tab 下 DataTable 的列内容
3. 点击「卵泡期」Tab
4. 点击「排卵期」Tab
**预期结果**: 顶部展示经期/卵泡期/排卵期/黄体期四个 Tab；列表区列含 banner 缩略图、内容类型徽标、标题、关联实体名、排序号、状态开关与操作列，行按排序号升序；切到卵泡期后仅展示该周期的 1 条；切到排卵期展示空态文案「该周期暂无推荐」而非空白或报错
**状态**: ⚠️ 环境阻塞
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-003/`
**阻塞说明**: 后端 `/api/admin/featured-cycle-items/page` 返回 500，周期推荐列表接口异常；Tab 切换与列表展示无法验证。
**最后更新**: 2026-08-19

### TC-featured-WEB-004: 新增弹窗按内容类型切换字段块
**关联需求**: featured/web 端周期推荐页面#表单按类型切换字段
**来源**: featured-cycle-feed
**优先级**: P1
**前置条件**: Manager 已登录；库中存在至少一个活动、一条路线、一篇文章
**测试步骤**:
1. 进入 /love-space/featured-cycle-items，打开新增弹窗
2. 内容类型选「tripperclub活动」，核对字段块
3. 内容类型切到「路线体验」，核对字段块
4. 内容类型切到「周期生活法」，核对字段块
**预期结果**: 弹窗顶部固定展示内容类型选择器、banner 上传与排序号；选活动时字段块为 活动下拉 + 推荐说明 + 活动说明（标注选填）；切到路线体验后活动专属字段消失，出现 路线下拉 + 主标题 + 副标题 + 推荐说明；切到周期生活法后仅剩 文章下拉 + 主标题；每次切换下方字段块内容被清空，不残留上一类型已填内容
**状态**: ⚠️ 环境阻塞
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-004/`
**阻塞说明**: 后端 `/api/admin/featured-cycle-items/page` 返回 500，新增周期推荐表单依赖的服务端列表/选项异常，无法完成内容类型切换校验。
**最后更新**: 2026-08-19

### TC-featured-WEB-005: 周期生活法选中文章后自动带出主标题
**关联需求**: featured/web 端周期推荐页面#文章类型自动带出主标题
**来源**: featured-cycle-feed
**优先级**: P1
**前置条件**: Manager 已登录；库中存在至少一篇标题可识别的文章
**测试步骤**:
1. 进入 /love-space/featured-cycle-items，打开新增弹窗
2. 内容类型选「周期生活法」
3. 在文章下拉中选中一篇文章
4. 在主标题输入框末尾追加文字
**预期结果**: 选中文章后主标题输入框自动填入该文章标题；输入框仍可编辑，追加文字生效且不被下拉选择重置
**状态**: ⚠️ 环境阻塞
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-005/`
**阻塞说明**: 后端 `/api/admin/featured-cycle-items/page` 返回 500，文章下拉选项依赖服务端正常响应，自动带出主标题无法验证。
**最后更新**: 2026-08-19

### TC-featured-WEB-006: 弹窗表单新增周期推荐
**关联需求**: featured/web 端周期推荐页面#新增周期推荐
**来源**: featured-cycle-feed
**优先级**: P1
**前置条件**: Manager 已登录；库中存在至少一个活动
**测试步骤**:
1. 进入 /love-space/featured-cycle-items，停留在「经期」Tab，打开新增弹窗
2. 内容类型选「tripperclub活动」，选一个活动，填推荐说明，上传 banner，填排序号后提交
**预期结果**: 保存成功有提示；弹窗关闭，经期列表即时刷新出现新条目，展示所上传 banner、类型徽标「tripperclub活动」、所选活动名与填写的排序号
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-006/`
**阻塞说明**: 无阻塞。已通过 Playwright 完成新增（活动类型、banner 上传、推荐说明）并在列表页验证数据回显；新条目默认状态为「已下线」。
**最后更新**: 2026-08-21

### TC-featured-WEB-007: 周期推荐上下线切换与删除确认
**关联需求**: featured/web 端周期推荐页面#周期推荐上下线与删除
**来源**: featured-cycle-feed
**优先级**: P1
**前置条件**: Manager 已登录；经期下至少存在 2 个条目
**测试步骤**:
1. 进入 /love-space/featured-cycle-items 的「经期」Tab
2. 切换第一个条目的状态开关
3. 刷新页面核对状态
4. 对第二个条目点击删除，在确认弹窗中确认
**预期结果**: 切换后该行状态即时更新并出现成功提示，刷新后状态保持；删除需经确认弹窗二次确认，确认后该条目从列表消失并出现成功提示
**状态**: ⚠️ 环境阻塞
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/regression/featured/TC-featured-WEB-007/`
**阻塞说明**: 后端 `/api/admin/featured-cycle-items/page` 返回 500，周期推荐列表接口异常，无法进入上下线/删除确认验证。
**最后更新**: 2026-08-19
