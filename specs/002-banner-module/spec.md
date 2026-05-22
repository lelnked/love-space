# Feature Specification: Banner Module

**Feature Branch**: `002-banner-module`

**Created**: 2026-05-22

**Status**: Draft

**Input**: 引入独立的 Banner 模块，替代当前 City 实体内置的 banner 字段；运营在管理后台可创建/编辑/启用/禁用 banner；移动端去掉 explore 模块，新增 banner 展示接口；City 的上线/下线状态变化要联动其关联 banner 的上线/下线状态。

## User Scenarios & Testing *(mandatory)*

### User Story 1 - 运营管理 City 类型 Banner (Priority: P1)

运营人员通过管理后台的"Banner 管理"菜单维护投放给移动端的 banner 资源：创建 banner、上传图片、选择关联的城市、命名 banner，并在列表页面控制 banner 是否对外展示。

**Why this priority**: 这是本特性的核心价值。没有这一项，新的 banner 体系无法上线运营，移动端也拿不到内容。

**Independent Test**: 在管理后台进入 Banner 菜单，创建一条 CITY 类型 banner（上传至少一张图片、选择一个已启用城市、填写名称），保存后在列表页将其切换为启用状态,仅凭这一条流程就能向移动端提供可展示的 banner。

**Acceptance Scenarios**:

1. **Given** 运营已登录管理后台且至少存在一个启用状态的城市，**When** 运营进入 Banner 列表点击"新增"，填写名称、上传至少一张图片、在城市下拉框中搜索并选中一个启用城市，提交保存，**Then** 系统创建一条类型为 CITY 的 banner，默认处于禁用（offline）状态，并出现在列表中。
2. **Given** 一条 banner 已存在，**When** 运营在列表页将该 banner 的开关切换为启用，**Then** banner 的 online 状态变为 true，移动端 banner 接口会返回它。
3. **Given** 城市下拉框中既包含启用城市也包含禁用城市，**When** 运营打开下拉框，**Then** 下拉框只展示启用状态的城市，并支持按名称搜索过滤。
4. **Given** 运营进入某条 banner 的编辑页面，**When** 查看页面控件，**Then** 页面提供名称、图片、关联城市的修改入口，但**不**提供启用/禁用切换；启用/禁用只能在列表页操作。
5. **Given** 运营编辑了一条 CITY 类型 banner 并将关联城市替换为另一个启用城市，**When** 保存，**Then** banner 的 link 更新为新城市的 id，更新时间被刷新。

---

### User Story 2 - 移动端浏览 Banner (Priority: P1)

移动端用户打开 App 后，原先的 explore 模块被替换为新的 banner 展示位；App 通过 banner 接口取得当前所有上线的 banner，连同关联实体的关键信息一起渲染，例如 CITY 类型 banner 会附带城市的 id 和名称用于点击跳转。

**Why this priority**: User Story 1 产出的数据必须有消费端才有意义；这是面向终端用户的可见交付。

**Independent Test**: 当后台存在至少一条启用的 CITY banner 且其关联城市为启用状态时，调用移动端 banner 接口应返回该 banner 的 id、name、type、image 列表和包含 `{id, name}` 的 data 对象。

**Acceptance Scenarios**:

1. **Given** 后台存在两条 banner，一条 online=true、一条 online=false，**When** 移动端请求 banner 列表，**Then** 仅返回 online=true 的那一条。
2. **Given** 一条 online 的 CITY banner 关联到城市 X，**When** 移动端获取该 banner，**Then** 返回结果中 `data` 为对象 `{ id: <X 的 id>, name: <X 的名称> }`，`image` 为图片 url 列表。
3. **Given** App 端原本存在 explore 模块，**When** 本特性发布，**Then** explore 相关入口与接口从 App 后端移除，由 banner 模块替代。

---

### User Story 3 - City 状态联动 Banner (Priority: P2)

运营在 City 管理中切换城市的 online 状态时，与该城市关联的所有 banner 会自动跟随上线或下线，避免出现"城市已下线但 banner 还在向用户展示"或"城市重新上线后还要手动逐条启用 banner"的运营失误。

**Why this priority**: 是数据一致性保障，不阻塞核心 CRUD，但显著降低运营出错风险，并保证移动端展示与城市状态一致。

