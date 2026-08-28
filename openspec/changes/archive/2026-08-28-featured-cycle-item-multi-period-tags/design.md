## Context

`GET /api/app/featured-cycle-items` 当前把 `FeaturedCycleItem.phase`（单值）直接映射到响应的 `period` 字段。运营会把同一个内容投放到多个周期，DB 里落成多条独立记录（各有自己的 `sort_order` / `banner` / 文案），客户端因此看不到跨周期关系。

同时，关联实体 id 被拆成 `activity_id` / `route_id` / `article_id` 三列，任一条目恒有两列为 null。`type` 已是判别列，三列不带来额外信息，却让实体、两端 DTO、web 表单各写一遍同构分派；本次的 target 聚合还要再按类型三选一取 id。两处都是 App 响应的破坏性变更，合并一次交付。

约束：
- `phase` 仍是单值列，多周期是「多条记录」而非「一列多值」——本次不改这一点。
- App 后端无用户体系，服务端不按用户筛选；周期判定在客户端。
- 现有实现（`FeaturedCycleItemQueryService.feed`）已把全量条目捞进内存做可见性过滤——运营配置级数据量（每周期个位数），这个口径继续沿用。
- admin 端与 web 端不参与本次变更。

## Goals / Non-Goals

**Goals:**
- 响应 `period` 由单值改为「该 target 覆盖的周期集合」数组，让客户端能在卡片上打出跨周期标签。
- 保持条目粒度、排序、可见性、过滤语义、400/空数组行为完全不变。

- 三个关联 id 列合并为单列 `target_id`，消除三处同构分派。

**Non-Goals:**
- 不合并同 target 的多条条目（明确要求返回两条）。
- 不把 `phase` 改成多值列——多周期投放仍是多条记录。
- 不改 web 后台的表单交互（仍先选类型、再按类型展示对应实体下拉）。
- 不引入外键：`target_id` 是多态列，天然无法加 FK，这是合并的已知代价（见 Risks）。

## Decisions

### 已定决策：聚合范围是全部可下发条目，先于查询参数过滤计算

`period` 数组在「`online=true` 且关联实体可见」的全部条目上计算，**不受本次请求的 `period` / `type` 参数影响**。

理由：若在过滤后的结果集上聚合，带 `?period=LUTEAL` 时数组恒为 `["LUTEAL"]`，字段退化成原来的单值，需求落空。带 `?type=ACTIVITY` 同理会丢掉其他类型条目的周期（虽然同 target 必然同 type，但保持口径统一更好推理）。

代价：不可下发的条目（自身下线、或关联实体下线/删除）不贡献周期，所以运营把某周期的条目下线后，其他周期条目上的标签会同步少一个。这与「只下发可见内容」的既有口径一致，是正确行为，不是缺陷。

**替代方案（未采用）**：在全部条目（含下线）上聚合。会把运营下线的内容以标签形式泄露给客户端，与可见性规则冲突。

### target 相同性判定：`(type, targetId)` 二元组

列合并后直接取 `(type, targetId)`，不再按类型三选一。带上 `type` 是因为 `targetId` 指向三张不同的表，UUID 理论上不会撞，但二元组语义更直白，也让 key 与「同一 target」的定义字面对齐。

实现：在 `FeaturedCycleItemQueryService.feed` 里，可见性过滤之后、`period`/`type` 参数过滤之前，用一次 `Collectors.groupingBy` 建 `Map<targetKey, EnumSet<Period>>`，`toResponse` 时按 key 取出。`EnumSet` 天然按枚举声明顺序迭代，去重与排序都不用额外写代码。

### 已定决策：DTO 与响应体一并合并为 `targetId`

不在服务层把 `targetId` 拆回三个字段下发。拆回去等于列合并只省了存储、没省代码，三处同构分派原样保留，收益归零。admin 请求体的 `targetId` 为必填（`@NotNull`），存在性校验仍按 `type` 分派到对应 repository，错误文案保持「关联活动/路线/文章不存在」的既有口径——用户看到的报错不变。

admin 响应的 `relatedTitle`（关联实体标题，实体已删时为 null）保留不变，web 端仍靠它标记「已删除」。

### 已定决策：不加外键，引用完整性仍靠服务层

多态列无法加 FK，这与项目既有的无外键口径一致，不是本次新引入的缺口。写入侧靠 `FeaturedCycleItemService` 的 `existsById` 校验，读取侧靠 `feed` 的可见性过滤（关联实体被删的条目自然不下发）。

