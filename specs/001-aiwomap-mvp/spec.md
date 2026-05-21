# Feature Specification: 爱女地图 MVP（同类象 App v1.0）

**Feature Branch**: `001-aiwomap-mvp`

**Created**: 2026-05-20

**Status**: Draft

**Input**: User description: "根据开发文档.md进行specify" — 基于 `开发文档.md` 的"爱女地图"业务范围，
覆盖 `love-space-web`（运营后台前端）、`love-space-admin`（运营后台后端）、`love-space-app`（移动端 App 后端）
三方协同交付的端到端 MVP。

## Clarifications

### Session 2026-05-20

- Q: 是否在实体上记录创建人 / 修改人字段？ → A: 不需要——所有实体仅保留 `createdAt` / `updatedAt`
  审计列，移除 `createdBy` / `updatedBy`。
- Q: 数据库迁移工具选型？ → A: 使用 Liquibase（changelog 形式），替代之前计划中的 Flyway。
- Q: App 端如何鉴权？ → A: App 端**不引入用户管理 / 账号系统**，所有 `/api/app/**` 接口改为 **API Key
  鉴权**：客户端在请求头 `X-API-Key: <key>` 中携带预共享密钥；后端通过配置项 `app.security.api-keys`
  维护允许的 key 列表（支持多 key 用于灰度 / 轮换）；缺失或不匹配返回 401。
- Q: 默认 admin 是否仍由应用代码 `AdminUserInitializer` 创建？ → A: 否，统一改为 Liquibase changelog
  `002-seed-admin-user.yaml` 单一植入路径（预生成 BCrypt 哈希 + `preConditions` 幂等），删除应用层初始化代码。
- Q: SC-006 日志切面 P95 <50ms、SC-008 App 端 P95 <500ms 是否纳入 MVP？ → A: 否，性能指标整体推迟到后续
  "性能专项需求"阶段处理；MVP 不在 CI 设硬性 P95 门槛。
- Q: City `sortOrder` 同时承担列表排序与 banner 准入是否合适？ → A: 拆分——重命名为 `bannerSortOrder`，**仅**
  服务于 explore banner（`>0` 进入并按数值升序；`=0` 不参与；负数拒绝）；**所有 admin 列表页（城市 / 分类 /
  标签 / 商户列表）默认按 `createdAt DESC` 排序**（商户额外保留 FR-050 `weight DESC, createdAt DESC` 业务规则）。
- Q: Banner 是否需要独立实体 / 子资源？ → A: 否，banner 即 City 本身——展示字段（背景图、中文名等）直接复用
  City 列，无 `CityBanner` 表 / DTO；筛选条件为 `online=true AND bannerSortOrder>0`。
- Q: App 端 `period` 查询语义？ → A: **单值**：传 `period=OVULATION` 等效于"商户 recommendedPeriods 包含该
  周期"；不支持多值/逗号分隔，多周期由前端发起多次请求合并。
- Q: App 端 `categoryId` 缺省时的过滤语义？ → A: 省略即"该城市所有上架商户（含 categoryId=NULL）"；不接受
  特殊 token（如 `none` / `null`）显式过滤"无分类"。
- Q: 文件上传 MIME 白名单？ → A: `image/png`、`image/jpeg`、`image/webp` 三类；同时校验文件首字节 magic
  number，伪造扩展名返回 400。
- Q: API Key 鉴权失败如何记录？ → A: `WARN` 级别审计日志（远端 IP / 是否携带头 / 路径 / 时间戳 + key 的
  SHA-256 前 6 字符脱敏指纹），**严禁打印 key 明文**。
- Q: JWT 库选型 JJWT vs Nimbus？ → A: 固定 JJWT 0.12.x，避免双 JWT 库；Nimbus JOSE 不引入。
- Q: 是否在 MVP 引入 springdoc-openapi？ → A: 否，MVP 接口契约由 `contracts/*.md` 维护；Swagger UI 推迟。
- Q: love-space-app 的 Liquibase 是否执行 admin 种子？ → A: 否，app 端 `db.changelog-master.yaml` **仅 include
  `001-init-schema.yaml`**；admin 用户种子是 admin 端专属。

### Session 2026-05-21

