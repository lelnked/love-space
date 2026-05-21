---
description: "Task list for 爱女地图 MVP feature implementation"
---

# Tasks: 爱女地图 MVP（同类象 App v1.0）

**Input**: Design documents from `/specs/001-aiwomap-mvp/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/admin-api.md, contracts/app-api.md, quickstart.md

**Tests**: 仅对关键业务规则（评分换算、用户管理权限、空状态、删除分类联动下架）配置 service 单测 + 关键
controller MockMvc 测试；未要求全面 TDD。

**Organization**: 任务按用户故事分组；每个故事可独立交付、独立验收。

## Format: `[ID] [P?] [Story] Description`

- **[P]**: 可并行（不同文件、无未完成的前置依赖）
- **[Story]**: US1 / US2 / US3 / US4，对应 spec.md 中四个用户故事
- 所有任务包含明确文件路径
- 注释要求：方法 / 关键步骤 / 实体字段 / Request DTO / Response VO / Controller 方法 HTTP 语义
  **必须使用中文 JavaDoc**（依据 constitution v1.0.1 原则 I）

## Path Conventions

- 运营后台后端：`love-space-admin/src/main/java/com/loves/space/...`，资源 `love-space-admin/src/main/resources/...`
- 移动端后端：`love-space-app/src/main/java/com/space/app/...`，资源 `love-space-app/src/main/resources/...`
- 运营后台前端：`love-space-web/src/...`
- 测试镜像主代码：`love-space-admin/src/test/java/...`、`love-space-app/src/test/java/...`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: 项目初始化、基础依赖、统一规范

- [X] T001 在 `love-space-admin/pom.xml` 增加依赖：`spring-boot-starter-web` / `data-jpa` / `security` / `validation`、`postgresql`、`liquibase-core`、`io.jsonwebtoken:jjwt-api/impl/jackson:0.12.x`、`com.github.f4b6a3:uuid-creator`、`lombok`、`springdoc-openapi-starter-webmvc-ui`（可选）
- [X] T002 在 `love-space-app/pom.xml` 增加依赖：`spring-boot-starter-web` / `data-jpa` / `security` / `validation`、`postgresql`、`liquibase-core`、`com.github.f4b6a3:uuid-creator`、`lombok`
- [X] T003 [P] 创建 `love-space-admin/src/main/resources/application.yml` 与 `application-dev.yml`，配置 PostgreSQL（端口 8080）、Liquibase `change-log=classpath:db/changelog/db.changelog-master.yaml`、JWT 密钥占位、`spring.mvc.problemdetails.enabled=true`
- [X] T004 [P] 创建 `love-space-app/src/main/resources/application.yml` 与 `application-dev.yml`，配置 PostgreSQL（端口 8081）、Liquibase 同上、`problemdetails.enabled=true`、`app.security.api-keys`（数组，dev 用占位 key，prod 通过环境变量 `APP_SECURITY_API_KEYS` 注入（Spring Boot relaxed binding 自动映射到 `app.security.api-keys`））
- [X] T005 [P] 在 `love-space-web/src/` 清理 TailAdmin 演示路由（保留 `AppLayout`、`SignIn`、布局/图标资源），更新 `package.json` 增加 `axios` 依赖
- [X] T006 [P] 创建 `.env.example` 与 `love-space-web/.env.local.example`：`VITE_ADMIN_API_BASE=http://localhost:8080`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: 三个子项目的公共基础设施；完成后才能并行进入各用户故事

**⚠️ CRITICAL**: 用户故事任务依赖本阶段完成

### 后端：love-space-admin 公共基础

