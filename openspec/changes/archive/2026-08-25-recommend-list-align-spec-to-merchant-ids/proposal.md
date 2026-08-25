## Why

`2026-08-23-recommend-list-city-merchant-cascade` 归档时没有 specs 目录，它落地的一整套 admin 行为——`status`（ONLINE/OFFLINE）、`POST /{id}/online` 人工恢复、cityId 创建后可改、清单内商户改由 create/update 的 `merchantIds` 有序数组整体替换、`PUT /{id}/merchants` 端点删除——都没进 living spec 与 `contracts/api-spec.json`。结果是：spec 写着"cityId 创建后不可改、清单无上架状态、每条关联维护排序号"，契约里还有已删除的 `PUT /{id}/merchants` 与 `RecommendListMerchantItem`，`tests/recommend-list/it.md` TC-004/007/008/009/010 五条 ✅ 用例全部指向不存在的行为或端点，admin 里 `replaceMerchants` / `RecommendListMerchantItemRequest` 成了只有单测在调的死代码。上一个 change（`app-recommend-list-owns-merchant-order`）的 runner 在造数时撞上了这些失配。本次把 recommend-list 域的行为真源一次对齐到现行代码，不改任何对外行为。

## What Changes

- living spec `recommend-list`：
  - 「推荐清单管理」改为：cityId 可改（变更时清单内已有商户须全部属于新城市，否则 400）；清单有 `status` ONLINE/OFFLINE，创建默认 ONLINE、可在创建/更新时设置；`POST /api/admin/recommend-lists/{id}/online` 人工恢复，清单内存在已下架商户时拒绝（400）。
  - 「清单内商户维护」改为：通过创建/更新请求的 `merchantIds`（有序 UUID 数组）整体替换；顺序 = 数组顺序，无独立排序号；校验同城、不重复、未下架；从数组中去掉即移除。
  - 「web 端推荐清单管理页面」措辞去掉"可调排序号 / 按排序号升序回显"，改为"按添加顺序"。
  - 「App 端清单与清单内商户查询」不动（上一 change 已同步）。
- `contracts/api-spec.json`：删除 `PUT /api/admin/recommend-lists/{id}/merchants` 与 `RecommendListMerchantItem` schema；`RecommendListCreateRequest` 补 `status`、`merchantIds`；`PUT /{id}` 补 requestBody（新 `RecommendListUpdateRequest` schema：cityId/status 可变、merchantIds 整体替换）并改 summary；新增 `POST /{id}/online`；`GET /{id}` summary 改"按清单保存顺序"。
- admin 后端：删除 `RecommendListService.replaceMerchants` 与 `RecommendListMerchantItemRequest`；`RecommendListServiceTest` 中 4 个 `replaceMerchants*` 用例改为经 `create/update(merchantIds)` 验证同一批 scenario，并补"拒绝已下架商户""修改城市需商户同属新城市""恢复清单需无下架商户"三个 scenario 的 UT。javadoc "按关联排序号升序" → "按清单保存顺序"。
- `tests/recommend-list/it.md`：TC-004 改为"cityId 可改"口径；TC-007～010 改走 `PUT /{id}` 的 `merchantIds`；新增上述三个 scenario 的 IT 用例。`tests/recommend-list/web.md` TC-WEB-002 去掉"排序号"措辞。
- **不改任何运行时行为**（app / admin / web 代码行为零变化，仅删死代码与改注释）。

不在本次范围（发现即记录，另行决策）：
1. cascade design 决策 3「商户下架时级联把清单置 OFFLINE」未落地，且**不需要**：App 端清单详情实时过滤下架商户（`RecommendListQueryService.detail` 的 `Merchant::isOnline`），admin 保存时拒绝下架商户入清单，两头已兜住；living spec App 需求"仅含上架商户"已覆盖。`status` 仅作运营手动上下架。
2. app 端 `GET /api/app/recommend-lists/{id}` 未过滤 `status`（列表接口过滤了 ONLINE，详情只看城市是否上架），OFFLINE 清单详情仍可直接访问——属 app 行为变化，另开 change。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `recommend-list`: MODIFIED「推荐清单管理」（cityId 可改、status、人工恢复）、MODIFIED「清单内商户维护」（merchantIds 有序数组整体替换 + 未下架校验）、MODIFIED「web 端推荐清单管理页面」（去排序号措辞）。

## Impact

- **love-space-admin**：`modules/recommendlist/service/RecommendListService.java`（删 `replaceMerchants`，javadoc）、`dto/RecommendListMerchantItemRequest.java`（删）、`dto/RecommendListMerchantResponse.java` / `RecommendListDetailResponse` javadoc；`RecommendListServiceTest` 重写 4 个、新增 3 个用例。
- **contracts/api-spec.json**：recommend-list admin 路径与 schema 如上。
- **tests/recommend-list/{it,web}.md**：TC-004、007～010、WEB-002 修改；新增 3 条 IT。
- **openspec/specs/recommend-list/spec.md**：三个 Requirement 的 delta。
- web / app 代码：不动。移动端：不受影响。
