# TC-featured-IT-022 断言明细

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | Step 2 HTTP 状态码 | 200 | 200 | ✅ |
| 2 | Step 2 Content-Type | application/json | application/json | ✅ |
| 3 | 四周期键齐全 | 含 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL | 四键全在 | ✅ |
| 4 | 每个分组均为空数组 | `[]` ×4 | `{"MENSTRUAL":[],"FOLLICULAR":[],"OVULATION":[],"LUTEAL":[]}` | ✅ |
| 5 | 不返回 404 | 非 404 | 200 | ✅ |
| 6 | 对照：不传 type 时 MENSTRUAL 含该 ACTIVITY 条目（证明数据存在、空是过滤所致） | 1 条 | 1 条 type=ACTIVITY | ✅ |
| 7 | 契约 schema 校验 | — | 该 operation 未声明 responses schema，跳过 | ⏭ |

结论: ✅ 通过
