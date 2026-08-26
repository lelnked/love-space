# TC-recommend-list-IT-012 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get

结果: ✅ 通过（7/7 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | 含清单字段 title/introduction/sortOrder | ✅ | `['cityId', 'id', 'introduction', 'sortOrder', 'title']` |
| 4 | merchants 顺序为 甲→乙（清单保存顺序，与 weight 无关） | ✅ | `['甲021653', '乙021653']`（甲 weight=1 在前，乙 weight=9 在后） |
| 5 | 已下架商户 丙 不出现 | ✅ | `len(merchants)=2，无 丙021653` |
| 6 | 每项仅含 id/name/address/logo 四字段 | ✅ | `['address', 'id', 'logo', 'name']` |
| 7 | 不含 recommendReason/sortOrder/merchantId/recommendSortOrder | ✅ | `已核对，均不存在` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get` 无 responses schema，按 summary 语义做字段级断言。
- 未发现契约漂移。
