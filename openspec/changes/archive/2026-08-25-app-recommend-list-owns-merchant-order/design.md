## Context

App 端"清单内商户"目前由两个接口重复提供：

| | `GET /api/app/recommend-lists/{id}` | `GET /api/app/merchants/page?recommendListId=` |
|---|---|---|
| 归属域 | recommend-list | merchant |
| 顺序 | 清单保存顺序（`sort_order` 升序） | `rlm.sort_order asc nulls last, weight desc, created_at desc` |
| 商户项 | `merchantId/name/logo/address/recommendReason/sortOrder` | `id/name/logo/address/tags/scores/loveIndex/recommendSortOrder` |

第二条让 `MerchantRepository` 的 native SQL left join 了 `loves_recommend_list_merchant`，`MerchantService` 注入了 `RecommendListMerchantRepository`——merchant 域反向依赖 recommend-list 域。

顺序的真源：web 表单按顺序提交 `merchantIds`，admin `applyMerchantIds` 把数组下标（1,2,3…）写入 `sort_order`。`sort_order` 只是关系表对"位置"的持久化，不是运营可编辑的业务字段。

## Goals / Non-Goals

**Goals:**
- 清单内商户只由 recommend-list 域提供；merchant 域不再感知清单。
- 清单详情商户项收敛为 `id / name / address / logo`，顺序由数组顺序表达。
- 商户列表排序回到纯 `weight DESC, createdAt DESC`。

**Non-Goals:**
- 不改库表、不做迁移（`sort_order` 列保留作位置持久化）。
- 不给清单详情补 `tags / scores / loveIndex` 等卡片字段（用户明确要求"不能太多"）。
- 不动 admin 与 web（`merchantIds` 有序数组语义不变）；admin 死代码 `replaceMerchants` 另开清理。

## Decisions

1. **删参数而不是留兼容** — `recommendListId` 直接从 `MerchantController.page` 移除；带了也会被 Spring 忽略并按普通列表返回，而不是 400。理由：移动端不在仓库，无法确认是否在用；忽略未知参数是 Spring MVC 默认行为，零代码即得到平滑降级。备选"保留参数但标 deprecated"会把反向依赖继续留在 merchant 域，违背本次目的。
2. **`RecommendListMerchantItemResponse` 字段名 `merchantId` → `id`** — 与 `MerchantListItemResponse.id`、`MerchantDetailResponse.id` 一致，App 端商户项统一用 `id`。**BREAKING**，随本次一并做，避免二次破坏。
3. **`MerchantRepository.searchOnlineNative` 保留 native SQL，仅去 join 与 order by 首项** — `periods @> jsonb` 仍需 native；不借机改回 Specification。改动最小。
4. **`RecommendListQueryService.detail` 保持 relations → `findAllById` → 按 relations 顺序组装的结构** — 已是"按保存顺序"的正确实现，只改 DTO 构造。不引入 merchant 域的组装方法（无卡片字段需求，YAGNI）。
5. **`RecommendListMerchantRepository` 留在 recommendlist 包，merchant 包不再 import 它** — 依赖方向 recommend-list → merchant 单向。

### 已定决策（§4.1 默认值）
- 顺序措辞：spec 与 javadoc 统一用"清单保存顺序"，不再出现"关联排序号升序"作为对外语义。
- Requirement 换名：openspec 验证器不允许 MODIFIED 块丢 scenario，按 city 域先例用 REMOVED「App 端清单查询」+ ADDED「App 端清单与清单内商户查询」；it.md / api-spec.json / 测试 `@scenario` 注释的关联需求同步改名。
- `tests/recommend-list/it.md`：删 TC-014，TC-012 改预期；用例编号不回填。

## api-spec.json 同步

- `/api/app/merchants/page`：删除 `recommendListId` 参数；summary 改为"App 商户列表分页（cityId 必填；period / categoryId 可选；weight DESC, createdAt DESC）"；`x-requirement` 改为 `recommend-list/App 端清单与清单内商户查询`（merchant spec 无 App 商户列表 Requirement，且新需求仍约束该接口"不受清单影响"）。
- `/api/app/recommend-lists/{id}`：summary 改为"App 清单详情含商户（按清单保存顺序；商户项仅 id/name/address/logo；所属城市下架 → 404）"，`x-requirement` 不变。

## Risks / Trade-offs

- [移动端若已消费 `recommendListId` / `recommendReason` / `merchantId`] → 已在 proposal Impact 标注 BREAKING；`recommendListId` 传了会被忽略而非报错，清单页需切到 `/recommend-lists/{id}` 并改字段名。
- [清单详情不分页] → 清单为运营手挑小集合，可接受；若将来单清单商户过多再议。
- [`recommendReason` 从 App 端消失] → 商户详情 `MerchantDetailResponse` 仍可承载；本次按用户要求只留四字段。

## Migration Plan

无数据迁移。部署顺序：app 后端先发，移动端随后适配（旧参数被忽略，不会 5xx）。回滚即回退 jar。
