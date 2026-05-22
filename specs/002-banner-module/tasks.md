---

description: "Task list for Banner Module (002-banner-module)"
---

# Tasks: Banner Module

**Input**: Design documents from `/specs/002-banner-module/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: 包含 service 单测、controller MockMvc 集成测试、事件监听器测试与 Specification metamodel 校验测试。规格中已要求"测试与本地可运行性"，因此本任务清单内含测试任务。

**Organization**: 任务按用户故事分阶段；Phase 1/2 为共享基础设施；Phase 3 = US1 (admin CRUD)、Phase 4 = US2 (app 接口)、Phase 5 = US3 (City 状态联动)；Phase 6 = 收尾。

## Format: `[ID] [P?] [Story] Description`

- **[P]**: 不同文件、无依赖时可并行
- **[Story]**: US1 / US2 / US3 对应 spec.md 中的用户故事；Setup / Foundational / Polish 阶段不带 Story 标签
- 所有任务路径均为仓库相对路径

---

## Phase 1: Setup (Shared Infrastructure)

- [X] T001 [P] 在 `love-space-admin/pom.xml` 的 `maven-compiler-plugin` 现有 `annotationProcessorPaths` 中追加 `org.hibernate.orm:hibernate-jpamodelgen` 注解处理器（带 `${hibernate.version}`），验证 metamodel 生成
- [X] T002 [P] 在 `love-space-app/pom.xml` 同样追加 hibernate-jpamodelgen 注解处理器
- [X] T003 [P] 确认两个后端 `.gitignore` 已忽略 `target/`
- [X] T004 admin / app 两端 `./mvnw clean -DskipTests compile` 均通过；`*_` metamodel 生成正常

---

## Phase 2: Foundational (Blocking Prerequisites)

**⚠️ CRITICAL**: 用户故事任务必须在本阶段全部完成后启动

- [X] T005 创建 Liquibase changelog `love-space-admin/src/main/resources/db/changelog/changes/003-create-loves-banner.sql`：建 `loves_banner` 表（列定义见 data-model.md），添加索引 `idx_loves_banner_type_online (type, online)` 与 `idx_loves_banner_linked_entity_id (linked_entity_id)`；附 `INSERT ... SELECT` 一次性从 `loves_city` 中 `banner_sort_order > 0 AND background_image IS NOT NULL` 的行生成对应 CITY banner 记录；提供 rollback SQL（`DROP TABLE loves_banner;`）
- [X] T006 创建 Liquibase changelog `love-space-admin/src/main/resources/db/changelog/changes/004-drop-city-banner-sort-order.sql`：`ALTER TABLE loves_city DROP COLUMN banner_sort_order;` 与相关索引；提供 rollback SQL 重新 `ADD COLUMN banner_sort_order INTEGER NOT NULL DEFAULT 0`
- [X] T007 在 `love-space-admin/src/main/resources/db/changelog/db.changelog-master.yaml` 中按顺序 include 上述两个 changelog（`003-create-loves-banner.sql` 在前、`004-drop-city-banner-sort-order.sql` 在后）
- [X] T008 [P] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/entity/BannerType.java` 创建枚举 `BannerType { CITY }`，配中文 JavaDoc 说明取值与预留扩展（宪法 I）
- [X] T009 [P] 在 `love-space-app/src/main/java/com/space/app/modules/banner/entity/BannerType.java` 创建独立枚举副本（宪法 IV 双后端隔离）
- [X] T010 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/entity/Banner.java` 创建 JPA 实体：UUIDv7 主键（`@PrePersist` 调用项目既有 UUIDv7 生成器）、`name`/`online`/`type`/`imageUrls`（`@JdbcTypeCode(SqlTypes.JSON)` + `List<String>`）/`linkedEntityId`/`createdAt`/`updatedAt`，无外键，每字段中文 JavaDoc（宪法 I/II/III）
- [X] T011 在 `love-space-app/src/main/java/com/space/app/modules/banner/entity/Banner.java` 创建只读副本实体（结构与 admin 相同，包路径独立）
- [X] T012 [P] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/repository/BannerRepository.java` 定义 `interface BannerRepository extends JpaRepository<Banner, UUID>, JpaSpecificationExecutor<Banner>`，禁止使用字段名字符串字面量；如需派生方法用方法名标识符（如 `findAllByLinkedEntityIdAndType`）
- [X] T013 [P] 在 `love-space-app/src/main/java/com/space/app/modules/banner/repository/BannerRepository.java` 定义只读 repo，同样基于 `JpaSpecificationExecutor`
- [X] T014 配置/确认现有 `OperationLog` 注解、统一响应包装 `ApiResponse<T>` 与 `PageResult<T>` 在 banner 模块可复用；不新增基础设施
- [X] T015 执行 `cd love-space-admin && ./mvnw -DskipTests compile` 验证 hibernate-jpamodelgen 在 banner 模块也生成 `Banner_`，路径 `target/generated-sources/annotations/com/loves/space/modules/banner/Banner_.java`

