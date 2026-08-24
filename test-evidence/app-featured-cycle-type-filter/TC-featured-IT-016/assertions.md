# TC-featured-IT-016 断言明细

执行日期: 2026-08-24 ｜ 结论: **✅ 通过**（12/12）

| # | 档位 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 状态码 | GET /api/app/featured-cycle-items | 200 | 200 | ✅ |
| 2 | 响应头 | Content-Type 含 application/json | 是 | `application/json` | ✅ |
| 3 | body | 顶层键齐全 | MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL 四键 | 四键齐全，顺序一致 | ✅ |
| 4 | body | FOLLICULAR（无条目周期）不缺键 | `[]` | `[]` | ✅ |
| 5 | body | MENSTRUAL 条数 | 1 | 1 | ✅ |
| 6 | body | MENSTRUAL[0].type | ACTIVITY | `ACTIVITY` | ✅ |
| 7 | body | MENSTRUAL[0].activityId 为所建活动 id | 01a034e1-e1e3-... | 一致 | ✅ |
| 8 | body | MENSTRUAL[0].banner.url 为 OSS 签名 URL | 含 `Expires`/`OSSAccessKeyId`/`Signature` | 三参数齐全，指向 `bound/` 前缀对象 | ✅ |
| 9 | body | OVULATION 条数 | 1 | 1 | ✅ |
| 10 | body | OVULATION[0].type / articleId | ARTICLE / 01a034e1-e2d9-... | 一致 | ✅ |
| 11 | body | OVULATION[0].banner.url 为签名 URL | 同上 | 一致 | ✅ |
| 12 | body | LUTEAL（仅含 online=false 条目） | `[]`，下线条目不下发 | `[]` | ✅ |

契约 schema 档: `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 200 响应的 schema，schema 档按规则跳过（不判失败）。
无契约漂移告警。
