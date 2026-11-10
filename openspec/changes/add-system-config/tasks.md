## 1. 数据库（admin Liquibase）

- [x] 1.1 新增 `love-space-admin/src/main/resources/db/changelog/changes/030-create-system-config.sql`：建表 `loves_system_config`（id uuid PK、config_key text NOT NULL、name text NOT NULL、config_value jsonb NOT NULL、description text、created_at timestamptz NOT NULL、updated_at timestamptz），唯一约束 `uk_loves_system_config_key`，带 `--rollback DROP TABLE loves_system_config;`
- [x] 1.2 新增 `love-space-admin/src/main/resources/db/changelog/changes/031-seed-ios-llm-popup-config.sql` 并在 `db.changelog-master.yaml` 中 include：预置 `ios_llm_popup`（名称「iOS LLM 弹窗开关」，`config_value` 为 `'{"enabled": true}'::jsonb`，说明「控制 iOS 系统调用 LLM 时是否需要弹窗」），带 `--rollback DELETE FROM loves_system_config WHERE config_key = 'ios_llm_popup';`

## 2. admin 后端 `modules/systemconfig`

- [x] 2.1 实体 `SystemConfig extends BaseAuditEntity`：`key`→`config_key`、`name`、`value`→`config_value`（String + `@ColumnTransformer(write = "?::jsonb")`）、`description`
- [x] 2.2 `SystemConfigRepository extends JpaRepository, JpaSpecificationExecutor`：`findByKey`
- [x] 2.3 DTO：移除 `SystemConfigCreateRequest`；保留 `SystemConfigUpdateRequest{name, value: JsonNode, description}`（name `@NotBlank @Size(64)`、description `@Size(500)`，中文 message）与 `SystemConfigItemResponse{id, key, name, value(@JsonRawValue String), description, createdAt, updatedAt}` + `from(entity)`
- [x] 2.4 `SystemConfigService`：保留 `page`（keyword 匹配 key/名称，`updatedAt DESC, createdAt DESC`）、`detail`、`update`（404「配置不存在」），移除 `create` 与 `delete`；value 校验 null/NullNode →「value 不能为空」、`toString()` > 65535 →「value 过长」；当 `key` 为 `ios_llm_popup` 时校验 `value` 为对象且 `enabled` 为布尔值，否则抛 `IllegalArgumentException("ios_llm_popup 的 enabled 必须为布尔值")`
- [x] 2.5 `SystemConfigController` `/api/admin/system-configs`：仅保留 `GET page`、`GET /{id}`、`PUT /{id}`（`@OperationLog("system-config:update")`），移除 `POST` 与 `DELETE`，中文 javadoc
- [x] 2.6 UT/IT（`SystemConfigTest`，MockMvc + Postgres，`@scenario` 注释）：
  - `system-config/系统配置数据模型#预置 ios_llm_popup 配置默认存在且 value 以 JSON 原值返回`
  - `system-config/后台系统配置分页查询#按 keyword 匹配 key 或名称`、`#详情不存在返回 404`、`#未登录被拒绝`
  - `system-config/后台编辑系统配置#编辑不能修改 key`、`#value 为 null 返回 400`、`#ios_llm_popup 的 enabled 非布尔值返回 400`
  - `operation-log/运营写操作留痕#系统配置修改留痕`

## 3. app 后端 `modules/systemconfig`

- [x] 3.1 只读实体 `SystemConfig`（映射 `loves_system_config`，`config_value` 以 jsonb String 读取）与 `SystemConfigRepository`：`findByKey`、`findAllByKeyIn`
- [x] 3.2 `SystemConfigItemResponse{key, value(@JsonRawValue String)}` + `from(entity)`，record `@param` 写全（openapi 生成依赖）
- [x] 3.3 `SystemConfigQueryService`（`@Transactional(readOnly = true)`）：`get(key)` 不存在抛 `ResourceNotFoundException("配置不存在")`；`list(keys)` 拆分/strip/去空/保序去重，0 个或 > 50 个抛 `IllegalArgumentException`，按请求顺序输出
- [x] 3.4 `SystemConfigController` `/api/app/system-configs`：`GET /{key}`、`GET ?keys=`，中文 javadoc
- [x] 3.5 IT（`SystemConfigReadIT`，`@scenario` 注释）：
  - `system-config/App 查询系统配置#单个 key 查询`、`#单个 key 不存在返回 404`、`#批量查询忽略不存在的 key 并保持顺序`、`#批量查询 keys 缺失或超限返回 400`、`#缺少 API-key 返回 401`
  - `system-config/后台编辑系统配置#编辑 value 后 App 立即读到新值`

## 4. web 前端

- [x] 4.1 `src/api/systemConfigs.ts`：保留 `SystemConfigItem`、`SystemConfigQuery`、`SystemConfigUpdateRequest`、`listSystemConfigs`、`getSystemConfig`、`updateSystemConfig`；移除 `createSystemConfig`、`deleteSystemConfig`、`SystemConfigCreateRequest`
- [x] 4.2 `src/pages/SystemConfigs/List.tsx` 列表页：移除顶部「+ 新增配置」按钮与操作列「删除」按钮及 `ConfigEditModal` 引用；操作列仅保留「编辑」按钮，点击后 `navigate(`/system-configs/${it.id}/edit`)`
- [x] 4.3 新增 `src/pages/SystemConfigs/Form.tsx`（路由 `/system-configs/:id/edit`）：根据 `useParams().id` 调用 `getSystemConfig(id)`，加载中显示「加载中...」，失败 toast 并跳回 `/system-configs`；加载成功后按 `config.key` 分发给专属表单组件（`ios_llm_popup` → `IosLlmPopupForm`）
- [x] 4.4 新增 `src/pages/SystemConfigs/IosLlmPopupForm.tsx`（`ios_llm_popup` 专属表单页，删除旧 `ConfigEditModal.tsx`）：展示只读 `key`（附「系统预置配置，key 不可修改」）、必填 `名称`、选填 `说明`，以及专属参数「是否需要弹窗」（开启/关闭开关控件，回填 `value.enabled`）；名称为空前端拦截提示「配置名称不能为空」；保存时组装 `value: { enabled }` 调用 `updateSystemConfig`，成功后 `toast.success("已保存，立即生效")` 并跳回 `/system-configs`
- [x] 4.5 `src/App.tsx` 注册 `/system-configs/:id/edit` 路由
- [x] 4.6 `tsc -b`、`vite build --mode test` 与 `npm run lint` 通过

## 5. 收尾

- [ ] 5.1 admin 与 app 全量 UT + 本 change IT 跑绿（两后端 IT 不并行）：本地 admin 测试环境 OSS `app.storage` 属性绑定失败 + 测试库端口不可达，待环境修复后重跑
- [x] 5.2 核对 `contracts/api-spec.json` 与实现一致（移除 POST/DELETE 及 `SystemConfigCreateRequest`，更新 PUT 描述）
