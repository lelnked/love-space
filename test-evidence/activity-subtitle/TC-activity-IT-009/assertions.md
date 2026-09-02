# TC-activity-IT-009 断言结果

活动 ID: `01a0608f-7e29-7e03-b08e-b6b1eb8bd0b7`

| 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 2 | 状态码 | 200 | 200 | ✅ |
| 2 | Content-Type | application/json | application/json | ✅ |
| 2 | 含全部展示字段 | title/images/tags/periods/level/introduction/editorNote/gatheringPlace/dismissalPlace/transportation/visa/itinerary | 全部存在 | ✅ |
| 2 | 不含 cityId | 无该字段 | 无该字段 | ✅ |
| 2 | detailHtml 文本与后台一致 | 含「第一段文字」「第二段文字」 | 两段文本均在且顺序一致 | ✅ |
| 2 | img src 为签名 URL | 含 Signature/Expires | http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/a009-inline.png?E... | ✅ |
| 2 | 详情字段集合扩容后既有字段不受影响 | landscape/itinerary 等仍正常 | landscape='山地'，itinerary 2 条，新增 subtitle='图文详情' | ✅ |

结论：✅ 通过。详情字段集合因 `subtitle` 扩容后，既有富文本/签名 URL/无 cityId 行为均未受影响。
