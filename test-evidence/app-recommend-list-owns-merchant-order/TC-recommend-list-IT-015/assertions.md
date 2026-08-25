# TC-recommend-list-IT-015 断言明细

执行日期: 2026-08-25 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1merchants~1page/get

结果: ✅ 通过（10/10 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 不带 recommendListId 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | 带 recommendListId 状态码 200 | ✅ | `HTTP/1.1 200` |
| 3 | Content-Type application/json(两次) | ✅ | |
| 4 | 不带: totalElements=3 | ✅ | `totalElements=3` |
| 5 | 带: totalElements=3 | ✅ | `totalElements=3` |
| 6 | 不带: content 顺序 weight 降序 乙(30)→丙(20)→甲(10) | ✅ | `["商户乙140600","商户丙140600","商户甲140600"]` |
| 7 | 带: content 顺序 weight 降序 乙→丙→甲(recommendListId 被忽略，非清单顺序 甲→乙) | ✅ | `["商户乙140600","商户丙140600","商户甲140600"]` |
| 8 | 两次 totalElements 与 content 顺序一致 | ✅ | |
| 9 | content[*] 不含 recommendSortOrder(两次) | ✅ | `["id","name","logo","address","tags","scores","loveIndex"]` |
| 10 | content[*] 含 id/name/logo/address/tags/scores/loveIndex | ✅ | `["id","name","logo","address","tags","scores","loveIndex"]` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1merchants~1page/get` 在 contracts/api-spec.json 中仅声明 summary/parameters（无 responses schema），无法做响应 schema 逐字段校验；已按 summary 语义与用例预期做字段级断言（见上表），未发现契约漂移。
- 请求参数自检：query 参数与契约 parameters 一致（`recommendListId` 已从契约移除，本用例故意传入以验证被忽略）。
