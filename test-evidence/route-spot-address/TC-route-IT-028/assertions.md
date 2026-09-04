# TC-route-IT-028 断言明细

执行日期: 2026-09-04 ｜ 结果: ✅ 通过 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema

- [x] 步骤2 POST 状态码=200
- [x] 步骤4 PUT 状态码=200
- [x] 步骤2/4 Content-Type 含 application/json
- [x] 步骤3 GET=200
- [x] 步骤3 spots[0].address == 成都市青羊区宽窄巷子
- [x] 步骤3 spots[1].address == null（未传 key）
- [x] 步骤5 GET=200
- [x] 步骤5 spots[0].address == 成都市锦江区春熙路
- [x] 步骤5 spots[1].address == null（显式 null）
- [x] name/image/introduction 不受影响
- [x] 请求契约自检：请求体地点结构符合 RouteSpot schema（name/image/introduction 必填，address string|null）
- [ ] ⚠️ 契约漂移：RouteUpsertRequest 声明 cityId，实现为 cityName（非本 change 引入，不判失败）；operation 未声明 responses，响应 schema 未核对
