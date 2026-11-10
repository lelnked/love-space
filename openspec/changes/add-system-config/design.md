## Context

运营后台目前没有通用的参数配置能力，App 需要的开关、文案、阈值都写死在代码里。proposal 已确认：新增 `loves_system_config` 表，admin 端提供增删改查，app 端提供按 key 只读查询，value 用 jsonb 存任意 JSON。

现状约束：
- 表结构由 admin 端 Liquibase 统一管理，app 端只映射已有表（`ddl-auto: validate`）。
- admin 与 app 刻意不共享代码，实体在两端各写一份。
- 两端都已有可写/可读 jsonb 的先例：admin `AiPromptTemplate.variables` 用 `String` + `@ColumnTransformer(write = "?::jsonb")`。
- 两端用 Jackson 3（`tools.jackson.databind`）。
- 错误响应由各端唯一的 `GlobalExceptionHandler` 产出，已有 `ConflictException` → 409、`ResourceNotFoundException` → 404、`IllegalArgumentException` → 400。

## Goals / Non-Goals

**Goals:**
- 运营在后台维护 key/value 配置，value 前后端双重 JSON 校验。
- App 按单个 key 或一组 key 读取配置，保存即生效。
- 新写接口纳入操作日志。

**Non-Goals:**
- 分组、启用开关、版本历史、JSON Schema 校验、App 缓存与推送、可见性开关（均见 proposal「不做」）。

## Decisions

### D1 value 在实体里用 String 映射 jsonb，在 DTO 里用 JsonNode 入参、原始 JSON 文本出参
- **实体**：`String value` + `@ColumnTransformer(write = "?::jsonb")`，与 `AiPromptTemplate.variables` 同一写法，不引入 Hibernate JSON 类型映射。
- **入参**：`JsonNode value`。Jackson 对 JSON `null` 生成 `NullNode`，字段缺失为 Java `null`；service 统一判断 `value == null || value.isNull()` → `IllegalArgumentException("value 不能为空")`。入库存 `value.toString()`（紧凑 JSON），长度 > 65535 → `IllegalArgumentException("value 过长")`。
- **出参**：响应 record 的 `value` 字段用 `String` + `@JsonRawValue`，把库里的 jsonb 文本原样嵌入响应，不做解析与重新序列化。
- **备选**：出参也用 `JsonNode`，需要在 `from()` 里注入 `ObjectMapper` 解析，多一步还要处理解析异常；库里的 jsonb 文本必然是合法 JSON，原样嵌入即可。

### D2 key 唯一性：应用层预检 + 数据库唯一约束兜底
- 新增前 `existsByKey` → `ConflictException("配置 key 已存在")`，与 `AiSceneService.create` 同口径。
- 并发同 key 新增时预检可能同时通过，由唯一约束 `uk_loves_system_config_key` 拦下，此时返回 500。运营后台低并发，接受该边界，代码处加 `ponytail:` 注释说明。

### D3 编辑请求不含 key
`SystemConfigUpdateRequest` 只有 `name / value / description`。客户端多传的 `key` 被 Jackson 忽略（Spring Boot 默认关闭 `FAIL_ON_UNKNOWN_PROPERTIES`），满足「key 不可改」，由 UT 锚定。

### D4 app 批量查询的解析与排序在 service 内完成
- `keys` 用 `@RequestParam String keys` 接收，缺失由框架返回 400。
- service 按逗号拆分、`strip`、去空项、`LinkedHashSet` 去重保序；数量为 0 或 > 50 → `IllegalArgumentException`（400）。
- 用 `findAllByKeyIn` 一次查出，再按请求顺序映射输出，不存在的 key 自然跳过。
- 单个查询 `findByKey` → 不存在抛 `ResourceNotFoundException("配置不存在")`。
- 服务端不加缓存：表行数少、按唯一索引查询，后续有性能问题再加。

### D5 前端用弹窗而非独立路由页
web 规范规定 5 个及以上字段的表单必须用独立路由页；本表单只有 key、名称、value、说明 4 个字段，按规范使用无遮罩弹窗（`showBackdrop={false}`），参考 `Managers` 与 `AiLimits` 的弹窗写法。

### D6 前端 JSON 校验只做「能否解析」
`JSON.parse` 成功且结果不为 `null` 即通过；空串与 `null` 提示「value 不能为空」。提交时把解析后的值（不是字符串）放进请求体的 `value`，保证后端收到 JSON 原值。「格式化」= `JSON.stringify(parsed, null, 2)`。

### D7 后台列表排序与 keyword
排序 `updatedAt DESC, createdAt DESC`（刚改过的配置排前面便于核对）；keyword 对 key 与名称做 `lower(...) like` 模糊匹配，与 AI 场景列表同写法。

### 已定决策
- **App 可见性**：全部配置对 App 可读，不做可见性开关；弹窗内提示勿存机密。（用户在 proposal 门禁选定选项 1。）
- **key 格式** `^[a-z][a-z0-9_.]{1,63}$`、创建后不可改；**删除**为物理删除；**value 上限** 65535 字符；**批量 keys 上限** 50。均为默认值拍板，理由见 proposal 全局约定。

