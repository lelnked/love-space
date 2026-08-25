# TC-city-IT-005 assertions

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 前置：下架前列表含该清单 | 含 | 含（1 条） | 通过 |
| 2 | 前置：下架前详情状态码 | 200 | 200 | 通过 |
| 3 | 城市下架接口状态码 | 200 | 200，`online:false` | 通过 |
| 4 | 下架后列表状态码 | 200 | 200 | 通过 |
| 5 | 下架后列表内容 | 空数组 | `[]` | 通过 |
| 6 | 下架后详情状态码 | 404 | 404 | 通过 |
| 7 | 404 响应体口径（app 端 ResourceNotFoundException 全局格式 status/error/message/path） | 符合 | 符合 | 通过 |
| 8 | 契约 schema（api-spec.json#/paths/~1api~1app~1recommend-lists/get） | 符合 | 符合 | 通过 |

结论：✅ 通过。城市下架 → 推荐清单级联不可见，语义未因本 change 变化。
