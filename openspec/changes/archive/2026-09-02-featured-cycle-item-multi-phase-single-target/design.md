## Context

周期推荐条目当前一条只归属一个周期（`loves_featured_cycle_item.phase text NOT NULL`），运营要把同一个活动/路线/文章投放到多个周期就得建多条重复条目。app 端的 `period` 数组因此是**跨条目聚合**算出来的（`FeaturedCycleItemQueryService.feed` 里按 `TargetKey(type,targetId)` 分组求 `EnumSet<Period>`），逻辑绕、且必须在过滤前算好否则退化成单值。

本次把周期改为条目自身持有的多值集合，并约束 `(type, targetId)` 全局唯一——一个关联实体只能有一条推荐。两者互为前提：唯一约束成立后，聚合逻辑才可以整块删除。

现状约束：
- schema 由 admin 端 Liquibase 统一管理（app 端 liquibase 关闭），两个后端各自映射同一张表。
- 项目对多值字段的既有口径是**内联 jsonb 数组**（`loves_merchant.periods`、`loves_route.tags`、`loves_article.category_ids`），不建关联表、不加外键。
- app 端响应字段名 `period` 与形状（周期枚举数组）不变，App 客户端零改动。

## Goals / Non-Goals

**Goals:**
- 一条条目持有多个周期（`phases`），创建至少一个、创建后可改。
- `(type, targetId)` 全局唯一，数据库约束 + 服务端校验双保险。
- 删除 app 端跨条目聚合逻辑，`period` 直接取自条目 `phases`。
- web 去 tab 改单列表 + 周期筛选下拉 + 表单周期多选。
- 存量数据无损合并（周期投放关系零丢失）。

**Non-Goals:**
- 不动 `type`（仍单选、创建后不可变）、不动 `targetId` 多态设计、不动 banner/文案字段与可见性过滤规则。
- 不改 app 端响应字段名与形状，不要求 App 客户端发版。
- 不给周期推荐引入城市关联（仍是全局配置）。

## Decisions

### D1：`phases` 存为内联 jsonb 字符串数组，不建关联表
`loves_featured_cycle_item` 新增 `phases jsonb NOT NULL DEFAULT '[]'::jsonb`，实体侧 `@JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private List<Period> phases`。

理由：与 `loves_merchant.periods` 完全同构——同一个 `Period` 枚举、同样的「按周期过滤」查询需求，既有实现已经跑通（app 端 `MerchantRepository` 用 `m.periods @> jsonb_build_array(cast(:periodName as text))`，admin 端 `MerchantService` Specification 用 `jsonb_exists`）。照抄先例零新概念。

**备选**：① `text[]` 原生数组——Postgres 更贴切，但项目无先例，Hibernate 要另配 ARRAY 类型；② `@ElementCollection` 关联表——JPA 语义最正，但项目通篇不建关联表、不加外键，会引入孤立风格。均否决。

### D2：唯一约束落在 `(type, target_id)`，与上下线状态无关
数据库加 `CREATE UNIQUE INDEX ux_loves_featured_cycle_item_target ON loves_featured_cycle_item (type, target_id)`；service 层在 insert/update 前先 `existsByTypeAndTargetIdAndIdNot(...)` 查一次，命中就抛按类型区分文案的中文业务异常。

理由：数据库约束保证并发下不出重复（唯一索引是唯一可靠的并发防线），service 预查只为把 500 变成 400 + 可读文案。下线条目同样占位——否则「下线后再建一条」会绕过约束，且上线时才炸。

### D3：周期由「创建后不可变」放宽为可改
原先不可变是为了防止条目在 4 个 tab 之间漂移导致运营找不到。tab 取消后该顾虑消失，而多选场景下「勾错了要重建整条（连 banner 一起重传）」的代价明显更高。`type` 仍不可变——它决定 `targetId` 指向哪张表和哪些文案列生效，改了等于换一条数据。

### D4：admin 列表 `phase` 参数保留原名，语义改为「包含」
不改参数名，避免 web 与契约同时改两处；语义从 `phase = :p` 变为 `jsonb_exists(phases, :p)`，对调用方是收敛的兼容变化（原来只有单周期时两者等价）。

### D5：app 端删除聚合，`period` 直读 `phases`
`FeaturedCycleItemQueryService.feed` 中 `TargetKey` 分组与 `periodsByTarget` 整块删除；`toResponse` 的 `periods` 入参改为从 `item.getPhases()` 构造 `EnumSet`（保留 `EnumSet` 是为了去重 + 按枚举声明顺序输出，这是契约要求的顺序）。过滤条件 `item.getPhase() == period` 改为 `item.getPhases().contains(period)`。

