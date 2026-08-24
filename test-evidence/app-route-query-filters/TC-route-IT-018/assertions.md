# TC-route-IT-018 断言明细

> 契约: `api-spec.json#/paths/~1api~1app~1routes/get`（cityName 可选 string、ambassadorId 可选 uuid，实际请求参数与之相符）。
> 契约中该 operation 未声明 responses/schema，故 schema 校验一项跳过（记录为「契约未声明」，非漂移、非失败）。

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 状态码 | 200 | 200 | ✅ |
| 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 3 | 条数 | 1（交集） | 1 | ✅ |
| 4 | 命中项 | 甲A路线018 | 甲A路线018 | ✅ |
| 5 | 不含城市乙的 A 路线 | 不含「乙A路线018」 | 不含 | ✅ |
| 6 | 不含城市甲的 B 路线 | 不含「甲B路线018」 | 不含 | ✅ |
| 7 | 命中项 city.name / ambassadorName | 组合城甲018 / 组合大使A018 | 一致 | ✅ |
| 8 | 契约 schema | — | 契约未声明响应 schema | ⏭ 跳过 |

**结论**: ✅ 通过（cityName 与 ambassadorId 为 AND 组合）
