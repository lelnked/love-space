## Context

recommend-list 域的 living spec / `contracts/api-spec.json` / `tests/recommend-list/*.md` 停留在 `map-and-recommend-list`（2026-08-16）的口径；`recommend-list-city-merchant-cascade`（2026-08-23 归档，无 specs 目录）与 f574009 把 admin 实现改成了：

| 项 | spec / 契约 / 用例现状 | 代码现状 |
|---|---|---|
| cityId | 创建后不可改（TC-004 ✅） | `update` 可改，已有商户不属新城市 → 400 |
| status | "清单无上架/下架状态" | `ONLINE/OFFLINE`，默认 ONLINE，create/update 可设 |
| 恢复 | 无 | `POST /{id}/online`，有下架商户拒绝 |
| 清单内商户 | `PUT /{id}/merchants` `[{merchantId, sortOrder}]`（TC-007～010 ✅） | create/update body `merchantIds` 有序数组；端点已删 |
| 排序 | 每条关联维护排序号 | 数组下标落 `sort_order`，无独立排序号 |
| 死代码 | — | `replaceMerchants` + `RecommendListMerchantItemRequest`，仅单测在调 |

## Goals / Non-Goals

**Goals:** 行为真源（spec、契约、living 用例、UT `@scenario` 锚点）与代码一致；删掉只有测试在养的死代码。

**Non-Goals:**
- 不改任何运行时行为。
- 不实现 cascade 决策 3（商户下架级联清单 OFFLINE）——App 详情实时过滤下架商户已覆盖该需求，级联多余；spec 不写。
- 不改 app 端详情对 `status` 的过滤（另开 change）。
- 不动 web 代码；不动库表。

## Decisions

1. **spec 只描述已实现行为**——cascade design 里未落地的"商户下架级联"不写；避免 spec 再次领先代码。备选"按 design 写全然后补实现"会把文档对齐变成功能开发，越界。
2. **三个 Requirement 都用 MODIFIED、保留全部既有 scenario 名**——TC-001～010、WEB-001～003 的「关联需求」字段全部不用改名，只改步骤/预期；新增 scenario 追加在块内。不触发 RENAMED 联动。
3. **`RecommendListUpdateRequest` 新增为 api-spec schema，`RecommendListCreateRequest` 补字段**——两者字段一致但语义不同（update 的 cityId/merchantIds null = 不修改），分开定义比共用一个 schema 更准确。
4. **单测重写而非删除**——`replaceMerchants*` 四个用例覆盖的 scenario 仍然有效，改经 `update(id, RecommendListUpdateRequest(merchantIds))` 走同一校验路径（`applyMerchantIds`）；额外补 3 个新 scenario 的 UT。删除 `replaceMerchants` 后 `applyMerchantIds` 是唯一实现，无重复逻辑。
5. **admin `RecommendListMerchantResponse.sortOrder` 保留**——admin 详情回传位置号对 web 无害（web 用它做本地排序基准），删了要动 web；非本次目标。javadoc 措辞改"清单保存顺序"。

### 已定决策（§4.1 默认值）
- 新增 IT 用例编号接 it.md 现有最大号（015）之后：016 拒绝已下架商户、017 修改城市需商户同属新城市、018 人工恢复清单。
- TC-004 保留编号与「关联需求」（推荐清单管理#创建清单）不变？——不：其行为对应新 scenario「修改所属城市需清单内商户同属新城市」，关联需求改指向该 scenario。

## api-spec.json 同步（design 阶段已执行）

- 删除 `paths./api/admin/recommend-lists/{id}/merchants` 与 `components.schemas.RecommendListMerchantItem`。
- `RecommendListCreateRequest`：补 `status`（enum ONLINE/OFFLINE，默认 ONLINE）、`merchantIds`（uuid 数组，顺序即清单保存顺序）。
- 新增 `RecommendListUpdateRequest`：title 必填；introduction；cityId（null = 不修改）；sortOrder（null = 0）；status（null = 不修改）；merchantIds（null = 不修改，非 null 整体替换）。
- `PUT /{id}`：summary "更新清单（cityId/status 可变；merchantIds 非 null 时整体替换，顺序即清单保存顺序）"，requestBody → `RecommendListUpdateRequest`。
- 新增 `POST /{id}/online`：summary "人工恢复清单为 ONLINE；清单内存在已下架商户 → 400"，`x-requirement: recommend-list/推荐清单管理`。
- `GET /{id}`：summary "清单详情（含商户明细，按清单保存顺序）"。
- `POST` create summary 补"可带 status / merchantIds"。

## Risks / Trade-offs

- [runner 回归 TC-004/007～010 时旧存证目录仍指向 2026-08-16 的 PUT /merchants 交换记录] → 用例「来源」改本 change，状态由 runner 重跑后回写；旧存证 git 留史。
- [WEB-002 步骤"添加 M1（排序号 2）与 M2（排序号 1）"改为按添加顺序] → 本次不重跑 web（无 web 代码改动、界面口径未变），只改用例文本，状态字段不动。

## Migration Plan

无数据迁移、无部署动作。