**Checkpoint**: Banner 表已建好、metamodel 已生成、双后端实体就绪，可并行进入用户故事实现

---

## Phase 3: User Story 1 - 运营管理 CITY Banner (Priority: P1) 🎯 MVP

**Goal**: 运营在 admin web "Banner" 菜单中完成 banner CRUD + 列表页上下线开关

**Independent Test**: 登录 admin web，新增一条带 1 张图片、关联 online 城市的 CITY banner（保存后 online=false），在列表页将其切换为 online；调用 `GET /api/admin/banners/{id}` 看到 online=true 且字段完整

### Tests for User Story 1

- [ ] T016 [P] [US1] 在 `love-space-admin/src/test/java/com/loves/space/modules/banner/repository/BannerSpecificationsMetamodelTest.java` 编写测试：通过反射/源扫描断言 `BannerSpecifications` 类源码不出现 `root.get("` / `path.get("` 等字段名字符串字面量调用（宪法 VI 合规守卫）
- [ ] T017 [P] [US1] 在 `love-space-admin/src/test/java/com/loves/space/modules/banner/service/BannerServiceTest.java` 编写 service 单测：创建/更新/列表/查询/删除/校验失败（名称为空、imageUrls 为空、CITY type 无 link、关联城市不存在）
- [ ] T018 [P] [US1] 在 `love-space-admin/src/test/java/com/loves/space/modules/banner/controller/BannerControllerTest.java` 编写 MockMvc 集成测试：POST/PUT/DELETE/GET 列表与详情、PUT 编辑请求若含 `online` 字段返回 400、关联城市离线时 `POST /banners/{id}/online {online:true}` 返回 400 `BANNER_LINKED_CITY_OFFLINE`

### Implementation for User Story 1 — 后端

