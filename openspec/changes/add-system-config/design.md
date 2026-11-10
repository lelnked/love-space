## Context

运营后台需要维护 App 运行时读取的系统配置（如 iOS 端调用 LLM 前是否需要弹窗）。配置项的 `key` 由系统通过 Liquibase 数据库迁移预置入库（`loves_system_config` 表，`config_value` 为 `jsonb`），不开放后台新增或删除，也不让运营手写原始 JSON 文本；每个预置 `key` 在前端开发独立的专属表单页面，后端按 `key` 校验 `value` 结构。本期预置首个配置项 `ios_llm_popup`（默认 `{"enabled": true}`）。

现状约束：
- 表结构与预置数据由 admin 端 Liquibase 统一管理，app 端只映射已有表（`ddl-auto: validate`）。
- admin 与 app 刻意不共享代码，实体在两端各写一份。
- 两端都已有可写/可读 jsonb 的先例：admin `AiPromptTemplate.variables` 用 `String` + `@ColumnTransformer(write = "?::jsonb")`。
- 两端用 Jackson 3（`tools.jackson.databind`）。
- 错误响应由各端唯一的 `GlobalExceptionHandler` 产出，已有 `ResourceNotFoundException` → 404、`IllegalArgumentException` → 400。

## Goals / Non-Goals

**Goals:**
- 通过 Liquibase 预置 `ios_llm_popup` 配置项（`{"enabled": true}`）。
- 运营在后台列表页查看配置，点击「编辑」进入该 `key` 专属的独立表单页完成可视化编辑与保存；后端按 `key` 校验 `value` 结构。
- App 按单个 key 或一组 key 读取配置，保存即生效。
- 编辑接口纳入操作日志（`system-config:update`）。

**Non-Goals:**
- 后台新增配置（`POST`）、后台删除配置（`DELETE`）、通用动态表单或原始 JSON 文本框编辑、分组、版本历史、App 缓存与推送、可见性开关（均见 proposal「不做」）。

## Decisions

### D1 value 在实体里用 String 映射 jsonb，在 DTO 里用 JsonNode 入参、原始 JSON 文本出参
- **实体**：`String value` + `@ColumnTransformer(write = "?::jsonb")`，与 `AiPromptTemplate.variables` 同一写法。
- **入参**：`JsonNode value`。Jackson 对 JSON `null` 生成 `NullNode`，字段缺失为 Java `null`；service 统一判断 `value == null || value.isNull() || value.isMissingNode()` → `IllegalArgumentException("value 不能为空")`。长度 > 65535 → `IllegalArgumentException("value 过长")`。
- **按 key 结构校验**：`SystemConfigService#validateAndNormalizeValue(String key, JsonNode value)` 按 `key` 校验 `value`：
  - `ios_llm_popup`：要求 `value.isObject()` 且 `value.get("enabled") != null && value.get("enabled").isBoolean()`，否则抛 `IllegalArgumentException("ios_llm_popup 的 enabled 必须为布尔值")`；落库文本规范化为 `{"enabled":true}` 或 `{"enabled":false}`。
  - 未知/其他 key（如测试构造的辅助行）：保留通用非 null + 长度校验，原样 `value.toString()` 落库。
- **出参**：响应 record 的 `value` 字段用 `String` + `@JsonRawValue`，把库里的 jsonb 文本原样嵌入响应。

### D2 预置配置由 Liquibase 迁移植入，移除 POST / DELETE 接口
- `030-create-system-config.sql` 建表 `loves_system_config`；新增 `031-seed-ios-llm-popup-config.sql` 幂等插入 `ios_llm_popup`（固定 UUID `'019794b6-b400-7000-8000-000000000031'::uuid`，`ON CONFLICT (config_key) DO NOTHING`）。
- 移除 `SystemConfigController#create`、`SystemConfigController#delete` 与 `SystemConfigCreateRequest`，杜绝运营误建或误删 key 导致 App 读取异常。

### D3 编辑请求不含 key
`SystemConfigUpdateRequest` 只有 `name / value / description`。客户端多传的 `key` 被 Jackson 忽略（Spring Boot 默认关闭 `FAIL_ON_UNKNOWN_PROPERTIES`），满足「key 不可改」，由 UT 锚定。

### D4 app 批量查询的解析与排序在 service 内完成
- `keys` 用 `@RequestParam String keys` 接收，缺失由框架返回 400。
- service 按逗号拆分、`strip`、去空项、`LinkedHashSet` 去重保序；数量为 0 或 > 50 → `IllegalArgumentException`（400）。
- 用 `findAllByKeyIn` 一次查出，再按请求顺序映射输出，不存在的 key 自然跳过。
- 单个查询 `findByKey` → 不存在抛 `ResourceNotFoundException("配置不存在")`。
- 服务端不加缓存：表行数少、按唯一索引查询。

### D5 前端按 key 拆分独立表单路由页，不做通用表单或 JSON 弹窗
- 列表页 `/system-configs`（`src/pages/SystemConfigs/List.tsx`）取消「+ 新增配置」与行内「删除」按钮，操作列只保留「编辑」按钮，点击后 `navigate(\`/system-configs/${it.id}/edit\`)`。
- 编辑路由 `/system-configs/:id/edit`（`src/pages/SystemConfigs/Form.tsx`）先调用 `getSystemConfig(id)` 拉取配置项，再按 `config.key` 分发渲染对应的专属表单页面组件：
  - `key === "ios_llm_popup"` → 渲染 `src/pages/SystemConfigs/IosLlmPopupForm.tsx`。
  - 后续每新增一个预置 `key`，在 `src/pages/SystemConfigs/` 下新增一个专属 `*Form.tsx` 组件并在 `Form.tsx` 中按 `key` 分发。
