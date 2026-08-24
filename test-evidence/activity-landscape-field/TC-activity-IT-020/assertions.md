# TC-activity-IT-020 断言结果

活动 ID: 01a034f6-d488-7660-a954-29a70028f0b1
执行时间: 2026-08-24
环境: admin http://localhost:21423 (test profile) / app http://localhost:8081，库 localhost:25432/love_space

| 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | POST /api/admin/activities 状态码 | 200 | 200 | ✅ |
| 1 | 响应 landscape | 海岸线景观 | 海岸线景观 | ✅ |
| 2 | GET /api/admin/activities/{id} 状态码 | 200 | 200 | ✅ |
| 2 | 响应 landscape | 海岸线景观 | 海岸线景观 | ✅ |
| 3 | PUT /api/admin/activities/{id} 状态码 | 200 | 200 | ✅ |
| 3 | 响应 landscape | 火山地貌 | 火山地貌 | ✅ |
| 4 | GET /api/app/activities/{id} 状态码 | 200 | 200 | ✅ |
| 4 | 响应 landscape | 火山地貌 | 火山地貌 | ✅ |

结论：landscape 字段在 admin 写入 → admin 查询 → app 查询三段贯通，019 迁移列生效。