- [X] T019 [P] [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/dto/BannerCreateRequest.java` 定义 record 字段 `name/type/imageUrls/link`（`@JsonProperty("link")` 映射到字段 `linkedEntityId`），每字段中文 JavaDoc + Bean Validation（`@NotBlank`、`@Size`、`@NotEmpty`、`@NotNull`）
- [X] T020 [P] [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/dto/BannerUpdateRequest.java` 同样定义；service 层若发现 JSON 中显式传入 `online` 键则抛 400
- [X] T021 [P] [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/dto/BannerOnlineRequest.java` 定义 `{ online: Boolean }`
- [X] T022 [P] [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/dto/BannerDetailResponse.java` 与 `BannerListItemResponse.java` 定义 record（含 `linkedCityName` 冗余字段），中文 JavaDoc
- [X] T023 [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/repository/BannerSpecifications.java` 实现 `nameContains` / `hasType` / `onlineEquals` / `linkedTo` 静态方法；**全部通过 `Banner_.name` / `Banner_.type` / `Banner_.online` / `Banner_.linkedEntityId` 引用属性**（宪法 VI），禁止 `root.get("name")` 类字面量
- [X] T024 [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/service/BannerService.java` 实现：`create` / `update` / `delete` / `getById` / `pageList(query, pageable)` / `setOnline(id, online)`；`setOnline` 中当 `type=CITY && online=true` 时调用 `CityRepository.findById` 校验关联城市 online=true，否则抛 `ValidationException("BANNER_LINKED_CITY_OFFLINE")`；列表查询用 `BannerSpecifications` 组合；列表项装配 `linkedCityName` 通过一次性批量查城市；所有方法中文 JavaDoc
- [X] T025 [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/controller/BannerController.java` 实现接口（按 `contracts/admin-banner-api.md`）：`POST /api/admin/banners` / `PUT /api/admin/banners/{id}` / `POST /api/admin/banners/{id}/online` / `GET /api/admin/banners` / `GET /api/admin/banners/{id}` / `DELETE /api/admin/banners/{id}`；每方法 `@OperationLog`、中文 JavaDoc 描述请求/响应/HTTP 语义/鉴权要求
- [X] T026 [US1] 在 `love-space-admin/src/main/java/com/loves/space/modules/city/controller/CityController.java` 中确认/补充 `GET /api/admin/cities?online=true&keyword=...` 支持仅返回 online 城市并按 `chineseName` 模糊匹配；如已支持仅核对参数命名一致；改动需走 metamodel

### Implementation for User Story 1 — 前端

- [X] T027 [P] [US1] 在 `love-space-web/src/services/banners.ts` 实现 API 客户端：`listBanners(params)` / `createBanner` / `updateBanner` / `deleteBanner` / `setBannerOnline` / `getBanner`
- [X] T028 [P] [US1] 在 `love-space-web/src/services/cities.ts` 中扩展 `listOnlineCities(keyword)` 接口（若已存在仅核对签名）
- [X] T029 [P] [US1] 在 `love-space-web/src/pages/Banners/components/CitySelect.tsx` 实现可搜索下拉框：首屏拉取 online 城市，输入即时本地过滤 `chineseName`/`englishName`；空态文案"未找到匹配的城市"
- [X] T030 [US1] 在 `love-space-web/src/pages/Banners/BannerList.tsx` 实现列表页：表格列（name、type、关联城市名、online 开关、updatedAt）、关键字 + type + online 过滤、分页；点击 online 开关调用 `setBannerOnline` 并在 4xx 时 toast 错误并回滚 UI
- [X] T031 [US1] 在 `love-space-web/src/pages/Banners/BannerForm.tsx` 实现新增/编辑共用表单：name、图片上传（复用既有上传组件）、type 选择（首期固定 CITY）、`<CitySelect />` 选择关联城市；**编辑模式 MUST NOT 渲染 online 开关**（FR-009）
- [X] T032 [US1] 在 `love-space-web/src/App.tsx` 注册 `/banners`、`/banners/new`、`/banners/:id/edit` 路由；在 `love-space-web/src/layout/AppSidebar.tsx`（或对应菜单源文件）新增 "Banner" 菜单项
- [X] T033 [US1] 在 `love-space-web/src/pages/Cities` 相关文件中移除 `bannerSortOrder` 表单字段、列表列与提交载荷字段；保持其余城市表单不变
- [X] T034 [US1] 在 `love-space-web` 执行 `npm run build` 与 `npm run lint`，确保通过

**Checkpoint**: 完成后 admin web 可独立完成 banner 完整 CRUD + 启用/禁用；MVP 可演示

---

## Phase 4: User Story 2 - 移动端浏览 Banner (Priority: P1)

**Goal**: app 后端新增 `GET /api/app/banners` 取代旧 explore 接口；删除 explore 模块

**Independent Test**: 在 admin 创建并启用一条 CITY banner（关联 online 城市），用合法 `X-API-Key` 调 `GET /api/app/banners` 应返回该 banner，且 `data` 为 `{id, name}`、`image` 为 url 列表

### Tests for User Story 2

- [ ] T035 [P] [US2] 在 `love-space-app/src/test/java/com/space/app/modules/banner/controller/BannerControllerTest.java` 编写 MockMvc 集成测试：仅 online banner 返回；CITY banner 关联城市 offline 时被过滤；缺失 `X-API-Key` 返回 401；`data.id`/`data.name` 与关联城市匹配
- [ ] T036 [P] [US2] 在 `love-space-app/src/test/java/com/space/app/modules/banner/service/BannerQueryServiceTest.java` 编写单测：构造混合数据（online/offline banner、online/offline 城市、被删除城市 id），断言过滤规则与 N+1 防御（批量 fetch city）

### Implementation for User Story 2 — 后端

- [X] T037 [P] [US2] 在 `love-space-app/src/main/java/com/space/app/modules/banner/dto/BannerItemResponse.java` 定义 record `{ id, name, type, image, data }`；`data` 类型 `Map<String, Object>`，字段中文 JavaDoc
- [X] T038 [US2] 在 `love-space-app/src/main/java/com/space/app/modules/banner/repository/BannerSpecifications.java`（或 service 内部）实现 `onlineTrue()` 与 `hasType(BannerType)` 等过滤器，全部走 `Banner_` metamodel
- [X] T039 [US2] 在 `love-space-app/src/main/java/com/space/app/modules/banner/service/BannerQueryService.java` 实现：取 online=true 的 banner（可选过滤 `type`、`cityId`），按 `updatedAt desc` 排序；对 CITY 类型批量查 `CityRepository` 拿 `{id, online, chineseName}`；跳过 city 不存在或 offline 的；装配 `BannerItemResponse`；全程使用 metamodel；中文 JavaDoc
- [X] T040 [US2] 在 `love-space-app/src/main/java/com/space/app/modules/banner/controller/BannerController.java` 实现 `GET /api/app/banners`（按 `contracts/app-banner-api.md`）；继承既有 `X-API-Key` 鉴权（项目记忆 `project_app_auth_api_key`）；中文 JavaDoc 描述参数/响应/鉴权
- [X] T041 [US2] **删除** `love-space-app/src/main/java/com/space/app/modules/explore/` 整目录（`controller/`、`service/`、`dto/`）
- [X] T042 [US2] 在 `love-space-app/src/main/java/com/space/app/modules/city/repository/CityRepository.java` 中移除既有 `findAllByOnlineTrueAndBannerSortOrderGreaterThanOrderByBannerSortOrderAsc` 方法（旧 explore 专用），并删除 `City` 实体中的 `bannerSortOrder` 字段；如有 `CityItemResponse` 中暴露 `bannerSortOrder` 字段一并清理
- [X] T043 [US2] 在 `love-space-app/src/main/java/com/space/app/modules/city/service/CityService.java` 中移除/重命名 `latestOnline()` 等仅服务 explore 的方法（仅删除真正未被其它模块引用的部分；保留 `findOnlineById`）
- [X] T044 [US2] 删除 explore 相关测试：搜索 `love-space-app/src/test/` 下 `Explore*Test*.java`，连同 explore 相关 fixtures 删除
- [X] T045 [US2] 执行 `cd love-space-app && ./mvnw test` 确认全部通过

**Checkpoint**: app 端旧 explore 接口下线，新 banner 接口可独立验证

---

## Phase 5: User Story 3 - City 状态联动 Banner (Priority: P2)

**Goal**: City online 切换自动同步关联 CITY banner 的 online 状态（事务后置事件）

**Independent Test**: 准备一个 online 城市 X 与两条关联 X 且 online=true 的 banner；切换 X 为 offline，再 GET `/api/app/banners` 不再返回这两条；切回 online，再次出现

### Tests for User Story 3

- [ ] T046 [P] [US3] 在 `love-space-admin/src/test/java/com/loves/space/modules/banner/event/BannerEventListenerTest.java` 编写测试：发布 `CityOnlineChangedEvent(cityId, true, false)`，断言所有关联 CITY banner 的 `online` 被批量置为 false；反向场景同理；单条更新失败时（mock 抛错）城市状态变更仍成功并产生 error log
- [ ] T047 [P] [US3] 在 `love-space-admin/src/test/java/com/loves/space/modules/city/service/CityOnlineEventPublishTest.java` 验证：`CityService.setOnline` 仅在状态发生变化时 `publishEvent`；状态相同时不发布

### Implementation for User Story 3

- [X] T048 [P] [US3] 在 `love-space-admin/src/main/java/com/loves/space/modules/city/event/CityOnlineChangedEvent.java` 定义 `public record CityOnlineChangedEvent(UUID cityId, boolean previousOnline, boolean currentOnline) {}`，中文 JavaDoc
- [X] T049 [US3] 修改 `love-space-admin/src/main/java/com/loves/space/modules/city/service/CityService.java`：
  - 注入 `ApplicationEventPublisher`
  - 修改 `setOnline(id, online)`（或等价方法）：读取旧 online 值；持久化新值后，若旧 ≠ 新则 `publishEvent(new CityOnlineChangedEvent(...))`
  - **移除** `setBannerSort` 方法与对 `bannerSortOrder` 字段的所有引用
  - 中文 JavaDoc 描述事件发布契机
- [X] T050 [US3] 修改 `love-space-admin/src/main/java/com/loves/space/modules/city/controller/CityController.java`：删除 `PUT /api/admin/cities/{id}/banner-sort` 端点与 `CityBannerSortRequest`
- [X] T051 [US3] 在 `love-space-admin/src/main/java/com/loves/space/modules/city/dto/` 下从 `CityCreateRequest`、`CityUpdateRequest`、`CityDetailResponse`、`CityItemResponse` 中**移除** `bannerSortOrder` 字段及其 JavaDoc/校验注解
- [X] T052 [US3] 修改 `love-space-admin/src/main/java/com/loves/space/modules/city/entity/City.java`：**删除** `bannerSortOrder` 字段、getter/setter、相关 JavaDoc 段；类级 JavaDoc 同步更新（移除"banner 排序及上架状态"措辞）
- [X] T053 [US3] 在 `love-space-admin/src/main/java/com/loves/space/modules/banner/event/BannerEventListener.java` 实现：
  - `@Component`
  - `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` 方法 `onCityOnlineChanged(CityOnlineChangedEvent event)`
  - 使用 `EntityManager` 的 `CriteriaUpdate<Banner>` API，**通过 `Banner_.online` / `Banner_.linkedEntityId` / `Banner_.type` 引用字段**（宪法 VI），批量执行 `UPDATE loves_banner SET online=? WHERE linked_entity_id=? AND type='CITY'`
  - `try/catch(Exception e)` 包裹，失败仅 `log.error` 不再抛出
  - 中文 JavaDoc 描述事件契机、事务阶段、失败策略
- [X] T054 [US3] 在前端 `love-space-web/src/pages/Cities` 中删除 `bannerSortOrder` 表单字段与"设置 banner 排序"按钮（如有）；与 T033 合并审视，避免遗留
- [X] T055 [US3] 跨后端编译验证：`cd love-space-admin && ./mvnw test` 通过；`cd love-space-app && ./mvnw test` 通过

**Checkpoint**: 三个用户故事全部可独立验证

---

## Phase 6: Polish & Cross-Cutting Concerns

- [X] T056 [P] 通读 admin 与 app banner 模块的所有 controller / service / DTO / entity / listener，确认中文 JavaDoc 完整（宪法 I）：方法、字段、关键步骤均到位
- [ ] T057 [P] grep 全仓库：`grep -rn "root.get(\"\|path.get(\"" love-space-admin/src/main love-space-app/src/main` 应为零结果（宪法 VI 守门）
- [X] T058 [P] grep 全仓库：确认无 `bannerSortOrder` / `banner_sort_order` / `BannerSortOrder` 残留：`grep -rn -i "bannersortorder\|banner_sort_order" love-space-admin/src love-space-app/src love-space-web/src` 应为零结果
- [ ] T059 按 `specs/002-banner-module/quickstart.md` 第 2 节人工走查端到端流程，记录通过项；任何失败立即建子任务修复
- [X] T060 [P] 在 `love-space-web` 执行 `npm run build && npm run lint` 与 `cd love-space-admin && ./mvnw test`、`cd love-space-app && ./mvnw test`，全部通过后准备 PR

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: 无前置
- **Foundational (Phase 2)**: 依赖 Phase 1 完成；阻塞所有用户故事
- **US1 (Phase 3)**: 依赖 Foundational 完成
- **US2 (Phase 4)**: 依赖 Foundational 完成；与 US1 独立，可并行（不同后端 / 不同包）
- **US3 (Phase 5)**: 依赖 Foundational 完成；建议在 US1 后开始，因 T053 监听器需要 admin 端 Banner 实体已可用（Phase 2 已就绪）；与 US2 互不冲突
- **Polish (Phase 6)**: 三个用户故事全部完成后

### User Story Dependencies

- US1 与 US2 完全独立（admin 后端 + 前端 vs app 后端）
- US3 仅依赖 Phase 2 中 Banner 实体已建好；可与 US1/US2 并行实现

### Within Each User Story

- Tests（T016–T018、T035–T036、T046–T047）SHOULD 先写、先失败再实现（TDD 友好）
- DTO/Entity 在 Service 之前；Service 在 Controller 之前；Controller 在前端 service client 之前
- 前端组件：service 客户端 → 子组件（CitySelect）→ 页面（List/Form）→ 路由 & 菜单

### Parallel Opportunities

- T001 / T002 / T003 并行
- T008 / T009 并行；T012 / T013 并行
- US1 各 DTO（T019–T022）并行；T016–T018 测试并行
- US2 测试 T035 / T036 并行
- US3 测试 T046 / T047 并行；事件类 T048 与监听器骨架可并行准备
- US1 后端（admin）与 US2 后端（app）可由不同开发并行推进

---

## Parallel Example: User Story 1 后端 DTO 与测试

```bash
# 一次性并行启动以下任务：
Task: "T019 Create BannerCreateRequest DTO"
Task: "T020 Create BannerUpdateRequest DTO"
Task: "T021 Create BannerOnlineRequest DTO"
Task: "T022 Create BannerDetailResponse / BannerListItemResponse DTO"
Task: "T016 BannerSpecifications metamodel guard test"
Task: "T017 BannerService unit test scaffolding"
Task: "T018 BannerController MockMvc test scaffolding"
```

---

## Implementation Strategy

### MVP (User Story 1 + User Story 2)

US1 与 US2 都是 P1：US1 让运营能产出 banner，US2 让 app 能消费 banner。建议：

1. 完成 Phase 1 (Setup) + Phase 2 (Foundational)
2. 并行推进 US1（admin + web）与 US2（app 后端）
3. 在 quickstart.md 第 2.1–2.3 节走查通过 → 可做 MVP 演示

### Incremental Delivery

1. MVP（US1 + US2）合入后立即可上线运营 + 移动端
2. 紧接着完成 US3（City 联动）→ 修复"城市下架但 banner 仍展示"的运营风险
3. Phase 6 Polish 收尾后开 PR

### Parallel Team Strategy

- Dev A：US1 后端（admin）— T019–T026
- Dev B：US1 前端 — T027–T034
- Dev C：US2 后端（app）— T035–T045
- Dev D：US3（admin 事件 + city 字段清理）— T046–T055

四路并行均不冲突（不同模块/不同文件）；T033 与 T054 同属 `pages/Cities` 字段清理需要协调一次。

---

## Notes

- 全程使用 `Banner_.fieldName` 引用属性，Code Review MUST 拒绝 `root.get("name")` 等字符串字面量（宪法 VI）
- Liquibase changelog 顺序：`003` 建表 + 迁移数据，`004` 删旧列；rollback SQL 必须可逆
- explore 模块全部删除，不留兼容层（宪法/CLAUDE 指引：不保留 backward-compat shim）
- 提交信息按既有约定加 `admin:` / `app:` / `web:` / `chore:` 前缀；跨子项目合并提交时在 body 中列出范围
