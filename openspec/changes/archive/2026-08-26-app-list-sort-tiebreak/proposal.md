## Why

App 端返回列表的只读接口中，凡实体带 `sortOrder` 排序字段的，排序口径本应统一为
「`sortOrder` 升序，同序号内按 `createdAt` 倒序（新的在前）」。当前实现有 6 处不一致：
2 处把同序号 tie-break 写成了 `createdAt ASC`（旧的在前），4 处根本没有 tie-break——
同 `sortOrder` 的多条记录顺序由数据库返回顺序决定，属于不确定行为，App 端翻页/刷新可能看到不同顺序。

## What Changes

- 统一 app 端「带 `sortOrder` 实体」的列表排序口径为 `sortOrder ASC, createdAt DESC`：
  - `GET /api/app/categories/page`（分类）：`createdAt ASC` → `createdAt DESC`
  - `GET /api/app/banners`（Banner）：`createdAt ASC` → `createdAt DESC`
  - `GET /api/app/merchants/{id}/reviews`（商户评价）：仅 `sortOrder ASC` → 补 `createdAt DESC`
  - `GET /api/app/recommend-lists`（推荐清单列表）：仅 `sortOrder ASC` → 补 `createdAt DESC`
  - `GET /api/app/recommend-lists/{id}`（清单内商户）：仅 `sortOrder ASC` → 补 `createdAt DESC`
  - `GET /api/app/routes`（路线列表）：仅 `sort_order ASC` → 补 `created_at DESC`
- `weight` 与 `sortOrder` 同属排序号，适用同一口径，差别只在方向：`sortOrder` 升序（小的靠前）、
  `weight` 降序（权重大的靠前），tie-break 一律 `createdAt DESC`。带 `weight` 的两处已符合，无需改动：
  - `GET /api/app/ambassadors`（爱女大使）：`weight DESC, createdAt DESC`
  - `GET /api/app/merchants/page`（商户列表）：`weight DESC, createdAt DESC`
- 其余已符合口径、无需改动：文章栏目、文章（两个接口）、周期推荐 `featured-cycle-items`。
- 实体确无排序号字段、排序口径另有约定、本次不动：活动（`createdAt DESC`）、城市（`createdAt DESC`）、
  上新推荐 `featured-items`（`createdAt DESC`）、标签（按 ID 集合批量取，无列表语义）。
- 无 API 请求/响应结构变化，无数据库变更，无前端变更。非 BREAKING（仅同序号内相对顺序变化）。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `merchant`: app 端分类列表与商户评价列表的排序要求补齐 `createdAt DESC` tie-break
- `banner`: app 端 Banner 列表同序号 tie-break 由 `createdAt ASC` 改为 `createdAt DESC`
- `recommend-list`: app 端清单列表与清单内商户列表补齐 `createdAt DESC` tie-break
- `route`: app 端路线列表补齐 `createdAt DESC` tie-break

## Impact

- 代码：`love-space-app` 六处查询排序
  - `modules/category/service/CategoryService.java`
  - `modules/banner/service/BannerQueryService.java`
  - `modules/merchant/service/MerchantReviewQueryService.java`
  - `modules/recommendlist/repository/RecommendListRepository.java`
  - `modules/recommendlist/repository/RecommendListMerchantRepository.java`
  - `modules/route/repository/RouteRepository.java`
- API 契约：`contracts/api-spec.json` 仅需更新上述 6 个接口的排序描述文案（无 schema 变化）
- admin 端：不受影响（admin 列表排序口径独立，本次不动）
- 数据库/依赖/环境变量：无变化
