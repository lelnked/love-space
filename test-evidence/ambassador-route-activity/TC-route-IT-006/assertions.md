# TC-route-IT-006 断言明细

执行日期: 2026-08-16

- [x] 创建返回 200
- ℹ️ 请求体已按契约 RouteUpsertRequest 自检（required: cityId,title,thumbnail,images≥1,ambassadorId；spots 各含 name/image/introduction）通过
- [x] 详情返回 200
- [x] title 与提交一致
- [x] sortOrder=2
- [x] cityId 与提交一致
- [x] ambassadorNote/travelTime/season/travelStatus 与提交一致
- [x] spots 按 S1→S2 顺序返回
- [x] spots 每个含 name/image/introduction
- [x] thumbnail 为签名 URL
- [x] images 均为签名 URL
- [x] 地点图片均为签名 URL
