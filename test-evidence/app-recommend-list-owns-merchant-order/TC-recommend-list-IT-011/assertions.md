# TC-recommend-list-IT-011 断言明细

执行日期: 2026-08-25 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1recommend-lists/get

结果: ✅ 通过（6/6 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type application/json | ✅ | `application/json` |
| 3 | 返回数组且 3 条(该城市全部清单) | ✅ | `len=3` |
| 4 | sortOrder 顺序 1→3→5 | ✅ | `[1,3,5]` |
| 5 | 每项含 id/title/introduction/cityId/sortOrder | ✅ | `["id","title","introduction","cityId","sortOrder"]` |
| 6 | cityId 均为 A | ✅ | |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1recommend-lists/get` 在 contracts/api-spec.json 中仅声明 summary/parameters（无 responses schema），无法做响应 schema 逐字段校验；已按 summary 语义与用例预期做字段级断言（见上表），未发现契约漂移。
- 请求参数自检：query 参数与契约 parameters 一致。