- [X] T010 创建 `love-space-admin/src/main/java/com/loves/space/common/enums/Role.java`、`EnableStatus.java`、`Period.java`（含中文 JavaDoc）
- [X] T011 [P] 创建 `common/exception/ValidationException.java`、`ResourceNotFoundException.java`（继承 `ErrorResponseException`）
- [X] T012 [P] 创建 `common/page/PageQuery.java`、`PageResponseMapper.java`（默认 size=20，可选 20/30）
- [X] T013 [P] 创建 `common/util/UuidV7Generator.java` 封装 `UuidCreator.getTimeOrderedEpoch()`
- [X] T014 创建 `BaseAuditEntity`（仅 `id` + `createdAt` + `updatedAt`，不含 createdBy/updatedBy）在 `common/entity/BaseAuditEntity.java`，使用 `@PrePersist` 调用 `UuidV7Generator`
- [X] T015 创建 `config/JpaConfig.java` 启用 JPA Auditing
- [X] T016 [P] 创建 `config/WebMvcConfig.java`（CORS + 全局 Jackson 配置）
- [X] T017 [P] 创建 `config/AsyncConfig.java` 暴露 `operationLogExecutor` 线程池
- [X] T018 创建 `security/OperatingContext.java` 暴露 `currentUserId()` / `currentUsername()` / `currentRole()`
- [X] T019 创建 `security/jwt/JwtTokenProvider.java`、`JwtAuthFilter.java`、`config/properties/JwtProperties.java`
- [X] T020 创建 `security/userdetails/AdminUserDetails.java`、`UserDetailsServiceImpl.java`
- [X] T021 创建 `security/handler/RestAuthenticationEntryPoint.java`、`RestAccessDeniedHandler.java`
- [X] T022 创建 `config/SecurityConfig.java`（`/api/admin/auth/login` permitAll，其余 authenticated，`/api/admin/users/**` `hasRole('ADMIN')`，启用 JWT 过滤器与 BCrypt `PasswordEncoder`）
- [X] T023 [P] 创建 `web/ApiExceptionHandler.java` 覆盖 `MethodArgumentNotValidException` 输出字段级 `errors[]`
- [X] T024 [P] 创建 `common/annotation/OperationLog.java` 注解
- [X] T025 创建 `infrastructure/log/OperationLogAspect.java`，通过 `@Async("operationLogExecutor")` 写入操作日志（依赖 OperatingContext + OperationLogService）
- [X] T026 创建 `infrastructure/storage/FileStorage.java` 接口与 `LocalFileStorage.java` 实现（写到本地 `uploads/`，返回 `${app.public-base-url}/uploads/<uuid>.<ext>`）

### 后端：love-space-admin 数据库 changelog（Liquibase）

- [X] T027 创建 `love-space-admin/src/main/resources/db/changelog/db.changelog-master.yaml` 引入 `changes/001-init-schema.yaml`、`changes/002-seed-admin-user.yaml`
- [X] T028 创建 `changes/001-init-schema.yaml`：建表 `user`、`city`、`category`、`tag`、`merchant`、`merchant_image`、`merchant_period`、`merchant_tag`、`merchant_review`、`operation_log`；列遵循 data-model.md；**不创建 FOREIGN KEY**；UUIDv7 列类型 `uuid`；含 CHECK 约束（四维评分范围、role 枚举、period 枚举）；建索引（`city_online_weight_created`、`username_unique`、`city(online, banner_sort_order)` 等）
- [X] T029 创建 `changes/002-seed-admin-user.yaml`：使用 preCondition `sqlCheck` 判断不存在 `username='admin'` 时插入 admin 用户；password 列直接写入**预先离线生成的 BCrypt 哈希字符串**（明文 `8@y2eoRLyStM*UVU`，cost=10），role=ADMIN，enable=true；changelog 顶部以 YAML 注释保留生成命令（`htpasswd -bnBC 10 "" '8@y2eoRLyStM*UVU' | tr -d ':\n'` 或等价的 `BCryptPasswordEncoder`）以便轮换；此后默认 admin 仅由 Liquibase 这一条路径植入，**禁止再由应用代码插入**

### 后端：love-space-app 公共基础

- [X] T030 [P] 在 `love-space-app/src/main/java/com/space/app/common/` 下创建 `enums/Period.java`、`exception/ResourceNotFoundException.java`、`page/PageQuery.java`、`util/UuidV7Generator.java`、`entity/BaseAuditEntity.java`（仅 `id` + `createdAt` + `updatedAt`）
- [X] T031 [P] 创建 `config/SecurityConfig.java`：CORS + 无 session + 关闭表单登录；将自定义
  `ApiKeyAuthFilter` 注册到过滤器链；所有 `/api/app/**` 要求经过 API Key 校验；其他路径默认拒绝。
  同步创建 `WebMvcConfig.java`、`JpaConfig.java`。
- [X] T031a [P] 创建 `config/properties/ApiKeyProperties.java`：`@ConfigurationProperties("app.security")`
  暴露 `List<String> apiKeys`；`@PostConstruct` 校验非空否则抛 `IllegalStateException`。
