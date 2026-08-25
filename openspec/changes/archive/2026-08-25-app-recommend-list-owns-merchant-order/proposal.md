## Why

推荐清单是运营手挑的、有序的小集合，"清单内商户顺序"是清单自己的数据——运营在 web 端按顺序提交 `merchantIds`，admin 把数组下标落到 `loves_recommend_list_merchant.sort_order`，App 端 `GET /api/app/recommend-lists/{id}` 已按该顺序返回。但商户列表 `GET /api/app/merchants/page` 又额外接了 `recommendListId` 参数：商户域 join 清单关联表、在自身 `weight DESC, createdAt DESC` 之上硬加一级清单排序、给每个商户项塞了 `recommendSortOrder`。这把"清单的顺序"泄漏成了"商户的属性"，造成 merchant → recommend-list 的反向依赖，且同一件事（清单内商户）有两个形状不同的接口在做。现在收敛：清单内商户只由 recommend-list 域提供，商户域回到纯粹的商户查询。

## What Changes

- **BREAKING** `GET /api/app/merchants/page` 移除可选参数 `recommendListId`；响应项 `MerchantListItemResponse` 移除 `recommendSortOrder` 字段。列表排序回到固定的 `weight DESC, createdAt DESC`。
- `GET /api/app/recommend-lists/{id}` 的 `merchants[]` 项收敛为仅 `id`、`name`、`address`、`logo` 四个字段（移除 `recommendReason`、`sortOrder`；`merchantId` 改名为 `id` 与其它 App 端商户项一致）。顺序即清单保存顺序，由数组顺序表达，不再回传排序号。
- 商户域代码去掉对 `loves_recommend_list_merchant` 的 join 与对 `RecommendListMerchantRepository` 的注入；依赖方向恢复为 recommend-list → merchant 单向。
- living spec 措辞由"按关联排序号升序"改为"按清单保存顺序"，`sort_order` 列仅作关系表的位置持久化，不再是对外业务概念（库表不变、无迁移）。

不在本次范围：admin 侧 `RecommendListService.replaceMerchants` / `RecommendListMerchantItemRequest`（无 controller 调用的死代码，另开清理）；`tests/recommend-list/it.md` TC-007 引用的 `PUT /api/admin/recommend-lists/{id}/merchants` 在 admin controller 中并不存在，属既有失配，另行处理。

## Capabilities

### New Capabilities
（无）

### Modified Capabilities
- `recommend-list`: REMOVED「App 端清单查询」（其 scenario「按推荐清单过滤商户列表」废止）→ ADDED「App 端清单与清单内商户查询」承接其余 scenario；清单详情商户项字段收敛为 id/name/address/logo，顺序为清单保存顺序，并明确商户列表接口不受清单影响。

## Impact

- **love-space-app**
  - `modules/merchant`: `MerchantController.page` 去参；`MerchantService.page` 去掉 recommendListId 分支与 `RecommendListMerchantRepository` 注入；`MerchantRepository.searchOnlineNative` 去 join、order by 退回 `weight desc, created_at desc`；`MerchantListItemResponse` 删 `recommendSortOrder`。
  - `modules/recommendlist`: `RecommendListMerchantItemResponse` 收敛为 `id/name/address/logo`；`RecommendListQueryService.detail` 相应调整。
  - 测试：`MerchantReadIT` 删按清单过滤用例；`RecommendListQueryServiceTest` 断言改为字段收敛与顺序；`MerchantControllerWebMvcTest` 若涉及 `recommendSortOrder` 同步调整。
  - `openapi.json` 重新生成。
- **contracts/api-spec.json**: `/api/app/merchants/page` 删除 `recommendListId` 参数并改 summary；`/api/app/recommend-lists/{id}` summary 注明商户项仅 id/name/address/logo。
- **tests/recommend-list/it.md**: 删除 TC-recommend-list-IT-014；TC-012 预期结果改为字段收敛 + 保存顺序。
- **移动端（不在本仓库）**: 若已调用 `recommendListId` 参数或读取 `recommendReason` / `sortOrder` / `merchantId`，需同步改为消费 `/api/app/recommend-lists/{id}` 的新字段。
- admin、web 不受影响（web 提交的 `merchantIds` 有序数组语义不变）。
