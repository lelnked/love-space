# TC-activity-IT-001 断言明细

执行日期: 2026-08-16

- [x] 创建返回 200
- [x] Content-Type 为 application/json
- ℹ️ 请求体已按契约 ActivityUpsertRequest 自检（required: cityId,title,images≥1；periods/level 枚举合法）通过
- [x] 详情返回 200
- [x] title 与提交一致
- [x] tags 与提交一致
- [x] periods 枚举一致
- [x] level="L2"
- [x] introduction/editorNote/gatheringPlace/dismissalPlace/transportation/visa 一致
- [x] itinerary 按 I1→I2 顺序返回
- [x] detailHtml 文本原样保存
- [x] images 为签名 URL
- [x] online=true
- ℹ️ 契约未声明响应 schema，响应字段按用例预期断言
