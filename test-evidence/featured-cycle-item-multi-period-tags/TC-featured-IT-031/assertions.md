# TC-featured-IT-031 断言明细

结果: ✅ 通过

契约: `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 responses/response schema（⚠️ 已知契约缺口，见 change 的 test-cases.md「待补契约」），body schema 层无契约可依，按 delta spec `openspec/changes/featured-cycle-item-multi-period-tags/specs/featured/spec.md` 断言响应体。

- ✅ 返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ 响应顶层为 JSON 数组 — list
- ✅ 数组恰含 1 条 — 1
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ Step 2: 数组仅含经期那条 — 01a04883-f1d9-77bf-9423-c225fcf150f9
- ✅ Step 2: period 为 ["MENSTRUAL"]（下线条目所属的 LUTEAL 不计入） — ['MENSTRUAL']
- ✅ 返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ 响应顶层为 JSON 数组 — list
- ✅ 数组恰含 2 条 — 2
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL', 'LUTEAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL', 'LUTEAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ Step 3: 两条 period 均为 ["MENSTRUAL","LUTEAL"]（标签随可见性变化） — [['MENSTRUAL', 'LUTEAL'], ['MENSTRUAL', 'LUTEAL']]
- ✅ Step 4: 接口仍返回 200 — 200
- ✅ Step 4: 数组不含 A 的任何条目 — 0
