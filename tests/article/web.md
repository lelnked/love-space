# article WEB 用例

### TC-article-WEB-001: 文章栏目页新增与删除
**关联需求**: article/web 端文章管理页面#栏目管理增删改
**来源**: article-and-featured-feed
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.100.117.79:5173/love-space/signin
**测试步骤**:
1. 登录后台，导航至 /love-space/article-categories
2. 打开新增弹窗表单，填写栏目名称、上传 icon、填写权重后提交
3. 在列表中对刚创建的栏目点击删除
**预期结果**: DataTable 展示 icon/名称/权重/操作列；新增提交后列表即时刷新出现新栏目；删除前出现确认弹窗，确认后列表即时刷新不再含该栏目
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/article-and-featured-feed/TC-article-WEB-001/`
**最后更新**: 2026-08-16

### TC-article-WEB-002: 文章列表展示与上下线开关
**关联需求**: article/web 端文章管理页面#文章列表与上下线
**来源**: article-and-featured-feed
**优先级**: P1
**前置条件**: Manager 已登录；已存在至少一篇文章（含图片、关联栏目）
**测试步骤**:
1. 登录后台，导航至 /love-space/articles
2. 核对 DataTable 列内容
3. 切换某文章的状态开关
**预期结果**: DataTable 展示图片、标题、关联栏目、状态开关与操作列；切换后该行状态即时更新且出现成功提示，刷新页面后状态保持
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/article-and-featured-feed/TC-article-WEB-002/`
**最后更新**: 2026-08-16

### TC-article-WEB-003: 文章表单富文本编辑与栏目多选回显
**关联需求**: article/web 端文章管理页面#文章表单富文本编辑
**来源**: article-and-featured-feed
**优先级**: P1
**前置条件**: Manager 已登录；存在至少两个栏目；文章表单页可打开（新建或编辑）
**测试步骤**:
1. 进入 /love-space/articles，打开文章表单页，填写必填字段（标题、图片），多选两个栏目
2. 在文章内容富文本编辑器中输入一段文本并插入 1 张图片，保存
3. 重新打开该文章的编辑表单页
**预期结果**: 保存成功有提示；重新打开后富文本编辑器回显此前录入的文本与图片（图片正常渲染），栏目多选回显为此前勾选的两个栏目
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/article-and-featured-feed/TC-article-WEB-003/`
**最后更新**: 2026-08-16
