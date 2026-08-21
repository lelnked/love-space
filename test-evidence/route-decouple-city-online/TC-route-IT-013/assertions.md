# TC-route-IT-013 断言明细（回归）

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1d | 前置：大使上线时 app 列表含该路线 | 1 条 | 1 条 | ✅ |
| 2 | 2 | 大使下线 状态码 / online | 200 / false | 200 / false | ✅ |
| 3 | 3 | app 列表 状态码 | 200 | 200 | ✅ |
| 4 | 3 | 列表不含该路线 | `[]` | `[]` | ✅ |
| 5 | 4 | app 详情 状态码 | 404 | 404 | ✅ |
| 6 | 4 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 7 | — | 契约 schema | `/api/app/routes/{id}` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