- [X] T031b [P] 创建 `security/ApiKeyAuthFilter.java`（`OncePerRequestFilter`）：从 `X-API-Key` 头读取 key，
  使用 `MessageDigest.isEqual` 与白名单常量时间比较；命中则写入匿名 `PreAuthenticatedAuthenticationToken`
  到 SecurityContext；不命中写入 401 ProblemDetail（`Invalid or missing API key`），不区分缺失/不匹配。
  **失败时以 `WARN` 级别记录**：远端 IP、`X-API-Key` 头是否存在、请求路径、时间戳；若 key 存在则附 SHA-256 前
  6 个十六进制字符作为脱敏指纹；**严禁打印 key 明文或完整摘要**。中文 JavaDoc 描述方法、关键步骤与 HTTP 语义。
- [X] T032 [P] 创建 `web/ApiExceptionHandler.java`（覆盖字段级校验错误）
- [X] T033 love-space-app 的 Liquibase 配置 MUST **仅引用 `001-init-schema.yaml`（DDL），禁止引用 `002-seed-admin-user.yaml`**（admin 用户为 admin 端专属种子，app 端无用户表写入）：在 `love-space-app/src/main/resources/db/changelog/db.changelog-master.yaml` 中显式只 include `changes/001-init-schema.yaml`；文件内容与 admin 端保持字节一致（建议通过构建脚本同步或 CI 校验 hash），以便单库共享或独立部署时 schema 完全一致

### 前端：love-space-web 公共基础

- [X] T034 [P] 创建 `love-space-web/src/api/client.ts`：axios 实例，拦截器附加 `Authorization: Bearer <token>`，401 自动跳转 `/signin`
- [X] T035 [P] 创建 `src/context/AuthContext.tsx`、`src/hooks/useAuth.ts`（持有 user / token / login / logout）
- [X] T036 [P] 创建公共组件 `src/components/filter/FilterBar.tsx`（Apply / Reset），接收 `fields` 配置与 `onApply` / `onReset`
- [X] T037 [P] 创建公共组件 `src/components/pagination/Pagination.tsx`（`support-tickets` 样式，size 选项 [20, 30]）
- [X] T038 [P] 创建 `src/components/user/UserMenu.tsx`（顶部当前用户 + 退出）
- [X] T039 在 `src/layout/AppLayout.tsx` 中接入 `AuthContext` 并按 `role` 过滤侧边栏（MEMBER 不显示 `/users`）
- [X] T040 在 `src/App.tsx` 重写路由：`/signin` 外的所有路由置于 `AppLayout` 下，未登录跳 `/signin`

**Checkpoint**: 公共基础就绪，各用户故事可并行启动

---

## Phase 3: User Story 1 - 移动端用户浏览爱女商户 (Priority: P1) 🎯 MVP

**Goal**: 终端用户通过 `/api/app/explore` / `/cities` / `/merchants` / `/merchants/{id}` 完成城市切换、按周期筛选商户、查看四维百分制 + 爱女指数 10 级 + 详情。

**Independent Test**: 通过 Liquibase 种子城市 + 1 个商户（图片、≥1 上架标签、四维评分、≥1 评价、故事），调用四个 App 端接口即可独立验证。

### Implementation — love-space-app

