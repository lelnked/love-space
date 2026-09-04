# TC-route-IT-006 断言明细

执行日期: 2026-09-04 ｜ 结果: ✅ 通过 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema

- [x] 步骤2 POST 状态码=200
- [x] 步骤2 Content-Type 含 application/json
- [x] 步骤3 GET 状态码=200
- [x] 详情 title/sortOrder/cityName/ambassadorNote/travelTime/season/travelStatus/ambassadorId 与提交一致
- [x] thumbnail 为签名 URL（bound/ 前缀）
- [x] images 2 张且均为签名 URL
- [x] spots 顺序 S1→S2
- [x] 每个 spot 含 name/image(签名URL)/introduction
- [x] 响应字段不减：spots 含 address key（本 change 新增，未传时为 null）
- [x] 请求契约自检：请求体地点结构符合 RouteSpot schema（name/image/introduction 必填，address string|null）
- [ ] ⚠️ 契约漂移：RouteUpsertRequest 声明 cityId，实现为 cityName（非本 change 引入，不判失败）；operation 未声明 responses，响应 schema 未核对
