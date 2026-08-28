# TC-featured-IT-017 断言明细

结果: ✅ 通过

契约: `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 responses/response schema（⚠️ 已知契约缺口，见 change 的 test-cases.md「待补契约」），body schema 层无契约可依，按 delta spec `openspec/changes/featured-cycle-item-multi-period-tags/specs/featured/spec.md` 断言响应体。

- ✅ 返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ 响应顶层为 JSON 数组 — list
- ✅ 数组恰含 2 条 — 2
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ Step 1: 两条 period 均为 ["MENSTRUAL"] — [['MENSTRUAL'], ['MENSTRUAL']]
- ✅ Step 2: 返回 200（不因关联实体不可见报 500） — 200
- ✅ Step 2: 数组不含该 ACTIVITY 条目 — 1
- ✅ Step 2: 未受影响的 ARTICLE 条目仍在数组中 — 1
- ✅ Step 3: 返回 200 — 200
- ✅ Step 3: 该 ACTIVITY 条目重新出现 — 2
- ✅ Step 4: 返回 200 — 200
- ✅ Step 4: 数组不含该 ARTICLE 条目 — 1
- ✅ Step 4: 未受影响的 ACTIVITY 条目仍在数组中 — 1
- ✅ Step 5: 返回 200（关联实体被删不报 500） — 200
- ✅ Step 5: 数组不含该 ARTICLE 条目 — 1
- ✅ Step 5: 未受影响的 ACTIVITY 条目仍在数组中 — 1
