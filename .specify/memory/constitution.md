<!--
Sync Impact Report
- Version change: 1.0.0 → 1.0.1
- Bump rationale: PATCH —— 在原则 I "中文 JavaDoc 注释强制" 中显式扩写
  "API 接收数据 / 返回数据（请求 DTO、响应 VO、统一响应包装类、错误码字段）的中文 JavaDoc
  要求"。属于对既有规则的澄清/细化，未新增原则、未改变向后兼容性。
- Modified principles:
  - I. 中文 JavaDoc 注释强制 — 子条目"实体字段"扩写为"实体字段 & API 入参/出参字段"，
    新增 controller 方法级 JavaDoc 必须描述请求体、响应体、HTTP 语义的显式要求。
- Added sections: 无
- Removed sections: 无
- Templates requiring updates:
  - ✅ .specify/templates/plan-template.md — 无结构性变化。
  - ✅ .specify/templates/spec-template.md — 无需调整。
  - ✅ .specify/templates/tasks-template.md — 任务生成时应将 "API 请求/响应 DTO 字段
    JavaDoc" 纳入注释校验项。
  - ⚠ love-space-admin / love-space-app: 现有/新增 controller、Request DTO、Response VO
    MUST 按本次澄清补齐中文 JavaDoc。
- Deferred TODOs: 无。
- 历史 1.0.0 → 现 1.0.1，原始批准日期 2026-05-20 保持不变。
-->

# love-space Constitution

本宪法适用于 love-space 工作区下的全部子项目：`love-space-web`（React 19 + Vite 管理后台前端）、
`love-space-admin`（Spring Boot 4.0.6 + Java 25 管理后台后端）、`love-space-app`（Spring Boot 4.0.6 +
Java 25 移动端后端）。所有代码评审、计划文档、任务生成与实现都 MUST 符合下列原则。

## Core Principles

### I. 中文 JavaDoc 注释强制（NON-NEGOTIABLE）

所有 Java 代码 MUST 满足下列注释要求：

- **方法**：每一个 `public`、`protected`、`package-private` 方法 MUST 配备 JavaDoc，且 JavaDoc
  主体内容 MUST 使用中文；需描述方法用途、入参语义、返回值含义、可能抛出的业务异常。
- **关键步骤**：方法体内对业务有判定/分支/状态变更意义的关键步骤 MUST 通过中文 JavaDoc 风格
  注释（即 `/** ... */` 或紧贴该步骤上方的中文行注释 `// 中文说明`）阐明意图与原因，不是描述代码字面动作。
- **实体字段 & API 入参/出参字段**：JPA 实体、Request DTO、Response VO、查询参数对象、统一响应
  包装类（如 `ApiResponse<T>`、`PageResult<T>`）、错误码 / 错误信息字段，每一个字段 MUST 配
  中文 JavaDoc 注释，说明字段业务含义、取值约束（枚举范围、长度上限、是否可空、单位）。嵌套对象
  与泛型 `T` 的具体化类型同样适用。
- **API 方法（controller 层）**：除通用方法 JavaDoc 要求外，MUST 在中文 JavaDoc 中显式描述：
  接收数据（请求体 / 路径参数 / 查询参数 / 请求头）的业务含义、返回数据（响应体结构、分页 / 列表
  语义）、HTTP 状态码语义、鉴权要求。OpenAPI/Swagger 注解（如 `@Operation`、`@Schema`）若使用，
  其 `description` MUST 与 JavaDoc 中文内容保持一致。
- **私有工具方法**可省略 JavaDoc，但若实现包含非平凡逻辑仍 SHOULD 写中文说明，理由：保障后续阅读者
  能快速理解业务语义而无需反推代码。

**Rationale**: 团队工作语言为中文，业务规则源自《需求文档.pdf》（中文）；强制中文 JavaDoc 可消除翻译
歧义，并使生成的 JavaDoc HTML、IDE Hover 提示直接对业务可读。

### II. UUIDv7 主键 & 禁用数据库外键（NON-NEGOTIABLE）

- 所有持久化实体的主键 MUST 使用 UUIDv7（时间有序 UUID），字段类型 MUST 为 `java.util.UUID`，
  数据库列类型在 PostgreSQL 上 MUST 为 `uuid`。
- ID 生成 MUST 在应用层完成（例如通过 `@PrePersist` 或显式构造器赋值），SHOULD NOT 依赖数据库
  默认值；MUST NOT 使用自增 `bigint`、`serial` 或随机 UUIDv4 作为主键。
- 表间关联字段 MUST 仅保存对方实体的 UUID 主键值（命名形如 `userId`、`coupleId`），MUST NOT
  在 DDL 或迁移脚本中创建 `FOREIGN KEY` 约束；JPA 关联 MUST NOT 使用 `@ManyToOne` /
  `@OneToMany` 触发外键生成——必要时使用 `@Column` 持有外键 ID 并在服务层显式校验引用完整性。
- `@JoinColumn` 若被使用，MUST 配 `foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)`。

**Rationale**: UUIDv7 在分布式与索引局部性上兼顾；移除数据库外键便于后续分库分表、软删除策略、
跨服务（admin / app 后端）共享数据，缺点（引用完整性）由服务层显式校验承担。

### III. 命名清晰且不缩写

- 实体字段、DTO 字段、数据库列名、Java 方法名 MUST 使用完整词汇，禁止缩写。例如：使用
  `safetyEnvironmentScore` 而非 `scoreS`、`address` 而非 `addr`、`description` 而非 `desc`。
