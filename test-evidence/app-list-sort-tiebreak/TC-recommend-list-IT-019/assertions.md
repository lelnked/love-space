# TC-recommend-list-IT-019 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1recommend-lists/get

结果: ✅ 通过（6/6 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | body 为数组且含 2 条 | ✅ | `len=2` |
| 4 | 两条 sortOrder 同为 0（并列） | ✅ | `[0, 0]` |
| 5 | 同序号按 createdAt 倒序：后创建的 B 排在 A 之前 | ✅ | `['清单B021653', '清单A021653']` |
| 6 | 每项含 id/title/introduction/cityId/sortOrder | ✅ | `['cityId', 'id', 'introduction', 'sortOrder', 'title']` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1recommend-lists/get` 仅声明 summary 与 query 参数 `cityId`（无 responses schema），无法逐字段 schema 校验；已按 summary 语义（"仅上架城市，sortOrder 升序、同序号 createdAt 倒序"）做字段级断言。
- 请求参数自检：`cityId` 为必填 uuid，取值合法。
- 未发现契约漂移。
