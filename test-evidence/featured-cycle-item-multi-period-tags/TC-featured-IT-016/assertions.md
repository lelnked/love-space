# TC-featured-IT-016 断言明细

结果: ✅ 通过

契约: `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 responses/response schema（⚠️ 已知契约缺口，见 change 的 test-cases.md「待补契约」），body schema 层无契约可依，按 delta spec `openspec/changes/featured-cycle-item-multi-period-tags/specs/featured/spec.md` 断言响应体。

- ✅ 返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ 响应顶层为 JSON 数组 — list
- ✅ 数组恰含 2 条 — 2
- ✅ period 为 JSON 数组而非字符串 — ['OVULATION']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ 无 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL 分组键（响应非对象） — list
- ✅ ACTIVITY 条目 period=["MENSTRUAL"]、type=ACTIVITY、targetId 等于该活动 id — ['MENSTRUAL']
- ✅ ARTICLE 条目 period=["OVULATION"]、type=ARTICLE、targetId 等于该文章 id — ['OVULATION']
- ✅ 每条 banner 为 http 开头签名 URL（非裸 objectKey） — ['http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bou', 'http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bou']
- ✅ 不含 LUTEAL 的下线条目 — 2
