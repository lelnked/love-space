## ADDED Requirements

### Requirement: 生成额度配置
生成接口的每日额度 SHALL 存于表 `loves_ai_limit`，一种限制一行：`limit_key`（主键）、`name`、`limit_value`、`updated_by`、`updated_at`。建表 changelog SHALL 同时写入两行初始数据：

| limit_key | name | limit_value |
|---|---|---|
| `PER_ANON_DAILY` | 单个匿名 ID 每日上限 | 50 |
| `GLOBAL_DAILY` | 全站每日上限 | 5000 |

初始行的 `updated_by` / `updated_at` 为空。本期 SHALL NOT 提供新增或删除限制的接口。

`limit_value` SHALL 只取 `-1` 或正整数，`-1` 表示不限制。

admin 端 SHALL 提供：
- `GET /api/admin/ai/limits`：返回全部限制 `[{key, name, value, updatedBy, updatedAt}]`，按 `key` 升序，不分页；
- `PUT /api/admin/ai/limits/{key}`，body `{value}`：只更新该行的 `limit_value`，并把该行 `updated_by` 置为当前 Manager username、`updated_at` 置为当前时间，返回更新后的该行；其他行 SHALL NOT 变化。`key` 不存在 → 404；`value` 缺失、为 0、小于 -1 或非整数 → 400「取值须为 -1 或正整数」。

#### Scenario: 初始数据
- **GIVEN** 新建库刚执行完迁移
- **WHEN** 已登录 Manager 请求 `GET /api/admin/ai/limits`
- **THEN** 返回 200 与两项：`GLOBAL_DAILY` value=5000、`PER_ANON_DAILY` value=50，两项 `updatedBy`、`updatedAt` 均为 null

#### Scenario: 修改单项只记录该项修改人
- **GIVEN** 以 admin 登录，两项都未修改过
- **WHEN** 请求 `PUT /api/admin/ai/limits/PER_ANON_DAILY`，body `{"value":-1}`，随后 `GET /api/admin/ai/limits`
- **THEN** PUT 返回 200 且 `value=-1`、`updatedBy=admin`、`updatedAt` 非空；`GLOBAL_DAILY` 的 `updatedBy`、`updatedAt` 仍为 null

#### Scenario: 非法取值被拒绝
- **GIVEN** 以 admin 登录
- **WHEN** 分别以 `{"value":0}`、`{"value":-2}`、`{}` 请求 `PUT /api/admin/ai/limits/GLOBAL_DAILY`
- **THEN** 均返回 400「取值须为 -1 或正整数」，该行数据不变

#### Scenario: 不存在的限制
- **GIVEN** 以 admin 登录
- **WHEN** 请求 `PUT /api/admin/ai/limits/NOT_EXIST`，body `{"value":10}`
- **THEN** 返回 404

### Requirement: 限流配置页面
web 端 SHALL 提供页面 `/ai/limits`（侧栏「AI 管理 › 限流配置」）：

```
┌ AI 管理 › 限流配置 ──────────────────────────────────────────────────────┐
│ 名称                    上限            最后修改人   修改时间      操作   │
│ 全站每日上限            5000 次         admin        10-26 14:30   编辑① │
│ 单个匿名 ID 每日上限    不限制 ②        —            —             编辑   │
│ 提示：-1 表示不限制，保存后立即生效                                      │
└──────────────────────────────────────────────────────────────────────────┘

┌ 编辑：单个匿名 ID 每日上限（弹窗）──────┐
│ 上限 *  [    50 ] 次 ③                  │
│         -1 表示不限制                   │
│                    [取消] [保存]④       │
└─────────────────────────────────────────┘
```

- 进入页面 SHALL 调 `GET /api/admin/ai/limits` 渲染列表；
- ② 值为 -1 SHALL 显示「不限制」，否则显示「N 次」；从未修改过的行，修改人与修改时间 SHALL 显示「—」；
- ① SHALL 打开编辑弹窗并回填当前值；
- ③ 只接受 `-1` 或正整数，不合法时 SHALL 在输入框下提示且 ④ 置灰；
- ④ SHALL 调 `PUT /api/admin/ai/limits/{key}`，成功后关闭弹窗、提示「已保存，立即生效」并刷新列表。

