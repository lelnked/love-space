# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/{domain}/{it,web,app}.md`（按 modules.md「端」列裁决落点；living 文件，runner 独占回写状态）。
> 本次仅涉及 app 后端接口，recommend-list 域「端」为 web 但 web 行为零 delta，故只产 IT 用例。

## 新增用例

- TC-recommend-list-IT-015: GET /api/app/merchants/page 商户列表不受清单影响（ADDED Scenario: recommend-list/App 端清单与清单内商户查询#商户列表不受清单影响）

## 修改用例

- TC-recommend-list-IT-012: GET /api/app/recommend-lists/{id} 详情按清单保存顺序返回上架商户四字段（MODIFIED: 商户项收敛为 id/name/address/logo，`merchantId`→`id`，去 `recommendReason`/`sortOrder`；顺序改为清单保存顺序；下架商户不出现）

## 已删除用例

- 已删除 TC-recommend-list-IT-014（REMOVED scenario：按推荐清单过滤商户列表；`recommendListId` 参数与 `recommendSortOrder` 字段已移除，编号不复用）

## 需重测用例

- TC-recommend-list-IT-011: GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序（行为未变，同一 RecommendListQueryService 受影响）
- TC-recommend-list-IT-013: GET /api/app/recommend-lists 下架城市清单不可见、详情 404（行为未变，detail 组装逻辑改动）
- `tests/merchant/it.md` 中无调用 `GET /api/app/merchants/page` 的既有 TC，无需列入

## 执行汇总

- 执行日期：2026-08-25 ｜ app baseUrl `http://localhost:8081`（X-API-Key）｜ 前置造数 admin test 实例 `http://localhost:21423`
- 总数 4 / ✅ 4 / ❌ 0 / ⚠️ 0 / 未执行 0
  - TC-recommend-list-IT-011 ✅（6/6）清单按 sortOrder 升序
  - TC-recommend-list-IT-012 ✅（11/11）merchants 甲→乙 按保存顺序、下架丙不出现、每项恰为 id/name/address/logo
  - TC-recommend-list-IT-013 ✅（4/4）下架城市列表 `[]`、详情 404
  - TC-recommend-list-IT-015 ✅（10/10）带/不带 `recommendListId` 结果一致，按 weight 降序，无 `recommendSortOrder`
- 存证：`test-evidence/app-recommend-list-owns-merchant-order/TC-recommend-list-IT-{011,012,013,015}/`
- 追溯矩阵：`openspec/changes/app-recommend-list-owns-merchant-order/traceability-matrix.md`（无 ⚠）
- 契约观察（admin 侧夹具接口，非本 change 范围）：api-spec.json 仍含已删除的 `PUT /api/admin/recommend-lists/{id}/merchants` 与 `RecommendListMerchantItem` schema；`RecommendListCreateRequest` schema 缺 `merchantIds`/`status`。建议另开小 change 清理。
