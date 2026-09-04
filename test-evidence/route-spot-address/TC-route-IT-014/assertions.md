# TC-route-IT-014 断言明细

执行日期: 2026-09-04 ｜ 结果: ✅ 通过 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema

- [x] GET 状态码=200
- [x] Content-Type 含 application/json
- [x] 路线图片列表为签名 URL
- [x] 地点按 S1→S2
- [x] 每个地点含名称/图片/介绍/地址(address key 存在)
- [x] 未填地址时 address 为 null
- [x] 含大使信息：id/name/avatar 签名 URL/tags
- [x] cityName 下发
- [x] 请求契约自检：请求体地点结构符合 RouteSpot schema（name/image/introduction 必填，address string|null）
- [ ] ⚠️ 契约漂移：RouteUpsertRequest 声明 cityId，实现为 cityName（非本 change 引入，不判失败）；operation 未声明 responses，响应 schema 未核对
