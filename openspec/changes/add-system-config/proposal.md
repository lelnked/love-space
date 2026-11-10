# Proposal: add-system-config（系统配置 key/value）

## Why

运营需要一处地方维护 App 运行时要读取的零散参数（开关、文案、阈值、列表等），目前每加一个参数都要改代码、发版。本 change 建立：运营在后台维护 key/value 配置 → 落库 `loves_system_config` → App 按 key 查询 这条主链路。value 用 jsonb 存，任意 JSON 结构都能放，不再为每个参数单独建字段。

## What Changes

按功能先后顺序（也是实现顺序）：

| # | 功能 | 端 | 后台菜单 |
|---|---|---|---|
| F1 | 系统配置管理：分页查询、新增、编辑、删除配置；value 前后端双重 JSON 校验 | admin + web | 系统配置 |
| F2 | App 配置查询：按单个 key 或一组 key 查询配置值 | app | — |

明确**不做**：配置分组/分类、启用禁用开关、版本历史与回滚、value 的 JSON Schema 结构校验、按 key 的类型约束、App 端缓存与变更推送、导入导出、App 获取全量配置、给 App 隐藏部分配置的可见性开关。

### 全局约定（各功能共用）

- **key**：运营录入的配置编码，全局唯一、必填、**创建后不可修改**（App 按 key 读取，改 key 等于让 App 读不到）。格式 `^[a-z][a-z0-9_.]{1,63}$`（小写字母开头，可含数字、下划线、点，2–64 位，如 `home.notice`、`cycle_default_days`）。
- **value**：必填，任意合法 JSON（对象、数组、字符串、数字、布尔），存 jsonb；JSON `null` 视为未填 → 400。序列化后长度 ≤ 65535 字符。
- **可见性**：所有配置都对持有 API-key 的 App 可读。App 的 API-key 随客户端分发，等同公开，**禁止在系统配置里存密钥、密码等机密**（页面表单处给出提示）。
- **权限**：沿用现有授权链，`/api/admin/system-configs/**` 所有登录运营（ADMIN、MEMBER）均可用，不新增角色或权限点；App 接口沿用全局 `X-API-Key` 校验。
- **操作日志**：新增、编辑、删除写现有操作日志；查看不留痕。
- **生效时机**：保存即生效，App 下一次请求读到新值（服务端不缓存）。
- **分页 / 错误格式**：沿用现有 `PageResponse{content,page,size,totalElements,totalPages}`（page 从 1 开始，size 白名单 20/200）与 `{status,error,message,path}`。
- **侧栏**：新增一级菜单「系统配置」，位于「AI 管理」与「操作日志」之间。

---

### F1 系统配置管理

**规则**
- key 唯一：新增时 key 已存在 → 409「配置 key 已存在」。编辑请求不含 key，key 不可改。
- value 校验分两层：
  - **前端**：保存前 `JSON.parse`，失败 → 表单内红字「value 必须是合法的 JSON」，不发请求；解析结果为 `null` → 同样拦截提示「value 不能为空」。
  - **后端**：body 中 `value` 缺失或为 JSON `null` → 400「value 不能为空」；超长 → 400「value 过长」。body 本身不是合法 JSON 由框架返回 400。
- name 必填 ≤ 64；description 选填 ≤ 500；key 格式不符 → 400。
- 删除为物理删除，二次确认弹窗提示「删除后 App 将无法读取该配置」；id 不存在 → 404「配置不存在」。

**表 `loves_system_config`**（一行 = 一个配置项）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | uuid | PK | |
| config_key | text | NOT NULL, UNIQUE | 配置 key，创建后不可改 |
| name | text | NOT NULL | 配置名称，≤64 |
| config_value | jsonb | NOT NULL | 配置值，任意 JSON |
| description | text | | 说明，≤500 |
| created_at | timestamptz | NOT NULL | |
| updated_at | timestamptz | | |