- Q: admin 项目的"用户"实体重命名范围？ → A: **全链路改名**：后端实体 `User` → `Manager`、DB 表 `loves_manager`、
  API 路径 `/api/admin/users` → `/api/admin/managers`、前端路由 `/users` → `/managers`；**`role` 枚举值
  `ADMIN/MEMBER` 保持不变**（角色仍是角色，不是身份概念）。原文档中所有"运营用户 / 用户管理"概念性表述统一改为
  "运营 Manager / Manager 管理"。
- Q: Liquibase changelog 的脚本与 master 格式？ → A: **master 保留 YAML** (`db.changelog-master.yaml`)，但 master
  内**仅做 include 列表**、不再内联 `changeSet`；实际表结构 / 种子数据全部改用**纯原生 SQL 文件**（如
  `001-init-schema.sql`、`002-seed-admin-manager.sql`），用 Liquibase formatted-SQL 头
  (`--liquibase formatted sql` + `--changeset author:id`) 控制幂等与回滚。
- Q: 所有数据库表必须加 `loves_` 前缀，命名形态？ → A: **单数 + snake_case**，与实体类一一对应：
  `loves_manager` / `loves_city` / `loves_category` / `loves_tag` / `loves_merchant` / `loves_merchant_image` /
  `loves_merchant_period` / `loves_merchant_tag` / `loves_merchant_review` / `loves_operation_log`。两个后端
  (`love-space-admin` / `love-space-app`) 共用同一套前缀规范。
- Q: Liquibase 版本选择？ → A: **不显式 pin 版本**，由 Spring Boot 4.0.6 BOM 默认 starter
  (`spring-boot-starter-liquibase`) 携带的版本决定，避免手动维护版本号漂移。

## User Scenarios & Testing *(mandatory)*

### User Story 1 - 移动端用户在城市内浏览爱女商户 (Priority: P1)

终端消费者打开 App 进入"探索"页：能看到当前城市信息与运营投放的推荐位，切换至商户列表后按推荐周期筛选，
点击任意商户进入详情，查看四维评分（百分制）、爱女指数（10 级 / 5 颗星半星）、已上架标签、图片、用户评价
与商户故事，从而判断该商户是否符合自身周期与诉求。

**Why this priority**: 这是产品最直接的对外价值——没有它，App 无内容可呈现；其他模块（后台、用户管理）都
是为它供数据。

**Independent Test**: 在数据库中预置 1 个上线城市 + 1 个上架商户（含图片、≥1 个上架标签、四维评分、评价、故事），
仅调用 `/api/app/explore`、`/api/app/cities`、`/api/app/merchants`、`/api/app/merchants/{id}` 即可独立验证 P1 流程，
完全不依赖管理后台 UI。

**Acceptance Scenarios**:

1. **Given** 系统已上线 ≥1 个城市，且 ≥1 个城市设置了 `bannerSortOrder > 0`，**When** App 调用 explore 接口，
   **Then** 返回当前城市信息 + banner 列表（= 所有 `online=true` 且 `bannerSortOrder>0` 的城市按 `bannerSortOrder ASC`，
   可能 0/1/N 张）+ 空状态标识。
2. **Given** 在某城市下存在多个上架商户，**When** 用户按 `period=排卵期` 过滤并请求商户列表，**Then** 仅返回包含该周期
   的上架商户，且排序为"权重降序、创建时间降序"。
3. **Given** 某商户有 3 个标签其中 1 个已下架，**When** 调用商户详情，**Then** 返回 2 个上架标签，且商户本身仍可见。
4. **Given** 商户四维原始分 S=24/L=20/E=20/I=16（满分 30/25/25/20），**When** 客户端拉详情，**Then** 服务端
   返回百分制 80/80/80/80 与爱女指数 80（对应客户端 8 级 / 4 颗星）。
5. **Given** 当前城市下当前周期无任何上架商户，**When** 用户筛选，**Then** 列表返回空集合并附带空状态语义而非 500 错误。

---

### User Story 2 - 运营人员管理城市 / 标签 / 商户内容 (Priority: P1)

运营 Manager（ADMIN 或 MEMBER 角色）登录运营后台，对城市、爱女标签、商户进行新增 / 编辑 / 上下架 / 排序 / 权重维护，
并按需上传图片、维护用户评价与商户故事。所有列表页统一带 Apply / Reset 筛选与右下角分页器（默认 20，可切 30）。

**Why this priority**: App 端的内容完全依赖运营录入。无后台，移动端只能显示空数据。

