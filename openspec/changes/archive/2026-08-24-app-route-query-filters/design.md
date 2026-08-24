## Context

`GET /api/app/routes` 现状：Controller 强制 `@RequestParam String cityName`，Service 先用 `cityRepository.findByChineseName` 反查城市（用于组装列表项里的 `city` 对象），再 `findAllByCityNameOrderBySortOrderAsc`，最后在内存中按大使 `online` 过滤。`contracts/api-spec.json` 里仍写着必填 `cityId`，与代码已经不一致。

本次要把列表过滤条件改成两个可选参数：`cityName`、`ambassadorId`。

## Goals / Non-Goals

**Goals:**
- 列表支持「不过滤 / 只按城市 / 只按大使 / 两者组合」四种查询形态。
- 契约（api-spec.json）与实现、living spec 三者对齐。

**Non-Goals:**
- 不做分页（现状即全量返回，本次不引入）。
- 不动详情接口 `GET /api/app/routes/{id}`。
- 不动 admin 端路线管理接口。

## Decisions

1. **过滤下沉到 SQL，沿用 merchant 的可选条件惯例**：`RouteRepository` 用一条带 `(:param is null or col = :param)` 的查询替换 `findAllByCityNameOrderBySortOrderAsc`，两个条件都可为 null。与 `MerchantRepository.searchOnlineNative` 写法一致，不引入 Specification/QueryDSL。
   - 备选：在 Service 里查全表后内存过滤 —— 否决，路线量随城市增长，全表扫描没必要。

2. **`cityName` 不存在城市时返回空数组的语义保留，但实现方式改变**：现状是「城市反查不到 → 直接 return 空」。改为：先反查城市，`cityName` 非空且查不到 → 返回空数组（早退，保持现有行为）；`cityName` 为空 → 不过滤城市。

3. **列表项里的 `city` 对象按路线自身 `cityName` 逐条反查**：不传 `cityName` 时列表跨城市，不能再共用同一个 `City`。按结果集中出现的 `cityName` 批量反查城市表（一次 `findAllByChineseNameIn`），组 Map 后填充；路线 `cityName` 为空或城市已删除时该项 `city` 为 `null`（与详情接口口径一致）。

4. **两个参数都不传 → 返回全部可见路线**（用户已确认）。不做 400 校验。

5. **`ambassadorId` 过滤后仍要过 `online` 过滤**：即传了一个已下线大使的 ID，返回空数组，而不是返回其路线。可见性规则优先。

**已定决策（未询问用户，按 §4.1 默认值）**：`cityName` 走精确匹配（不做模糊/前缀匹配），与现行 `findByChineseName` 一致；两个参数为 AND 关系，不支持 OR。

## Risks / Trade-offs

- [移除 `cityId` 是破坏性变更] → 契约里 `cityId` 从未被现行代码实现（代码已是 `cityName`），实际调用方若还在传 `cityId`，改动后会被 Spring 忽略而返回全部路线而非报错。→ 缓解：本次变更同步更新 `contracts/api-spec.json`，并在 IT 用例中覆盖「不传参数返回全部」，让语义变化可见。
- [不分页，全量返回] → 当前数据量小；若路线总量增长到影响响应体积，再补分页，届时需要一个新 change。

## Migration Plan

无数据库 schema 变更，无数据迁移。部署即生效；回滚即回退代码与契约文件。
