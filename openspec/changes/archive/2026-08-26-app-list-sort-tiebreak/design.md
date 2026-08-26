## Context

app 端有 15 个返回列表的只读接口。其中 8 个的实体带排序号字段（`sortOrder` 或 `weight`），
但排序口径在三次迭代中分头落地，形成了三种写法：

| 写法 | 出现处 |
|---|---|
| 排序号 + `createdAt DESC`（正确口径） | 文章栏目、文章 ×2、周期推荐、爱女大使（weight）、商户列表（weight） |
| 排序号 + `createdAt ASC` | 分类、Banner |
| 只有排序号，无 tie-break | 商户评价、推荐清单列表、清单内商户、路线列表 |

无 tie-break 的 4 处，同排序号记录的相对顺序由 PostgreSQL 返回顺序决定——不保证稳定，
App 端刷新或翻页可能看到不同顺序。这是本次要消除的不确定行为。

排序落点也分三层：Spring Data 方法名派生（repository）、`Sort` 对象（service）、native SQL `ORDER BY`。
六处修改分别落在各自既有的那一层，不做统一重构。

## Goals / Non-Goals

**Goals:**
- app 端带排序号的列表接口排序口径收敛为一条规则：排序号（`sortOrder` ASC / `weight` DESC）+ `createdAt DESC`
- 消除 4 处同序号顺序不确定的行为
- living specs 与 `contracts/api-spec.json` 的排序措辞与实现一致

**Non-Goals:**
- 不改 admin 端任何列表排序（admin 列表排序口径独立，另有分页/筛选语义）
- 不改无排序号实体的列表（活动、城市、上新推荐、标签）
- 不引入统一的排序常量/工具类或抽象基类——六处一次性改完，抽象在这里是净负债
- 不改数据库（不加索引；这些表数据量在千级以内，`ORDER BY sort_order, created_at DESC` 走顺序扫描足够）
- 不补 `/api/app/categories/page` 与 `/api/app/merchants/{merchantId}/reviews` 在 api-spec.json 中的缺失条目（既有缺口，见 Open Questions）

## Decisions

**D1：tie-break 方向为 `createdAt DESC`（新的在前）**
运营配置排序号时常留大量并列（默认 0），此时「最近新增的先露出」是内容型 App 的通行预期，
也与已有的多数实现（文章、周期推荐、大使、商户）一致。取 `ASC` 会让新内容沉底。
备选：用 `id DESC`（UUIDv7 单调递增，等价于创建序且省一列比较）——否决，`createdAt` 语义显式，
且 spec 里已用「创建时间」表述，用 id 排序会让 spec 与实现出现语义落差。

**D2：`weight` 与 `sortOrder` 同规则、反方向**
两者都是运营手工排序号，差别只在语义方向：`sortOrder` 是「第几位」（升序），`weight` 是「权重」（降序）。
tie-break 统一 `createdAt DESC`。带 `weight` 的两处（爱女大使、商户列表）现状已符合，本次零改动，
仅在 spec 中把这条规则写明，防止后续新接口再分叉。

**D3：修改落在各自既有的排序层，不统一重构**
- 方法名派生（`RecommendListRepository`、`RecommendListMerchantRepository`）→ 方法名追加 `CreatedAtDesc`，调用方随之改名
- `Sort` 对象（`CategoryService`、`BannerQueryService`、`MerchantReviewQueryService`）→ 改/加一个 `Sort.Order`
- native SQL（`RouteRepository.search`）→ `ORDER BY` 追加 `created_at desc`
备选：抽一个共享 `Sort` 常量或 `SortSpecs` 工具类——否决。三层落点物理上无法共用同一个 `Sort` 实例
（native SQL 拼不进 `Sort` 对象），强行统一只会造出一个半数场景用不上的工具类。

**D4：`MerchantReview` 的 `sortOrder` 可空**
`MerchantReview.sortOrder` 是 `Integer`（列 `nullable = false`，但类型可空），其余为 `int`。
DB 层非空约束保证不会出现 NULL，因此不需要 `NULLS LAST` 处理。

**D5：契约文案同步**
`contracts/api-spec.json` 中 4 个接口的 `summary` 排序措辞在本阶段一并更新（无 schema 变化）：
`/api/app/banners`、`/api/app/recommend-lists`、`/api/app/recommend-lists/{id}`、`/api/app/routes`。

## Risks / Trade-offs

- **[已上线数据的同序号顺序会变]** → 仅影响并列排序号条目的相对位置，不影响可见性、字段与分页总数；
  变更方向是「新内容前移」，属于产品预期方向，不需要数据迁移或灰度。
- **[Banner / 分类由 ASC 翻成 DESC，是两处真正的顺序反转]**（其余 4 处是从"不确定"变"确定"）→
  IT 用例显式覆盖这两处的同序号顺序断言，回归时能直接看出来。
- **[方法名派生改名影响调用方]** → 编译期即报错，不存在漏改的运行期风险。
- **[无索引支撑]** → 现有表规模下无影响；若某表增长到万级且该列表成为热点，再补
  `(sort_order, created_at DESC)` 复合索引，属独立优化，不阻塞本次。

## Migration Plan

无数据迁移、无 schema 变更、无配置变更。改完随 app 后端常规发布；回滚即代码回滚。

## Open Questions

- `/api/app/categories/page` 与 `/api/app/merchants/{merchantId}/reviews` 两个接口在
  `contracts/api-spec.json` 中缺失条目（既有缺口，非本次引入）。本 change 只改行为不补契约条目，
  留待后续单独补齐。
