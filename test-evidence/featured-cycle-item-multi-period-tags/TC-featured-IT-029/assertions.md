# TC-featured-IT-029 断言明细

结果: ✅ 通过

契约: `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 responses/response schema（⚠️ 已知契约缺口，见 change 的 test-cases.md「待补契约」），body schema 层无契约可依，按 delta spec `openspec/changes/featured-cycle-item-multi-period-tags/specs/featured/spec.md` 断言响应体。

- ✅ 返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ 响应顶层为 JSON 数组 — list
- ✅ 数组恰含 1 条 — 1
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL', 'LUTEAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ 恰含黄体期那条（过滤按条目自身持久化的所属周期，不按 period 数组） — 01a04883-f154-797c-93b8-05934aedea4b
- ✅ period 为 ["MENSTRUAL","LUTEAL"]，未被查询参数收窄为 ["LUTEAL"] — ['MENSTRUAL', 'LUTEAL']
