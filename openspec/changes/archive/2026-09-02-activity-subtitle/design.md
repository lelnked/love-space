## Context

活动实体已有一组可空文本字段（`landscape`、`introduction` 等），本次新增的 `subtitle` 与 `landscape` 完全同构：可空 text 列、admin 可写可清空、app 只读下发。改动横跨 web/admin/app 三端 + 一条 schema 迁移 + 两份契约文件，故写 design 记录已定决策。

数据库 schema 由 admin 端 Liquibase 统一管理（app 端 liquibase 关闭），app 端 `Activity` 实体只是同一张表的只读映射，需同步加字段。

## Goals / Non-Goals

**Goals:**
- `loves_activity` 增可空 `subtitle` 列，CMS 可录入、修改、清空
- admin 活动列表项与详情、app 活动列表项与详情、app 周期推荐 `target`（ACTIVITY 形状）均返回 `subtitle`

**Non-Goals:**
- 不做长度/格式校验之外的业务规则（不唯一、不必填、不参与搜索与排序）
- 不改条目自身手填的 `subtitle` 文案字段语义——周期推荐条目上的 `subtitle` 与活动的 `subtitle` 是两个独立字段
- 不给路线加同名字段

## Decisions

1. **列类型与可空性**：`ALTER TABLE loves_activity ADD COLUMN subtitle text;`，nullable，无默认值。沿用 `019-add-activity-landscape.sql` 的写法，新 changeset 编号 `023-add-activity-subtitle`，含 `--rollback DROP COLUMN`。备选「varchar(N)」被否：既有文本列一律 text，加长度上限反而要在两端同步维护。
2. **空值口径**：空白 → null 的归一在 web 表单做（既有 `value.trim() || null` 惯例），admin 后端与 `landscape`、`introduction` 等同类可空文本字段一样原样保存、不额外 trim（单给 subtitle 加 trim 会成孤例）；app 侧原样下发，**不回落为 title**。与 `FeaturedCycleItemTargetResponse.ArticleTarget.coverTitle` 的口径一致——回落是列表页展示层的事，`target` 只做直出。
3. **校验**：`ActivityUpsertRequest.subtitle` 不加任何 bean validation 注解。已核对 `ArticleUpsertRequest.subtitle` 同样无约束，两侧口径一致；活动的 `landscape` 等可空文本字段亦无长度限制，单独给 subtitle 加上限会成为孤例。
4. **target 字段顺序**：`ActivityTarget(id, title, subtitle, cover, level)`，`subtitle` 紧跟 `title`，与 `ArticleTarget` 中 `coverTitle` 的位置习惯一致。record 组件新增位于中部不影响 JSON 消费方（按名解析）。
5. **查询服务无需改查询**：`FeaturedCycleItemQueryService` 已把活动实体整体捞进 `id→实体 Map`，装配 target 时直接取 `activity.getSubtitle()`，查询次数不变。
6. **契约更新时机**：`contracts/api-spec.json`（admin 面 + app 面）与 `love-space-app/docs/openapi.json` 在 apply 阶段随代码一并改，operation 保留/补 `x-requirement` 反链。

## Risks / Trade-offs

- [app 端实体漏加字段 → app 侧 `subtitle` 恒为 null 且无编译错误] → IT 用例覆盖「admin 写入后 app 读到」的跨端链路，不只断言 admin 自身回显。
- [`subtitle` 与周期推荐条目手填 `subtitle` 同名，易被误当同一字段] → spec 与 DTO javadoc 明确二者独立；新增 scenario 专门断言活动未填时 `target.subtitle` 为 null；另注意 ACTIVITY 类条目自身并不持有 `subtitle`（该文案字段只适用于 ROUTE，admin 侧对其他类型强制置 null），故两者实际不会同时出现。

## Migration Plan

新增可空列，纯加法，无数据回填。上线顺序不敏感：先部署 admin（跑迁移）再部署 app 即可；旧版 app 后端连新库不受影响（多一列不读）。回滚 = 执行 changeset 的 `--rollback` 语句。

## Open Questions

无。
