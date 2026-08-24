# TC-route-IT-019 断言明细

> 契约: `api-spec.json#/paths/~1api~1app~1routes/get`（cityName 可选 string、ambassadorId 可选 uuid，实际请求参数与之相符）。
> 契约中该 operation 未声明 responses/schema，故 schema 校验一项跳过（记录为「契约未声明」，非漂移、非失败）。

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 前置：系统内至少 1 条可见路线 | ≥1 | 不带参数查询返回 11 条 | ✅ |
| 2 | 前置：无中文名「不存在城」的城市 | 0 条 | admin GET /api/admin/cities?name=不存在城 返回 [] | ✅ |
| 3 | 状态码 | 200（不是 404） | 200 | ✅ |
| 4 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 5 | body 为空数组 | [] | [] | ✅ |
| 6 | 未退化为返回全部路线 | 长度 0 ≠ 11 | 长度 0 | ✅ |
| 7 | 契约 schema | — | 契约未声明响应 schema | ⏭ 跳过 |

**结论**: ✅ 通过