**Independent Test**: 直接通过运营后台前端，使用初始 admin 账号登录，依次完成：新增城市并上线 → 新增标签并上架 →
新增商户（含 logo、≥1 张图片、四维评分、≥1 个标签、≥1 条评价、故事、权重、推荐周期、上架）→ 列表中可看到该商户。
此过程与 App 端解耦，可单独 demo。

**Acceptance Scenarios**:

1. **Given** 运营已登录，**When** 创建一个名为"上海"的城市并上线，**Then** 城市出现在 `/cities` 列表，App 端
   `/api/app/cities` 同步可见。
2. **Given** 新增商户表单填写完整且四维评分均在各自上限内（S≤30、L≤25、E≤25、I≤20），**When** 提交，**Then**
   商户保存成功并默认下架，运营再点击"上架"后 App 端可查询到。
3. **Given** 商户名称长度为 16 个汉字，**When** 提交，**Then** 返回校验错误（≤15 汉字）。
4. **Given** 已存在的标签被运营下架，**When** 任一 App 详情接口拉取关联该标签的商户，**Then** 返回的标签列表
   不再包含该标签，但商户依然在线。
5. **Given** 运营删除一个分类，**When** 该分类下原本绑定 5 个上架商户，**Then** 这 5 个商户自动下架，App 端列表
   不再返回。
6. **Given** 列表页设置筛选条件后点击 Apply，**When** 再点击 Reset，**Then** 筛选条件清空、表格恢复默认列表，
   且分页回到第 1 页。

---

### User Story 3 - 系统管理员管理运营 Manager 账号 (Priority: P2)

只有 ADMIN 角色 Manager 可见"Manager 管理"入口，能分页 / 过滤查看运营 Manager、新建 MEMBER 账号、启用 / 停用账号、
重置密码。新建 Manager 接口在后端强制 `role=MEMBER`，前端 UI 也不暴露角色选择。

**Why this priority**: 没有它，多人协作时只能共用初始 admin 账号；但单人运营场景下 P1+P2 即可承载内容生产，
故定 P2。

**Independent Test**: 用初始 admin 账号登录 → 进入 `/managers` 列表 → 创建一个新 Manager（无论前端是否传 role=ADMIN，
后端最终落库为 MEMBER）→ 该 Manager 能登录但看不到 `/managers` 菜单 → admin 停用该 Manager → 该 Manager 再次登录失败。

**Acceptance Scenarios**:

1. **Given** 当前登录 Manager 角色为 MEMBER，**When** 访问 `/managers` 列表或 `/api/admin/managers`，**Then** 前端
   菜单不展示且后端返回 403 / 拒绝。
2. **Given** 管理员调用新建 Manager 接口并显式传 `role=ADMIN`，**When** 后端持久化，**Then** 实际 role 仍为 MEMBER。
3. **Given** 默认 admin 账号配置（username=admin, password=`8@y2eoRLyStM*UVU`, role=ADMIN, enable=true），
   **When** 系统首次启动，**Then** `loves_manager` 表中存在该账号，且密码以 BCrypt 形式存储。
4. **Given** 一个已停用的 Manager，**When** 该 Manager 尝试登录，**Then** 登录失败并提示账号已停用。

---

### User Story 4 - 运营操作的审计与可追溯 (Priority: P3)

所有 admin 端关键写操作（城市 / 标签 / 分类 / 商户 / 用户的创建、修改、上下线、删除、密码重置）通过 `@OperationLog`
异步落库；管理员可按操作人 / 模块 / 时间区间在 `/logs` 页面分页查看。

**Why this priority**: 治理与合规价值高，但对最小可用产品不阻塞，可在 P1/P2 走通后补齐。

**Independent Test**: 触发一次"创建城市"操作 → 查 `/api/admin/logs` 能看到该条日志记录（含 username、module、
action、target、payload、createdAt），即使其余日志为空也能通过。

**Acceptance Scenarios**:

1. **Given** Admin 修改了一个商户的权重，**When** 之后查询操作日志，**Then** 列表中存在一条 module=merchant、
   action=update、target=<商户 id> 的记录。
2. **Given** 按操作人筛选 `username=admin` 且时间区间为今天，**When** 查询，**Then** 仅返回 admin 今日的日志。
3. **Given** 日志条目数量超过 1 页（>20 条），**When** 翻页，**Then** 分页器位于表格右下角且默认每页 20，可切换 30。

