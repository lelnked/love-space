# TC-route-IT-013 断言明细

> 契约: `api-spec.json#/paths/~1api~1app~1routes/get`、`#/paths/~1api~1app~1routes~1{id}/get`；二者均未声明 responses/schema，schema 校验跳过。

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 前置：下线前列表含该路线 | 含「下线路线013」 | 含 | ✅ |
| 2 | admin 下线大使状态码 | 200 | 200 | ✅ |
| 3 | 下线后列表状态码 | 200 | 200 | ✅ |
| 4 | 下线后列表不含该路线 | 空数组 | [] | ✅ |
| 5 | 下线后详情状态码 | 404 | 404 | ✅ |
| 6 | 详情错误体 | 含 message | `route not found: <id>` | ✅ |
| 7 | 契约 schema | — | 契约未声明响应 schema | ⏭ 跳过 |

**结论**: ✅ 通过
