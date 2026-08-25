# TC-recommend-list-IT-013 断言明细

执行日期: 2026-08-25 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get

结果: ✅ 通过（4/4 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 列表 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | 列表 返回空数组 | ✅ | `[]` |
| 3 | 详情 状态码 404 | ✅ | `HTTP/1.1 404` |
| 4 | 详情 错误体 status=404 | ✅ | `{"status":404,"error":"Not Found","message":"recommend list not found: 01a0393e-04ac-7772-8e08-a5e1020e349b","path":"/api/app/recommend-lists/01a0393e-04ac-7772-8e08-a5e1020e349b"}` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1recommend-lists~1{id}/get` 在 contracts/api-spec.json 中仅声明 summary/parameters（无 responses schema），无法做响应 schema 逐字段校验；已按 summary 语义与用例预期做字段级断言（见上表），未发现契约漂移。
- 请求参数自检：query 参数与契约 parameters 一致。