---

### Edge Cases

- 当某商户的图片列表为空时，后端 MUST 在新增/编辑时拦截（≥1 张），但若历史数据为空，详情接口仍返回空数组而非 500。
- 当用户评价含 emoji 字符（包括组合 emoji，如 👨‍👩‍👧）时，存取链路全部按 UTF-8 / 4 字节字符兼容存储。
- 当四维评分中任一项超过其上限（如 S=31）时，新增/编辑接口必须返回字段级校验错误，不允许写入。
- 当 App 端请求 `cityId` 不存在或已下线的城市时，返回空列表并附空状态语义，而非 404。
- 当商户故事文本超过 5000 字时，新增/编辑接口拒绝并提示长度上限。
- 当标签或分类正被多个商户引用时，标签下架 / 分类删除的语义不同：标签下架仅隐藏，分类删除联动下架商户。
- 当同一城市存在同名（chineseName）记录时，新增 / 编辑必须拒绝（名称不重复）。
- 当 token 过期或非法时，admin 端接口统一返回 401；前端拦截并跳转登录页。
- 当 App 端请求缺失 `X-API-Key` 头或 key 不在白名单时，统一返回 401（不区分具体原因）；当配置中
  `app.security.api-keys` 为空时，应用 MUST 启动失败并打印明确告警，避免裸奔。
- 当文件上传单图超过 20MB 或类型不被允许时，上传接口返回字段级校验错误。
- 当默认 admin 账号已存在时，重复启动应用 MUST NOT 重复创建或重置该账号。

## Requirements *(mandatory)*

### Functional Requirements

#### 通用 / 平台

- **FR-001**: 系统 MUST 提供运营后台与移动端两套相互独立的 API 入口，路径前缀分别为 `/api/admin/**` 与
  `/api/app/**`，互不混用。
- **FR-002**: 系统 MUST 对所有运营后台接口（除登录）启用登录态校验；未登录访问返回 401。
- **FR-003**: 系统 MUST 对"Manager 管理"相关接口（`/api/admin/managers/**`）强制要求 `ROLE_ADMIN`；其他角色
  访问返回 403。
- **FR-004**: 系统 MUST 在管理端写操作完成后异步记录操作日志，记录至少包含操作人、所属模块、动作、目标对象、时间。
- **FR-005**: 系统 MUST 对失败请求返回符合 RFC 7807 的 ProblemDetail 响应；成功响应直接返回业务对象，不再包装。
- **FR-006**: 列表类管理端接口 MUST 支持分页（默认 20，可切换 20 / 30）与筛选（Apply / Reset 语义），分页器
  位于表格右下角。

#### App 端（love-space-app）

- **FR-010**: 系统 MUST 提供 `/api/app/explore` 返回：当前城市信息 + 推荐 banner 列表（0/1/N）+ 空状态标识。
  **banner 数据直接复用 City 自身**：banner 列表 = 已上线（`online=true`）且 `bannerSortOrder > 0` 的城市集合，
  按 `bannerSortOrder ASC` 排序；展示字段（背景图 / 中文名 等）直接取自 City，不存在独立 banner 实体。
  `bannerSortOrder` MUST `>= 0`（后端拒绝负数）；`= 0` 表示不进入 banner。
- **FR-011**: 系统 MUST 提供 `/api/app/cities` 返回所有已上线城市，按运营排序。
- **FR-012**: 系统 MUST 提供 `/api/app/merchants` 商户列表接口，参数 `cityId`（必填）/ `period`（可选，**单值**：
  MENSTRUAL / FOLLICULAR / OVULATION / LUTEAL；语义为"商户的 recommendedPeriods 包含该值"，多周期请前端发起多次
  请求合并）/ `categoryId`（预留，可选；**省略时返回该城市所有上架商户，包括 categoryId 为 NULL 的；不支持
  以特殊值显式过滤"无分类"**）/ `page` / `size`，排序"权重降序、创建时间降序"。
- **FR-013**: 系统 MUST 提供 `/api/app/merchants/{id}` 详情，返回图片、logo、地址、坐标（预留可空）、仅上架标签、
  四维百分制评分、爱女指数（10 级）、用户评价（昵称 / 标题 / 正文，支持 emoji）、商户故事（≤5000 字）。
