# TC-recommend-list-IT-011 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1recommend-lists/get

结果: ✅ 通过（6/6 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | 返回该城市全部清单共 3 条 | ✅ | `len=3` |
| 4 | 按 sortOrder 1→3→5 升序 | ✅ | `[1, 3, 5]` |
| 5 | 每项含 id/title/introduction/cityId/sortOrder | ✅ | `['cityId', 'id', 'introduction', 'sortOrder', 'title']` |
| 6 | cityId 均为该城市 | ✅ | `True` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1recommend-lists/get` 仅声明 summary 与 query 参数 `cityId`（无 responses schema），无法逐字段 schema 校验；已按 summary 语义（"仅上架城市，sortOrder 升序、同序号 createdAt 倒序"）做字段级断言。
- 请求参数自检：`cityId` 为必填 uuid，取值合法。
- 未发现契约漂移。
