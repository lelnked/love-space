# Proposal: add-system-config（系统配置 key/value）

## Why

运营需要一处地方维护 App 运行时要读取的系统参数（如客户端弹窗开关等），目前每加一个参数都要改代码、发版。本 change 建立：数据库通过迁移预置配置项 `key`（落库 `loves_system_config`，`config_value` 为 `jsonb`）→ 运营在后台按 `key` 进入专属页面表单编辑配置 → App 按 `key` 查询 这条主链路。不开放后台随意新增或删除配置项，也不让运营手写原始 JSON，每个预置 `key` 由专属的表单页面承载编辑。本期预置首个配置项 `ios_llm_popup`（iOS LLM 弹窗开关）。

## What Changes

按功能先后顺序（也是实现顺序）：

| # | 功能 | 端 | 后台菜单 |
|---|---|---|---|
| F1 | 系统配置管理：预置配置分页查询、详情查询、按 key 专属页面表单编辑保存（无新增、无删除）；前后端按 key 校验 value 结构 | admin + web | 系统配置 |
| F2 | App 配置查询：按单个 key 或一组 key 查询配置值 | app | — |

明确**不做**：后台新增配置（`POST`）、后台删除配置（`DELETE`）、通用动态表单引擎或原始 JSON 文本框编辑、配置分组/分类、配置项本身的启用禁用开关、版本历史与回滚、App 端缓存与变更推送、导入导出、App 获取全量配置、给 App 隐藏部分配置的可见性开关。

### 全局约定（各功能共用）

- **key**：系统通过 Liquibase 数据库迁移预置的配置编码，全局唯一、**不可新增、不可删除、不可修改**。格式 `^[a-z][a-z0-9_.]{1,63}$`（小写字母开头，可含数字、下划线、点，2–64 位）。
- **value**：必填，存 `jsonb`；结构由各预置 `key` 明确定义，前端由该 `key` 专属的 Form 页面收集并组装 JSON 对象提交，后端按 `key` 校验 `value` 字段结构与类型。JSON `null` 视为未填 → 400；序列化后长度 ≤ 65535 字符。
- **本期预置配置清单**：

| key | 默认名称 | 默认 value | 默认说明 | value 字段规则 |
|---|---|---|---|---|
| `ios_llm_popup` | iOS LLM 弹窗开关 | `{"enabled": true}` | 控制 iOS 系统调用 LLM 时是否需要弹窗 | JSON 对象，`enabled` 必填且必须为布尔值（`true` / `false`） |

- **可见性**：所有配置都对持有 API-key 的 App 可读。
- **权限**：沿用现有授权链，`/api/admin/system-configs/**` 所有登录运营（ADMIN、MEMBER）均可用，不新增角色或权限点；App 接口沿用全局 `X-API-Key` 校验。
- **操作日志**：编辑保存写现有操作日志（`system-config:update`）；查看不留痕。
- **生效时机**：保存即生效，App 下一次请求读到新值（服务端不缓存）。
- **分页 / 错误格式**：沿用现有 `PageResponse{content,page,size,totalElements,totalPages}`（page 从 1 开始，size 白名单 20/200）与 `{status,error,message,path}`。
- **侧栏**：新增一级菜单「系统配置」，位于「AI 管理」与「操作日志」之间。

---

### F1 系统配置管理

**规则**
- 配置项通过 Liquibase 迁移预置入库，后台只读列表/详情并编辑已有配置，不提供新增与删除接口。
- 编辑请求不含 `key`，`key` 不可修改；`name` 必填 ≤ 64；`description` 选填 ≤ 500。
- `value` 按该配置项的 `key` 做结构校验：
  - Body 中 `value` 缺失或为 JSON `null` → 400「value 不能为空」；超长 → 400「value 过长」。
  - `key = ios_llm_popup`：`value` 必须为 JSON 对象且包含布尔字段 `enabled`（`true` 或 `false`），否则 → 400「ios_llm_popup 的 enabled 必须为布尔值」；服务端落库时规范化为 `{"enabled": <boolean>}`。
- `id` 不存在 → 404「配置不存在」。

**表 `loves_system_config`**（一行 = 一个预置配置项）

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | uuid | PK | |
| config_key | text | NOT NULL, UNIQUE | 配置 key，预置后不可改 |
| name | text | NOT NULL | 配置名称，≤64 |
| config_value | jsonb | NOT NULL | 配置值，结构由 key 决定 |
| description | text | | 说明，≤500 |
| created_at | timestamptz | NOT NULL | |
| updated_at | timestamptz | | |

唯一约束：`uk_loves_system_config_key (config_key)`，同时承担 App 按 key 查询的索引。迁移脚本预置 `ios_llm_popup` 初始行。

**API**