**Independent Test**: 准备一个启用城市 X，并新建两条关联 X、当前处于 online=true 状态的 banner；将 X 切换为 offline，再请求移动端 banner 列表，应不再返回这两条 banner；将 X 重新切换为 online，再次请求，应重新返回这两条 banner。

**Acceptance Scenarios**:

1. **Given** 城市 X 当前为 online，关联了 N 条 online=true 的 CITY banner，**When** 运营将 X 切换为 offline，**Then** 系统自动将这 N 条 banner 的 online 全部置为 false。
2. **Given** 城市 X 当前为 offline，关联了 N 条 online=false 的 CITY banner，**When** 运营将 X 切换为 online，**Then** 系统自动将这 N 条 banner 的 online 全部置为 true。
3. **Given** 城市状态切换过程中联动更新失败，**When** 任一 banner 更新出错，**Then** 错误被记录但不阻塞城市本身的状态变更（最终一致；运营可在列表页手动修正个别 banner）。

---

### Edge Cases

- 创建 CITY banner 时，所选城市在保存的瞬间被另一管理员设为 offline：保存仍然成功（banner 与城市的关联是 id 级），但因关联城市不再启用，该 banner 在列表页可见、可编辑；启用动作的可用性见下条。
- 在 banner 列表页对一条 CITY banner 执行启用，但其关联城市当前为 offline：禁止启用，提示运营先启用对应城市，或先改关联城市。
- 城市被删除（若业务允许删除）：其关联 banner 的 link 指向不存在的 id；移动端接口在拉取时跳过这些不可解析的 banner，后台列表正常显示并允许运营修正。
- 上传图片为空：banner 创建/保存失败并提示"至少上传一张图片"。
- 城市下拉框搜索无匹配：显示空态文案"未找到匹配的城市"。
- 旧 City 实体上原有的 banner 字段与数据：迁移到新的 banner 表后，City 表上的旧字段被清理；现有 banner 数据按照 CITY 类型迁移并保留原有 online 状态。

## Requirements *(mandatory)*

### Functional Requirements

**数据模型与迁移**

- **FR-001**: 系统 MUST 移除 City 实体上一切与 banner 相关的字段、接口与业务逻辑（包括 admin 后端 DTO、web 前端 city 表单中的 banner 区块）。
- **FR-002**: 系统 MUST 新增独立的 Banner 实体，至少包含：唯一 id（UUID）、name、online（布尔，默认 false）、type（枚举，首期取值 `CITY`）、image（图片 url 列表，至少 1 张）、link（字符串；当 type=CITY 时存放对应 City 的 id）、created_at、updated_at。
- **FR-003**: 系统 MUST 提供从旧 `City.banner` 字段到新 Banner 表的一次性数据迁移，保留原有图片、关联城市与上线状态。

**管理后台（admin / web）**

- **FR-004**: 管理后台 MUST 提供一个新的"Banner"菜单项，包含列表页与新增/编辑页。
- **FR-005**: Banner 列表页 MUST 展示 name、type、关联对象的可识别名称（CITY 类型显示城市名）、online 状态、updated_at，并支持按 name 模糊搜索、按 type 与 online 过滤、分页。
- **FR-006**: Banner 列表页 MUST 是唯一可以切换 banner online/offline 状态的入口。
- **FR-007**: 新增/编辑页 MUST 支持设置 name、上传一张或多张图片、选择 type；当 type=CITY 时 MUST 提供"关联城市"下拉框。
- **FR-008**: "关联城市"下拉框 MUST 仅展示当前 online=true 的城市，MUST 支持基于城市名称的关键字搜索过滤。
- **FR-009**: 编辑页 MUST NOT 包含 online 切换控件；保存编辑不得改变 banner 的 online 状态。
- **FR-010**: 创建 banner 时，online 字段 MUST 默认为 false；上线只能通过列表页的开关完成。
- **FR-011**: 在列表页将一条 CITY banner 切换为 online 时，系统 MUST 校验其关联城市当前为 online；若否则拒绝该操作并提示。

**移动端（app 后端）**