- 表示"当前请求上下文 / 当前用户持有者"的组件 MUST 命名为 `OperatingContext`，MUST NOT
  使用 `CurrentUserHolder`、`UserContext`、`RequestContext` 等替代名。
- 控制器路径按业务域划分；admin 后端 SHOULD 使用 `/api/admin/...` 前缀，app 后端 SHOULD
  使用 `/api/app/...` 前缀，禁止跨后端复用 controller。

**Rationale**: 完整命名降低后续维护者阅读成本；统一 `OperatingContext` 命名避免历史上多名共存的混乱。

### IV. 双后端隔离与分层

- `love-space-admin` 与 `love-space-app` 是相互独立的 Spring Boot 应用，MUST NOT 互相引用
  对方包；任何跨后端共用的实体或 DTO 须显式复制并各自维护，或抽到未来引入的共享库（当前不存在）。
- 后端代码 MUST 遵循标准 Spring Boot 分层：`controller` / `service` / `repository` / `entity`
  / `dto`，包路径分别位于 `com.loves.space.<模块>` 与 `com.space.app.<模块>` 之下。
- 前端 `love-space-web` MUST 仅对接 admin 后端 API；MUST NOT 直接调用 app 后端。
- 类型 / 接口客户端在前后端之间手工同步：MUST 先改 Java controller + DTO，再在 React 端镜像类型。

**Rationale**: 管理后台与移动端的业务节奏、安全模型、扩展方向不同，物理隔离防止隐性耦合。

### V. 测试与本地可运行性

- 每个后端 MUST 维持 `./mvnw test` 一键可运行；新增代码 SHOULD 至少含 service 层单元测试，
  涉及 controller 的新 API SHOULD 含 MockMvc 集成测试。
- `application.properties` MUST NOT 包含真实生产凭据；本地数据库连接信息以默认 PostgreSQL
  本地配置呈现，敏感值通过环境变量覆盖。
- `love-space-web` MUST 保持 `npm run build`（含 `tsc -b`）与 `npm run lint` 通过。
- 提交前 MUST 在本地运行相应构建/测试命令；CI 缺失不构成豁免理由。

**Rationale**: 工作区跨语言、跨子项目，统一"本地能跑通"的硬要求是协作的最小公约数。

## 技术与工具栈约束

- **语言版本**：Java 25（两个后端 pom.xml `<java.version>25</java.version>`）；TypeScript
  + React 19；Node 18+（推荐 20+）。
- **后端框架**：Spring Boot 4.0.6，依赖 Spring Web MVC、Spring Data JPA、Spring Security、
  PostgreSQL Driver、Lombok。引入新依赖 MUST 在 PR 说明中给出理由。
- **前端框架**：Vite 6、Tailwind CSS v4（CSS 配置，不使用 `tailwind.config.js`）、react-router v7。
- **数据库**：PostgreSQL。迁移脚本（若未来引入 Flyway / Liquibase）MUST 不含外键约束。
- **ID 生成**：使用 UUIDv7 生成工具（如 `com.github.f4b6a3:uuid-creator` 或等价实现）；
  禁止使用 `UUID.randomUUID()`（UUIDv4）作为主键。
- **包根**：admin 后端 `com.loves.space`、app 后端 `com.space.app`，MUST 保持不变。

## 开发工作流

- 仓库为单一 git 仓库管理多个子项目；提交信息 MUST 以 `web:` / `admin:` / `app:` /
  `docs:` / `chore:` 等前缀标记影响范围；跨子项目改动可合并在同一 commit，但 message 中要列出。
- 分支策略：主分支 `main`；功能开发 MUST 在 `###-feature-name` 风格的分支上进行（由
  `/speckit-git-feature` 创建）。
- 规格驱动：新功能 MUST 走 `/speckit-specify` → `/speckit-plan` → `/speckit-tasks` →
  `/speckit-implement` 流程；`Constitution Check` 阶段 MUST 逐条对照本宪法五项原则与"技术
  与工具栈约束"。
- 代码评审：每个 PR MUST 检查 (a) 中文 JavaDoc 完整性、(b) 主键为 UUIDv7、(c) DDL/JPA 无外键、
  (d) 字段命名无缩写、(e) `OperatingContext` 命名一致。任何违反 MUST 在合入前修复或在
  Complexity Tracking 中显式说明豁免理由。

## Governance

- 本宪法 supersedes 所有非正式约定；冲突时以本宪法为准。
- **修订流程**：提案 MUST 通过 PR 提交，描述变更动机、影响面、迁移计划；至少一位仓库维护者
  批准方可合入。修订后 MUST 同步更新 `.specify/templates/*` 中相关章节并在本文件顶部 Sync
  Impact Report 中记录。
- **版本语义**：MAJOR 用于不向后兼容的原则移除/重定义；MINOR 用于新增原则或显著扩展指导；
  PATCH 用于澄清、措辞与排版修正。
- **合规审查**：所有 PR 评审、`/speckit-analyze`、`/speckit-plan` 中的 Constitution Check
  环节 MUST 验证本宪法条款；任何例外 MUST 在 plan 的 Complexity Tracking 中记录。
- **运行时指引**：详细的代码库结构与命令在 `CLAUDE.md` 中维护；本宪法仅规定不可让步的原则。

**Version**: 1.0.1 | **Ratified**: 2026-05-20 | **Last Amended**: 2026-05-20
