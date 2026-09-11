# 追溯矩阵（交付核对）：2026-09-11-merchant-name-length-1000

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change 2026-09-11-merchant-name-length-1000`

## 需求与场景
- **merchant/商户名称长度上限**: 名称 1000 字边界通过 / 名称超过 1000 字被拒绝 / 名称超长时既有商户数据保持不变 / 名称 16~1000 字可保存（原 15 字上限放开） / web 表单同口径校验

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-merchant-IT-001 | POST /api/admin/merchants 创建商户保存推荐理由 | merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由 | api-spec.json#/paths/~1api~1admin~1merchants/post | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-001/` | ✅ |
| TC-merchant-IT-002 | PUT /api/admin/merchants/{id} 更新推荐理由 | merchant/商户编辑推荐理由#admin 创建/更新商户时保存推荐理由 | api-spec.json#/paths/~1api~1admin~1merchants~1{id}/put | map-and-recommend-list | IT | `test-evidence/map-and-recommend-list/TC-merchant-IT-002/` | ✅ |
| TC-merchant-IT-010 | 商户名称 1000 字边界通过 | merchant/商户名称长度上限#名称 1000 字边界通过 | api-spec.json#/paths/~1api~1admin~1merchants/post | merchant-name-length-1000 | IT | - | ⬜ |
| TC-merchant-IT-011 | 商户名称 1001 字被拒绝 | merchant/商户名称长度上限#名称超过 1000 字被拒绝 | api-spec.json#/paths/~1api~1admin~1merchants~1{id}/put | merchant-name-length-1000 | IT | - | ⬜ |
| TC-merchant-IT-012 | 商户名称 16~1000 字可保存（原 15 字上限放开） | merchant/商户名称长度上限#名称 16~1000 字可保存（原 15 字上限放开） | api-spec.json#/paths/~1api~1admin~1merchants/post | merchant-name-length-1000 | IT | - | ⬜ |
| TC-merchant-IT-013 | 商户名称超长时既有商户数据保持不变 | merchant/商户名称长度上限#名称超长时既有商户数据保持不变 | api-spec.json#/paths/~1api~1admin~1merchants~1{id}/put | merchant-name-length-1000 | IT | - | ⬜ |
| TC-merchant-WEB-001 | 商户表单录入推荐理由并回显 | merchant/商户编辑推荐理由#web 商户表单录入推荐理由 | - | map-and-recommend-list | WEB | `test-evidence/regression/merchant/TC-merchant-WEB-001/` | ✅ |
| TC-merchant-WEB-003 | 商户表单名称 16~1000 字可保存回显 | merchant/商户名称长度上限#web 表单同口径校验 | - | merchant-name-length-1000 | WEB | - | ⬜ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：8
- ✅ 通过：3 (37.5%)
- ❌ 失败：0
- ⬜ 未测：5
