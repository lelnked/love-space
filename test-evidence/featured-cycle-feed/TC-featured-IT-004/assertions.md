# TC-featured-IT-004 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-items~1{id}/put`：请求体符合 FeaturedItemUpsertRequest。响应 schema 未声明，记跳过。

- ✅ 前置创建 状态码 = 200（实际 200），城市A=01a01f6d-0e85-78c3-b42c-17544a8c99e5 城市B=01a01f6d-0e8a-7df8-b54e-bb926775cc47
- ✅ 更新 状态码 = 200（实际 200）
- ✅ 详情 状态码 = 200（实际 200）
- ✅ description 更新生效（实际 '改写后的说明'）
- ✅ banner 为新图签名 URL（与更新前不同）
- ✅ 关联城市仍为城市 A（cityId 变更被忽略且不报错），实际 '01a01f6d-0e85-78c3-b42c-17544a8c99e5'
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
