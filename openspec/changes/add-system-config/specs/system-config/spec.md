## ADDED Requirements

### Requirement: 系统配置数据模型
系统 SHALL 以表 `loves_system_config` 存储 key/value 配置，一行代表一个配置项：

- `config_key`：配置 key，必填，全局唯一（唯一约束 `uk_loves_system_config_key`），格式 SHALL 匹配 `^[a-z][a-z0-9_.]{1,63}$`，创建后 SHALL NOT 被修改；
- `name`：配置名称，必填，长度 ≤ 64；
- `config_value`：配置值，jsonb，必填，可为任意合法 JSON（对象、数组、字符串、数字、布尔），JSON `null` 视为未填；序列化后长度 ≤ 65535 字符；
- `description`：说明，选填，长度 ≤ 500；
- `created_at` / `updated_at`。

接口中 key、value 字段名分别为 `key`、`value`，`value` SHALL 以 JSON 原值传输（不是字符串化的 JSON）。

#### Scenario: value 以 JSON 原值往返
- **GIVEN** 以 admin 身份登录
- **WHEN** 新增配置 `{key:"home.notice", name:"首页公告", value:{"text":"放假通知","show":true}}`，随后查询其详情
- **THEN** 详情的 `value` 为对象 `{"text":"放假通知","show":true}`，而不是字符串

#### Scenario: 标量与数组均可作为 value
- **GIVEN** 以 admin 身份登录
- **WHEN** 分别以 `28`、`"hello"`、`false`、`[1,2,3]` 作为 value 新增配置
- **THEN** 均返回 201，详情中的 value 类型与提交时一致

### Requirement: 后台系统配置分页查询
`GET /api/admin/system-configs/page` SHALL 返回 `PageResponse<SystemConfigItem>`，`SystemConfigItem = {id, key, name, value, description, createdAt, updatedAt}`。

- `keyword` 选填，SHALL 对 key 与名称做不区分大小写的模糊匹配；
- `page` 从 1 开始，`size` 只接受 20 / 200；
- 结果 SHALL 按 `updated_at DESC, created_at DESC` 排序。

`GET /api/admin/system-configs/{id}` SHALL 返回 `SystemConfigItem`；id 不存在 SHALL 返回 404「配置不存在」。

所有 `/api/admin/system-configs/**` 接口 SHALL 要求 Manager 登录（ADMIN、MEMBER 均可），未登录返回 401。

#### Scenario: 按 keyword 匹配 key 或名称
- **GIVEN** 存在配置 `home.notice`（首页公告）与 `cycle_default_days`（默认周期）
- **WHEN** 请求 `GET /api/admin/system-configs/page?keyword=notice&page=1&size=20`
- **THEN** 返回 200，`content` 仅含 `home.notice`

#### Scenario: 详情不存在返回 404
- **GIVEN** 以 admin 身份登录
- **WHEN** 请求一个不存在 id 的详情
- **THEN** 返回 404，message 为「配置不存在」

#### Scenario: 未登录被拒绝
- **GIVEN** 请求未携带 JWT
- **WHEN** 请求 `GET /api/admin/system-configs/page`
- **THEN** 返回 401

### Requirement: 后台新增系统配置
`POST /api/admin/system-configs` SHALL 接收 `{key*, name*, value*, description?}`，成功返回 201 与 `SystemConfigItem`。

- key 已存在 SHALL 返回 409「配置 key 已存在」；
- key 缺失或格式不符、name 缺失或超长、description 超长 SHALL 返回 400；
- value 缺失或为 JSON `null` SHALL 返回 400「value 不能为空」；value 序列化后超过 65535 字符 SHALL 返回 400「value 过长」。

#### Scenario: 新增成功
- **GIVEN** 以 admin 身份登录且 key `home.notice` 不存在
- **WHEN** 提交 `{key:"home.notice", name:"首页公告", value:{"text":"hi"}}`
- **THEN** 返回 201，响应含 id、key、name、value，description 为 null

#### Scenario: key 重复返回 409
- **GIVEN** 已存在 key `home.notice`
- **WHEN** 再次以 key `home.notice` 新增
- **THEN** 返回 409，message 为「配置 key 已存在」

#### Scenario: key 格式非法返回 400
- **GIVEN** 以 admin 身份登录
- **WHEN** 以 key `Home-Notice` 新增
- **THEN** 返回 400

#### Scenario: value 为 null 返回 400
- **GIVEN** 以 admin 身份登录
- **WHEN** 提交 `{key:"a.b", name:"x", value:null}` 或不含 value 字段
- **THEN** 返回 400，message 为「value 不能为空」

### Requirement: 后台编辑系统配置
`PUT /api/admin/system-configs/{id}` SHALL 接收 `{name*, value*, description?}` 并返回更新后的 `SystemConfigItem`；请求体中的 key SHALL 被忽略，key 保持不变。校验规则同新增；id 不存在 SHALL 返回 404「配置不存在」。保存后 `updated_at` SHALL 更新，且 App 下一次查询 SHALL 读到新值。

#### Scenario: 编辑 value 后 App 立即读到新值
- **GIVEN** 配置 `home.notice` 的 value 为 `{"text":"旧"}`
- **WHEN** 运营把 value 改为 `{"text":"新"}`，随后 App 请求 `GET /api/app/system-configs/home.notice`
- **THEN** App 返回的 value 为 `{"text":"新"}`

#### Scenario: 编辑不能修改 key
- **GIVEN** 存在配置 `home.notice`
- **WHEN** 提交 `PUT` 且请求体携带 `key:"other.key"`
- **THEN** 返回 200，响应中 key 仍为 `home.notice`