**替代方案（未采用）**：为保住外键而维持三列。三列各自能加 FK，但项目本就没加，为一个没在用的能力付三处分派的代价不划算。

**替代方案（未采用）**：在 SQL 里做窗口聚合。数据量个位数，且可见性判定依赖跨模块的 online 状态（活动/大使/文章分属不同 repository，项目无外键也不 join），SQL 聚合反而要把可见性规则下沉到 DB，破坏现有分层。

### 契约更新

`contracts/api-spec.json` 中 `/api/app/featured-cycle-items` 的 `get.summary` 与 `period` 参数 description 需同步改写，说明 `period` 响应字段为数组、且过滤按条目自身所属周期。该 operation 已有 `x-requirement: "featured/App 端周期推荐查询"` 反链，Requirement 名不变，反链无需改动。

注意：该 operation 目前未在契约里声明 response schema（只有 summary + parameters），故本次没有响应体 schema 可改，语义变化落在 summary 文字上。

## Risks / Trade-offs

- **[破坏性变更：App 客户端解析 `period` 会失败]** → 字段类型从字符串变数组，老客户端必然解析异常。需与客户端同步发版；本仓库无法保证，交付时在变更说明里显式标注 BREAKING，由用户决定放量节奏。
- **[标签随可见性漂移，运营可能困惑]** → 下线某周期条目会让其他条目上的标签少一个。属正确行为，在 admin 端无需改动；若运营反馈需要，另开 change 在后台加提示。
- **[迁移不可逆点：三列在同一个 changeset 里被删]** → rollback 脚本已备（信息无损，`type` 足以还原分派）。生产执行前先在测试环境跑一遍 forward + rollback 验证。
- **[app 后端早于 admin 发布会启动失败]** → 实体引用尚不存在的 `target_id` 列。部署顺序在上面写死：admin 先行。
- **[三列全空的脏行会中止迁移]** → 这是刻意的（见 Migration Plan）。上线前先跑一次 `SELECT count(*) FROM loves_featured_cycle_item WHERE COALESCE(activity_id, route_id, article_id) IS NULL` 确认为 0。
- **[失去加外键的可能性]** → 多态列无法加 FK。项目本就无外键，实际损失为零；若日后要强引用完整性，需要的是三张关联表各自的中间表，而不是退回三列。
- **[内存聚合随数据量增长退化]** → 现为 O(n) 分组，n 是全部周期推荐条目数（个位数到几十）。与既有全量捞取口径同量级，不引入新的性能拐点；若条目数量级变化，聚合与可见性过滤会一起需要重做，届时统一处理。

## Migration Plan

Liquibase changeset 由 admin 端统一管理（app 端 liquibase 关闭），新建 `022-merge-featured-cycle-item-target-id.sql`，单个 changeset 内顺序执行：

1. `ALTER TABLE loves_featured_cycle_item ADD COLUMN target_id uuid;`
2. `UPDATE loves_featured_cycle_item SET target_id = COALESCE(activity_id, route_id, article_id);`
3. `ALTER TABLE loves_featured_cycle_item ALTER COLUMN target_id SET NOT NULL;`
4. `ALTER TABLE loves_featured_cycle_item DROP COLUMN activity_id, DROP COLUMN route_id, DROP COLUMN article_id;`

`COALESCE` 安全：三列中恒有且仅有一列非空（由服务层保证，`type` 判别）。若历史数据存在三列全空的脏行，第 3 步会失败并中止迁移——这正是想要的行为，脏数据必须先人工处理，不能静默带着 null 进新列。

索引 `ix_loves_featured_cycle_item_phase (phase, sort_order)` 不含被删列，不受影响。

**rollback**：加回三列，按 `type` 把 `target_id` 分派回对应列（`UPDATE ... SET activity_id = CASE WHEN type = 'ACTIVITY' THEN target_id END, ...`），再删 `target_id`。信息无损，可逆。

部署顺序：admin 后端先发（跑迁移）→ app 后端 → web → App 客户端。app 后端启动不跑 liquibase，但其实体依赖新列，因此必须晚于 admin 发布。

**回滚窗口**：admin 与 app 已发布、客户端未跟进时，客户端是坏的但服务端自洽；回滚需同时回退两个后端并执行 liquibase rollback。

## Open Questions

无。
