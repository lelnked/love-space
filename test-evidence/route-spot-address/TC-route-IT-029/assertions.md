# TC-route-IT-029 断言明细

执行日期: 2026-09-04 ｜ 结果: ✅ 通过 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema

- [x] 步骤1 admin 创建路线 R=200
- [x] 步骤2 app GET 状态码=200
- [x] 步骤2 Content-Type 含 application/json
- [x] spots 按 S1→S2
- [x] spots[0].address == 成都市青羊区宽窄巷子
- [x] spots[1] 含 address key 且为 null
- [x] 每个地点仍含名称/图片(签名URL)/介绍
- [x] 请求契约自检：请求体地点结构符合 RouteSpot schema（name/image/introduction 必填，address string|null）
- [ ] ⚠️ 契约漂移：RouteUpsertRequest 声明 cityId，实现为 cityName（非本 change 引入，不判失败）；operation 未声明 responses，响应 schema 未核对