- **FR-014**: 系统 MUST 在后端完成四维百分制换算 `单项百分制 = 运营填分 ÷ 当前维度满分 × 100`（仅保留整数）与
  爱女指数 10 级映射；客户端不参与计算。
- **FR-015**: 系统 MUST 在标签下架时，对 App 端详情 / 列表中的标签做隐藏处理，但不影响商户自身上下架状态。
- **FR-016**: App 端 API MUST 为只读，MVP 期不提供账号系统、不接受写入请求；App 端 MUST NOT 提供任何
  用户管理 / 注册 / 登录接口。
- **FR-017**: 所有 `/api/app/**` 接口 MUST 通过 API Key 鉴权，客户端在请求头 `X-API-Key` 中携带；
  后端通过配置项 `app.security.api-keys`（数组，支持多 key）维护允许的 key；命中任一即放行，全部不命中
  返回 401（ProblemDetail）。API Key 校验 MUST 为常量时间比较以避免侧信道；MUST NOT 写入业务数据库，
  仅来自配置 / 环境变量。
- **FR-018**: API Key 鉴权失败 MUST NOT 透露具体原因（如"key 已过期"），统一返回 401 + 通用提示。
- **FR-019**: API Key 鉴权失败 MUST 以 `WARN` 级别记录一条审计日志，字段包含远端 IP、`X-API-Key` 头是否存在、
  请求路径与时间戳；**MUST NOT** 记录任何 key 明文或可逆摘要（如需排查可记录 key 的 SHA-256 前 6 个十六进制字符
  作为脱敏指纹）。

#### 运营后台后端（love-space-admin）

- **FR-020**: 系统 MUST 提供 `POST /api/admin/auth/login`、`POST /api/admin/auth/logout`、
  `GET /api/admin/auth/me` 三个鉴权接口。
- **FR-021**: 系统 MUST 在数据库初始化（Liquibase formatted-SQL changelog `002-seed-admin-manager.sql`，
  通过 `db.changelog-master.yaml` include 引入）阶段植入默认 admin 账号到 `loves_manager` 表
  （username=`admin`，密码=`8@y2eoRLyStM*UVU`，role=ADMIN，enable=true，密码以**预生成的 BCrypt 哈希字符串**
  直接写入，cost=10）；changeset MUST 使用 `--precondition-sql-check` 在 `username='admin'` 已存在时跳过以保证
  幂等；应用层代码 MUST NOT 再二次创建该账号（避免双植入路径）。
- **FR-022**: 系统 MUST 提供运营 Manager 管理接口 `/api/admin/managers/**`（仅 ADMIN）：分页列表
  （按 username 模糊 / role / 启用状态 / 创建时间过滤）、新建（强制 role=MEMBER）、详情、启用、停用、重置密码。
- **FR-023**: 系统 MUST 在城市管理中支持：分页列表（名称 / 上下线过滤，**默认按 `createdAt DESC` 排序**）、
  新增（名称不重复）、编辑、删除、上线 / 下线、维护 `bannerSortOrder`（`>=0`；`>0` 即作为 explore banner，
  数值越小越靠前）。**`bannerSortOrder` 不影响城市列表排序，仅影响 banner 展示**。
- **FR-024**: 系统 MUST 在分类管理中支持：列表（默认按 `createdAt DESC` 排序）、新增（名称不重复，≤10 汉字）、
  编辑、删除（删除后该分类下所有商户自动下架）。**不再维护分类排序字段**。
- **FR-025**: 系统 MUST 在标签管理中支持：分页列表（名称 / 上下架过滤，默认按 `createdAt DESC` 排序）、
  新增（不重名，≤6 汉字）、编辑、上架 / 下架。
- **FR-026**: 系统 MUST 在商户管理中支持：分页列表（名称 / cityId / categoryId / period / 上下架过滤）、新增、
  详情、编辑、删除、上架 / 下架。
- **FR-027**: 商户字段 MUST 满足：name ≤15 汉字（不校验唯一）、logo 必填且仅 1 张、images ≥1 张、推荐周期为
  月经期 / 卵泡期 / 排卵期 / 黄体期的多选、四维评分各自不超过上限（30/25/25/20）、标签数量无上限按时间排序、
  故事 ≤5000 字、可填 longitude/latitude（可空）、weight 可调。
