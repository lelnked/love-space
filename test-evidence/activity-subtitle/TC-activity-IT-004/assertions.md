# TC-activity-IT-004 断言结果

活动 ID: `01a0608d-9cb6-7b38-b0b9-25a197ef8064`

| 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 2 | PUT 状态码（携带多余 cityId 不报 400） | 200 | 200 | ✅ |
| 3 | GET 状态码 | 200 | 200 | ✅ |
| 3 | title 更新生效 | 海岛露营节-改名 | 海岛露营节-改名 | ✅ |
| 3 | level | L3 | L3 | ✅ |
| 3 | periods | ["MENSTRUAL"] | ["MENSTRUAL"] | ✅ |
| 3 | itinerary | 1 条 NEW-D1 | 1 条 NEW-D1 | ✅ |
| 3 | 响应不含 cityId | 无该字段 | 无该字段 | ✅ |
| 3 | updatedAt 已刷新 | > createdAt | 2026-09-02T05:18:23.082601Z > 2026-09-02T05:18:08.553934Z | ✅ |

结论：✅ 通过。不带 subtitle 的 PUT 仍合法，多余 cityId 被静默忽略。
