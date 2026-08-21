# TC-city-IT-006 断言明细

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1e | 前置：城市上架时 app 活动列表含该活动 | 1 条 | 1 条（id `01a01fb6-4325-…`） | ✅ |
| 2 | 1f | 前置：app 路线列表含该路线 | 1 条 | 1 条（id `01a01fb6-43a1-…`） | ✅ |
| 3 | 2 | 城市下架 状态码 / online | 200 / false | 200 / false | ✅ |
| 4 | 3 | app 活动列表 状态码 | 200 | 200 | ✅ |
| 5 | 3 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 6 | 3 | 活动列表为空（级联生效） | `[]` | `[]` | ✅ |
| 7 | 4 | app 活动详情 状态码 | 404 | 404 | ✅ |
| 8 | 4 | body `message` | 活动不存在语义 | "activity not found: 01a01fb6-4325-…" | ✅ |
| 9 | — | 本用例不再断言路线隐藏（路线侧由 TC-city-IT-008 承载） | — | 路线侧未断言 | ✅ |
| 10 | — | 契约 schema | api-spec.json 中 `/api/app/activities` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
