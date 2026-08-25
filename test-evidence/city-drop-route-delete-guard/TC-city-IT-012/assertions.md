# TC-city-IT-012 assertions

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 未上架城市详情状态码 | 404 | 404 | 通过 |
| 2 | 不存在 UUID 详情状态码 | 404 | 404 | 通过 |
| 3 | 两次响应体均为 app 端 ResourceNotFoundException 全局口径（status/error/message/path） | 符合 | 符合 | 通过 |
| 4 | 两种情形错误信息不泄露上下架状态差异 | 同一口径 | 同为 `city not found: {id}` | 通过 |
| 5 | 契约 schema（api-spec.json#/paths/~1api~1app~1cities~1{id}/get 的 404 响应） | 符合 | 符合 | 通过 |

结论：✅ 通过。（本轮以运行中的 app 实例经 HTTP 实跑复核，与 `CityReadIT#detailReturns404WhenOfflineOrMissing` 同语义。）
