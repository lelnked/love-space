# activity WEB 用例

### TC-activity-WEB-001: 活动列表展示与上下线开关
**关联需求**: activity/web 端活动管理页面#活动列表与上下线
**来源**: ambassador-route-activity
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.93.172.18:5173/love-space/signin；已存在至少一个上线活动（含图片、所属城市、级别）
**测试步骤**:
1. 登录后台，导航至 /love-space/activities
2. 核对 DataTable 列内容
3. 切换某上线活动的状态开关为下线
**预期结果**: DataTable 展示图片、标题、所属城市、级别、状态开关与操作列；切换后该行状态即时变为下线且出现成功提示，刷新页面后状态保持
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-WEB-001/`
**最后更新**: 2026-08-19

### TC-activity-WEB-002: 活动表单富文本编辑并回显
**关联需求**: activity/web 端活动管理页面#活动表单富文本编辑
**来源**: ambassador-route-activity
**优先级**: P1
**前置条件**: Manager 已登录；存在上架城市；活动表单可打开（新建或编辑）
**测试步骤**:
1. 进入 /love-space/activities，打开活动表单，填写必填字段（城市、标题、图片），勾选周期多选与级别单选，添加 1 条路线子条目
2. 在「活动详情说明」富文本编辑器中输入一段文本并插入 1 张图片，保存
3. 重新打开该活动的编辑表单
**预期结果**: 保存成功有提示；重新打开后富文本编辑器回显此前录入的文本与图片（图片正常渲染），周期/级别/路线子条目与录入一致
**状态**: ⚠️ 环境阻塞
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/ambassador-route-activity/TC-activity-WEB-002/`
**阻塞说明**: 本地 OSS 仅配占位符，后端要求图片 objectKey 格式为 `images/<id>.<ext>`；前端保存前校验“至少 1 张图片”失败，富文本保存/回显链路无法完成。
**最后更新**: 2026-08-19