## 接口与文件映射

| 接口 | 实现 |
|---|---|
| `GET /api/admin/system-configs/page` | admin `SystemConfigController#page` → `SystemConfigService#page` |
| `GET /api/admin/system-configs/{id}` | `SystemConfigController#get` |
| `POST /api/admin/system-configs` | `SystemConfigController#create`，`@OperationLog("system-config:create")`，201 |
| `PUT /api/admin/system-configs/{id}` | `SystemConfigController#update`，`@OperationLog("system-config:update")` |
| `DELETE /api/admin/system-configs/{id}` | `SystemConfigController#delete`，`@OperationLog("system-config:delete")`，204 |
| `GET /api/app/system-configs/{key}` | app `SystemConfigController#get` → `SystemConfigQueryService#get` |
| `GET /api/app/system-configs?keys=` | app `SystemConfigController#list` → `SystemConfigQueryService#list` |

文件清单：
- admin：`db/changelog/changes/030-create-system-config.sql`；`modules/systemconfig/{entity/SystemConfig, repository/SystemConfigRepository, service/SystemConfigService, controller/SystemConfigController, dto/SystemConfigCreateRequest, dto/SystemConfigUpdateRequest, dto/SystemConfigItemResponse}`。
- app：`modules/systemconfig/{entity/SystemConfig, repository/SystemConfigRepository, service/SystemConfigQueryService, controller/SystemConfigController, dto/SystemConfigItemResponse}`。
- web：`src/api/systemConfigs.ts`、`src/pages/SystemConfigs/List.tsx`、`src/pages/SystemConfigs/ConfigEditModal.tsx`、`src/App.tsx` 路由、`src/layout/AppSidebar.tsx` 菜单。

## 界面实现映射

| 线框区域 | 组件 / 文件 |
|---|---|
| ① 搜索 key / 名称 | `pages/SystemConfigs/List.tsx` 内 `FilterBar`（text 字段 `keyword`） |
| ② + 新增配置 | `List.tsx` 顶部按钮 → 打开 `ConfigEditModal`（create 模式） |
| 表格（key / 名称 / value 预览 / 更新时间 / 操作；说明不在列表展示） | `List.tsx` 内 `DataTable` 的 `Column[]`；value 预览列单行截断 40 字符，`title` 属性显示全文 |
| ③ 编辑 | `DataTable` 操作列按钮 → `getSystemConfig(id)` → `ConfigEditModal`（edit 模式） |
| ④ 删除 | 操作列按钮 → `useConfirm()` 二次确认 → `deleteSystemConfig(id)` → `useToast()` 反馈并刷新 |
| ⑤ 分页 | `List.tsx` 表格右下角 `Pagination`（20 / 200） |
| 弹窗外壳 | `pages/SystemConfigs/ConfigEditModal.tsx`，`Modal showBackdrop={false}` |
| ⑥ key（编辑时只读） | `ConfigEditModal` 内 `Input`，edit 模式 `disabled` 并附「创建后不可修改」小字 |
| ⑦ 名称 | `ConfigEditModal` 内 `Input` |
| ⑧ 格式化 | `ConfigEditModal` 内按钮，调用 `parseJsonValue()` 后 `JSON.stringify(v, null, 2)` |
| ⑨ value 文本框 + 校验错误 | `ConfigEditModal` 内 `textarea`（等宽字体），`onBlur` 与提交时校验，错误显示在下方 `text-error-500` |
| ⑩ 说明 | `ConfigEditModal` 内 `Input` |
| 机密提示 | `ConfigEditModal` 底部 `text-xs text-gray-400` 常驻提示 |
| ⑪ 保存 / 后端错误 | `ConfigEditModal` 提交 → `createSystemConfig` / `updateSystemConfig`；失败时把后端 `detail` 显示在弹窗内，弹窗不关闭 |
| 侧栏菜单 | `layout/AppSidebar.tsx`，「AI 管理」与「操作日志」之间新增「系统配置」 |
| 路由 | `App.tsx` `<AppLayout />` 内注册 `/system-configs` |

## Risks / Trade-offs

- [运营误删或改坏配置导致 App 异常] → 删除二次确认并提示影响；操作日志可追溯改前改后由谁操作；App 侧应对缺失 key 做兜底（App 客户端不在本仓库，交付时告知）。
- [配置里误存机密] → 弹窗常驻提示；不做技术拦截。
- [并发同 key 新增返回 500 而非 409] → 低并发后台可接受，见 D2。
- [value 很大时 App 批量查询响应变大] → 单值 65535 字符 × 最多 50 个，最坏约 3MB；实际配置远小于此，暂不另设整体上限。

## Migration Plan

1. 发版时 admin 启动自动执行 `030-create-system-config.sql`（纯新增表）。
2. app 随后发版（app 实体 `validate` 依赖该表存在，**admin 必须先于 app 部署**，与现有发版顺序一致）。
3. 回滚：changeset 带 `--rollback DROP TABLE loves_system_config;`，回滚前先回退 app。

## Open Questions

无。
