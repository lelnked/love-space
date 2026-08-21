# auth Specification

## Purpose
运营账号（Manager）的登录认证与会话：JWT 签发与校验、登录失败统一口径、当前登录人查询与登出、admin 端授权链，以及 web 端登录页与路由守卫。
## Requirements
### Requirement: 运营账号登录
admin 端 SHALL 提供 `POST /api/admin/auth/login`（免认证），接受 `{username, password}`（均 `@NotBlank`），校验通过后签发 JWT。响应顶层字段为 `manager`（**不是 `user`**）：`{token, manager:{id, username, nickname, role}}`。

登录失败 SHALL 统一返回 401 与同一条中文消息「用户名或密码错误，或账号已停用」——用户名不存在、密码错误、账号 `enable=false` 三种原因合并，防止账号枚举。

登录接口 SHALL NOT 记录操作日志（此时 SecurityContext 尚未填充，审计切面取不到操作人）。

#### Scenario: 内置管理员登录成功
- **GIVEN** 种子账号 admin（role=ADMIN、enable=true）存在
- **WHEN** 以正确密码提交登录
- **THEN** 返回 200，`token` 非空，`manager.username` 为 admin、`manager.role` 为 ADMIN

#### Scenario: 密码错误被拒绝
- **GIVEN** 一个存在且启用的账号
- **WHEN** 以错误密码提交登录
- **THEN** 返回 401 及消息「用户名或密码错误，或账号已停用」

#### Scenario: 停用账号无法登录
- **GIVEN** 一个 `enable=false` 的账号
- **WHEN** 以**正确**密码提交登录
- **THEN** 返回 401 及与密码错误完全相同的消息

#### Scenario: 用户名不存在与密码错误不可区分
- **GIVEN** 一个不存在的用户名
- **WHEN** 提交登录
- **THEN** 返回 401，消息与「密码错误」「账号停用」三者完全一致

### Requirement: JWT 会话与授权链
admin 端 SHALL 以无状态 JWT 承载会话：HS256 签名，claims 含 `sub`（Manager UUID）、`username`、`role`，issuer 取自配置，有效期默认 720 分钟。session 策略 STATELESS，CSRF / formLogin / httpBasic 全部关闭，密码以 BCrypt（cost=10）存储。

JWT 过滤器解析失败时 SHALL NOT 直接返回错误，而是清空 SecurityContext 后放行，由授权层决定结果——以保证免认证路径不受非法 token 影响。

授权链 SHALL 按此顺序生效：`/api/admin/auth/login` 免认证 → `/uploads/**` 与 `/error` 免认证 → `/api/admin/managers/**` 要求 ADMIN 角色 → `/api/admin/**` 要求已认证 → 其余一律拒绝。

未认证访问受保护路径 SHALL 返回 401「未登录或登录已过期」；已认证但角色不足 SHALL 返回 403「权限不足」。错误响应体统一为 `{status, error, message, path}`。

#### Scenario: 无 token 访问受保护接口
- **GIVEN** 未携带 Authorization 头
- **WHEN** 请求任一 `/api/admin/**` 受保护接口
- **THEN** 返回 401 及消息「未登录或登录已过期」

#### Scenario: 非法 token 不影响免认证路径
- **GIVEN** 携带一个无法解析的 Authorization 头
- **WHEN** 请求 `POST /api/admin/auth/login`
- **THEN** 登录照常处理，不因 token 非法而失败

#### Scenario: 角色不足返回 403
- **GIVEN** 以 MEMBER 角色账号登录取得 token
- **WHEN** 请求 `/api/admin/managers/page`
- **THEN** 返回 403 及消息「权限不足」

### Requirement: 当前登录人查询与登出
admin 端 SHALL 提供 `GET /api/admin/auth/me`，返回当前登录人 `{id, username, nickname, role}`（**不含 `enable` 与 `createdAt``）。上下文缺失返回 401「未登录」；上下文中的 Manager 已被删除返回 401「管理员不存在」。

admin 端 SHALL 提供 `POST /api/admin/auth/logout`，返回 204 且服务端不做任何状态变更（无状态 JWT 无法吊销），仅记录审计日志 `auth:logout`。

#### Scenario: 查询当前登录人
- **GIVEN** 持有有效 token
- **WHEN** 请求 `GET /api/admin/auth/me`
- **THEN** 返回 200，含 id、username、nickname、role 四个字段

#### Scenario: 登出返回 204 且 token 仍然有效
- **GIVEN** 持有有效 token
- **WHEN** 请求 `POST /api/admin/auth/logout`，随后用**同一 token** 请求 `GET /api/admin/auth/me`
- **THEN** 登出返回 204；后续 `/me` 仍返回 200——服务端不吊销 token

### Requirement: web 端登录页与路由守卫
web 端 SHALL 在后台外壳之外提供 `/signin` 登录页：字段「用户名 *」与「密码 *」（密码可切换明文），提交按钮文案「登录」、提交中「登录中...」，两字段任一为空时按钮禁用，用户名提交前去除首尾空白。

登录成功 SHALL 跳转 `/cities` 并将 token 与账号信息持久化到 localStorage（键 `love-space:token`、`love-space:user`）。登录失败 401 SHALL 提示「用户名或密码错误，或账号已停用」。

路由守卫 SHALL 在 token 或账号信息缺失时重定向到 `/signin`。全局响应拦截器 SHALL 在收到 401 且请求**非**登录接口时清除 token 并跳转登录页。`/signup` SHALL 重定向到 `/signin`（不提供注册）。

退出登录 SHALL 由前端清除 localStorage 并跳转 `/signin`，不调用后端登出接口。

侧栏「管理员管理」入口 SHALL 仅对 ADMIN 角色显示。

#### Scenario: 登录成功进入地图管理
- **GIVEN** 停留在登录页
- **WHEN** 填入正确的用户名与密码并提交
- **THEN** 跳转到地图管理页，顶栏显示当前账号

#### Scenario: 两字段任一为空时无法提交
- **GIVEN** 停留在登录页
- **WHEN** 只填用户名、密码留空
- **THEN** 登录按钮为禁用态

#### Scenario: 未登录访问后台被拦回登录页
- **GIVEN** 本地无 token
- **WHEN** 直接访问任一后台页面地址
- **THEN** 重定向到 `/signin`

#### Scenario: 非 ADMIN 角色看不到管理员管理入口
- **GIVEN** 以 MEMBER 角色账号登录
- **WHEN** 查看左侧导航
- **THEN** 不出现「管理员管理」入口
