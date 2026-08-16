# TC-route-IT-006 断言明细

执行日期: 2026-08-16

- ✅ 创建返回 200
- ℹ️ 请求体已按契约 RouteUpsertRequest 自检（required: cityId,title,thumbnail,images≥1,ambassadorId；spots 各含 name/image/introduction）通过
- ✅ 详情返回 200
- ✅ title 与提交一致
- ✅ sortOrder=2
- ✅ cityId 与提交一致
- ✅ ambassadorNote/travelTime/season/travelStatus 与提交一致
- ✅ spots 按 S1→S2 顺序返回
- ✅ spots 每个含 name/image/introduction
- ✅ thumbnail 为签名 URL
- ✅ images 均为签名 URL
- ✅ 地点图片均为签名 URL