仓储侧 `findAllByOnlineTrueOrderBySortOrderAscCreatedAtDesc()` 不变——周期过滤在内存里做即可（该接口本就要先取全量做关联实体可见性过滤，条目量级为运营手工配置的数十条，不值得为它写 jsonb 查询）。

### D6：迁移分三步，合并保留最早一条
单个 changeset 内：
1. 新增 `phases jsonb NOT NULL DEFAULT '[]'::jsonb`。
2. 回填：按 `(type, target_id)` 分组，把组内所有 `phase` 去重聚合成 jsonb 数组，写到组内 `created_at` 最早那条上；`phases` 数组元素顺序按 `MENSTRUAL / FOLLICULAR / OVULATION / LUTEAL` 排列（用 `CASE` 显式定序，不依赖插入顺序）。
3. 删除组内非最早的条目，再 `DROP COLUMN phase`，最后建唯一索引。

回滚脚本反向：加回 `phase text`、取 `phases` 首元素回填、删 `phases` 与唯一索引——**合并掉的条目无法恢复**，这是 D6 的已知不可逆点（用户已确认）。

## 已定决策（未再向用户确认的默认取值）

- `phases` 落库前去重并按枚举声明顺序排序（与 app 端 `period` 输出顺序口径一致）。
- `phases` 为空数组或缺省一律 400，不做「空 = 全部周期」的隐式语义。
- admin 列表继续按 `sortOrder` 升序、同序号创建时间倒序；不因多周期改排序口径。
- web 周期筛选下拉默认「全部周期」，选择不持久化到 URL 之外（沿用页面既有分页状态写法）。
- 唯一约束冲突文案按 `type` 区分：「该活动/路线/文章已存在周期推荐」。

## 界面实现映射

| 线框区域 | 实现位置 |
|---|---|
| ① 页头 + 「新增周期推荐」按钮 | `love-space-web/src/pages/FeaturedCycleItems/List.tsx`（沿用现有页头，去掉 `?phase=` 查询串） |
| ② 周期筛选下拉（含「全部周期」） | `List.tsx`——用 `components/filter/FilterBar` 的 `select` 字段替换现有 `phase` tab 组（FilterBar 的 select 自带「全部」项），state 由 `useState<Period>("MENSTRUAL")` 改为 `useState<FilterValues>({})`，符合规范「列表页三件套 FilterBar + DataTable + Pagination」 |
| ③ DataTable 列（含新增「投放周期」列） | `List.tsx` 的 `Column[]` 定义，周期标签复用页面既有 `PERIOD_LABEL` 映射 |
| ④ 周期多选勾选框组 | `love-space-web/src/pages/FeaturedCycleItems/Form.tsx`（**独立路由页**，非弹窗——字段数超 5 个，见 web 规范 §6），四个周期各一 checkbox，至少一项校验 |
| ⑤ 内容类型下拉 + 按类型动态字段 | `Form.tsx` 既有逻辑不变，仅确保切换类型时 ④ 的勾选状态不被重置 |
| 类型定义 | `love-space-web/src/api/featuredCycleItems.ts`：`phase: Period` → `phases: Period[]` |

## Risks / Trade-offs

- **[迁移丢文案]** 同一 target 的多条条目合并后，只有最早那条的 banner 与文案留存 → 迁移前先备份 `loves_featured_cycle_item` 全表；changeset comment 中写明该行为。
- **[唯一索引建不起来]** 若步骤 3 的去重有漏，`CREATE UNIQUE INDEX` 会失败并中止整个 changeset → 这是刻意行为（与 022 迁移同口径），失败即回滚，不留半迁移状态。
- **[jsonb 过滤无索引]** `jsonb_exists(phases, ...)` 走全表扫描 → 条目量级为运营手工配置的数十条，不加 GIN 索引；若量级增长再补 `CREATE INDEX ... USING gin (phases)`。
- **[两端实体不同步]** admin 与 app 各有一份 `FeaturedCycleItem`，`phases` 必须同时改，漏一端会在启动或查询时炸 → tasks 中两端实体改动放同一任务项。
- **[web 端旧 URL]** 现有 `/featured-cycle-items/create?phase=X` 链接失效 → 该查询串仅用于预填，去掉即可，无外部依赖。

## Migration Plan

1. admin 端新增 Liquibase changeset `024-featured-cycle-item-multi-phase.sql`（合并 → 加列回填 → 删列 → 建唯一索引，含 rollback）。
2. 两端实体、admin DTO/service/controller、app service 同步改动。
3. web 类型与页面改动。
4. `contracts/api-spec.json` 与 `love-space-app/docs/openapi.json` 同步（operation 保留 `x-requirement` 反链）。
5. 部署顺序：admin 先起（跑迁移）→ app 重启 → web 发布。app 与 web 在旧版本下遇到新 schema 会因缺列/多列报错，故**不支持灰度共存**，需短暂停机窗口。

## Open Questions

无。