#### Scenario: 列表展示
- **GIVEN** `PER_ANON_DAILY` 为 -1 且从未修改，`GLOBAL_DAILY` 为 5000 且由 admin 修改过
- **WHEN** 打开 `/ai/limits`
- **THEN** 列表区「单个匿名 ID 每日上限」行上限列显示「不限制」、修改人与时间显示「—」；「全站每日上限」行显示「5000 次」与修改人 admin

#### Scenario: 编辑保存
- **GIVEN** 已打开 `/ai/limits`
- **WHEN** 点「全站每日上限」行的「编辑」，输入 3000 并保存
- **THEN** 弹窗关闭、提示「已保存，立即生效」，该行显示「3000 次」，修改人为当前登录账号

#### Scenario: 非法输入禁止保存
- **GIVEN** 已打开编辑弹窗
- **WHEN** 输入 0
- **THEN** 输入框下显示「取值须为 -1 或正整数」，保存按钮不可点击

## MODIFIED Requirements

### Requirement: 生成接口每日额度
app 端 SHALL 在写生成记录前检查每日额度，"每天"按北京时间（Asia/Shanghai）自然日计算，计数取 `loves_ai_generation` 当日 `created_at` 记录数（所有状态都计入）。额度 SHALL 在每次检查时从 `loves_ai_limit` 实时读取（不缓存、不读环境变量），后台修改后下一次请求即生效：

- `PER_ANON_DAILY` 不为 -1 且当前匿名 ID 当日记录数 ≥ 该值 → 429「今日生成次数已达上限」；
- `GLOBAL_DAILY` 不为 -1 且全站当日记录数 ≥ 该值 → 429「服务繁忙，请稍后再试」；
- 某项为 -1 → 跳过该项检查。

`loves_ai_limit` 中缺少上述任一行 SHALL 视为部署错误，生成接口返回 500，不放行。

超额被拒的请求 SHALL NOT 写记录；去重命中的重复提交 SHALL NOT 占用额度。并发下可能超出上限少量次数（已知上限，可接受）。

#### Scenario: 匿名 ID 超出每日次数
- **GIVEN** `PER_ANON_DAILY=2`，匿名 ID `anon-A` 今日已有 2 条记录
- **WHEN** `anon-A` 以新的 clientRequestId 提交
- **THEN** 返回 429「今日生成次数已达上限」，记录数不变

#### Scenario: 全站超出每日总量
- **GIVEN** `GLOBAL_DAILY=3`，今日全站已有 3 条记录
- **WHEN** 任意匿名 ID 以新的 clientRequestId 提交
- **THEN** 返回 429「服务繁忙，请稍后再试」

#### Scenario: 昨日记录不计入
- **GIVEN** `PER_ANON_DAILY=2`，`anon-A` 昨日（北京时间）有 2 条、今日 0 条
- **WHEN** `anon-A` 提交
- **THEN** 正常生成

#### Scenario: 去重不占额度
- **GIVEN** `PER_ANON_DAILY=1`，`anon-A` 今日已有 1 条 SUCCESS 记录 `c-1`
- **WHEN** `anon-A` 重复提交 `c-1`
- **THEN** 返回 200 与原结果（不返回 429）

#### Scenario: -1 不限制
- **GIVEN** `PER_ANON_DAILY=-1`、`GLOBAL_DAILY=-1`，`anon-A` 今日已有 100 条记录
- **WHEN** `anon-A` 以新的 clientRequestId 提交
- **THEN** 正常生成，不返回 429

#### Scenario: 修改即时生效
- **GIVEN** `PER_ANON_DAILY=2`，`anon-A` 今日已有 2 条记录，提交被 429 拒绝
- **WHEN** 后台把 `PER_ANON_DAILY` 改为 3，`anon-A` 再以新的 clientRequestId 提交
- **THEN** 正常生成（无需重启 app）