唯一约束：`uk_loves_system_config_key (config_key)`，同时承担 App 按 key 查询的索引。

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/admin/system-configs/page` | query：`keyword?`（模糊匹配 key / 名称）、`page`、`size`（20/200） | `PageResponse<SystemConfigItem>`，按 `updated_at DESC, created_at DESC` |
| GET | `/api/admin/system-configs/{id}` | path：id | `SystemConfigItem`；不存在 → 404「配置不存在」 |
| POST | `/api/admin/system-configs` | body：`{key*, name*, value*, description?}` | 201 `SystemConfigItem`；key 重复 → 409「配置 key 已存在」；校验失败 → 400 |
| PUT | `/api/admin/system-configs/{id}` | body：`{name*, value*, description?}`（不含 key） | `SystemConfigItem`；不存在 → 404；校验失败 → 400 |
| DELETE | `/api/admin/system-configs/{id}` | path：id | 204；不存在 → 404 |

`SystemConfigItem` = `{id, key, name, value, description, createdAt, updatedAt}`，其中 `value` 是 JSON 原值（不是字符串化的 JSON）。

操作日志：`system-config:create`、`system-config:update`、`system-config:delete`

**页面 `/system-configs`**

```
┌ 系统配置 ────────────────────────────────────────────────────────────────┐
│ [搜索 key / 名称          ]①                                [+ 新增配置]② │
├──────────────────────────────────────────────────────────────────────────┤
│ key                 名称        value（预览）              更新时间     操作      │
│ home.notice         首页公告    {"text":"国庆...          10-20 14:00  编辑③ 删除④│
│ cycle_default_days  默认周期    28                        10-21 09:30  编辑  删除 │
├──────────────────────────────────────────────────────────────────────────┤
│                                              共 2 条  < 1 >  20条/页 ⑤   │
└──────────────────────────────────────────────────────────────────────────┘

┌ 新增 / 编辑配置（弹窗）─────────────────────────────┐
│ key *     [home.notice                ] ⑥           │  ← 编辑时只读
│ 名称 *    [首页公告                   ] ⑦           │
│ value *   ┌────────────────────────────┐ [格式化]⑧  │
│           │ {                          │            │
│           │   "text": "国庆放假通知"   │ ⑨          │
│           │ }                          │            │
│           └────────────────────────────┘            │
│           ✗ value 必须是合法的 JSON                 │  ← 校验失败时显示
│ 说明      [首页顶部公告栏             ] ⑩           │
│ 提示：App 可读取全部配置，请勿填写密钥、密码等机密   │
│                               [取消] [保存]⑪        │
└─────────────────────────────────────────────────────┘
```
①⑤ → `GET /system-configs/page`；②⑪(新增) → 前端 JSON 校验通过后 `POST /system-configs`；③ → `GET /system-configs/{id}`，⑪(编辑) → 前端 JSON 校验通过后 `PUT /system-configs/{id}`；④ → 二次确认后 `DELETE /system-configs/{id}`；⑧ 纯前端：合法 JSON 时按 2 空格缩进重排，非法时显示校验错误；⑨ 失焦时即做 JSON 校验。列表 value 预览为单行序列化，超过 40 字符截断加省略号，悬停显示全文。

---

### F2 App 配置查询

**规则**
- 只读，按 key 精确匹配；不分页。
- 单个查询：key 不存在 → 404「配置不存在」。
- 批量查询：`keys` 逗号分隔，必填，去重后 1–50 个，超出 → 400；不存在的 key 直接不出现在结果中（不报错），结果按请求中 key 的顺序返回。

**表**：无新表，读 `loves_system_config`。

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/app/system-configs/{key}` | path：key | `AppSystemConfig`；不存在 → 404「配置不存在」 |
| GET | `/api/app/system-configs` | query：`keys*`（逗号分隔，1–50 个） | `AppSystemConfig[]`；缺 keys 或超 50 → 400 |

`AppSystemConfig` = `{key, value}`，`value` 为 JSON 原值。name、description 属于后台管理信息，不下发给 App。

鉴权：全局 `X-API-Key`，缺失或非法 → 401。

---

## Capabilities

### New Capabilities
- `system-config`：系统配置 key/value 全链路——后台配置的分页查询、新增、编辑、删除（key 唯一且不可改、value 为任意合法 JSON 并前后端双重校验），App 按单个 key / 一组 key 查询配置值。新域 `system-config` 需在 `tests/modules.md` 登记（端：web）。

### Modified Capabilities
- `operation-log`：留痕覆盖面新增系统配置模块的 3 个写接口（`system-config:*`），模块/接口计数随之更新。

## Impact

- **数据库**：admin 端 Liquibase 新增 `030-create-system-config.sql`，建 1 张表 `loves_system_config`，纯新增、可回滚。
- **love-space-admin**：新增 `modules/systemconfig`（entity / repository / service / controller / dto），5 个接口。
- **love-space-app**：新增 `modules/systemconfig`（只读 entity / repository / query service / controller），2 个接口。
- **love-space-web**：侧栏新增一级菜单「系统配置」+ 1 个路由页面 `/system-configs`（列表 + 新增/编辑弹窗）。
- **契约**：`contracts/api-spec.json` 新增 admin 端 5 个接口；`love-space-app/docs/openapi.json` 新增 2 个接口。
- **部署/环境变量/外部依赖**：无新增环境变量与依赖；随常规发版执行迁移即可。
