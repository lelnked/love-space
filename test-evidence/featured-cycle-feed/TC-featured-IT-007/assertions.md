# TC-featured-IT-007 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-cycle-items/post`：请求体自检符合 FeaturedCycleItemUpsertRequest（required phase/type/banner 齐备，phase∈Period 枚举，type∈FeaturedCycleItemType 枚举）。契约只声明请求体 schema，响应 schema 未声明，记跳过。

- ✅ 状态码 = 200，token 为三段式 JWT（实际 200）
- ✅ 前置活动创建 状态码 = 200（实际 200），id=01a01f68-f051-70cf-b504-7bbaba40314a，title=周期活动ACT007
- ✅ 创建 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 响应含 id（01a01f68-f05b-70ec-bd4a-c170637cf48a）
- ✅ 详情 状态码 = 200（实际 200）
- ✅ 详情响应头 Content-Type 含 application/json
- ✅ phase = 'MENSTRUAL'（实际 'MENSTRUAL'）
- ✅ type = 'ACTIVITY'（实际 'ACTIVITY'）
- ✅ activityId = '01a01f68-f051-70cf-b504-7bbaba40314a'（实际 '01a01f68-f051-70cf-b504-7bbaba40314a'）
- ✅ description = '经期慢下来'（实际 '经期慢下来'）
- ✅ note = '周末两日'（实际 '周末两日'）
- ✅ sortOrder = 1（实际 1）
- ✅ online = False（实际 False）
- ✅ 关联活动标题回显（字段名为 relatedTitle）= '周期活动ACT007'（实际 '周期活动ACT007'）
- ✅ banner 为签名 URL（http 开头、非裸 objectKey）：http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bou…
- ✅ routeId = null（实际 None）
- ✅ articleId = null（实际 None）
- ✅ title = null（实际 None）
- ✅ subtitle = null（实际 None）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
