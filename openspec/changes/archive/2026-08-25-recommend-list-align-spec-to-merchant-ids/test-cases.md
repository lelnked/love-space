# 受影响测试用例清单

> 本文件登记本 change 影响的 TC ID 清单 = 交付验证的执行范围。
> 用例本体在 `tests/recommend-list/{it,web}.md`（living 文件，runner 独占回写状态）。
> 本 change 不改任何运行时行为：living spec / 契约 / 用例对齐到现行代码，admin 仅删死代码与改注释。

## 新增用例

- TC-recommend-list-IT-016: PUT /api/admin/recommend-lists/{id} merchantIds 含已下架商户被拒绝（ADDED Scenario: recommend-list/清单内商户维护#拒绝已下架商户）P0
- TC-recommend-list-IT-017: POST /api/admin/recommend-lists status 默认 ONLINE 且可带 status/merchantIds 创建（MODIFIED Scenario: recommend-list/推荐清单管理#创建清单 新增 status 断言；契约 POST 新增 status/merchantIds 字段）P1
- TC-recommend-list-IT-018: POST /api/admin/recommend-lists/{id}/online 人工恢复清单（ADDED Scenario: recommend-list/推荐清单管理#人工恢复清单；新增写接口 POST /{id}/online）P0

> 编号说明：016/017/018 接 it.md 现有最大号 015；014 永不复用。
> design 原拟 017 = 「修改所属城市需清单内商户同属新城市」，该 scenario 两个分支已由 TC-004 完整覆盖，故 017 改覆盖「创建清单」的 status 新断言。
> 016/018 前置的商户下架走 `PUT /api/admin/merchants/{id}/online` `{"online": false}`（代码存在、api-spec.json 未登记，用例内标 ⚠️ 待补契约，归 merchant 域，本 change 不补）。

## 修改用例

- TC-recommend-list-IT-004: PUT /api/admin/recommend-lists/{id} 修改所属城市需清单内商户同属新城市（MODIFIED: 原「cityId 不可变」口径失效；关联需求改指向新 Scenario 推荐清单管理#修改所属城市需清单内商户同属新城市，两分支：含 A 商户改 B → 400；无商户改 B → 200）
- TC-recommend-list-IT-007: PUT /api/admin/recommend-lists/{id} merchantIds 整体替换本城市商户并按数组顺序回显（MODIFIED: `PUT /{id}/merchants` 端点已删，改走 PUT /{id} 的 `merchantIds: [M2, M1]`，断言数组顺序回显）
- TC-recommend-list-IT-008: PUT /api/admin/recommend-lists/{id} merchantIds 含跨城市商户被拒绝（MODIFIED: 同上改走 PUT /{id} merchantIds）
- TC-recommend-list-IT-009: PUT /api/admin/recommend-lists/{id} merchantIds 重复商户被拒绝（MODIFIED: 同上，`[M1, M1]`）
- TC-recommend-list-IT-010: PUT /api/admin/recommend-lists/{id} merchantIds 去掉商户即移除且不影响商户本身（MODIFIED: 同上，先 `[M1, M2]` 再 `[M2]`）
- TC-recommend-list-WEB-002: 清单编辑界面维护商户（仅本城市可选）（MODIFIED: 去掉「排序号」措辞，改「依次添加 M2、M1」「按添加顺序回显」；**仅改文本，本次不重跑 web**——无 web 代码改动、界面口径未变，状态/存证字段保留原值）

以上修改用例均保留原 状态/执行存证/最后更新 字段值（增量合并），由本次交付 IT 重跑后回写。

## 需重测用例

同一 `RecommendListService` 被删死代码 / 改注释，行为未变，回归确认：

- TC-recommend-list-IT-001
- TC-recommend-list-IT-002
- TC-recommend-list-IT-003
- TC-recommend-list-IT-005（步骤 1 前置措辞同步：原引用已删除的 `PUT /{id}/merchants`，改为「通过创建/更新请求的 `merchantIds` 关联」；断言与来源不变）
- TC-recommend-list-IT-006

## 执行汇总

- 执行日期：2026-08-25 ｜ admin test 实例 `http://localhost:21423`（JWT）
- 总数 13 / ✅ 13 / ❌ 0 / ⚠️ 2（契约漂移，不判失败）/ 未执行 0
  - 修改：IT-004（cityId 两分支）、007（[M2,M1] 顺序回显）、008/009（跨城市/重复 → 400）、010（移除不影响商户）✅
  - 新增：IT-016（下架商户 → 400）、017（status 默认 ONLINE / 带 status+merchantIds 创建）、018（/online 三步：拒绝→恢复→幂等）✅
  - 重测：IT-001/002/003/005/006 ✅
  - WEB-002 仅改文本未重跑（无 web 改动）
- ⚠️ 契约漂移：`PUT /api/admin/merchants/{id}/online` 代码存在但 api-spec.json 未登记（merchant 域，另补）
- 存证：`test-evidence/recommend-list-align-spec-to-merchant-ids/TC-recommend-list-IT-{001..010,016,017,018}/`
- 追溯矩阵：`openspec/changes/recommend-list-align-spec-to-merchant-ids/traceability-matrix.md`（✅ 追溯矩阵已生成：./openspec/changes/recommend-list-align-spec-to-merchant-ids/traceability-matrix.md（无 ⚠））
- 质量门禁：admin UT 126/126 ✅、RecommendListServiceTest 11/11 ✅、admin package ✅；web/app 零改动跳过