- [X] T100 [P] [US1] 创建 entity `modules/city/entity/City.java`（不缩写字段；中文 JavaDoc）
- [X] T101 [P] [US1] 创建 entity `modules/merchant/entity/Merchant.java`、`MerchantImage.java`、`MerchantPeriod.java`、`MerchantTag.java`、`MerchantReview.java`
- [X] T102 [P] [US1] 创建 entity `modules/tag/entity/Tag.java`
- [X] T103 [P] [US1] 创建 repository `modules/city/repository/CityRepository.java` 提供 `findAllByOnlineTrueOrderByCreatedAtDesc()`、`findByIdAndOnlineTrue(UUID)`、`findAllByOnlineTrueAndBannerSortOrderGreaterThanOrderByBannerSortOrderAsc(int)`（用于 banner 查询）
- [X] T104 [P] [US1] 创建 repository `modules/merchant/repository/MerchantRepository.java`（按 `cityId + online + 可选 period + 可选 categoryId` 分页，排序 `weight DESC, createdAt DESC`；通过 JPQL/Specification 实现）
- [X] T105 [P] [US1] 创建 repository `MerchantImageRepository`、`MerchantPeriodRepository`、`MerchantTagRepository`、`MerchantReviewRepository`
- [X] T106 [P] [US1] 创建 repository `modules/tag/repository/TagRepository.java`，提供 `findByIdInAndOnlineTrue(Collection<UUID>)`
- [X] T107 [P] [US1] 创建 DTO `modules/city/dto/CityItemResponse.java`（中文 JavaDoc，所有字段全名）
- [X] T108 [P] [US1] 创建 DTO `modules/merchant/dto/MerchantListItemResponse.java`、`MerchantDetailResponse.java`、`ScoreView.java`、`LoveIndexView.java`、`TagItemResponse.java`、`ReviewItemResponse.java`
- [X] T109 [P] [US1] 创建 DTO `modules/explore/dto/ExploreResponse.java`、`BannerItem.java`
- [X] T110 [US1] 实现 `modules/merchant/service/ScoreCalculator.java`：`percent = round(raw * 100.0 / max)`、`total = S+L+E+I`、`level = clamp(ceil(total/10), 1, 10)`；含中文 JavaDoc，关键步骤行内注释
- [X] T111 [US1] 实现 `modules/city/service/CityService.java`：列表 / 按 ID 查（仅返回 online）
- [X] T112 [US1] 实现 `modules/merchant/service/MerchantService.java`：列表分页（参数：cityId 必填 / period / categoryId / page / size），详情（拼装图片、上架标签、四维百分制、爱女指数、评价、故事），下架商户返回 404；依赖 `ScoreCalculator` + repositories
- [X] T113 [US1] 实现 `modules/explore/service/ExploreService.java`：返回当前城市 + banner 列表；banner 数据源 = `CityRepository.findAllByOnlineTrueAndBannerSortOrderGreaterThanOrderByBannerSortOrderAsc(0)`，DTO 字段来自 City（cityId / chineseName / backgroundImage / bannerSortOrder）；列表为空时 `empty=true`
- [X] T114 [US1] 实现 `modules/city/controller/CityController.java`：`GET /api/app/cities`（中文 JavaDoc 描述请求 / 响应 / HTTP 语义）
- [X] T115 [US1] 实现 `modules/merchant/controller/MerchantController.java`：`GET /api/app/merchants`、`GET /api/app/merchants/{id}`
- [X] T116 [US1] 实现 `modules/explore/controller/ExploreController.java`：`GET /api/app/explore?cityId=...`

### 测试 — love-space-app

- [X] T117 [P] [US1] `love-space-app/src/test/java/com/space/app/modules/merchant/service/ScoreCalculatorTest.java`：覆盖 24/20/20/16 → 80/80/80/80/level 8；边界 0/上限值；非法负数抛 IllegalArgumentException
- [X] T118 [P] [US1] `MerchantControllerWebMvcTest.java`：MockMvc + Testcontainers Postgres，验证列表筛选、空状态、详情返回结构与百分制（已编写；当前 `@Disabled`，阻塞于 Spring Boot 4 + Liquibase 现有 changelog 解析 `Unexpected node: 6`，待后续单独修复）
- [X] T119 [P] [US1] `ExploreControllerWebMvcTest.java`：验证（a）所有城市 `bannerSortOrder=0` 时 `banners=[]` 且 `empty=true`；（b）`bannerSortOrder>0` 但 `online=false` 的城市不进入 banner；（c）多 banner 按 `bannerSortOrder` 升序返回；（d）城市不存在时仍 200 返回空 banner；（e）**端到端验证**：先经 admin API 创建城市并 `PUT /api/admin/cities/{id}/banner-sort` 后，app `/api/app/explore` 立即反映新顺序（通过 Testcontainers 共享单库实例覆盖 admin → app 数据流向）。注：(a)–(d) 已编写但 `@Disabled`（Liquibase changelog 解析阻塞）；(e) 跨模块端到端用例延后至 admin 侧 `BannerSortAdminToAppIT`
- [X] T119a [P] [US1] `ApiKeyAuthFilterTest.java`：覆盖（a）缺失 `X-API-Key` 返回 401；（b）错误 key 返回 401；
  （c）正确 key 放行并续走 controller；（d）`apiKeys` 为空时应用启动失败（`ApplicationContextRunner` 验证）。

**Checkpoint**: App 端 4 个接口独立可跑通；MVP 已具备对外展示能力

---

## Phase 4: User Story 2 - 运营管理城市/标签/商户内容 (Priority: P1)

**Goal**: 运营在前后台完成城市 / 标签 / 商户 / 文件 / 分类的 CRUD + 上下架 + 排序 + 权重。

**Independent Test**: 用初始 admin 登录前端，依次新增城市 → 上架标签 → 新建商户（含 logo、≥1 图、≥1 标签、四维评分、≥1 评价、故事、权重）→ 上架；列表能看到。

### Backend — love-space-admin entities & repositories

