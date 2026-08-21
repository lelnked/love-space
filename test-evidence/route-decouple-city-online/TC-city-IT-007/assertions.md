# TC-city-IT-007 断言明细（回归）

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1c | 前置：app 信息流含该条目 | 含 `01a01fb6-ba2f-…` | 含（列表共 3 条） | ✅ |
| 2 | 1c | 状态码 / Content-Type | 200 / application/json | 200 / application/json | ✅ |
| 3 | 2 | 城市下架 状态码 / online | 200 / false | 200 / false | ✅ |
| 4 | 3 | 下架后信息流状态码 | 200 | 200 | ✅ |
| 5 | 3 | 下架后不含该城市条目 | 不含 `01a01fb6-ba2f-…` | 不含（剩余 2 条为其他城市存量条目） | ✅ |
| 6 | — | 契约 schema | `/api/app/featured-items` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
