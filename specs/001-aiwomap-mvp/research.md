# Phase 0 Research — 爱女地图 MVP

> 目标：消除 spec / plan 中遗留的不确定项，固化关键技术选型与最佳实践。

## 1. UUIDv7 主键生成（Java）

- **Decision**: 使用 `com.github.f4b6a3:uuid-creator`（最新稳定版），通过
  `UuidCreator.getTimeOrderedEpoch()` 在 `@PrePersist` 钩子内生成主键。
- **Rationale**: 该库轻量、零依赖、API 直观，支持 v7（time-ordered epoch）；JDK 自带的 `UUID.randomUUID()`
  仅支持 v4 不符合 constitution 原则 II。
- **Alternatives considered**:
  - Hibernate 6 自带 `@UuidGenerator(style = TIME)` 生成 v7：可行，但绑定 Hibernate；保留备选。
  - 自实现 v7 算法：维护成本高，无收益。

## 2. JPA 无外键关联

- **Decision**: 关联实体只持有对方 `UUID` 字段（如 `cityId`、`merchantId`、`tagId`），不使用 `@ManyToOne` /
  `@OneToMany` / `@JoinColumn`；多对多（标签、周期）通过显式中间表实体（`MerchantTag` / `MerchantPeriod`）建模。
  Liquibase changelog DDL 不写 `FOREIGN KEY` 子句。
- **Rationale**: 满足 constitution 原则 II；保留未来分库 / 跨服务共享数据的灵活性；引用完整性由 service 层显式校验。
- **Alternatives considered**:
  - 使用 `@JoinColumn(foreignKey = @ForeignKey(NO_CONSTRAINT))`：等效但易被 IDE / 同事误读，不如直接拆 ID 字段。

## 3. JWT 选型

- **Decision**: 采用 `io.jsonwebtoken:jjwt-api/impl/jackson` 0.12.x；密钥使用 HMAC-SHA256；token 有效期 8 小时；
  通过 `JwtAuthFilter` 注入 SecurityContext。
- **Rationale**: API 简洁，社区主流，文档充足；spring boot 4 兼容良好；满足 MVP 单点运营后台需求。
- **Alternatives considered**:
  - Nimbus JOSE + Spring Authorization Server：功能强大但本期单服务无需 OIDC。
  - Spring Security 内置 `OAuth2ResourceServer`：可行，将来需要 JWKS 时再切换。

## 4. 数据库迁移

- **Decision**: 采用 **Liquibase**（Clarifications 2026-05-20）+ **formatted-SQL changeset**（Clarifications
  2026-05-21）。结构如下：
  - 入口：`src/main/resources/db/changelog/db.changelog-master.yaml`，**仅 `<include>` 列表，不内联 changeSet**
  - 子文件：`changes/001-init-schema.sql`、`changes/002-seed-admin-manager.sql`，每个文件顶部带
    `--liquibase formatted sql`，每个 changeset 标注 `--changeset author:id` 与 `--rollback ...`
  - Spring Boot 自动配置：`spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml`
  - 所有 DDL 创建的表名带 `loves_` 前缀（单数 snake_case）
  - Liquibase 版本：**不显式 pin**，由 `spring-boot-starter-liquibase`（Spring Boot 4.0.6 BOM）默认版本提供
- **Rationale**: 与用户多次明确决议一致；formatted-SQL 让 DDL 直接可读、DBA 友好；master 退化为 include 列表
  避免双语法（YAML changeSet vs. SQL）混淆；不 pin 版本可随 Spring Boot 升级自动跟进 Liquibase 修复。
- **Alternatives considered**:
  - YAML / XML changeSet（含表结构内联）：可读性差、DBA 不友好，已弃用。
  - Flyway：用户已明确改为 Liquibase。
  - 纯 JPA `ddl-auto=update`：违反生产规范。
  - 显式 pin Liquibase 版本：增加维护负担，已弃用。

## 5. 操作日志异步落库

- **Decision**: 自定义注解 `@OperationLog("module:action")` + AOP（`@Around`），在写操作成功返回后通过
  `@Async` 提交到独立的 `operationLogExecutor` 线程池写入；失败仅打 warn，不阻塞主流程。
- **Rationale**: 解耦业务与审计（性能门槛已统一推迟到"性能专项需求"阶段，MVP 不在 CI 设硬性 P95 阻塞）。
- **Alternatives considered**:
  - 同步落库：实现简单但增加主流程延迟。
  - 消息队列（Kafka）：MVP 体量不需要，过度设计。

## 6. 文件上传（图片）

- **Decision**: MVP 选择本地文件系统存储，路径前缀 `/uploads/`；通过 `FileStorage` 接口隔离实现，
  保留 MinIO / OSS 替换点。返回完整 URL（含运营后台域名）。
