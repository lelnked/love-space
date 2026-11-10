# system-config Specification

## Purpose
系统配置 key/value：后台对预置配置分页查询与编辑（value 为 JSON，按 key 做专属校验与专属表单），App 按 key 单个/批量只读查询。
## Requirements
### Requirement: 系统配置数据模型
系统 SHALL 以表 `loves_system_config` 存储系统预置的 key/value 配置，一行代表一个配置项：

- `config_key`：配置 key，必填，全局唯一（唯一约束 `uk_loves_system_config_key`），格式匹配 `^[a-z][a-z0-9_.]{1,63}$`，由数据库迁移预置，创建后 SHALL NOT 被修改；
- `name`：配置名称，必填，长度 ≤ 64；
- `config_value`：配置值，jsonb，必填，JSON `null` 视为未填；序列化后长度 ≤ 65535 字符；具体字段结构由各预置 `key` 定义；
- `description`：说明，选填，长度 ≤ 500；
- `created_at` / `updated_at`。

数据库迁移 SHALL 预置配置项 `ios_llm_popup`（名称「iOS LLM 弹窗开关」，说明「控制 iOS 系统调用 LLM 时是否需要弹窗」，初始 `config_value` 为 `{"enabled": true}`）。

接口中 key、value 字段名分别为 `key`、`value`，`value` SHALL 以 JSON 原值传输（不是字符串化的 JSON）。

#### Scenario: 预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回
- **GIVEN** 数据库迁移已执行并预置 `ios_llm_popup`
- **WHEN** 以 admin 身份查询配置列表与该项详情
- **THEN** 响应中 `ios_llm_popup` 的 `value` 为对象 `{"enabled": true}`，而不是字符串

### Requirement: 后台系统配置分页查询
`GET /api/admin/system-configs/page` SHALL 返回 `PageResponse<SystemConfigItem>`，`SystemConfigItem = {id, key, name, value, description, createdAt, updatedAt}`。

- `keyword` 选填，SHALL 对 key 与名称做不区分大小写的模糊匹配；
- `page` 从 1 开始，`size` 只接受 20 / 200；
- 结果 SHALL 按 `updated_at DESC, created_at DESC` 排序。

`GET /api/admin/system-configs/{id}` SHALL 返回 `SystemConfigItem`；id 不存在 SHALL 返回 404「配置不存在」。

所有 `/api/admin/system-configs/**` 接口 SHALL 要求 Manager 登录（ADMIN、MEMBER 均可），未登录返回 401。

#### Scenario: 按 keyword 匹配 key 或名称
- **GIVEN** 存在配置 `ios_llm_popup`（iOS LLM 弹窗开关）与另一配置项
- **WHEN** 请求 `GET /api/admin/system-configs/page?keyword=llm_popup&page=1&size=20`
- **THEN** 返回 200，`content` 仅含 `ios_llm_popup`

#### Scenario: 详情不存在返回 404
- **GIVEN** 以 admin 身份登录
- **WHEN** 请求一个不存在 id 的详情
- **THEN** 返回 404，message 为「配置不存在」

#### Scenario: 未登录被拒绝
- **GIVEN** 请求未携带 JWT
- **WHEN** 请求 `GET /api/admin/system-configs/page`
- **THEN** 返回 401

### Requirement: 后台编辑系统配置
`PUT /api/admin/system-configs/{id}` SHALL 接收 `{name*, value*, description?}` 并返回更新后的 `SystemConfigItem`；请求体中的 `key` SHALL 被忽略，`key` 保持不变。后台 SHALL NOT 提供新增或删除系统配置的接口。

校验规则：
- `id` 不存在 SHALL 返回 404「配置不存在」；
- `name` 缺失或超长（> 64）、`description` 超长（> 500）SHALL 返回 400；
- `value` 缺失或为 JSON `null` SHALL 返回 400「value 不能为空」；`value` 序列化后超过 65535 字符 SHALL 返回 400「value 过长」；
- 当配置项 `key` 为 `ios_llm_popup` 时，`value` SHALL 为 JSON 对象且包含布尔类型字段 `enabled`（`true` 或 `false`），否则 SHALL 返回 400「ios_llm_popup 的 enabled 必须为布尔值」。

保存后 `updated_at` SHALL 更新，且 App 下一次查询 SHALL 读到新值。

#### Scenario: 编辑 value 后 App 立即读到新值
- **GIVEN** 预置配置 `ios_llm_popup` 的 value 为 `{"enabled": true}`
- **WHEN** 运营把 value 改为 `{"enabled": false}`，随后 App 请求 `GET /api/app/system-configs/ios_llm_popup`
- **THEN** App 返回的 value 为 `{"enabled": false}`

#### Scenario: 编辑不能修改 key
- **GIVEN** 存在预置配置 `ios_llm_popup`
- **WHEN** 提交 `PUT` 且请求体携带 `key:"other_key"`
- **THEN** 返回 200，响应中 key 仍为 `ios_llm_popup`

