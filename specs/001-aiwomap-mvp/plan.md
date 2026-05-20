# Implementation Plan: 爱女地图 MVP（同类象 App v1.0）

**Branch**: `001-aiwomap-mvp` | **Date**: 2026-05-20 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-aiwomap-mvp/spec.md`

## Summary

交付"爱女地图"MVP 端到端能力：

1. `love-space-admin` 提供运营后台 REST API（鉴权 / 用户 / 城市 / 分类 / 标签 / 商户 / 文件 / 操作日志），
   以 Spring Boot 4.0.6 + Java 25 + Spring Security + JWT + Spring Data JPA + PostgreSQL 实现。
2. `love-space-app` 提供移动端只读 REST API（探索 / 城市 / 商户列表 / 商户详情），同栈但
   **不引入用户管理 / 账号系统**；通过 `X-API-Key` 请求头 + 预共享 API Key 列表（配置 `app.security.api-keys`）
   进行鉴权；四维评分 → 百分制 / 10 级映射在 service 内的 `ScoreCalculator` 完成。
3. `love-space-web` 替换 TailAdmin 演示路由，提供登录、用户管理（仅 Admin 可见）、城市 / 分类 / 标签 / 商户
   CRUD、操作日志查询；公共组件抽出 Apply/Reset 筛选区与 `support-tickets` 风格分页器（默认 20，可切 30）。

技术路线遵循 constitution v1.0.1：方法 / 关键步骤 / 实体字段 / API 入参出参字段全部使用中文 JavaDoc；
主键 UUIDv7（`uuid-creator`）；JPA / DDL 不创建外键约束；命名禁止缩写；`OperatingContext` 命名固定。

## Technical Context

**Language/Version**: Java 25（两个后端，pom.xml `<java.version>25</java.version>`）；TypeScript 5 + React 19；
Node 20+ 推荐。

**Primary Dependencies**:
- 后端：Spring Boot 4.0.6（Web MVC / Data JPA / Security / Validation）、Lombok、PostgreSQL Driver、
  Liquibase（迁移，使用 YAML / XML changelog）、`com.github.f4b6a3:uuid-creator`（UUIDv7 生成）、
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
- 默认 admin 账号：`admin` / `8@y2eoRLyStM*UVU`，由 Liquibase changelog `002-seed-admin-user.yaml` 以预生成 BCrypt 哈希植入（preCondition 保证幂等）；应用层不再二次写入。

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
│           ├── user/      {controller, service, repository, entity, dto, mapper, init}
│           ├── city/      {controller, service, repository, entity, dto}
│           ├── category/  {controller, service, repository, entity, dto}
│           ├── tag/       {controller, service, repository, entity, dto}
│           ├── merchant/  {controller, service, repository, entity, dto}
│           ├── file/      {controller, dto, service}
│           └── operationlog/ {controller, service, repository, entity, dto}
│   └── src/main/resources/
│       ├── application.yml / application-dev.yml / application-prod.yml
│       └── db/changelog/
│           ├── db.changelog-master.yaml
│           └── changes/{001-init-schema.yaml, 002-seed-admin-user.yaml}
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
        ├── pages/{SignIn, Users/{List, Create}, Cities/{List, Form},
        │          Categories/List, Tags/List,
        │          Merchants/{List, Form}, Logs/List}
        ├── components/{filter/FilterBar, pagination/Pagination,
        │               user/UserMenu, form/...}
        ├── api/{auth, users, cities, categories, tags, merchants, files, logs}.ts
        ├── context/{AuthContext, OperatingUserContext}
        └── hooks/{useAuth, usePagination, useFilter}
```

**Structure Decision**: 沿用现有 `love-space-admin` / `love-space-app` / `love-space-web` 三目录结构；
admin / app 各自 `com.loves.space` 与 `com.space.app` 包根不变；前端按 page-by-feature 重构 `src/pages`，
公共组件抽到 `src/components/filter` 与 `src/components/pagination`。

## Complexity Tracking

> 无违规，无需填写。
