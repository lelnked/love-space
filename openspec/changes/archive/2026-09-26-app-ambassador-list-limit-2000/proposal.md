## Why

`GET /api/app/ambassadors` 的 `limit` 缺省 3、上限 20（`AmbassadorQueryService.DEFAULT_LIMIT/MAX_LIMIT`），这是最初「首页只展示 3 位大使」时期的口径。App 现在需要一次性拿到全部上线大使（选择器、按大使筛路线入口），20 条的硬上限让客户端无法取全，且不传 `limit` 只回 3 条不符合「列表接口」的直觉。

## What Changes

- `limit` 缺省值 **3 → 2000**，上限 **20 → 2000**：不传 `limit` 即返回全部上线大使（现量级远小于 2000）。
- 非法值口径不变：`limit` 为空或 < 1 回落缺省值（2000），> 2000 收敛到 2000，不返回 400。
- 排序、可见性（仅 `online=true`）、字段结构、详情接口均不变。
- 非 BREAKING：属放宽，原来传 `limit=5` 的客户端行为完全不变；只有「不传 limit」的调用方从 3 条变为全量。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `route`：新增「app 端爱女大使只读查询」要求，登记 `limit` 缺省/上限口径（此前只存在于实现，living specs 无登记）。

## Impact

- app：`modules/ambassador/service/AmbassadorQueryService.java`（两个常量 + javadoc）、`modules/ambassador/controller/AmbassadorController.java`（javadoc）、`AmbassadorReadIT`（默认条数与上限断言）。
- 契约：`contracts/api-spec.json` 与 `love-space-app/docs/openapi.json`（后者由 `scripts/generate-app-openapi.js` 生成）的 `limit` default/maximum 与 summary。
- 测试：`tests/route/it.md` 的 TC-route-IT-020 / TC-route-IT-021 预期值需改写。
- admin / web：无影响（admin 侧大使列表走独立分页接口）。
- DB / 迁移 / 环境变量：无。
