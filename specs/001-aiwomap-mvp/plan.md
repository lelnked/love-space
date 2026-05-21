# Implementation Plan: 爱女地图 MVP（同类象 App v1.0）

**Branch**: `001-aiwomap-mvp` | **Date**: 2026-05-20（2026-05-21 增补 Manager 重命名 / Liquibase formatted-SQL / `loves_` 表前缀澄清） | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-aiwomap-mvp/spec.md`

## Summary

交付"爱女地图"MVP 端到端能力：

1. `love-space-admin` 提供运营后台 REST API（鉴权 / 用户 / 城市 / 分类 / 标签 / 商户 / 文件 / 操作日志），
   以 Spring Boot 4.0.6 + Java 25 + Spring Security + JWT + Spring Data JPA + PostgreSQL 实现。
2. `love-space-app` 提供移动端只读 REST API（探索 / 城市 / 商户列表 / 商户详情），同栈但
   **不引入用户管理 / 账号系统**；通过 `X-API-Key` 请求头 + 预共享 API Key 列表（配置 `app.security.api-keys`）
   进行鉴权；四维评分 → 百分制 / 10 级映射在 service 内的 `ScoreCalculator` 完成。
3. `love-space-web` 替换 TailAdmin 演示路由，提供登录、Manager 管理（仅 ADMIN 可见）、城市 / 分类 / 标签 / 商户
   CRUD、操作日志查询；公共组件抽出 Apply/Reset 筛选区与 `support-tickets` 风格分页器（默认 20，可切 30）。

**2026-05-21 增补澄清**：
- 后端"运营用户"概念全链路更名为 **Manager**（实体 `Manager`、表 `loves_manager`、API `/api/admin/managers/**`、
  前端路由 `/managers`）；`role` 枚举值 `ADMIN/MEMBER` 保持不变。
- Liquibase changelog 入口仍为 `db.changelog-master.yaml`，但仅做 `<include>` 列表；**所有 changeset 改用
  Liquibase formatted-SQL 文件**（`--liquibase formatted sql` + `--changeset author:id` 头）。
- 所有数据库表统一加 `loves_` 前缀（单数 + snake_case），admin / app 两个后端共用同一命名规范。
- Liquibase 不显式 pin 版本，由 Spring Boot 4.0.6 `spring-boot-starter-liquibase` BOM 默认版本决定。

技术路线遵循 constitution v1.0.1：方法 / 关键步骤 / 实体字段 / API 入参出参字段全部使用中文 JavaDoc；
主键 UUIDv7（`uuid-creator`）；JPA / DDL 不创建外键约束；命名禁止缩写；`OperatingContext` 命名固定。

## Technical Context

**Language/Version**: Java 25（两个后端，pom.xml `<java.version>25</java.version>`）；TypeScript 5 + React 19；
Node 20+ 推荐。

**Primary Dependencies**:
- 后端：Spring Boot 4.0.6（Web MVC / Data JPA / Security / Validation）、Lombok、PostgreSQL Driver、
  Liquibase（**不显式 pin 版本**，使用 `spring-boot-starter-liquibase` 携带的默认版本；master 为
  `db.changelog-master.yaml` 仅 include，实际脚本为 **Liquibase formatted-SQL** 文件）、
  `com.github.f4b6a3:uuid-creator`（UUIDv7 生成）、
  JJWT 0.12.x（JWT 签发与解析，固定选型；不引入 Nimbus JOSE 以避免双 JWT 库）；
  **MVP 不引入 springdoc-openapi**，接口契约由 `contracts/*.md` 维护，Swagger UI 推迟到后续性能/对外开放阶段再评估。
- 前端：React 19、Vite 6、Tailwind CSS v4、react-router v7、axios、轻量状态管理（React Context；
  不引入 Redux）。

**Storage**: PostgreSQL 16；本地或 MinIO 对象存储用于图片（MVP）；统一不创建外键约束。

**Testing**:
- 后端：JUnit 5 + Spring Boot Test + MockMvc + Testcontainers (PostgreSQL) 集成测试；service 单测用 Mockito。
- 前端：Vitest + React Testing Library（按需引入）。

**Target Platform**: Linux 服务器（两个 Spring Boot 服务独立部署）；运营后台浏览器（Chrome / Edge 最新两版）；
移动 App 由独立团队开发，本规格仅约束后端契约。

**Project Type**: Web 应用（前端 + 双后端微服务）。

**Performance Goals**:
- MVP 阶段不设硬性 P95 门槛；性能验收（App 列表 / explore 响应时延、日志切面延迟开销）**统一推迟到后续"性能专项需求"阶段**专门处理。
- MVP 仅要求功能正确，不在 CI 中加压测阻塞条件。

**Constraints**:
- 不在数据库层创建外键。
- 主键统一 UUIDv7，应用层生成。
- 中文 JavaDoc 强制覆盖方法 / 关键步骤 / 实体字段 / API 入参出参 / Controller 方法 HTTP 语义。
- 单图上传 ≤20MB；商户故事 ≤5000 字；商户名 ≤15 汉字；标签名 ≤6 汉字；分类名 ≤10 汉字。
- 默认 admin 账号：`admin` / `8@y2eoRLyStM*UVU`，由 Liquibase formatted-SQL changelog
  `002-seed-admin-manager.sql` 以预生成 BCrypt 哈希写入 `loves_manager` 表（changeset 通过
  `--precondition-sql-check` 保证幂等）；应用层不再二次写入。
- 所有数据库表统一加 `loves_` 前缀，单数 snake_case；`@Entity` 通过 `@Table(name="loves_xxx")` 显式标注。

**Scale/Scope**:
- 城市 ≤100、商户 ≤1 万、单商户图片 ≤30 张、单商户评价 ≤200 条；运营 ≤20 人；MVP 期 App QPS 上限按数百级别预估。

## Constitution Check

> 来源：`.specify/memory/constitution.md` v1.0.1

| 原则 | 计划落实方式 | 状态 |
|---|---|---|
| I. 中文 JavaDoc 注释强制 | Controller / Service / Repository / Entity / Request DTO / Response VO 全部字段及方法配中文 JavaDoc；Controller 方法 JavaDoc 显式描述请求体 / 响应体 / HTTP 状态码 / 鉴权要求；PR review checklist 强制核对。 | ✅ Pass |
| II. UUIDv7 主键 & 禁用外键 | 所有 `@Entity` 主键 `UUID`，`@PrePersist` 内通过 `UuidCreator.getTimeOrderedEpoch()` 赋值；关联字段仅以 `UUID xxxId` 列存在；JPA 不使用 `@ManyToOne`/`@JoinColumn` 触发外键；Liquibase changelog DDL 不含 `FOREIGN KEY`。 | ✅ Pass |
| III. 命名清晰且不缩写 | 严格遵守 `开发文档.md` 6.3 节命名（`safetyEnvironmentScore` 等）；当前用户上下文类命名 `OperatingContext`。 | ✅ Pass |
| IV. 双后端隔离与分层 | `com.loves.space.modules.<feature>` vs `com.space.app.modules.<feature>` 互不引用；前端只对接 admin。 | ✅ Pass |
| V. 测试与本地可运行性 | 后端 `./mvnw test` 全绿；新增 service 单测 + 关键 controller MockMvc 测试；前端 `npm run build` + `npm run lint` 通过；`application.yml` 不含真实生产凭据。 | ✅ Pass |

**结论**：无违规，无需 Complexity Tracking。

## Project Structure

### Documentation (this feature)

```text
specs/001-aiwomap-mvp/
├── plan.md              # 本文件
├── research.md          # Phase 0 输出
├── data-model.md        # Phase 1 输出
├── quickstart.md        # Phase 1 输出
├── contracts/           # Phase 1 输出
│   ├── admin-api.md
│   └── app-api.md
├── checklists/
│   └── requirements.md  # /speckit-specify 阶段生成
└── tasks.md             # 由 /speckit-tasks 生成（暂不创建）
```

### Source Code (repository root)

```text
love-space/
├── love-space-admin/                              # 运营后台后端
│   └── src/main/java/com/loves/space/
│       ├── LoveSpaceAdminApplication.java
│       ├── common/{annotation, constant, enums, exception, page, util, validation}
│       ├── config/{WebMvcConfig, JpaConfig, SecurityConfig, SwaggerConfig, AsyncConfig,
│       │          properties/JwtProperties}
│       ├── security/{jwt, userdetails, handler, OperatingContext}
│       ├── infrastructure/{storage, log}
│       ├── web/ApiExceptionHandler
│       └── modules/
│           ├── auth/      {controller, service, dto}
│           ├── manager/   {controller, service, repository, entity, dto, mapper}  # 原 user/，2026-05-21 重命名
│           ├── city/      {controller, service, repository, entity, dto}
│           ├── category/  {controller, service, repository, entity, dto}
│           ├── tag/       {controller, service, repository, entity, dto}
│           ├── merchant/  {controller, service, repository, entity, dto}
│           ├── file/      {controller, dto, service}
│           └── operationlog/ {controller, service, repository, entity, dto}
│   └── src/main/resources/
│       ├── application.yml / application-dev.yml / application-prod.yml
│       └── db/changelog/
│           ├── db.changelog-master.yaml          # 仅 <include>，不内联 changeset
│           └── changes/{001-init-schema.sql,    # formatted-SQL，DDL 含 `loves_` 前缀
│                       002-seed-admin-manager.sql}  # formatted-SQL，写 loves_manager
│
├── love-space-app/                                # 移动端后端
│   └── src/main/java/com/space/app/
│       ├── LoveSpaceAppApplication.java
│       ├── common/{enums, exception, page, util}
│       ├── config/{WebMvcConfig, JpaConfig, SecurityConfig}
│       ├── web/ApiExceptionHandler
│       └── modules/
│           ├── explore/   {controller, service, dto}
│           ├── city/      {controller, service, repository, entity, dto}
│           ├── merchant/  {controller, service(ScoreCalculator), repository, entity, dto}
│           └── tag/       {repository, entity}
│
└── love-space-web/                                # 运营后台前端
    └── src/
        ├── App.tsx                                # 路由重构
        ├── layout/AppLayout.tsx                   # 含按角色过滤的侧边栏
        ├── pages/{SignIn, Managers/{List, Create}, Cities/{List, Form},
        │          Categories/List, Tags/List,
        │          Merchants/{List, Form}, Logs/List}
        ├── components/{filter/FilterBar, pagination/Pagination,
        │               user/UserMenu, form/...}        # UserMenu 组件名保留，仅展示当前 Manager
        ├── api/{auth, managers, cities, categories, tags, merchants, files, logs}.ts
        ├── context/{AuthContext, OperatingManagerContext}
        └── hooks/{useAuth, usePagination, useFilter}
```

**Structure Decision**: 沿用现有 `love-space-admin` / `love-space-app` / `love-space-web` 三目录结构；
admin / app 各自 `com.loves.space` 与 `com.space.app` 包根不变；前端按 page-by-feature 重构 `src/pages`，
公共组件抽到 `src/components/filter` 与 `src/components/pagination`。

## 2026-05-21 增补：重命名 / 表前缀 / changelog 迁移

> 由 `/speckit-clarify` 第二轮（2026-05-21）派生，作用于本次 plan 之后的 tasks 阶段。

**A. User → Manager 重命名清单**（搜索式替换，需逐处验证）：

| 旧 | 新 |
|---|---|
| 包 `com.loves.space.modules.user` | `com.loves.space.modules.manager` |
| 类 `User` / `UserEntity` / `UserRepository` / `UserService` / `UserController` / `UserMapper` / `UserDto` | 同名 `Manager*` |
| 表 `user` | `loves_manager` |
| API `/api/admin/users/**` | `/api/admin/managers/**` |
| 前端目录 `src/pages/Users/*`、API client `api/users.ts`、路由 `/users`、`/users/create` | `Managers/*`、`api/managers.ts`、`/managers`、`/managers/create` |
| Context `OperatingUserContext` | `OperatingManagerContext` |
| OperationLog 字段 `userId` | `managerId` |
| `AdminUserInitializer`（如还残留应用层初始化代码） | 删除 |
| 角色枚举值 `ADMIN`/`MEMBER` | **不变** |
| Spring Security 注解 `ROLE_ADMIN` / `hasRole('ADMIN')` | **不变** |
| 当前请求 holder 类名 `OperatingContext` | **不变**（按 memory feedback：禁止改名为 `CurrentUserHolder`，同理也不改为 `OperatingManagerHolder`） |

**B. 数据库表 `loves_` 前缀清单**：

`loves_manager`、`loves_city`、`loves_category`、`loves_tag`、`loves_merchant`、`loves_merchant_image`、
`loves_merchant_period`、`loves_merchant_tag`、`loves_merchant_review`、`loves_operation_log`。

每个 `@Entity` 显式 `@Table(name="loves_xxx")`；DDL 由 formatted-SQL changeset 创建；admin / app 共享同一套
物理表名（同库或同 schema），但访问的实体类各自定义在各自的包内。

**C. Liquibase 迁移策略调整**：

1. `pom.xml`：仅保留 `spring-boot-starter-liquibase` 依赖，**不显式声明 `<version>`**，让 Spring Boot 4.0.6 BOM
   解析版本。`liquibase-core` 不重复声明。
2. `application.yml`：`spring.liquibase.change-log: classpath:/db/changelog/db.changelog-master.yaml`。
3. `db.changelog-master.yaml`：仅包含一个 `databaseChangeLog: [ {include: {file: ...}}, ... ]` 列表，
   **不内联任何 `changeSet`**。两个后端的 master 各自维护；admin master include `001-init-schema.sql` +
   `002-seed-admin-manager.sql`；app master 仅 include `001-init-schema.sql`（按现有 spec 约束）。
4. 实际脚本文件用 Liquibase formatted-SQL：
   ```sql
   --liquibase formatted sql

   --changeset loves:001-init-schema
   --comment: 初始化 loves_* 业务表（无外键约束）
   CREATE TABLE loves_manager (...);
   CREATE TABLE loves_city (...);
   ...
   --rollback DROP TABLE loves_manager, loves_city, ...;
   ```
5. 种子文件 `002-seed-admin-manager.sql`：
   ```sql
   --liquibase formatted sql

   --changeset loves:002-seed-admin-manager
   --precondition-sql-check expectedResult:0 SELECT count(*) FROM loves_manager WHERE username='admin'
   --onFail MARK_RAN
   INSERT INTO loves_manager (id, username, password, nickname, role, enable, created_at, updated_at)
   VALUES ('<UUIDv7 hex>', 'admin', '$2a$10$<BCryptHash>', 'admin', 'ADMIN', true, now(), now());
   --rollback DELETE FROM loves_manager WHERE username='admin';
   ```
6. 既有 `002-seed-admin-user.yaml`（如已落盘）→ tasks 阶段执行物理删除 + 新增 SQL 替代。

**D. Constitution 复检**（本次增补不引入新违规）：

| 原则 | 复检结论 |
|---|---|
| I. 中文 JavaDoc | 仅命名/前缀调整，注释规范无变化 ✅ |
| II. UUIDv7 主键 & 禁外键 | formatted-SQL DDL 必须保持无 `FOREIGN KEY`；种子值用预生成 UUIDv7 ✅ |
| III. 命名不缩写 | `Manager` 全词、`loves_merchant_image` 等均符合 ✅ |
| IV. 双后端隔离 | admin / app 共享物理表名空间但实体类与 Repository 各自维护，包路径不互相 import ✅ |
| V. 测试与可运行性 | Liquibase 迁移本地必须能在干净 PG 上一次过；Testcontainers 路径不变 ✅ |

## Complexity Tracking

> 无违规，无需填写。