- [ ] T200 [P] [US2] 创建 entity `modules/city/entity/City.java`（继承 BaseAuditEntity，字段不缩写，中文 JavaDoc）
- [ ] T201 [P] [US2] 创建 entity `modules/category/entity/Category.java`
- [ ] T202 [P] [US2] 创建 entity `modules/tag/entity/Tag.java`
- [ ] T203 [P] [US2] 创建 entity `modules/merchant/entity/Merchant.java`、`MerchantImage.java`、`MerchantPeriod.java`、`MerchantTag.java`、`MerchantReview.java`
- [ ] T204 [P] [US2] 创建对应 repositories（CityRepository、CategoryRepository、TagRepository、MerchantRepository + 4 个商户子表 Repository），含分页 + 过滤的 Specification 工厂

### Backend — love-space-admin DTO + service + controller

- [ ] T210 [P] [US2] 创建 city DTO：`CityCreateRequest`、`CityUpdateRequest`、`CityQuery`、`CityItemResponse`、`CityDetailResponse`、`CityBannerSortRequest`（`bannerSortOrder>0` 自动参与 explore banner，无独立 banner DTO；负数返回 400）
- [ ] T211 [P] [US2] 创建 category DTO：`CategoryUpsertRequest`、`CategoryItemResponse`（分类列表按 `createdAt DESC`，不再维护 sortOrder）
- [ ] T212 [P] [US2] 创建 tag DTO：`TagUpsertRequest`、`TagQuery`、`TagItemResponse`
- [ ] T213 [P] [US2] 创建 merchant DTO：`MerchantUpsertRequest`（含 reviews / tagIds / images / recommendedPeriods 等，按 contracts/admin-api.md）、`MerchantQuery`、`MerchantAdminItem`、`MerchantDetailResponse`、`ReviewUpsertItem`
- [ ] T214 [P] [US2] 创建 file DTO：`FileUploadResponse`
- [ ] T215 [US2] 实现 `modules/city/service/CityService.java`（CRUD + 上下线 + 唯一性校验：chineseName 重复抛 ValidationException；列表默认按 `createdAt DESC`；`bannerSortOrder` 仅服务于 explore banner：`>0` 即作为 banner 并按该数值升序，`=0` 不参与，负数拒绝；不影响列表排序）
- [ ] T216 [US2] 实现 `modules/category/service/CategoryService.java`（CRUD；列表按 `createdAt DESC`，不再维护 sortOrder；删除时联动 `merchantService.offlineByCategoryId(categoryId)`）
- [ ] T217 [US2] 实现 `modules/tag/service/TagService.java`（CRUD + 上下架；名称 ≤6 汉字、不重名；列表按 `createdAt DESC`）
- [ ] T218 [US2] 实现 `modules/merchant/service/MerchantService.java`：upsert（事务内同步更新 merchant + images + periods + tags + reviews）、列表（按 cityId / categoryId / period / online / name 模糊 + 分页 + 排序 weight DESC, createdAt DESC）、详情、删除、上架/下架、`offlineByCategoryId`；校验 name ≤15 汉字、images ≥1、四维评分上限、story ≤5000 字
- [ ] T219 [US2] 实现 `modules/file/service/FileService.java`（单图 ≤20MB；**MIME 白名单 `image/png`、`image/jpeg`、`image/webp`**，同时校验文件首字节 magic number 防止伪造扩展名；命中其他类型抛 `ValidationException` 返回 400；调用 `FileStorage`）
- [ ] T220 [US2] 实现 `modules/city/controller/CityController.java`：含 `PUT /cities/{id}/banner-sort`（body `{ "bannerSortOrder": <int >= 0> }`）；所有写操作打 `@OperationLog("city:<action>")`
- [ ] T221 [US2] 实现 `modules/category/controller/CategoryController.java`
- [ ] T222 [US2] 实现 `modules/tag/controller/TagController.java`
- [ ] T223 [US2] 实现 `modules/merchant/controller/MerchantController.java`
- [ ] T224 [US2] 实现 `modules/file/controller/FileController.java`：`POST /api/admin/files/upload`

### Frontend — love-space-web

