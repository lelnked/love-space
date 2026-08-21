# TC-featured-IT-008 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-cycle-items/post`：请求体自检符合 FeaturedCycleItemUpsertRequest。契约只声明请求体 schema，响应 schema 未声明，记跳过。

- ✅ 前置路线已存在：id=01a01f69-4ac0-7563-a630-b071dd3f5432，路线实体主标题='路线主标题R008'（与下一步手填 title 不同）
- ✅ 创建 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 详情 状态码 = 200（实际 200）
- ✅ type = 'ROUTE'（实际 'ROUTE'）
- ✅ routeId = '01a01f69-4ac0-7563-a630-b071dd3f5432'（实际 '01a01f69-4ac0-7563-a630-b071dd3f5432'）
- ✅ title = '排卵期就该出门'（实际 '排卵期就该出门'）
- ✅ subtitle = '三天两夜'（实际 '三天两夜'）
- ✅ description = '体力最好的几天'（实际 '体力最好的几天'）
- ✅ phase = 'OVULATION'（实际 'OVULATION'）
- ✅ title 为手填值，不等于路线实体主标题 '路线主标题R008'
- ✅ banner 为签名 URL（http 开头）
- ✅ activityId = null（实际 None）
- ✅ articleId = null（实际 None）
- ✅ note = null（实际 None）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
