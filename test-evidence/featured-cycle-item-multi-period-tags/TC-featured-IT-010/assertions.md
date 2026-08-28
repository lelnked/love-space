# TC-featured-IT-010 断言明细

结果: ✅ 通过

契约: `FeaturedCycleItemUpsertRequest` schema 仍为 activityId/routeId/articleId 三字段，`FeaturedCycleItemResponse` 未声明（⚠️ 已知契约缺口，tasks 6.1 待做），故不做 schema 断言，仅做响应体断言。

- ✅ Step 1: ROUTE 缺 subtitle: 返回 400 — 400
- ✅ Step 1: ROUTE 缺 subtitle: message 为中文业务错误 — 副标题不能为空
- ✅ Step 2: ACTIVITY 缺 description: 返回 400 — 400
- ✅ Step 2: ACTIVITY 缺 description: message 为中文业务错误 — 推荐说明不能为空
- ✅ Step 3: ARTICLE 缺 banner: 返回 400 — 400
- ✅ Step 3: ARTICLE 缺 banner: message 为中文业务错误 — banner 图片不能为空
- ✅ Step 4: 缺 phase: 返回 400 — 400
- ✅ Step 4: 缺 phase: message 为中文业务错误 — 所属周期不能为空
- ✅ Step 5: 缺 type: 返回 400 — 400
- ✅ Step 5: 缺 type: message 为中文业务错误 — 内容类型不能为空
- ✅ 条目均未创建（page 计数不变） — 0 -> 0