#### Scenario: 配置行缺失
- **GIVEN** `loves_ai_limit` 中不存在 `GLOBAL_DAILY` 行
- **WHEN** 任意匿名 ID 以新的 clientRequestId 提交
- **THEN** 返回 500，不写生成记录

### Requirement: AI 管理写操作留痕
AI 管理的写接口 SHALL 通过现有操作日志留痕：

| 接口 | module:action |
|---|---|
| `POST /api/admin/ai/scenes` | `ai-scene:create` |
| `PUT /api/admin/ai/scenes/{id}` | `ai-scene:update` |
| `PUT /api/admin/ai/scenes/{id}/enabled` | `ai-scene:set-enabled` |
| `PUT /api/admin/ai/scenes/{id}/models` | `ai-scene:set-models` |
| `POST /api/admin/ai/prompt-templates` | `ai-prompt:create` |
| `PUT /api/admin/ai/prompt-templates/{id}` | `ai-prompt:update` |
| `POST /api/admin/ai/prompt-templates/{id}/publish` | `ai-prompt:publish` |
| `PUT /api/admin/ai/generations/{id}/rating` | `ai-generation:rate` |
| `PUT /api/admin/ai/limits/{key}` | `ai-limit:update` |

`POST /prompt-templates/{id}/test` 与 `POST /models/test-connection` SHALL NOT 留痕。

#### Scenario: 发布留痕
- **GIVEN** 以 admin 登录
- **WHEN** 发布一个已测试模板，随后查询操作日志
- **THEN** 存在 `module=ai-prompt`、`action=publish`、`target` 为该模板 id 的记录

#### Scenario: 测试不留痕
- **GIVEN** 以 admin 登录
- **WHEN** 测试一个模板，随后查询操作日志
- **THEN** 不存在 `module=ai-prompt`、`action=test` 的记录

#### Scenario: 修改限流配置留痕
- **GIVEN** 以 admin 登录
- **WHEN** 修改 `GLOBAL_DAILY`，随后查询操作日志
- **THEN** 存在 `module=ai-limit`、`action=update`、`target` 为 `GLOBAL_DAILY` 的记录

### Requirement: AI 管理权限与菜单
`/api/admin/ai/**` SHALL 对所有已登录 Manager（ADMIN、MEMBER）开放，未登录返回 401。web 端侧栏 SHALL 新增一级菜单「AI 管理」，对所有角色可见，子菜单依次为「业务配置」(`/ai/scenes`)、「模型配置」(`/ai/models`)、「提示词管理」(`/ai/prompts`)、「生成记录」(`/ai/generations`)、「限流配置」(`/ai/limits`)。

#### Scenario: MEMBER 可访问 AI 管理
- **GIVEN** 以 MEMBER 角色登录
- **WHEN** 请求 `GET /api/admin/ai/scenes/page`
- **THEN** 返回 200

#### Scenario: 未登录被拒绝
- **GIVEN** 未携带 token
- **WHEN** 请求 `GET /api/admin/ai/scenes/page`
- **THEN** 返回 401

#### Scenario: 侧栏菜单
- **GIVEN** 以 MEMBER 登录后台
- **WHEN** 展开侧栏「AI 管理」
- **THEN** 依次显示「业务配置」「模型配置」「提示词管理」「生成记录」「限流配置」五个子菜单

#### Scenario: MEMBER 可评价生成记录
- **GIVEN** 以 MEMBER 角色登录，存在一条成功的生成记录
- **WHEN** 请求 `PUT /api/admin/ai/generations/{id}/rating`
- **THEN** 返回 204（不返回 403）

#### Scenario: MEMBER 可修改限流配置
- **GIVEN** 以 MEMBER 角色登录
- **WHEN** 请求 `PUT /api/admin/ai/limits/GLOBAL_DAILY`，body `{"value":5000}`
- **THEN** 返回 200（不返回 403）