- **FR-028**: 系统 MUST 提供文件上传接口 `POST /api/admin/files/upload`，单图 ≤20MB，**仅允许 MIME 白名单
  `image/png`、`image/jpeg`、`image/webp`**（其他类型 / 伪造扩展名返回 400），返回可访问 URL。
- **FR-029**: 系统 MUST 提供操作日志查询 `GET /api/admin/logs`，支持按操作人 / 模块 / 时间区间过滤与分页。

#### 运营后台前端（love-space-web）

- **FR-040**: 前端 MUST 替换 TailAdmin 演示路由，按需提供：`/signin`、`/managers`、`/managers/create`、`/cities`、
  `/cities/create`、`/cities/:id/edit`、`/categories`、`/tags`、`/merchants`、`/merchants/create`、
  `/merchants/:id/edit`、`/logs`。
- **FR-041**: 前端 MUST 根据当前登录 Manager 角色过滤侧边栏菜单：MEMBER 不展示"Manager 管理"入口。
- **FR-042**: 前端新增 Manager 表单 MUST 锁定 role=MEMBER 且不暴露角色选择控件。
- **FR-043**: 所有列表页 MUST 在顶部提供 Apply / Reset 两个按钮控制筛选区，分页器统一使用 `support-tickets` 组件
  样式并放置在表格右下角。
- **FR-044**: 商户表单 MUST 按以下分区展示：基础信息 / 图片（多图 + logo）/ 周期 + 分类 + 城市 / 地址 + 坐标 /
  标签 / 四维评分 / 用户评价（动态列表，支持 emoji）/ 故事 / 权重 / 上下架。
- **FR-045**: 前端 MUST 提供顶部用户菜单展示当前 Manager 与"退出"操作。

#### 数据 / 关键业务规则

- **FR-050**: 商户排序规则 MUST 为 `weight DESC, createdAt DESC`，应用于 App 列表与 admin 列表默认排序。
- **FR-051**: 删除分类 MUST 在 service 层联动下架该分类下所有商户。
- **FR-052**: 标签下架 MUST 在 App 端商户视图中隐藏该标签，且不影响商户的上下架状态。
- **FR-053**: Manager 密码 MUST 使用 BCrypt 单向哈希存储；登录时通过哈希比对完成校验。
- **FR-055**: 数据库表名 MUST 统一加 `loves_` 前缀，单数 + snake_case 形态，与实体类一一对应（如 `Manager` →
  `loves_manager`、`MerchantImage` → `loves_merchant_image`）。两个后端 (admin / app) 共用该命名约定。
- **FR-056**: Liquibase changelog MUST 以 `db.changelog-master.yaml` 作为入口，但 master 仅包含 `<include>` 列表
  不内联 `changeSet`；实际表结构与种子数据脚本 MUST 使用 Liquibase formatted-SQL 文件
  (`--liquibase formatted sql` 头 + `--changeset author:id`)，禁止用 YAML/XML 描述 changeSet 内容。
- **FR-057**: 项目 MUST NOT 显式 pin Liquibase 版本，由 Spring Boot 4.0.6 `spring-boot-starter-liquibase` BOM
  默认版本决定。
- **FR-054**: 接口 MUST 在校验失败时返回字段级错误信息（field + message 列表），便于前端表单逐字段提示。

### Key Entities *(include if feature involves data)*

- **Manager（运营 Manager，表 `loves_manager`）**：username（唯一）、password（BCrypt）、nickname、
  role（ADMIN/MEMBER）、enable、createdAt。
- **City（城市，表 `loves_city`）**：chineseName、englishName、chineseProvince、englishProvince、backgroundImage、
  bannerSortOrder（`>=0`；`>0` 表示参与 explore banner 展示）、online、createdAt。
- **Category（分类，MVP 预留，表 `loves_category`）**：name、createdAt（无 sortOrder，列表按创建时间倒序）。
- **Tag（爱女标签，表 `loves_tag`）**：name（不重名，≤6 汉字）、online、createdAt。
- **Merchant（商户，表 `loves_merchant`）**：name（≤15 汉字）、logo、address、longitude、latitude、cityId、
  categoryId、safetyEnvironmentScore、businessRightsScore、experienceFriendlyScore、socialContributionScore、
  story（≤5000 字）、weight、online、createdAt；关联多张图片、多个推荐周期、多个标签、多条评价。
