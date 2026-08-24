# TC-route-IT-017 断言明细

> 契约: `api-spec.json#/paths/~1api~1app~1routes/get`（cityName 可选 string、ambassadorId 可选 uuid，实际请求参数与之相符）。
> 契约中该 operation 未声明 responses/schema，故 schema 校验一项跳过（记录为「契约未声明」，非漂移、非失败）。

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 步骤 2 状态码 | 200 | 200 | ✅ |
| 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 3 | 条数 | 2（仅大使 A 名下） | 2 | ✅ |
| 4 | 不含大使 B 的路线 | 不含「B路线017-2」 | 不含 | ✅ |
| 5 | 每项 ambassadorName | 大使A017 | 2 项均为 大使A017 | ✅ |
| 6 | 按 sortOrder 升序 | 1 → 3 | [1, 3] | ✅ |
| 7 | 步骤 3（A 下线）状态码 | 200 | 200 | ✅ |
| 8 | 步骤 3 返回空数组 | [] | [] | ✅ |
| 9 | 契约 schema | — | 契约未声明响应 schema | ⏭ 跳过 |

**结论**: ✅ 通过（大使下线优先于过滤条件）