- **Rationale**: 部署最快；接口抽象保证后续替换无业务感知。
- **Alternatives considered**:
  - 直接接 OSS：需运营提供 bucket / AK；MVP 阶段不阻塞。
  - MinIO 本地容器：运维多一层依赖，留作后续。

## 7. 四维评分 → 百分制 / 10 级 映射

- **Decision**: 在 admin 与 app 各自的 `merchant` 模块内提供 `ScoreCalculator`（纯静态方法）；
  - 百分制 `percent = Math.round(raw * 100.0 / max)`（四舍五入保留整数）。
  - 爱女指数 `total = S + L + E + I`（满分 100）。
  - 10 级映射：`level = max(1, min(10, (int) Math.ceil(total / 10.0)))`；每级对应客户端 0.5 颗星。
- **Rationale**: 与 spec FR-014、SC-003 严格一致；服务端唯一计算来源避免客户端漂移。
- **Alternatives considered**: 客户端计算 / 数据库视图计算 — 不符合 constitution 与 spec 约束。

## 8. ProblemDetail 与字段级校验

- **Decision**: 开启 `spring.mvc.problemdetails.enabled=true`，业务异常继承 `ErrorResponseException`；
  字段级校验在 `ApiExceptionHandler` 覆盖 `handleMethodArgumentNotValid`，附加 `errors: [{field, message}]`。
- **Rationale**: 与开发文档第七节完全一致；避免自造 `ApiResponse`。

## 9. 前端公共组件

- **Decision**:
  - `FilterBar`：受控组件，接收 `fields` 配置 + `onApply` / `onReset` 回调，内部维护草稿态。
  - `Pagination`：复用 TailAdmin `support-tickets` 组件视觉；props：`page`、`size`、`total`、
    `onChange(page, size)`；`size` 选项固定 [20, 30]。
- **Rationale**: 统一所有列表页 UX；满足 spec FR-006 / FR-043 / SC-009。

## 10. 待确认项（spec Assumptions 收敛）

| 项 | 默认决定 | 触发再讨论的条件 |
|---|---|---|
| 性能扩容 | 单实例 + PG 单库，按数百 QPS 设计；负载升高后再水平扩容 | App DAU 突破 1 万 |
| 图片存储 | 本地存储 MVP；接口预留 MinIO / OSS 切换 | 运维就绪 MinIO 或运营选定云厂商 |
| 商户坐标 | schema 预留，表单显示但允许空；App 详情返回可空 | 引入地图 SDK 时启用 |
| 分类筛选 | 接口与 DB 字段就位；App 端是否开放筛选由运营评估开关 | 运营提供首批分类数据后开启 |

## 11. App 端 API Key 鉴权

- **Decision**: `love-space-app` 不引入用户 / 账号系统；通过自定义 `OncePerRequestFilter`
  `ApiKeyAuthFilter` 校验请求头 `X-API-Key`，从 `ApiKeyProperties.apiKeys: List<String>` 加载允许集。
  - 命中：构造一个匿名 `Authentication`（`PreAuthenticatedAuthenticationToken`，principal = "api-client"）
    并写入 SecurityContext；
  - 不命中或缺失：写入 401 ProblemDetail，不再透露具体原因；
  - 比较使用 `MessageDigest.isEqual` 进行常量时间比较，避免侧信道；
  - 启动时若 `apiKeys` 为空，`@PostConstruct` 抛 `IllegalStateException` 阻断启动，避免裸奔。
- **Rationale**: MVP 客户端为可控的官方 App，预共享密钥配合 HTTPS 即可达到合理的最小防护；
  零业务数据库依赖；轮换通过追加新 key + 灰度切换 + 移除旧 key 完成。
- **Alternatives considered**:
  - JWT / OAuth：需账号体系，与"App 端无用户系统"决策冲突。
  - mTLS：运维门槛高，MVP 不引入。
  - 网关层 API Key：可作为后续部署优化，与当前应用层方案不冲突，先在应用内实现确保本地可跑。

## 12. 中文 JavaDoc 一致性策略

- **Decision**:
  - 模板：方法 JavaDoc 起始为"@brief 中文一句话"，后随 `@param` / `@return` / `@throws` 中文说明；
  - Controller 方法额外含"接收数据 / 返回数据 / HTTP 状态码 / 鉴权要求"四段；
  - 字段 JavaDoc 单行结构：`/** 业务含义（约束）。*/`。
- **Rationale**: 与 constitution 原则 I（v1.0.1 扩写后）的具体要求逐条对齐；统一模板降低 PR review 摩擦。
- **后续动作**: 若团队规模扩大，可引入 ArchUnit 测试断言所有 `@RestController` / `@Entity` / `*Request` /
  `*Response` 类的字段含 JavaDoc。
