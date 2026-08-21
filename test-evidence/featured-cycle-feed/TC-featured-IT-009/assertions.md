# TC-featured-IT-009 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-cycle-items/post`：请求体自检符合 FeaturedCycleItemUpsertRequest。契约只声明请求体 schema，响应 schema 未声明，记跳过。

- ✅ 前置文章已存在：id=01a01f69-4aec-7f76-923f-db6e93d69028，title='周期文章T009'
- ✅ 创建 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 详情 状态码 = 200（实际 200）
- ✅ type = 'ARTICLE'（实际 'ARTICLE'）
- ✅ articleId = '01a01f69-4aec-7f76-923f-db6e93d69028'（实际 '01a01f69-4aec-7f76-923f-db6e93d69028'）
- ✅ title = '黄体期生活法'（实际 '黄体期生活法'）
- ✅ phase = 'LUTEAL'（实际 'LUTEAL'）
- ✅ 关联文章标题回显（relatedTitle）= '周期文章T009'（实际 '周期文章T009'）
- ✅ banner 为签名 URL（http 开头）
- ✅ activityId = null（实际 None）
- ✅ routeId = null（实际 None）
- ✅ subtitle = null（实际 None）
- ✅ description = null（实际 None）
- ✅ note = null（实际 None）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
