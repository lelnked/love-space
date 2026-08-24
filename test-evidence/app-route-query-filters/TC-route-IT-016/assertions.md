# TC-route-IT-016 断言明细

> 契约: `api-spec.json#/paths/~1api~1app~1routes/get`（cityName 可选 string、ambassadorId 可选 uuid，实际请求参数与之相符）。
> 契约中该 operation 未声明 responses/schema，故 schema 校验一项跳过（记录为「契约未声明」，非漂移、非失败）。

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 状态码 | 200 | 200 | ✅ |
| 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 3 | body 为数组且条数 | 2（本轮开始 DB 无可见路线） | 2 | ✅ |
| 4 | 同时含两个城市的路线 | 全量路线016-甲、全量路线016-乙 | 二者均在 | ✅ |
| 5 | 按 sortOrder 升序 | 1 → 2 | [1(乙), 2(甲)] | ✅ |
| 6 | 每项 city 对应各自城市 id | 乙=01a034b1-396e-…、甲=01a034b1-395a-… | 与 admin 创建返回的 id 一一对应 | ✅ |
| 7 | 每项 city.name 为中文名 | 全量城乙016 / 全量城甲016 | 一致 | ✅ |
| 8 | 每项含缩略图签名 URL 与大使名 | 非空签名 URL、全量大使016 | 一致 | ✅ |
| 9 | 契约 schema | — | 契约未声明响应 schema | ⏭ 跳过 |

**结论**: ✅ 通过