#### Scenario: value 为 null 返回 400
- **GIVEN** 以 admin 身份登录且存在预置配置 `ios_llm_popup`
- **WHEN** 提交 `PUT` 且 `{name:"iOS LLM 弹窗开关", value:null}` 或不含 value 字段
- **THEN** 返回 400，message 为「value 不能为空」

#### Scenario: ios_llm_popup 的 enabled 非布尔值返回 400
- **GIVEN** 以 admin 身份登录且存在预置配置 `ios_llm_popup`
- **WHEN** 提交 `PUT` 且 `value` 为 `{}`、`{"enabled": "yes"}` 或 `true`
- **THEN** 返回 400，message 为「ios_llm_popup 的 enabled 必须为布尔值」

### Requirement: App 查询系统配置
app 端 SHALL 提供只读查询，返回 `AppSystemConfig = {key, value}`，SHALL NOT 下发 name、description 等后台管理字段。接口沿用全局 `X-API-Key` 鉴权，缺失或非法返回 401。服务端 SHALL NOT 缓存配置。

- `GET /api/app/system-configs/{key}`：按 key 精确匹配；不存在 SHALL 返回 404「配置不存在」。
- `GET /api/app/system-configs?keys=k1,k2`：`keys` 必填，逗号分隔，去除空白与空项、去重后 SHALL 为 1–50 个，否则返回 400；不存在的 key SHALL 不出现在结果中；结果 SHALL 按请求中 key 的首次出现顺序排列。

#### Scenario: 单个 key 查询
- **GIVEN** 存在配置 `ios_llm_popup`，value 为 `{"enabled": true}`
- **WHEN** App 携带合法 API-key 请求 `GET /api/app/system-configs/ios_llm_popup`
- **THEN** 返回 200 与 `{key:"ios_llm_popup", value:{"enabled":true}}`，不含 name、description

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
- **WHEN** 请求 `GET /api/app/system-configs/ios_llm_popup`
- **THEN** 返回 401

### Requirement: 系统配置管理页面
后台 SHALL 在侧栏「AI 管理」与「操作日志」之间新增一级菜单「系统配置」，列表路由 `/system-configs`，编辑路由 `/system-configs/:id/edit`。

- **列表页 `/system-configs`**：顶部为 key / 名称搜索框，SHALL NOT 提供新增按钮；表格列为 key、名称、value 预览、更新时间、操作（仅「编辑」按钮，SHALL NOT 提供删除按钮）；value 预览为单行序列化文本，超过 40 字符截断加省略号，悬停显示全文；说明不在列表展示；底部分页（20/200 条每页）。
- **按 key 专属编辑页 `/system-configs/:id/edit`**：点击某行「编辑」跳转 `/system-configs/{id}/edit`，页面通过 `GET /api/admin/system-configs/{id}` 加载详情，并根据配置项的 `key` 渲染对应的专属表单页面组件（每个预置 `key` 独立开发一个表单页面组件，不使用通用 JSON 文本框弹窗）。
- **`ios_llm_popup` 专属表单页**：
  - 基本信息区：key（只读展示 `ios_llm_popup`，附「系统预置配置，key 不可修改」提示）、名称（必填，≤64）、说明（选填，≤500）；
  - 配置项参数区：「是否需要弹窗」布尔选择控件（开启 = `true`，关闭 = `false`），进入页面时回填当前 `value.enabled`；
  - 保存与取消：名称为空时在名称输入框下方显示「配置名称不能为空」且 SHALL NOT 发出保存请求；点击「保存」将表单值组装为 `value: { enabled: boolean }` 提交 `PUT /api/admin/system-configs/{id}`，保存成功后提示「已保存，立即生效」并跳转回 `/system-configs`；点击「取消」返回 `/system-configs`。

#### Scenario: 列表页展示预置配置且无新增与删除入口
- **GIVEN** 运营登录后台并进入 `/system-configs`
- **WHEN** 列表加载完成
- **THEN** 页面展示 `ios_llm_popup`（名称「iOS LLM 弹窗开关」），页面上不存在「+ 新增配置」按钮，操作列仅有「编辑」按钮而无「删除」按钮

#### Scenario: 进入 ios_llm_popup 专属表单页回填并切换开关保存
- **GIVEN** 列表中 `ios_llm_popup` 的当前 value 为 `{"enabled": true}`
- **WHEN** 点击该行「编辑」进入 `/system-configs/{id}/edit`，将「是否需要弹窗」切换为关闭并点击「保存」
- **THEN** 编辑页中 key 输入框只读显示 `ios_llm_popup`，保存后跳转回 `/system-configs`，该行 value 预览更新为 `{"enabled":false}`

#### Scenario: 编辑页名称为空时前端拦截
- **GIVEN** 运营在 `ios_llm_popup` 编辑页清空「名称」输入框
- **WHEN** 点击「保存」
- **THEN** 名称输入框下方显示「配置名称不能为空」，且未发出 `PUT /api/admin/system-configs/{id}` 请求
