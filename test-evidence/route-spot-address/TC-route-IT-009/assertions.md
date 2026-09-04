# TC-route-IT-009 断言明细

执行日期: 2026-09-04 ｜ 结果: ✅ 通过 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema

- [x] 前置 城市A 下路线创建成功且 2 个地点
- [x] 步骤2 PUT 状态码=200（实现口径：未返回 400）
- [x] 步骤2 Content-Type 含 application/json
- [x] 步骤3 GET 状态码=200
- [x] title 改名生效
- [x] sortOrder=9
- [x] spots 仅剩 1 个新地点
- [x] 所属城市不可变：cityName 仍为城市 A（PUT 传 B 被忽略或 400）
- [x] 不带 address 的 PUT 仍合法，spot 含 address=null
- [x] 请求契约自检：请求体地点结构符合 RouteSpot schema（name/image/introduction 必填，address string|null）
- [ ] ⚠️ 契约漂移：RouteUpsertRequest 声明 cityId，实现为 cityName（非本 change 引入，不判失败）；operation 未声明 responses，响应 schema 未核对

## 重测说明

上一轮（同日）此项失败：PUT 传异 cityName 被改写。admin 修复 RouteService 后重启，本轮 PUT 传城市 B 返回 200 且被忽略，详情 cityName 保持创建时的城市 A，判通过。
