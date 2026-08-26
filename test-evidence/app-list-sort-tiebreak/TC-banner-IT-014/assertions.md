# TC-banner-IT-014 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1banners/get

结果: ✅ 通过（5/5 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | body 为数组 | ✅ | `type=list, len=0` |
| 4 | 结果中不含该下架 Banner | ✅ | `[] —— 下架Banner021653 未出现` |
| 5 | 前置状态核对：Banner online=false | ✅ | `创建响应 online=false` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1banners/get` 仅声明 summary 与 query parameters（无 responses schema），故无法做响应 schema 逐字段校验；已按 summary 语义（"数组，sortOrder 升序、同序号 createdAt 倒序"）与用例预期做字段级断言。
- 请求参数自检：`positionCode` 为契约声明的必填 query 参数，取值合法。
- 未发现契约漂移。
