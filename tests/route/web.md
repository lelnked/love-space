# route WEB 用例

### TC-route-WEB-001: 大使列表展示与上下线开关
**关联需求**: route/web 端大使与路线管理页面#大使列表与上下线
**来源**: ambassador-route-activity
**优先级**: P1
**前置条件**: Manager 账号可登录 http://100.100.117.79:5173/love-space/signin；已存在至少一个上线大使（含头像与标签）
**测试步骤**:
1. 登录后台，导航至 /love-space/ambassadors
2. 核对 DataTable 列内容
3. 切换某上线大使的状态开关为下线
**预期结果**: DataTable 展示头像、名称、标签、状态开关与操作列；切换后该行状态即时变为下线且出现成功提示，刷新页面后状态保持
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-WEB-001/`
**最后更新**: 2026-08-16

### TC-route-WEB-002: 路线表单维护地点子列表并按添加顺序回显
**关联需求**: route/web 端大使与路线管理页面#路线表单维护地点
**来源**: ambassador-route-activity
**优先级**: P1
**前置条件**: Manager 已登录；存在上架城市与上线大使；路线表单可打开（新建或编辑）
**测试步骤**:
1. 进入 /love-space/routes，打开路线表单，填写基础必填字段（城市、主标题、缩略图、路线图片、大使）
2. 在地点子列表依次添加 2 个地点（各填名称、图片、介绍，顺序为 S1、S2）并保存
3. 重新打开该路线的编辑表单
**预期结果**: 保存成功有提示；重新打开后地点子列表按 S1→S2 添加顺序回显，各项名称/图片/介绍与录入一致
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-WEB-002/`
**最后更新**: 2026-08-16

### TC-route-WEB-003: 删除路线需确认（确认删除、取消保留）
**关联需求**: route/web 端大使与路线管理页面#删除路线需确认
**来源**: ambassador-route-activity
**优先级**: P1
**前置条件**: Manager 已登录；/love-space/routes 列表中存在至少一条路线
**测试步骤**:
1. 对路线 X 点击删除，在确认弹窗点「取消」
2. 对路线 X 再次点击删除，在确认弹窗点「确认」
**预期结果**: 步骤 1 后弹窗关闭、路线 X 仍在列表中；步骤 2 后路线 X 从 DataTable 消失，列表行数减 1
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: `test-evidence/ambassador-route-activity/TC-route-WEB-003/`
**最后更新**: 2026-08-16