- **MerchantImage（商户图片，表 `loves_merchant_image`）**：merchantId、url、sortOrder。
- **MerchantPeriod（商户推荐周期，表 `loves_merchant_period`）**：merchantId、period（月经期 / 卵泡期 / 排卵期 / 黄体期）。
- **MerchantTag（商户标签关联，表 `loves_merchant_tag`）**：merchantId、tagId。
- **MerchantReview（用户评价，表 `loves_merchant_review`）**：merchantId、nickname、title、content（支持 emoji）、sortOrder。
- **OperationLog（操作日志，表 `loves_operation_log`）**：managerId、username、module、action、target、payload、createdAt。

> 通用审计：所有实体含 createdAt / updatedAt（不记录 createdBy / updatedBy）。引用关系仅以对方主键
> ID 字段持有，不在数据库层创建外键约束（依据 constitution 原则 II）。

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 终端用户在加载某城市的商户列表后，能在 5 秒内看到至少首屏的商户卡片（含 logo、名称、四维百分制评分、
  爱女指数）。
- **SC-002**: 运营人员在受过 30 分钟培训后，能够从零开始完成"新增一个上线城市 + 一个上架商户（含图片、标签、评分、
  故事、评价）"的全流程，平均耗时 ≤10 分钟，错误率 <10%。
- **SC-003**: 当某商户的四维原始分变化时，App 端在下一次详情请求中返回的百分制分值与新原始分严格一致（按规则
  四舍五入到整数），偏差率为 0。
- **SC-004**: Manager 管理入口对 MEMBER 角色 Manager 在前端不可见、在后端 100% 被拒绝；安全验证用例通过率 100%。
- **SC-005**: 初次启动后，默认 admin 账号（`loves_manager` 表）在 100% 启动场景下被创建且仅创建一次
  （重复启动不重复写入或重置）。
- **SC-006**: 管理后台关键写操作（增 / 改 / 删 / 上下线 / 重置密码）在操作完成后，对应操作日志可查率 ≥99%。
  > 性能门槛"日志写入对原操作 P95 延迟影响 <50ms"**推迟到性能专项需求阶段**再行验收，MVP 不阻塞。
- **SC-007**: 当用户评价含 emoji（含组合 emoji）时，后端存取与 App 端展示均保持字符完整，丢失率为 0。
- **SC-008**: ~~商户列表与 App 端 explore 接口 P95 ≤500ms~~ **推迟**：性能验收推迟到性能专项需求阶段处理，MVP 不阻塞发布；仅保留功能正确性验证。
- **SC-009**: 列表页筛选 Apply 后所得记录数 = 同等条件下手工核对结果，差异为 0；Reset 后筛选条件全部清空且页码
  回到 1。

## Assumptions

- MVP 不提供 C 端账号体系；App 接口全部为只读匿名访问。
- 运营人员数量在 MVP 阶段 ≤20 人，并发请求量不高，无需复杂多租户隔离。
- 现阶段数据规模上限：城市 ≤100、商户 ≤1 万、单商户图片 ≤30 张、单商户评价 ≤200 条。
- 图片存储 MVP 默认采用本地或 MinIO；公有云 OSS 接入是后续工作（见开发文档第十节）。
- 客户端坐标字段（longitude/latitude）本期 schema 预留，是否实际写入由运营 / 产品后续决策。
- 分类筛选按 MVP 预留处理（接口 / 字段就位但 App 端是否暴露开关由运营评估）。
- 多语言：城市名称同时维护中英文与省份中英文，前端及 App 端展示策略遵循各自的国际化方案；本规格不约束语言切换 UI。
- 移动端版本：本规格不约束 iOS / Android 客户端实现细节，仅约定 App 后端契约。
- 所有时间戳以 UTC 存储，前端按用户时区渲染；管理后台筛选输入按用户本地时区解析。
- 数据库为 PostgreSQL；不创建外键约束（依据 constitution）；ID 使用 UUIDv7。数据库迁移使用 Liquibase
  （由 Spring Boot 4.0.6 starter 携带的默认版本，不显式 pin），changelog 入口为 `db.changelog-master.yaml`
  仅 include，实际脚本采用 formatted-SQL；所有表带 `loves_` 前缀（单数 snake_case）。
- 默认 admin 账号密码 `8@y2eoRLyStM*UVU` 仅用于环境初始化，首次上线后由运营自行通过"重置密码"接口修改。