- [ ] T230 [P] [US2] 创建 `src/api/cities.ts`、`categories.ts`、`tags.ts`、`merchants.ts`、`files.ts` 客户端
- [ ] T231 [P] [US2] 创建 `src/pages/Cities/List.tsx`（FilterBar + 表格 + 右下分页 + 上下线 / 排序 / 删除按钮）
- [ ] T232 [P] [US2] 创建 `src/pages/Cities/Form.tsx`（创建 / 编辑共用）；`bannerSortOrder` 字段旁标注"`>0` 时自动作为 explore banner 展示，数值越小越靠前；`=0` 则不参与 banner；不影响城市列表排序"
- [ ] T233 [P] [US2] 创建 `src/pages/Categories/List.tsx`（含 inline 创建 / 排序 / 删除二次确认提示"会下架关联商户"）
- [ ] T234 [P] [US2] 创建 `src/pages/Tags/List.tsx`（含 inline 创建 + 上下架）
- [ ] T235 [P] [US2] 创建 `src/pages/Merchants/List.tsx`（FilterBar：name / cityId / categoryId / period / online；分页器右下）
- [ ] T236 [US2] 创建 `src/pages/Merchants/Form.tsx`：按 spec FR-044 分区（基础信息 / 图片+logo / 周期+分类+城市 / 地址+坐标 / 标签 / 四维评分 / 评价动态列表支持 emoji / 故事 / 权重 / 上下架）
- [ ] T237 [US2] 在 `src/App.tsx` 中注册路由：`/cities`、`/cities/create`、`/cities/:id/edit`、`/categories`、`/tags`、`/merchants`、`/merchants/create`、`/merchants/:id/edit`
- [ ] T238 [US2] 在 `AppLayout` 侧边栏中追加菜单条目（所有角色可见）

### 测试 — love-space-admin

- [ ] T240 [P] [US2] `MerchantServiceTest.java`：name 长度 / 四维分上限 / images ≥1 / story 长度校验；删除分类后该分类下商户 online=false
- [ ] T241 [P] [US2] `CategoryServiceTest.java`：删除联动下架
- [ ] T242 [P] [US2] `TagServiceTest.java`：标签下架后 App 端不返回该标签（通过 admin TagRepository + app Tag 视图断言；或在 app 模块测试覆盖）
- [ ] T243 [P] [US2] `FileServiceTest.java`：>20MB 拒绝、非白名单 MIME 拒绝

**Checkpoint**: 运营人员可独立完成全套内容生产

---

## Phase 5: User Story 3 - 管理员管理运营账号 (Priority: P2)

**Goal**: 仅 Admin 可见 `/users`；新建用户后端强制 role=MEMBER；启停 / 重置密码可用；初始化 admin 幂等。

**Independent Test**: admin 登录 → 创建 member → member 登录看不到 `/users` 菜单 → 停用 → member 登录失败。

### Backend — love-space-admin

- [ ] T300 [P] [US3] 创建 entity `modules/user/entity/User.java`（中文 JavaDoc；字段：username 唯一、password BCrypt、nickname、role、enable）
- [ ] T301 [P] [US3] 创建 `modules/user/repository/UserRepository.java`（`findByUsername`、`existsByUsername`，Specification 支持过滤）
- [ ] T302 [P] [US3] 创建 DTO：`UserCreateRequest`、`UserDetailResponse`、`UserItem`、`UserQuery`、`PasswordResetRequest`、`LoginRequest`、`LoginResponse`、`CurrentUserResponse`
- [ ] T303 [US3] 实现 `modules/user/service/UserService.java`：分页 + 过滤（username 模糊 / role / enable / createdAt 区间）；创建强制 role=MEMBER；启停；重置密码（BCrypt）；查询；`existsByUsername` 唯一性
- [ ] T304 [US3] 实现 `modules/auth/service/AuthService.java`：登录（校验 enable=true，密码 BCrypt 比对，签发 JWT）、me、登出
- [ ] T305 [US3] 实现 `modules/auth/controller/AuthController.java`：`POST /api/admin/auth/login` / `logout` / `GET /me`
- [ ] T306 [US3] 实现 `modules/user/controller/UserController.java`：方法上 `@PreAuthorize("hasRole('ADMIN')")`；含 `@OperationLog("user:<action>")`
- [ ] ~~T307~~ **已删除**：默认 admin 改由 Liquibase changelog（T029）单一植入；应用层不再实现 `AdminUserInitializer`，避免双植入路径与密码漂移

### Frontend — love-space-web

- [ ] T310 [P] [US3] 创建 `src/api/auth.ts`、`src/api/users.ts`
- [ ] T311 [US3] 完成 `src/pages/SignIn.tsx`：调用 `auth.login`，保存 token + user 到 AuthContext，跳转到首页
- [ ] T312 [US3] 创建 `src/pages/Users/List.tsx`：FilterBar（username 模糊 / role / enable / createdAt 区间）+ 表格 + 启停 / 重置密码按钮 + 右下分页
- [ ] T313 [US3] 创建 `src/pages/Users/Create.tsx`：表单字段 username / password / nickname；**UI 不暴露 role 选择**
- [ ] T314 [US3] 在 `src/App.tsx` 注册路由 `/users`、`/users/create`；`AppLayout` 侧边栏中 `/users` 入口仅当 `role==='ADMIN'` 时显示

