## Context

`GET /api/app/featured-cycle-items` 现状：`FeaturedCycleItemQueryService.feed()` 一次捞出全部上线条目，在内存中按关联实体可见性过滤，塞进以 `Period` 为键的 `LinkedHashMap`（四个键预置为空列表）。数据量是运营配置级（每周期个位数），已有 `ponytail:` 注释说明「全量捞出内存过滤即可」。

## Goals / Non-Goals

**Goals:**
- 列表支持可选的内容类型过滤，响应结构与既有可见性规则完全不变。
- 契约同步。

**Non-Goals:**
- 不改响应结构（不因为过滤了单一类型就退化成扁平数组）。
- 不加分页。
- 不动 admin 端 `/api/admin/featured-cycle-items/page`（早已支持 `type`）。

## Decisions

1. **过滤放在内存里，不下沉到 SQL**：沿用现有 `ponytail:` 注释确立的口径——数据量是运营配置级，`feed()` 本来就全量捞出后内存过滤，多一个 `type` 判断不值得再开一条 repository 查询。
   - 备选：给 `FeaturedCycleItemRepository` 加 `findAllByOnlineTrueAndTypeOrderBy...` —— 否决，为个位数数据增加一个方法与一条分支，收益为零。

2. **四周期键恒在**：过滤后即使某周期（甚至全部周期）为空，仍返回空数组。客户端已按「键恒在」写死解析，改成动态键会是不必要的破坏性变更。

3. **非法类型值返回 400**：`@RequestParam(required = false) FeaturedCycleItemType type` 由 Spring 做枚举转换，非法值自动 400。不写自定义校验。

**已定决策（按 §4.1 默认值）**：参数名沿用 admin 端分页接口的 `type`，取值为枚举名（大写），与 `FeaturedCycleItemType` 一致，不做大小写宽松匹配。

## Risks / Trade-offs

- [纯新增可选参数，无破坏性] → 不传 `type` 时行为与过去逐字一致，既有客户端不受影响；既有 IT 用例 TC-featured-IT-016~020 原样回归即可验证这一点。

## Migration Plan

无 schema 变更、无数据迁移。部署即生效。