- `IosLlmPopupForm.tsx` 遵循 `docs/web-开发规范.md` §6 独立表单页样式规范（`fieldset` + `legend` 分组）：
  - 基本信息分组：`key`（`disabled` 只读 + 小字「系统预置配置，key 不可修改」）、`名称 *`、`说明`；
  - 配置项参数分组：`是否需要弹窗 *`（开启/关闭单选或开关控件，绑定 `enabled: boolean`）；
  - 提交时组装 `{ name, description, value: { enabled } }` 调用 `updateSystemConfig(id, body)`，成功后 `toast.success("已保存，立即生效")` 并 `navigate("/system-configs")`。

### D6 后台列表排序与 keyword
排序 `updatedAt DESC, createdAt DESC`；keyword 对 key 与名称做 `lower(...) like` 模糊匹配。

### 已定决策
- **`ios_llm_popup` 的 key 与 value 结构**：用户在 clarify 中选定 `key = ios_llm_popup`（名称「iOS LLM 弹窗开关」），`value = {"enabled": true}`（仅含一个布尔开关，保留对象结构便于后续扩展）。
- **新增与删除接口处置**：用户在 clarify 中选定前后端同步移除 `POST` 与 `DELETE` 接口，预置配置统一由 Liquibase 迁移初始化入库。
- **表单组织方式**：用户明确要求取消通用 JSON 编辑弹窗，每个预置配置项单独开发一个专属 Form 页面组件，按 `key` 渲染。

## 接口与文件映射

| 接口 | 实现 |
|---|---|
| `GET /api/admin/system-configs/page` | admin `SystemConfigController#page` → `SystemConfigService#page` |
| `GET /api/admin/system-configs/{id}` | `SystemConfigController#get` → `SystemConfigService#detail` |
| `PUT /api/admin/system-configs/{id}` | `SystemConfigController#update`，`@OperationLog("system-config:update")` |
| `GET /api/app/system-configs/{key}` | app `SystemConfigController#get` → `SystemConfigQueryService#get` |
| `GET /api/app/system-configs?keys=` | app `SystemConfigController#list` → `SystemConfigQueryService#list` |

文件清单：
- admin：`db/changelog/changes/030-create-system-config.sql`、`db/changelog/changes/031-seed-ios-llm-popup-config.sql`；`modules/systemconfig/{entity/SystemConfig, repository/SystemConfigRepository, service/SystemConfigService, controller/SystemConfigController, dto/SystemConfigUpdateRequest, dto/SystemConfigItemResponse}`。
- app：`modules/systemconfig/{entity/SystemConfig, repository/SystemConfigRepository, service/SystemConfigQueryService, controller/SystemConfigController, dto/SystemConfigItemResponse}`。
- web：`src/api/systemConfigs.ts`、`src/pages/SystemConfigs/List.tsx`、`src/pages/SystemConfigs/Form.tsx`、`src/pages/SystemConfigs/IosLlmPopupForm.tsx`、`src/App.tsx` 路由、`src/layout/AppSidebar.tsx` 菜单。

## 界面实现映射

| 线框区域 | 组件 / 文件 |
|---|---|
| ① 搜索 key / 名称 | `pages/SystemConfigs/List.tsx` 内 `FilterBar`（text 字段 `keyword`） |
| 表格（key / 名称 / value 预览 / 更新时间 / 操作） | `List.tsx` 内 `DataTable` 的 `Column[]`；value 预览列单行截断 40 字符，`title` 属性显示全文 |
| ② 编辑按钮 | `DataTable` 操作列唯一按钮 → `navigate(\`/system-configs/${it.id}/edit\`)` |
| ③ 分页 | `List.tsx` 表格右下角 `Pagination`（20 / 200） |
| 编辑页路由容器 | `pages/SystemConfigs/Form.tsx`，按 `useParams().id` 调用 `getSystemConfig(id)`，按 `config.key` 分发给具体表单组件 |
| `ios_llm_popup` 专属表单页 | `pages/SystemConfigs/IosLlmPopupForm.tsx` |
| ④ key（只读） | `IosLlmPopupForm` 内 `Input disabled` + 小字「系统预置配置，key 不可修改」 |
| ⑤ 名称 | `IosLlmPopupForm` 内 `Input`（必填） |
| ⑥ 说明 | `IosLlmPopupForm` 内 `Input`（选填） |
| ⑦ 是否需要弹窗 | `IosLlmPopupForm` 内开启/关闭选择控件，受控绑定 `enabled: boolean` |
| ⑧ 保存 / ⑨ 取消 | `IosLlmPopupForm` 提交 → `updateSystemConfig` → `toast.success("已保存，立即生效")` + `navigate("/system-configs")` |
| 侧栏菜单 | `layout/AppSidebar.tsx`，「AI 管理」与「操作日志」之间一级菜单「系统配置」 |
| 路由 | `App.tsx` `<AppLayout />` 内注册 `/system-configs` 与 `/system-configs/:id/edit` |

## Risks / Trade-offs

- [新增预置配置需要发版] → 预置配置本身意味着客户端有新代码消费该 key，随版本增加 Liquibase 迁移与对应专属表单页符合研发节奏，且彻底消除了运营手写 JSON 写错字段名/类型的风险。

## Migration Plan

1. 发版时 admin 启动自动执行 `030-create-system-config.sql` 与 `031-seed-ios-llm-popup-config.sql`（建表并预置 `ios_llm_popup`）。
2. app 随后发版（app 实体 `validate` 依赖该表存在，**admin 必须先于 app 部署**）。
3. 回滚：changeset 带 `--rollback`，回滚前先回退 app。

## Open Questions

无。