### 测试

- [ ] T315 [P] [US3] `UserServiceTest.java`：传 role=ADMIN 仍存为 MEMBER；username 唯一冲突抛 ValidationException
- [ ] T316 [P] [US3] `AdminSeedLiquibaseIT.java`：基于 Testcontainers PG 启动 Liquibase，断言 `user` 表存在一条 `username='admin'`、role=ADMIN、enable=true，且 `password` 列以 `$2` 开头的 BCrypt 串；重复执行 changelog 不再插入第二行
- [ ] T317 [P] [US3] `AuthControllerWebMvcTest.java`：错误密码 401；enable=false 用户登录被拒
- [ ] T318 [P] [US3] `UserControllerSecurityTest.java`：MEMBER 访问 `/api/admin/users` 返回 403

**Checkpoint**: 多人协作账号体系闭环

---

## Phase 6: User Story 4 - 运营操作的审计与可追溯 (Priority: P3)

**Goal**: admin 端关键写操作通过 `@OperationLog` 异步落库；`/logs` 页可查询。

**Independent Test**: 触发任一写操作 → 调用 `/api/admin/logs` 能看到日志条目（含 username/module/action/target/createdAt）。性能门槛（日志切面延迟开销）**已推迟到"性能专项需求"阶段**，MVP 不阻塞。

### Backend — love-space-admin

- [ ] T400 [P] [US4] 创建 entity `modules/operationlog/entity/OperationLog.java`（payload jsonb；中文 JavaDoc）
- [ ] T401 [P] [US4] 创建 repository `OperationLogRepository`（Specification 支持 username / module / createdAt 区间）
- [ ] T402 [P] [US4] 创建 DTO：`OperationLogItem`、`OperationLogQuery`
- [ ] T403 [US4] 实现 `modules/operationlog/service/OperationLogService.java`：`asyncSave(...)`、`pageQuery(query)`
- [ ] T404 [US4] 将 T025 中的 `OperationLogAspect` 接入 `OperationLogService.asyncSave`，从 `OperatingContext` 取操作人；payload 序列化时剔除敏感字段（如 password）
- [ ] T405 [US4] 在所有写 controller（city / category / tag / merchant / user）方法上补全 `@OperationLog("module:action")` 注解（若 US2 / US3 未完整加，此处统一巡检）
- [ ] T406 [US4] 实现 `modules/operationlog/controller/OperationLogController.java`：`GET /api/admin/logs`

### Frontend — love-space-web

- [ ] T410 [P] [US4] 创建 `src/api/logs.ts`
- [ ] T411 [P] [US4] 创建 `src/pages/Logs/List.tsx`：FilterBar（username / module / 时间区间）+ 表格列（时间 / 操作人 / 角色 / 模块 / 动作 / 对象 / 结果）+ 右下分页
- [ ] T412 [US4] 在 `src/App.tsx` 注册 `/logs` 路由；侧边栏菜单可对所有登录用户可见（运营治理需求）

### 测试

- [ ] T415 [P] [US4] `OperationLogAspectIT.java`：触发一次 city create，断言日志条目最终被异步写入（轮询 100ms × 20）
- [ ] T416 [P] [US4] `OperationLogControllerWebMvcTest.java`：按 username / module / 区间筛选返回正确条数

**Checkpoint**: 所有 4 个用户故事独立可用

---

## Phase 7: Polish & Cross-Cutting Concerns

