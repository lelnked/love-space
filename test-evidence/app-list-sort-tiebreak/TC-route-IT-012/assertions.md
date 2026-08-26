# TC-route-IT-012 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1routes/get

结果: ✅ 通过（7/7 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | 返回该城市全部可见路线共 3 条 | ✅ | `len=3` |
| 4 | 按 sortOrder 1→3→5 升序 | ✅ | `[1, 3, 5]` |
| 5 | 每项含缩略图（签名 URL） | ✅ | `thumbnail={id,url}，url 含 Expires/Signature` |
| 6 | 每项含主标题 title | ✅ | `['路线1-021653', '路线3-021653', '路线5-021653']` |
| 7 | 每项含大使名称 ambassadorName | ✅ | `{'排序大使021653'}` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1routes/get` 仅声明 summary 与 query 参数（无 responses schema），无法逐字段 schema 校验；已按 summary 语义（"sortOrder 升序、同序号 createdAt 倒序"）做字段级断言。
- 请求参数自检：`cityName` 为契约声明的可选 query 参数，取值合法。
- ⚠️ **契约漂移（既有，非本 change 引入）**：`api-spec.json#/components/schemas/RouteUpsertRequest` 声明必填字段为 `cityId`(uuid)，但 admin 实现 `RouteUpsertRequest` 实际使用 `cityName`(string)；用 `cityId` 提交会被 400 拒绝（"所属城市不能为空"）。本存证前置步骤按实现用 `cityName`。该漂移只影响 admin 写接口的契约文档，不影响本用例断言的 app 读接口行为，故标 ⚠️ 不判失败。
