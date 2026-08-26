# TC-route-IT-019 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1routes/get

结果: ✅ 通过（6/6 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | body 为数组且含该路线 | ✅ | `len=1, title=无城路线021653` |
| 4 | 该路线 city 字段为 null | ✅ | `city=None` |
| 5 | 其余字段正常返回（id/title/thumbnail/sortOrder/ambassadorName） | ✅ | `['ambassadorName', 'city', 'id', 'sortOrder', 'thumbnail', 'title']` |
| 6 | 缩略图为签名 URL | ✅ | `thumbnail.url 含 Expires/Signature` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1routes/get` 无 responses schema；契约中 `cityName` 参数 description 明确「不做城市库校验；城市表中无同名城市时该路线的 city 为 null」，实测一致。
- ⚠️ **契约漂移（既有，非本 change 引入）**：`#/components/schemas/RouteUpsertRequest` 声明必填 `cityId`(uuid)，admin 实现实际用 `cityName`(string)；只影响 admin 写接口文档，不影响本用例断言的 app 读接口。
