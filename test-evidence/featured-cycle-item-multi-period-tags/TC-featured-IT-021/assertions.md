# TC-featured-IT-021 断言明细

结果: ✅ 通过

契约: `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 responses/response schema（⚠️ 已知契约缺口，见 change 的 test-cases.md「待补契约」），body schema 层无契约可依，按 delta spec `openspec/changes/featured-cycle-item-multi-period-tags/specs/featured/spec.md` 断言响应体。

- ✅ 返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ 响应顶层为 JSON 数组 — list
- ✅ 数组恰含 1 条 — 1
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ Step 2: 仅含该 ARTICLE 条目，period=["MENSTRUAL"]、targetId 等于该文章 id — ['MENSTRUAL']
- ✅ Step 2: 响应无 articleId 字段 — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ Step 2: 不含 ACTIVITY/ROUTE 条目 — 1
- ✅ Step 3: 返回 200 且数组含全部 3 条（不传 type 行为不变） — 3
