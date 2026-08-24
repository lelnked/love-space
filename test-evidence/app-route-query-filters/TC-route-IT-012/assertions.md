# TC-route-IT-012 断言明细

> 契约: `api-spec.json#/paths/~1api~1app~1routes/get`（cityName 可选 string、ambassadorId 可选 uuid，实际请求参数与之相符）。
> 契约中该 operation 未声明 responses/schema，故 schema 校验一项跳过（记录为「契约未声明」，非漂移、非失败）。

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 状态码 | 200 | 200 | ✅ |
| 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 3 | 条数 | 3（该城市全部可见路线） | 3 | ✅ |
| 4 | 按 sortOrder 升序 | 1 → 3 → 5 | [1, 3, 5] | ✅ |
| 5 | 每项 city.name | 排序城012 | 排序城012 ×3 | ✅ |
| 6 | 每项缩略图为签名 URL | thumbnail.url 含 Signature | 3 项均含 | ✅ |
| 7 | 每项主标题 | 排序路线012-1/3/5 | 一致 | ✅ |
| 8 | 每项大使名称 | 路线大使012 | 一致 | ✅ |
| 9 | 契约 schema | — | 契约未声明响应 schema | ⏭ 跳过 |

**结论**: ✅ 通过（查询参数已由 cityId 改为 cityName，按中文名过滤生效）
