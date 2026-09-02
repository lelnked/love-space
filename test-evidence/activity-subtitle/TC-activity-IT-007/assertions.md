# TC-activity-IT-007 断言结果

| 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 2 | 状态码 | 200 | 200 | ✅ |
| 2 | Content-Type | application/json | application/json | ✅ |
| 2 | 列表含全部上线活动（不因城市上架状态被筛掉） | id 集合 = admin online 集合（52 条） | 完全相等（52 条，差集为空） | ✅ |
| 2 | 排序 | createdAt 倒序 | 与 admin createdAt 倒序序列逐项一致 | ✅ |
| 2 | 每项含 title/subtitle/images/tags/level/periods | 全部存在 | 全部 52 项均含这 6 个键 | ✅ |
| 2 | images 为签名 URL | 含 Signature 与 Expires | 有图项的 URL 均含 Signature/Expires | ✅ |
| 2 | 填写了副标题的活动 subtitle 与后台一致 | 山野轻装 | '山野轻装' | ✅ |

结论：✅ 通过。列表项字段集合已扩容出 `subtitle`，已填副标题的活动取值与后台录入一致；全局上线活动无城市筛选、createdAt 倒序不变。