| 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|
| GET | `/api/admin/system-configs/page` | query：`keyword?`（模糊匹配 key / 名称）、`page`、`size`（20/200） | `PageResponse<SystemConfigItem>`，按 `updated_at DESC, created_at DESC` |
| GET | `/api/admin/system-configs/{id}` | path：id | `SystemConfigItem`；不存在 → 404「配置不存在」 |
| PUT | `/api/admin/system-configs/{id}` | body：`{name*, value*, description?}`（不含 key） | `SystemConfigItem`；不存在 → 404；校验失败 → 400 |

`SystemConfigItem` = `{id, key, name, value, description, createdAt, updatedAt}`，其中 `value` 是 JSON 原值（不是字符串化的 JSON）。

操作日志：`system-config:update`

**页面 `/system-configs` 与按 key 专属编辑页 `/system-configs/:id/edit`**

```
┌ 系统配置（/system-configs）──────────────────────────────────────────────┐
│ [搜索 key / 名称          ]①                                             │
├──────────────────────────────────────────────────────────────────────────┤
│ key             名称               value（预览）       更新时间     操作 │
│ ios_llm_popup   iOS LLM 弹窗开关   {"enabled":true}    11-10 22:00  编辑②│
├──────────────────────────────────────────────────────────────────────────┤
│                                              共 1 条  < 1 >  20条/页 ③   │
└──────────────────────────────────────────────────────────────────────────┘

┌ 编辑系统配置：iOS LLM 弹窗开关（/system-configs/:id/edit）───────────────┐
│ ┌ 基本信息 ────────────────────────────────────────────────────────────┐ │
│ │ key           [ios_llm_popup                          ] ④（只读）    │ │
│ │               系统预置配置，key 不可修改                             │ │
│ │ 名称 *        [iOS LLM 弹窗开关                       ] ⑤            │ │
│ │ 说明          [控制 iOS 系统调用 LLM 时是否需要弹窗    ] ⑥           │ │
│ └──────────────────────────────────────────────────────────────────────┘ │
│ ┌ 配置项参数（ios_llm_popup 专属表单）─────────────────────────────────┐ │
│ │ 是否需要弹窗 *  [● 开启（需要弹窗）  ○ 关闭（不弹窗） ] ⑦           │ │
│ └──────────────────────────────────────────────────────────────────────┘ │
│ [保存]⑧  [取消]⑨                                                         │
└──────────────────────────────────────────────────────────────────────────┘
```
①③ → `GET /system-configs/page`；② → 跳转 `/system-configs/{id}/edit`，进入页面后调用 `GET /system-configs/{id}` 拉取配置详情，并按 `config.key` 渲染该配置项专属的表单页面组件（`ios_llm_popup` 渲染 `IosLlmPopupForm`）；⑧ → 组装 `value: { enabled }` 调用 `PUT /system-configs/{id}`，成功后提示「已保存，立即生效」并返回 `/system-configs`；⑨ → 返回 `/system-configs`。列表 value 预览为单行序列化，超过 40 字符截断加省略号，悬停显示全文。后续每新增一个预置 `key`，单独新增一个对应的 Form 页面组件。

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

`AppSystemConfig` = `{key, value}`，`value` 为 JSON 原值（例如 `{"key": "ios_llm_popup", "value": {"enabled": true}}`）。name、description 属于后台管理信息，不下发给 App。

鉴权：全局 `X-API-Key`，缺失或非法 → 401。

---

## Capabilities

### New Capabilities
- `system-config`：系统配置预置 key/value 全链路——Liquibase 预置 `ios_llm_popup`，后台配置的分页查询、详情查询、按 key 专属独立表单页编辑（无新增、无删除，前后端按 key 校验 value 结构），App 按单个 key / 一组 key 查询配置值。新域 `system-config` 在 `tests/modules.md` 登记（端：web）。

### Modified Capabilities
- `operation-log`：留痕覆盖面新增系统配置模块的 1 个写接口（`system-config:update`），模块/接口计数更新为 21 个业务模块共 70 个写接口。

## Impact

- **数据库**：admin 端 Liquibase 新增迁移预置 `ios_llm_popup` 配置记录（`{"enabled": true}`）。
- **love-space-admin**：`modules/systemconfig` 移除 `POST` 新增与 `DELETE` 删除接口及 `SystemConfigCreateRequest`，保留 `GET page`、`GET /{id}`、`PUT /{id}` 3 个接口，并在 `update` 中按 `key` 校验 `value` 结构。
- **love-space-app**：`modules/systemconfig` 保持 2 个只读查询接口不变。
- **love-space-web**：移除通用 JSON 弹窗 `ConfigEditModal.tsx` 及列表页的新增/删除按钮；新增路由 `/system-configs/:id/edit` 及 `ios_llm_popup` 专属表单页组件 `IosLlmPopupForm.tsx`。
- **契约**：`contracts/api-spec.json` 移除 admin 端系统配置的 `POST` 与 `DELETE`，保留 3 个接口。
- **部署/环境变量/外部依赖**：无新增环境变量与依赖；随常规发版执行迁移即可。
