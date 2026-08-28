# TC-featured-IT-033 断言明细

结果: ✅ 通过

契约: `FeaturedCycleItemUpsertRequest` schema 仍为 activityId/routeId/articleId 三字段，`FeaturedCycleItemResponse` 未声明（⚠️ 已知契约缺口，tasks 6.1 待做），故不做 schema 断言，仅做响应体断言。

- ✅ Step 2: POST ACTIVITY 不带 targetId: 返回 400（非 500） — 400
- ✅ Step 2: POST ACTIVITY 不带 targetId: message 为中文业务错误 — 关联实体不能为空
- ✅ Step 3: POST ROUTE 不带 targetId: 返回 400（非 500） — 400
- ✅ Step 3: POST ROUTE 不带 targetId: message 为中文业务错误 — 关联实体不能为空
- ✅ Step 4: POST ARTICLE 不带 targetId: 返回 400（非 500） — 400
- ✅ Step 4: POST ARTICLE 不带 targetId: message 为中文业务错误 — 关联实体不能为空
- ✅ Step 5: PUT targetId=null 返回 400（非 500） — 400
- ✅ Step 5: message 为中文业务错误 — 关联实体不能为空
- ✅ 分页总数不变（三次创建均未落库） — 1 -> 1
- ✅ 被 PUT 条目 targetId 保持原值未被清空 — 01a04880-e3aa-78a7-9596-2105be6a3454
- ✅ 其余字段保持原值 — ('原始描述', 7)
