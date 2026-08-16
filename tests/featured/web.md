# featured WEB 用例

### TC-featured-WEB-001: 精选推荐列表展示与上下线开关
**关联需求**: featured/web 端精选推荐页面#精选推荐列表与上下线
**来源**: article-and-featured-feed
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.100.117.79:5173/love-space/signin；已存在至少一个推荐条目（含 banner、关联城市、说明）
**测试步骤**:
1. 登录后台，导航至 /love-space/featured-items
2. 核对 DataTable 列内容
3. 切换某条目的状态开关
**预期结果**: DataTable 展示 banner 图、关联城市、推荐说明、状态开关与操作列；切换后该行状态即时更新且出现成功提示，刷新页面后状态保持
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/article-and-featured-feed/TC-featured-WEB-001/`
**最后更新**: 2026-08-16

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
**最后更新**: 2026-08-16