- **FR-012**: 系统 MUST 从 app 后端移除 explore 模块（路由、控制器、服务、相关 DTO/资源）。
- **FR-013**: 系统 MUST 新增 app 端 banner 接口，返回当前 online=true 的 banner 列表，每条包含：id、name、type、image（字符串列表）、data（对象）。
- **FR-014**: 当 banner.type=CITY 时，`data` 字段 MUST 至少包含关联城市的 `id` 与 `name`；其他类型暂不在本特性范围内，但接口结构 MUST 允许 `data` 容纳任意 JSON 对象，以便后续扩展。
- **FR-015**: app 端 banner 接口 MUST 跳过其关联实体已不存在或已下线的 banner（防御性过滤），即便其 online=true。

**City 状态联动**

- **FR-016**: 当 City 的 online 状态发生变化时，admin 后端 MUST 发布领域事件（`CityOnlineChangedEvent`，由 `CityService` 发布）。
- **FR-017**: 系统 MUST 提供一个事件监听器 `BannerEventListener` 处理 `CityOnlineChangedEvent`：将所有 link 指向该 city 的 CITY 类型 banner 的 online 状态同步为城市的新状态。
- **FR-018**: 联动更新 MUST 在城市状态变更事务提交之后进行；单条 banner 更新失败不得回滚城市状态；失败 MUST 被记录到日志以便排查。

**通用**

- **FR-019**: Banner 的创建、修改、上线、下线、删除（如果支持）操作 MUST 对任一已登录的 Manager 开放（无论角色为 ADMIN 还是 MEMBER），无需额外角色限制；即落入 `/api/admin/**` 的默认 `.authenticated()` 规则。只有 Manager 自身管理类接口（`/api/admin/managers/**`）才限制为 ADMIN，本特性不涉及。
- **FR-020**: 系统 MUST 在保存时校验：name 非空、image 至少 1 张、type=CITY 时 link 非空且对应城市存在。

### Key Entities

- **Banner**: 投放给移动端的图片广告/导航位。属性：id、name、online、type（首期仅 `CITY`，可扩展）、image（url 列表）、link（指向关联实体的 id；type=CITY 时为 City 的 id）、created_at、updated_at。
- **City（变化）**: 仍由 City 模块拥有，移除其内嵌的 banner 字段；新增 online 状态变化时发布领域事件的职责。

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 运营可在 3 分钟内完成"创建一条 CITY banner → 在列表页启用 → 在移动端可见"的端到端流程。
- **SC-002**: City 的 online 状态切换后，关联 banner 在移动端的可见性变化 100% 与新城市状态一致（在事件处理完成后）。
- **SC-003**: 旧 City 数据迁移完成后，原本存在 banner 内容的所有城市在新 Banner 表中均有对应记录，迁移丢失率为 0%。
- **SC-004**: 移动端 banner 接口在 P95 下的返回耗时不劣化于原 explore 接口的 P95；接口结构能在不引入破坏性改动的前提下支持 CITY 之外的至少一种新 type。
- **SC-005**: 编辑页不存在任何 online 切换控件（UI 走查通过率 100%）。

## Assumptions

- 图片上传沿用项目现有的图片存储/上传能力（与 City、Merchant 已有图片上传一致），本特性不引入新的存储服务。
- 运营账号统一以 Manager 标识（参见项目命名约定），banner 模块的鉴权与权限策略复用现有 Manager 体系，无需新增角色。
- 首期 type 枚举仅有 `CITY`，但实体、接口与 UI 应预留扩展位以便未来新增类型而无需破坏性变更。
- City 的删除（若存在）当前不会主动级联删除其关联 banner，由运营在列表页处理孤立的 banner。
- 旧 explore 模块在移动端客户端的入口下线由客户端团队同步处理；本特性只负责服务端的移除。

## Implementation Guidance *(non-binding hints captured from input)*

- admin 后端：`CityService` 在 online 状态变更时发布 `CityOnlineChangedEvent`；`BannerEventListener` 监听并更新对应 banner（建议使用 Spring `@TransactionalEventListener(phase = AFTER_COMMIT)` 以满足 FR-018）。
- 数据库：新表沿用项目命名约定，例如 `loves_banner`；image 列以 JSON 数组或专用图片关联表存储均可。
- 前端：在 `love-space-web` 新增 `pages/Banners/` 路由与菜单项；城市下拉框需要支持远程或本地搜索过滤，仅请求 online 城市。
