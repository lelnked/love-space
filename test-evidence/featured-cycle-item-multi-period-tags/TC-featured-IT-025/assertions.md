# TC-featured-IT-025 断言明细

结果: ✅ 通过

契约: `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 responses/response schema（⚠️ 已知契约缺口，见 change 的 test-cases.md「待补契约」），body schema 层无契约可依，按 delta spec `openspec/changes/featured-cycle-item-multi-period-tags/specs/featured/spec.md` 断言响应体。

- ✅ 返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ 响应顶层为 JSON 数组 — list
- ✅ 数组恰含 1 条 — 1
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ period 为 ["MENSTRUAL"]（该文章只配在经期） — ['MENSTRUAL']
- ✅ type=ARTICLE — ARTICLE
- ✅ targetId 等于该文章 id — 01a04883-f090-7c6d-8846-5f06f011fd52
- ✅ 响应无 articleId 字段 — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ 不含 MENSTRUAL 的 ACTIVITY 条目，也不含 FOLLICULAR 的 ARTICLE 条目 — 1
