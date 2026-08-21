# TC-featured-IT-001 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-items/post`：请求体自检符合 FeaturedItemUpsertRequest（required cityId、banner）。响应 schema 契约未声明，记跳过。

本轮为 featured-cycle-feed 交付轮回归：确认新增周期推荐模块未带坏既有「地图上新推荐」。

- ✅ 状态码 = 200，token 为三段式 JWT（实际 200）
- ✅ 前置城市 状态码 = 200（实际 200），online = true
- ✅ 创建 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 详情 状态码 = 200（实际 200）
- ✅ cityId = 01a01f6d-0e2f-7e2e-9b56-f80c09a0f3f1（实际 '01a01f6d-0e2f-7e2e-9b56-f80c09a0f3f1'）
- ✅ banner 为签名 URL（http 开头、非裸 objectKey）
- ✅ description = '地图上新'（实际 '地图上新'）
- ✅ online = true（实际 True）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
