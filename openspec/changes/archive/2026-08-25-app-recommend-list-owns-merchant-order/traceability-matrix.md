# 追溯矩阵（交付核对）：app-recommend-list-owns-merchant-order

> 生成物勿手改。生成命令：`node scripts/generate-traceability-matrix.js --change app-recommend-list-owns-merchant-order`

## 需求与场景
- **recommend-list/App 端清单与清单内商户查询**: 查询上架城市的清单 / 清单详情返回商户明细 / 商户列表不受清单影响 / 下架城市清单不可见

## 测试用例追溯

| 用例 ID | 标题 | 关联需求 | 关联契约 | 来源 | 类型 | 存证 | 状态 |
|---|---|---|---|---|---|---|---|
| TC-recommend-list-IT-011 | GET /api/app/recommend-lists 上架城市清单按 sortOrder 升序 | recommend-list/App 端清单与清单内商户查询#查询上架城市的清单 | api-spec.json#/paths/~1api~1app~1recommend-lists/get | map-and-recommend-list | IT | `test-evidence/app-recommend-list-owns-merchant-order/TC-recommend-list-IT-011/` | ✅ |
| TC-recommend-list-IT-012 | GET /api/app/recommend-lists/{id} 详情按清单保存顺序返回上架商户四字段 | recommend-list/App 端清单与清单内商户查询#清单详情返回商户明细 | api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get | app-recommend-list-owns-merchant-order | IT | `test-evidence/app-recommend-list-owns-merchant-order/TC-recommend-list-IT-012/` | ✅ |
| TC-recommend-list-IT-013 | GET /api/app/recommend-lists 下架城市清单不可见、详情 404 | recommend-list/App 端清单与清单内商户查询#下架城市清单不可见 | api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get | map-and-recommend-list | IT | `test-evidence/app-recommend-list-owns-merchant-order/TC-recommend-list-IT-013/` | ✅ |
| TC-recommend-list-IT-015 | GET /api/app/merchants/page 商户列表不受清单影响 | recommend-list/App 端清单与清单内商户查询#商户列表不受清单影响 | api-spec.json#/paths/~1api~1app~1merchants~1page/get | app-recommend-list-owns-merchant-order | IT | `test-evidence/app-recommend-list-owns-merchant-order/TC-recommend-list-IT-015/` | ✅ |

## 覆盖核对

- ✅ 正反向覆盖完整，无悬空用例，状态可信

## 测试统计
- 总数：4
- ✅ 通过：4 (100.0%)
- ❌ 失败：0
- ⬜ 未测：0