### Requirement: 后台删除系统配置
`DELETE /api/admin/system-configs/{id}` SHALL 物理删除配置并返回 204；id 不存在 SHALL 返回 404「配置不存在」。删除后 App 查询该 key SHALL 返回 404。

#### Scenario: 删除后 App 查不到
- **GIVEN** 存在配置 `home.notice`
- **WHEN** 运营删除该配置，随后 App 请求 `GET /api/app/system-configs/home.notice`
- **THEN** 删除返回 204，App 返回 404

### Requirement: App 查询系统配置
app 端 SHALL 提供只读查询，返回 `AppSystemConfig = {key, value}`，SHALL NOT 下发 name、description 等后台管理字段。接口沿用全局 `X-API-Key` 鉴权，缺失或非法返回 401。服务端 SHALL NOT 缓存配置。

- `GET /api/app/system-configs/{key}`：按 key 精确匹配；不存在 SHALL 返回 404「配置不存在」。
- `GET /api/app/system-configs?keys=k1,k2`：`keys` 必填，逗号分隔，去除空白与空项、去重后 SHALL 为 1–50 个，否则返回 400；不存在的 key SHALL 不出现在结果中；结果 SHALL 按请求中 key 的首次出现顺序排列。

#### Scenario: 单个 key 查询
- **GIVEN** 存在配置 `home.notice`，value 为 `{"text":"hi"}`
- **WHEN** App 携带合法 API-key 请求 `GET /api/app/system-configs/home.notice`
- **THEN** 返回 200 与 `{key:"home.notice", value:{"text":"hi"}}`，不含 name、description

#### Scenario: 单个 key 不存在返回 404
- **GIVEN** 不存在 key `no.such`
- **WHEN** App 请求 `GET /api/app/system-configs/no.such`
- **THEN** 返回 404，message 为「配置不存在」

#### Scenario: 批量查询忽略不存在的 key 并保持顺序
- **GIVEN** 存在配置 `a.one` 与 `b.two`，不存在 `c.none`
- **WHEN** App 请求 `GET /api/app/system-configs?keys=b.two,c.none,a.one,b.two`
- **THEN** 返回 200，数组依次为 `b.two`、`a.one`，共 2 项

#### Scenario: 批量查询 keys 缺失或超限返回 400
- **GIVEN** App 携带合法 API-key
- **WHEN** 请求不带 `keys`，或 `keys` 去重后超过 50 个
- **THEN** 返回 400

#### Scenario: 缺少 API-key 返回 401
- **GIVEN** 请求未携带 `X-API-Key`
- **WHEN** 请求 `GET /api/app/system-configs/home.notice`
- **THEN** 返回 401

### Requirement: 系统配置管理页面
后台 SHALL 在侧栏「AI 管理」与「操作日志」之间新增一级菜单「系统配置」，路由 `/system-configs`，页面由列表区与新增/编辑弹窗组成。

- **列表区**：顶部为 key / 名称搜索框与「+ 新增配置」按钮；表格列为 key、名称、value 预览、更新时间、操作（编辑、删除）；value 预览为单行序列化文本，超过 40 字符截断加省略号，悬停显示全文；说明不在列表展示，只在编辑弹窗中查看与修改；底部分页（20/200 条每页）。
- **弹窗**：key（必填，编辑时只读）、名称（必填）、value（必填，多行文本框，旁有「格式化」按钮）、说明（选填）；弹窗内常驻提示「App 可读取全部配置，请勿填写密钥、密码等机密」。
- **value 校验**：value 文本框失焦与点击保存时 SHALL 用 JSON 解析校验；不合法时 SHALL 在文本框下方显示「value 必须是合法的 JSON」且 SHALL NOT 发出保存请求；为空或解析结果为 `null` 时显示「value 不能为空」并阻止保存。
- **格式化**：value 合法时按 2 空格缩进重排；不合法时显示校验错误、不改动文本。
- **删除**：点击删除 SHALL 弹出二次确认，提示「删除后 App 将无法读取该配置」，确认后才调用删除接口。
- 后端返回的错误（如 409「配置 key 已存在」）SHALL 在弹窗内展示，弹窗不关闭。

#### Scenario: 非法 JSON 被前端拦截
- **GIVEN** 运营打开新增配置弹窗并填好 key 与名称
- **WHEN** 在 value 中输入 `{text: hi}` 后点击保存
- **THEN** 弹窗 value 区域下方显示「value 必须是合法的 JSON」，弹窗不关闭，且没有发出 `POST /api/admin/system-configs` 请求

#### Scenario: 格式化合法 JSON
- **GIVEN** value 文本框内容为 `{"a":1,"b":[1,2]}`
- **WHEN** 点击「格式化」
- **THEN** value 文本框内容变为 2 空格缩进的多行 JSON

#### Scenario: 新增成功后列表刷新
- **GIVEN** 运营在新增弹窗填写合法的 key、名称与 value
- **WHEN** 点击保存
- **THEN** 弹窗关闭，列表区出现该配置行，value 列显示其单行预览

#### Scenario: 编辑时 key 只读
- **GIVEN** 列表中存在配置 `home.notice`
- **WHEN** 点击该行「编辑」
- **THEN** 弹窗中 key 输入框显示 `home.notice` 且不可编辑，名称、value、说明已回填

#### Scenario: key 重复时弹窗内提示
- **GIVEN** 已存在 key `home.notice`
- **WHEN** 在新增弹窗以 `home.notice` 保存
- **THEN** 弹窗内显示「配置 key 已存在」，弹窗不关闭

#### Scenario: 删除需二次确认
- **GIVEN** 列表中存在配置 `home.notice`
- **WHEN** 点击该行「删除」并在确认框中确认
- **THEN** 确认框提示「删除后 App 将无法读取该配置」，确认后该行从列表区消失
