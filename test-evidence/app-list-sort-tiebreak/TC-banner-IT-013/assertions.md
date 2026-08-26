# TC-banner-IT-013 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1banners/get

结果: ✅ 通过（6/6 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | body 为数组且含 2 条 | ✅ | `len=2` |
| 4 | 两条 sortOrder 同为 5（并列） | ✅ | `DB: BannerC=5, BannerD=5` |
| 5 | 同序号按 createdAt 倒序：后创建的 D 排在 C 之前 | ✅ | `['BannerD021653', 'BannerC021653']` |
| 6 | 每项字段为 {id,name,type,image,data} | ✅ | `['data', 'id', 'image', 'name', 'type']` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1banners/get` 仅声明 summary 与 query parameters（无 responses schema），故无法做响应 schema 逐字段校验；已按 summary 语义（"数组，sortOrder 升序、同序号 createdAt 倒序"）与用例预期做字段级断言。
- 请求参数自检：`positionCode` 为契约声明的必填 query 参数，取值合法。
- 未发现契约漂移。
