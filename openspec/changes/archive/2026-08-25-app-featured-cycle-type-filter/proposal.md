## Why

App 端周期推荐接口一次性下发四周期全部条目，客户端无法只取某一种内容类型（活动/路线/文章）。移动端需要按内容类型拆分的入口，当前只能全量拉回后自行过滤。

## What Changes

- `GET /api/app/featured-cycle-items` 新增可选查询参数 `type`（`ACTIVITY` / `ROUTE` / `ARTICLE`）：传入时仅下发该类型的条目；不传时行为不变（全部类型）。
- 响应结构不变：四周期分组键恒在，过滤后无条目的周期为空数组，组内仍按 `sortOrder` 升序、同序号创建时间倒序。
- 可见性规则不变（条目 `online=true` 且关联实体可见）。
- `contracts/api-spec.json` 同步新增该可选参数。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `featured`: App 端周期推荐查询新增按内容类型过滤的可选条件。

## Impact

- 代码：`love-space-app` featuredcycle 模块（Controller / QueryService）。
- 契约：`contracts/api-spec.json` 的 `/api/app/featured-cycle-items` GET 参数。
- 测试：`tests/featured/it.md` 新增按类型过滤与非法类型值的用例；既有 TC-featured-IT-016~020 行为不变，需回归。
- 前端/admin：无影响（admin 端 `/api/admin/featured-cycle-items/page` 早已支持 `type` 过滤）。
