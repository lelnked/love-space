# TC-featured-IT-002 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-items/post`：请求体自检符合 FeaturedItemUpsertRequest（required cityId、banner）。响应 schema 契约未声明，记跳过。

- ✅ 前置：上架城市 id=01a01f6d-0e49-77e8-b3a5-292f99396d7e；当前 featured-items totalElements=6
- ✅ 缺 banner（cityId 合法） → 状态码 = 400（实际 400）
- ✅ 缺 banner（cityId 合法） → message 为中文业务错误：'banner 图片不能为空'
- ✅ cityId 为不存在的 UUID（banner 合法） → 状态码 = 400（实际 400）
- ✅ cityId 为不存在的 UUID（banner 合法） → message 为中文业务错误：'关联城市不存在：cb6362fc-af74-4987-a090-7026442a0ea3'
- ✅ 缺 cityId → 状态码 = 400（实际 400）
- ✅ 缺 cityId → message 为中文业务错误：'关联城市不能为空'
- ✅ 条目均未创建：totalElements 仍为 6（实际 6）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
