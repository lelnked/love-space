## ADDED Requirements

### Requirement: 运营账号管理
admin 端 SHALL 提供运营账号（Manager）管理，全部接口要求 **ADMIN 角色**（控制器级 `hasRole('ADMIN')` + 安全配置路径匹配双重保障）：

| 接口 | 说明 | 成功状态码 |
|---|---|---|
| `GET /api/admin/managers/page` | 分页列表 | 200 |
| `POST /api/admin/managers` | 创建账号 | **201** |
| `GET /api/admin/managers/{id}` | 账号详情 | 200 |
| `PUT /api/admin/managers/{id}/enable` | 启用 | **204** |
| `PUT /api/admin/managers/{id}/disable` | 停用 | **204** |
| `PUT /api/admin/managers/{id}/password` | 重置密码 | **204** |

创建请求体为 `{username, password, nickname?}`——**不含 `role` 字段**，服务端强制落库 `role=MEMBER`、`enable=true`，密码以 BCrypt 存储。字段约束与中文消息：用户名必填且不超过 64 字符；密码必填且长度 8~128；昵称不超过 64 字符。

用户名 SHALL 全局唯一，重复时返回 400「用户名已存在：{username}」。

该域 SHALL NOT 提供删除接口——账号只能启用/停用，不可删除。

#### Scenario: 创建账号强制为 MEMBER 角色
- **GIVEN** 以 ADMIN 身份登录
- **WHEN** 提交创建请求（即使请求体额外携带 `role=ADMIN`）
- **THEN** 返回 201，落库账号 `role` 为 MEMBER、`enable` 为 true

#### Scenario: 用户名重复被拒绝
- **GIVEN** 已存在用户名 `ops01` 的账号
- **WHEN** 再次以同一用户名创建
- **THEN** 返回 400 及消息「用户名已存在：ops01」

#### Scenario: 密码长度不足被拒绝
- **GIVEN** 以 ADMIN 身份登录
- **WHEN** 提交 7 位密码的创建请求
- **THEN** 返回 400 及消息「密码长度需为 8~128 个字符」

#### Scenario: 重置密码后旧密码失效
- **GIVEN** 一个已启用账号，其当前密码可正常登录
- **WHEN** 管理员对该账号重置密码
- **THEN** 返回 204；以旧密码登录返回 401，以新密码登录返回 200

### Requirement: 账号启停与内置管理员保护
admin 端 SHALL 通过启用/停用切换账号的 `enable` 布尔字段。停用后该账号无法登录。

内置 admin 账号 SHALL NOT 被停用——尝试停用返回 400「内置管理员 admin 账号不可停用」。该保护仅覆盖停用，内置 admin 的**密码仍可被重置**。

重置密码 SHALL NOT 校验旧密码。

#### Scenario: 停用后无法登录
- **GIVEN** 一个已启用的 MEMBER 账号
- **WHEN** 管理员停用该账号，随后该账号尝试登录
- **THEN** 停用返回 204；登录返回 401

#### Scenario: 内置 admin 不可停用
- **GIVEN** 种子账号 admin
- **WHEN** 尝试停用它
- **THEN** 返回 400 及消息「内置管理员 admin 账号不可停用」，且该账号仍为启用状态

#### Scenario: 启停可往复切换
- **GIVEN** 一个已启用账号
- **WHEN** 先停用再启用
- **THEN** 两次均返回 204，最终 `enable` 为 true

### Requirement: 运营账号分页查询
admin 端账号列表 SHALL 支持过滤：`username`（**模糊匹配**，不去除首尾空白）、`role`（精确）、`enable`（精确）、`createdAtFrom` / `createdAtTo`（创建时间闭区间）。

排序 SHALL 固定为创建时间倒序，不接受客户端指定。

分页 SHALL 遵循项目统一口径：`page` 从 1 开始计数，缺省第 1 页，小于等于 0 归一为第 1 页；`size` 仅接受 **20 与 30** 两个值，其余任何值一律校正为 20。响应结构为 `{content, page, size, totalElements, totalPages}`，其中 `page` 以 1 为基回传。

资源不存在时 SHALL 返回 **400**「管理员不存在：{id}」（本项目统一口径，非 404）。

#### Scenario: 按用户名模糊过滤
- **GIVEN** 存在账号 `ops01` 与 `ops02`
- **WHEN** 以 `username=ops` 查询
- **THEN** 返回 200，两个账号都在结果中

#### Scenario: 页大小非白名单值被校正
- **GIVEN** 账号数量多于 30
- **WHEN** 以 `size=25` 查询
- **THEN** 返回 200，响应 `size` 为 20

#### Scenario: 列表按创建时间倒序
- **GIVEN** 先后创建账号 A、B、C
- **WHEN** 查询第 1 页
- **THEN** 返回顺序为 C、B、A

#### Scenario: 查询不存在的账号返回 400
- **GIVEN** 一个未使用的 UUID
- **WHEN** 查询该账号详情
- **THEN** 返回 400 及消息「管理员不存在：{id}」

### Requirement: web 端管理员管理页面
web 端 SHALL 在 `/managers` 提供管理员管理页：筛选栏含「用户名」（模糊）、「角色」（管理员/成员）、「状态」（启用/停用）三项；列表列为用户名、昵称（空显示 `-`）、角色（ADMIN 显示「管理员」、MEMBER 显示「成员」）、状态（启用/停用徽标）、创建时间、操作。

操作列 SHALL 提供「停用」/「启用」与「重置密码」两个按钮，其中**内置 admin 账号不渲染启停按钮**。启停切换 SHALL NOT 弹出二次确认。

新增与重置密码 SHALL 复用同一弹窗的两种模式：新增模式填用户名、密码、昵称，按钮「创建」；重置密码模式下用户名与昵称禁用且预填，仅填新密码，按钮「重置」。前端 SHALL 在密码不足 8 位时给出「密码至少 8 位」的字段级提示。

分页默认每页 20 条，筛选变更后回到第 1 页。

#### Scenario: 列表按角色与状态渲染
- **GIVEN** 已以 ADMIN 登录且存在若干账号
- **WHEN** 进入管理员管理页
- **THEN** 列表展示用户名、昵称、角色中文名与状态徽标，昵称为空的行显示 `-`

#### Scenario: 内置 admin 行不显示启停按钮
- **GIVEN** 列表中含内置 admin 账号
- **WHEN** 查看该行操作列
- **THEN** 只有「重置密码」按钮，无「停用」按钮

#### Scenario: 弹窗创建新账号
- **GIVEN** 已进入管理员管理页
- **WHEN** 点击「新增管理员」，填入用户名与不少于 8 位的密码并提交
- **THEN** 创建成功，列表出现该账号且角色为「成员」、状态为「启用」

#### Scenario: 密码不足 8 位前端拦截
- **GIVEN** 新增弹窗已打开
- **WHEN** 填入 7 位密码并提交
- **THEN** 密码字段下方提示「密码至少 8 位」，请求不发出
