# TC-activity-IT-001 断言结果

活动 ID: `01a0608d-9cb6-7b38-b0b9-25a197ef8064`

| 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | POST 状态码 | 200 | 200 | ✅ |
| 1 | 响应不含 cityId | 无该字段 | 无该字段 | ✅ |
| 2 | GET 状态码 | 200 | 200 | ✅ |
| 2 | Content-Type | application/json | application/json | ✅ |
| 2 | title | 海岛露营节 | 海岛露营节 | ✅ |
| 2 | itinerary 顺序 | I1→I2 | I1→I2 | ✅ |
| 2 | periods | ["FOLLICULAR","OVULATION"] | ["FOLLICULAR", "OVULATION"] | ✅ |
| 2 | level | L2 | L2 | ✅ |
| 2 | detailHtml 原样 | <p>纯文本段落</p> | <p>纯文本段落</p> | ✅ |
| 2 | images 为签名 URL | 2 张，含 Signature/Expires | 2 张，均含 Signature/Expires | ✅ |
| 2 | online | True | True | ✅ |
| 2 | introduction/editorNote/gatheringPlace/dismissalPlace/transportation/visa | 与提交一致 | 全部一致 | ✅ |
| 2 | subtitle 键存在（本 change 新增，未填为 null） | null | None | ✅ |

结论：✅ 通过。不带 subtitle 的既有请求体仍合法，响应字段不减且新增 `subtitle`=null，`cityId` 仍不下发。