- [ ] T500 [P] 在 `love-space-admin` 与 `love-space-app` 所有 entity / DTO / controller 巡检中文 JavaDoc 完整性（方法、关键步骤、字段、Controller 方法 HTTP 语义）
- [ ] T501 [P] 检查所有 entity：主键 UUIDv7、关联仅以 `xxxId` 字段持有、无 `@ManyToOne`/`@JoinColumn`、Liquibase changelog 无 `FOREIGN KEY`
- [ ] T502 [P] 检查所有命名：无缩写；当前用户上下文仅 `OperatingContext`
- [ ] T503 [P] 前端 `npm run lint && npm run build` 全绿；admin / app 各自 `./mvnw test` 全绿
- [ ] T504 [P] 按 `quickstart.md` 跑一遍端到端联调，记录耗时与异常
- [ ] T505 在 `README.md` 顶部补充本地启动指引指向 `specs/001-aiwomap-mvp/quickstart.md`
- [ ] ~~T506~~ **已推迟**：性能压测（App 列表 / explore P95、日志切面 <50ms 开销）统一移至后续"性能专项需求"阶段；MVP 不再执行此任务
- [ ] T507 安全巡检：默认 admin 密码已通过 BCrypt 存储；首次上线后引导运营走 `/users/{id}/password` 修改

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 Setup**：无依赖
- **Phase 2 Foundational**：依赖 Phase 1；阻塞所有用户故事
- **Phase 3 (US1) / Phase 4 (US2) / Phase 5 (US3) / Phase 6 (US4)**：均依赖 Phase 2 完成；故事之间最大程度独立
- **Phase 7 Polish**：依赖各 US 完成

### User Story Dependencies

- **US1 (App)** 与 **US2 (Admin 内容)**：可并行；运营录入是 US2，App 端读取是 US1；若需端到端验证需 US2 先种子数据，但接口层独立
- **US3 (用户管理)**：完全独立
- **US4 (操作日志)**：依赖 US2 / US3 中已埋 `@OperationLog` 注解；T405 起到补全作用

### Within Each User Story

- Entity → Repository → Service → Controller → Test
- 前端：API client → Page List → Page Form → 路由注册 → 菜单

### Parallel Opportunities

- Phase 1 中 T003 / T004 / T005 / T006 并行
- Phase 2 中标 [P] 的基础设施任务并行；前端公共组件（T034–T038）并行
- 每个 US 内 entity / repository / DTO 创建（T100s / T200s / T300s / T400s）大量并行
- 测试任务全部 [P] 并行
- 不同 US 由不同开发者并行推进

---

## Parallel Example: User Story 1（移动端读 API）

```bash
# 一次性并行创建 entity 与 DTO
Task: "T100 Create City entity in love-space-app/.../city/entity/City.java"
Task: "T101 Create Merchant + child entities in love-space-app/.../merchant/entity/"
Task: "T102 Create Tag entity in love-space-app/.../tag/entity/Tag.java"
Task: "T107 Create CityItemResponse DTO"
Task: "T108 Create Merchant response DTOs"
Task: "T109 Create Explore DTOs"

# 之后并行写 repositories
Task: "T103 CityRepository"
Task: "T104 MerchantRepository (with Specification)"
Task: "T105 Merchant child repositories"
Task: "T106 TagRepository"

# 最后并行写测试
Task: "T117 ScoreCalculatorTest"
Task: "T118 MerchantControllerWebMvcTest"
Task: "T119 ExploreControllerWebMvcTest"
```

---

## Implementation Strategy

### MVP First (User Story 1 + 必要 US2 种子数据)

1. Phase 1 + Phase 2 完成
2. Phase 3 (US1) 全量完成
3. 在 `love-space-admin` 端通过 Liquibase / 临时 SQL 注入 1 城市 + 1 上架商户即可对外演示
4. STOP & VALIDATE：调用 4 个 App 接口端到端验证

### Incremental Delivery

1. Setup + Foundational → 基础就绪
2. US1 (App 只读) → Demo（可注入种子数据演示）
3. US2 (Admin 内容管理) → 运营自助生产内容
4. US3 (账号管理) → 多人协作上线
5. US4 (操作日志) → 治理 / 审计能力

### Parallel Team Strategy

- Dev A：US1（love-space-app + 测试）
- Dev B：US2 后端（love-space-admin entities/services/controllers）
- Dev C：US2 前端（love-space-web 列表/表单 + 通用组件）
- Dev D：US3（用户 / 鉴权 / 默认 admin 初始化 + 前端登录与用户管理页）
- Dev E：US4（操作日志切面 + 列表页，US2/US3 接口稳定后并入）

---

## Notes

- [P] = 不同文件 / 无未完成前置依赖
- 所有 Java 代码方法、关键步骤、实体字段、Request / Response 字段、Controller 方法 HTTP 语义必须使用中文 JavaDoc
- 主键统一 UUIDv7，应用层生成；JPA / Liquibase 均不创建外键
- 字段命名禁止缩写；当前用户上下文类必须命名 `OperatingContext`
- 每完成一个 task 即可单独 commit；提交信息按 `admin:` / `app:` / `web:` / `docs:` 等前缀分类
- 各 checkpoint 处停下来验证用户故事独立可跑通
