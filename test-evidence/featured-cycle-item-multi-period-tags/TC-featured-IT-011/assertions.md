# TC-featured-IT-011 断言明细

结果: ✅ 通过

契约: `FeaturedCycleItemUpsertRequest` schema 仍为 activityId/routeId/articleId 三字段，`FeaturedCycleItemResponse` 未声明（⚠️ 已知契约缺口，tasks 6.1 待做），故不做 schema 断言，仅做响应体断言。

- ✅ Step 1: ACTIVITY targetId 不存在: 返回 400 — 400
- ✅ Step 1: ACTIVITY targetId 不存在: message 按类型区分「关联活动不存在」 — 关联活动不存在：a270de73-d79e-4845-8b7d-dea50cbac199
- ✅ Step 2: ROUTE targetId 不存在: 返回 400 — 400
- ✅ Step 2: ROUTE targetId 不存在: message 按类型区分「关联路线不存在」 — 关联路线不存在：a270de73-d79e-4845-8b7d-dea50cbac199
- ✅ Step 3: ARTICLE targetId 不存在: 返回 400 — 400
- ✅ Step 3: ARTICLE targetId 不存在: message 按类型区分「关联文章不存在」 — 关联文章不存在：a270de73-d79e-4845-8b7d-dea50cbac199
- ✅ Step 4: ACTIVITY targetId 传已存在文章 id（跨表不命中）: 返回 400 — 400
- ✅ Step 4: ACTIVITY targetId 传已存在文章 id（跨表不命中）: message 按类型区分「关联活动不存在」 — 关联活动不存在：01a04880-e30b-75ff-ae67-ed1f01f717d5
- ✅ 条目均未创建（page 计数不变） — 0 -> 0
