# manager WEB 用例

### TC-manager-WEB-001: 管理员列表按角色与状态渲染
**关联需求**: manager/web 端管理员管理页面#列表按角色与状态渲染
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 已以 ADMIN（admin / 8@y2eoRLyStM*UVU）登录 http://100.100.117.79:5173/love-space/；系统中存在若干账号，其中至少一个昵称为空
**测试步骤**:
1. 侧栏点击「管理员管理」，进入 /love-space/managers
2. 查看筛选栏与列表表头、各行内容
**预期结果**: 筛选栏含「用户名」「角色」（管理员/成员）「状态」（启用/停用）三项；列表列为用户名、昵称、角色、状态、创建时间、操作；ADMIN 行角色显示「管理员」、MEMBER 行显示「成员」；状态显示启用/停用徽标；昵称为空的行显示 `-`；分页默认每页 20 条
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-WEB-002: 内置 admin 行不显示启停按钮
**关联需求**: manager/web 端管理员管理页面#内置 admin 行不显示启停按钮
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 已以 ADMIN 登录并进入 /love-space/managers；列表中含内置 admin 账号
**测试步骤**:
1. 在筛选栏「用户名」填 `admin` 并查询，定位内置 admin 所在行
2. 查看该行操作列的按钮
3. 对照查看任一 MEMBER 行的操作列
**预期结果**: 内置 admin 行操作列只有「重置密码」按钮，无「停用」/「启用」按钮；MEMBER 行同时有「停用」（或「启用」）与「重置密码」两个按钮
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-WEB-003: 弹窗创建新账号
**关联需求**: manager/web 端管理员管理页面#弹窗创建新账号
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 已以 ADMIN 登录并进入 /love-space/managers；用户名 `web_mgr_new` 尚未被占用
**测试步骤**:
1. 点击「新增管理员」，弹窗以新增模式打开（用户名、密码、昵称均可编辑，按钮文案「创建」）
2. 用户名填 `web_mgr_new`，密码填 `Passw0rd!23`（不少于 8 位），昵称填 `新建成员`
3. 点击「创建」提交
4. 在筛选栏「用户名」填 `web_mgr_new` 查询
**预期结果**: 弹窗关闭并提示创建成功；列表中出现 `web_mgr_new`，角色显示「成员」、状态显示「启用」、昵称显示 `新建成员`
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: 
**最后更新**: 2026-08-21

### TC-manager-WEB-004: 密码不足 8 位前端拦截且不发请求
**关联需求**: manager/web 端管理员管理页面#密码不足 8 位前端拦截
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 已以 ADMIN 登录并进入 /love-space/managers；新增弹窗已打开
**测试步骤**:
1. 用户名填 `web_mgr_short`，密码填 7 位 `Pass1!2`
2. 点击「创建」提交，同时监听网络请求
**预期结果**: 密码字段下方出现字段级提示「密码至少 8 位」；不发出 POST /api/admin/managers 请求；弹窗保持打开，列表无新增行
**状态**: ⬜ 未测试
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: 
**最后更新**: 2026-08-21
