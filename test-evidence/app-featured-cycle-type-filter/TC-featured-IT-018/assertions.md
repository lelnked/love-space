# TC-featured-IT-018 断言明细

执行日期: 2026-08-24 ｜ 结论: **✅ 通过**（9/9）

| # | 档位 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 状态码 | 步骤 2 GET | 200 | 200 | ✅ |
| 2 | body | 步骤 2 四周期键齐全 | 四键 | `['MENSTRUAL','FOLLICULAR','OVULATION','LUTEAL']` | ✅ |
| 3 | body | 步骤 2 OVULATION 含该 ROUTE 条目 | 1 条，type=ROUTE，routeId=01a034e2-faa7-... | 一致 | ✅ |
| 4 | 状态码 | 步骤 3 GET（大使下线后） | 200 | 200 | ✅ |
| 5 | body | 步骤 3 该条目从 OVULATION 消失 | `[]` | `[]` | ✅ |
| 6 | body | 步骤 3 四周期键仍齐全 | 四键 | 四键 | ✅ |
| 7 | 状态码 | 步骤 4 GET（大使恢复上线） | 200 | 200 | ✅ |
| 8 | body | 步骤 4 条目重新出现且内容一致 | id/type/routeId 同步骤 2 | 完全一致 | ✅ |
| 9 | 语义 | 全程无 5xx | 无 | 三次 GET 均 200 | ✅ |

契约 schema 档: 该 operation 未声明 200 响应 schema，跳过（不判失败）。

⚠️ 契约漂移（不判失败，供人工确认）: `api-spec.json#/paths/~1api~1admin~1routes/post` 仍声明 `cityId`（uuid）必填，
而实现（`RouteUpsertRequest`）已改为 `cityName`（文本）必填、无 `cityId`。该漂移属 route 域契约滞后，
本用例按实现字段发请求方能建出夹具；与被测 operation `/api/app/featured-cycle-items` 无关。
