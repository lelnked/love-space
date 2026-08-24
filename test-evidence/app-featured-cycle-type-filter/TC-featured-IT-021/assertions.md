# TC-featured-IT-021 断言明细

执行日期: 2026-08-24 ｜ 结论: **✅ 通过**（11/11）

| # | 档位 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 状态码 | GET `?type=ARTICLE` | 200 | 200 | ✅ |
| 2 | 响应头 | Content-Type 含 application/json | 是 | `application/json` | ✅ |
| 3 | body | 过滤态四周期键齐全 | MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL | 四键齐全 | ✅ |
| 4 | body | MENSTRUAL 仅 1 条 | 1 | 1 | ✅ |
| 5 | body | 该条 type=ARTICLE | ARTICLE | `ARTICLE` | ✅ |
| 6 | body | 该条 articleId 非空且等于所建文章 | 01a034e4-0840-... | 一致 | ✅ |
| 7 | body | 不含 ACTIVITY 条目 `...2cec87` | 不含 | 不含（activityId 全为 null） | ✅ |
| 8 | body | 不含 ROUTE 条目 `...fc1d3b` | 不含 | 不含（routeId 全为 null） | ✅ |
| 9 | 状态码 | GET 不带 type | 200 | 200 | ✅ |
| 10 | body | 不带 type 时 MENSTRUAL 含全部 3 条 | ACTIVITY + ROUTE + ARTICLE 各 1 | 3 条齐全 | ✅ |
| 11 | body | 不带 type 时其余三周期为空数组 | `[]` ×3 | `[]` ×3 | ✅ |

请求契约自检: `type` 为可选 query 参数，取值 `ARTICLE` 属 `FeaturedCycleItemType` 枚举，请求合法。
契约 schema 档: `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 200 响应 schema，跳过（不判失败）。
无契约漂移告警。
