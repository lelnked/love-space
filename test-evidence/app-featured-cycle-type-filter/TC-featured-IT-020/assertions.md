# TC-featured-IT-020 断言明细

执行日期: 2026-08-24 ｜ 结论: **✅ 通过**（7/7）

| # | 档位 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 状态码 | 步骤 2 GET（城市 online=false） | 200 | 200 | ✅ |
| 2 | body | ROUTE 条目出现在 OVULATION（城市下架不再过滤路线） | 含 `01a034e3-97f2-...bdf0be` | 含 | ✅ |
| 3 | body | 同城市的 ACTIVITY 条目不出现（活动侧仍要求城市上架） | 不含 `01a034e3-98b0-...e7994c` | 不含 | ✅ |
| 4 | 状态码 | 步骤 3 GET（城市上架后） | 200 | 200 | ✅ |
| 5 | body | ACTIVITY 条目出现 | 含 `...e7994c` | 含 | ✅ |
| 6 | body | ROUTE 条目仍在且内容前后一致 | id/type/title/description 同步骤 2 | 完全一致 | ✅ |
| 7 | body | 四周期键齐全 | 四键 | 两次响应均四键齐全 | ✅ |

契约 schema 档: 该 operation 未声明 200 响应 schema，跳过（不判失败）。

⚠️ 契约漂移（不判失败）: 同 TC-018——`api-spec.json` 的 `POST /api/admin/routes` 仍写 `cityId` 必填，实现已改为 `cityName`。
