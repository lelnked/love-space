# TC-featured-IT-012 断言明细

结果: ✅ 通过

契约: `FeaturedCycleItemUpsertRequest` schema 仍为 activityId/routeId/articleId 三字段，`FeaturedCycleItemResponse` 未声明（⚠️ 已知契约缺口，tasks 6.1 待做），故不做 schema 断言，仅做响应体断言。

- ✅ 步骤 2 更新返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ phase 仍为 MENSTRUAL（传入值被忽略） — MENSTRUAL
- ✅ type 仍为 ACTIVITY（传入值被忽略） — ACTIVITY
- ✅ targetId 仍为该活动 id — 01a04880-e34f-7ddb-bae7-590e3cbbc333
- ✅ description 已按提交值更新 — 更新后的描述
- ✅ title 未被写入（仍为 null） — None
- ✅ 步骤 4 返回 400 — 400
- ✅ 步骤 4 message 为「关联活动不存在」（校验按持久化类型 ACTIVITY 分派） — 关联活动不存在：01a04880-e358-7f3a-9e73-2a1a93b314ac
