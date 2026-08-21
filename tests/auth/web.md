# auth WEB 用例

### TC-auth-WEB-001: 登录成功跳转地图管理页
**关联需求**: auth/web 端登录页与路由守卫#登录成功进入地图管理
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 前端运行于 http://100.100.117.79:5173/love-space/；admin 账号（admin / 8@y2eoRLyStM*UVU）可用；浏览器 localStorage 已清空
**测试步骤**:
1. 访问 http://100.100.117.79:5173/love-space/signin
2. 「用户名 *」填 `admin`，「密码 *」填 `8@y2eoRLyStM*UVU`
3. 点击「登录」按钮
**预期结果**: 按钮提交中文案为「登录中...」；跳转到 /love-space/cities 地图管理页，顶栏显示当前账号；localStorage 中 `love-space:token` 与 `love-space:user` 均已写入
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-WEB-002: 两字段任一为空时登录按钮禁用
**关联需求**: auth/web 端登录页与路由守卫#两字段任一为空时无法提交
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P1
**前置条件**: 停留在 http://100.100.117.79:5173/love-space/signin，两字段均为空
**测试步骤**:
1. 观察初始状态下「登录」按钮的可用性
2. 「用户名 *」填 `admin`，密码留空，再次观察按钮
3. 清空用户名，仅填密码，再次观察按钮
**预期结果**: 三种情况下「登录」按钮均为禁用态（disabled），无法提交
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-WEB-003: 未登录直接访问后台页面被重定向到登录页
**关联需求**: auth/web 端登录页与路由守卫#未登录访问后台被拦回登录页
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 清空 localStorage（无 `love-space:token` 与 `love-space:user`）
**测试步骤**:
1. 直接访问 http://100.100.117.79:5173/love-space/cities
2. 再直接访问 http://100.100.117.79:5173/love-space/managers
**预期结果**: 两次均重定向到 /love-space/signin，登录页表单可见，后台外壳（侧栏/顶栏）不渲染
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: 
**最后更新**: 2026-08-21

### TC-auth-WEB-004: MEMBER 角色侧栏无「管理员管理」入口
**关联需求**: auth/web 端登录页与路由守卫#非 ADMIN 角色看不到管理员管理入口
**来源**: baseline-auth-manager-banner-log-file
**优先级**: P0
**前置条件**: 存在一个启用的 MEMBER 账号（可先以 admin 登录在管理员管理页创建，如 `web_member` / `Passw0rd!23`）
**测试步骤**:
1. 退出登录，回到 http://100.100.117.79:5173/love-space/signin
2. 以 MEMBER 账号 `web_member` / `Passw0rd!23` 登录
3. 查看左侧导航所有菜单项
**预期结果**: 登录成功进入地图管理页；左侧导航不出现「管理员管理」入口（ADMIN 登录时该入口可见）
**状态**: ✅ 通过
**执行方式**: web-test-runner（@playwright/mcp）
**执行存证**: 
**最后更新**: 2026-08-21
