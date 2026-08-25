## Context

`route-remove-city-id`（2026-08-23）跨 admin/app/web/DB 移除了路线的城市关联，但该 change 未产出 delta specs（归档目录下只有一份 README），因此 living specs 的清理被漏掉。本次是一次纯粹的对齐：让 `openspec/specs/city/spec.md` 与测试追上早已落地的实现。

## Goals / Non-Goals

**Goals:**
- 删除已失去前提的 Requirement「城市下存在路线时禁止删除」。
- 清理锚定该行为的失效 UT 与孤儿 IT 用例，恢复 admin unit-test 门禁 100% 通过。

**Non-Goals:**
- 不改 `CityService.delete()` 或任何生产代码——实现已是目标状态。
- 不新增「地图名被引用时告警」之类的替代约束。
- 不动城市删除的其余级联语义（Banner 下架、商户处理）。

## Decisions

### 1. REMOVED 而非 MODIFIED
约束的判据（路线的 `cityId`）已被物理删除，无法改写成一个仍然成立的版本。按 route-remove-city-id 归档 README 的明确意图「No city association is retained」，路线与地图之间不应再有任何强关联语义，因此整条 Requirement 删除。

**备选（已否决）**：改成按 `city_name` 文本匹配拒绝删除——路线的 `city_name` 是自由文本、可随意填写且无唯一性约束，用它做删除前置校验会产生「改个字就能绕过」的假保护，语义比没有更糟。若产品确有此诉求，应作为新 Requirement 单独提，且需先解决地图名的规范化问题。

### 2. 本次不视为行为变更，无需 BREAKING 标记
删除 Requirement 通常意味着行为收缩，但此处实现侧的行为在 2026-08-23 就已改变，本次 change 的 diff 不含任何生产代码。对外可见行为（`DELETE /api/admin/cities/{id}` 对有路线的城市返回 200）在本次前后完全一致。

### 3. 测试删除而非标记跳过
`CityServiceTest.deleteRejectedWhenCityHasRoutes` 锚定的场景已不存在，`@Disabled` 会把一条死代码永久留在仓库里。按 test-cases 规则「REMOVED → 删除对应用例，git 留史，不加废弃标记」，UT 与 IT 用例一并删除。其私有夹具 `routeIn(UUID cityId)` 只被该测试调用，且参数 `cityId` 在 016 之后就已成为未使用参数（这正是测试失效却没人发现的原因），随之删除。

## 已定决策（未询问用户，按 §4.1 默认值方向自行判断）

- **清理方向为「删除约束」而非「重建约束」**：依据是 route-remove-city-id 归档 README 的明确表述与 016 迁移的实际内容，属于可从既有工件推断的情形，不构成 §4.1 第 1 类的产品策略取舍；且本次不改变任何用户可见行为，决策可逆成本低。
- **顺带修订 TC-city-IT-006 而非另开 change**：交付轮回归暴露该用例是 `route-decouple-city-online` 的同类遗留——其「关联需求」指向已不存在的 Requirement「地图下架对活动级联生效」，预期结果（活动不可见）与现行 living spec「地图下架对路线与活动均不级联#下架城市后 app 端活动仍可见」方向相反，且该 Scenario 此前**零用例覆盖**。方向由 living spec 明确给出、无产品取舍空间，不触发 §4.1 任一类；同属 city 域、同属本 change 已在处理的 tests/city/it.md、不改任何生产代码，故就地修订，避免留一条每轮回归都「未执行」的死用例。
- **route 域的契约漂移不在本次处理**：回归同时发现 `contracts/api-spec.json` 的 `GET /api/app/routes` 仍声明 `cityId` 查询参数、`POST /api/admin/routes` 仍把 `cityId` 列为 required，实际实现均已是 `cityName`。这属 route 域行为契约，超出 city 域边界，记为待办另开 change。

## Risks / Trade-offs

- **风险：产品原意其实是想保留这条保护**，只是实现被误删 → 缓解：本次只动 spec 与测试、不动生产代码，若后续确认需要保护，重新提 Requirement 并实现即可，无数据代价。归档 README 的表述与 016 迁移内容均支持「彻底解耦」的读法。
- **权衡：删除两条 P0/P1 IT 用例会让 city 域用例总数下降** → 它们锚定的行为已不存在，保留只会在每次回归里产生假失败或假通过。

## Migration Plan

无数据迁移、无部署顺序要求。合并后 `CityServiceTest` 由 7 个测试减为 6 个，admin unit-test 恢复 111 → 110 全通过。

## Open Questions

无。
