# TC-featured-IT-020 断言明细

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

前置清理：周期推荐 feed 为全局（不按城市过滤），执行前先删除历史遗留条目，四分组初始为空。

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1a | 创建下架城市 状态码 / online | 200 / false | 200 / false | ✅ |
| 2 | 1c | 在下架城市下创建路线（大使 online=true） | 200 | 200 | ✅ |
| 3 | 1d | 在同一下架城市下创建上线活动 | 200 / online=true | 200 / true | ✅ |
| 4 | 1e/1f | OVULATION 下各建 1 个上线 ROUTE / ACTIVITY 条目 | 均 200 | 均 200 | ✅ |
| 5 | 2 | app 周期推荐 状态码 | 200 | 200 | ✅ |
| 6 | 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 7 | 2 | ROUTE 条目出现在 OVULATION（城市下架不再过滤） | 含 `01a01fb9-c6e8-…` | 含 | ✅ |
| 8 | 2 | 同城市 ACTIVITY 条目**不出现**（活动侧仍要求城市上架） | 不含 `01a01fb9-c737-…` | 不含（OVULATION 仅 1 条 ROUTE） | ✅ |
| 9 | 2 | 四分组键齐全 | MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL | 齐全 | ✅ |
| 10 | 3a | 城市上架 状态码 / online | 200 / true | 200 / true | ✅ |
| 11 | 3b | 上架后 OVULATION 含两条条目 | ROUTE + ACTIVITY | 均含 | ✅ |
| 12 | 3b | ROUTE 条目前后状态一致 | id 与字段不变 | 一致 | ✅ |
| 13 | — | 契约 schema | `/api/app/featured-cycle-items` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无

补充观察（非断言项）：`POST /api/admin/featured-cycle-items` 对 `type=ROUTE` 必填 `title`/`subtitle`/`description`，缺任一返回 400 中文校验错误（本轮构造夹具时命中，非被测行为）。
