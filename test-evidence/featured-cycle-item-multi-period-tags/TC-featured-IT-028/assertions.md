# TC-featured-IT-028 断言明细

结果: ✅ 通过

契约: `api-spec.json#/paths/~1api~1app~1featured-cycle-items/get` 未声明 responses/response schema（⚠️ 已知契约缺口，见 change 的 test-cases.md「待补契约」），body schema 层无契约可依，按 delta spec `openspec/changes/featured-cycle-item-multi-period-tags/specs/featured/spec.md` 断言响应体。说明: 用例文本写「两条的 banner 与主标题填不同值」，但 ACTIVITY 类型条目按既有设计不承载 title/subtitle（TC-featured-IT-007 已断言 ACTIVITY 条目 title/subtitle 为 null），故本条按 delta spec 的原文「两条的 banner 与**文案**仍为各自条目的配置」断言 banner + description，等价覆盖「互不串写」意图。

- ✅ 返回 200 — 200
- ✅ Content-Type 含 application/json — application/json
- ✅ 响应顶层为 JSON 数组 — list
- ✅ 数组恰含 2 条 — 2
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL', 'LUTEAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ period 为 JSON 数组而非字符串 — ['MENSTRUAL', 'LUTEAL']
- ✅ 条目不含 activityId/routeId/articleId — ['banner', 'description', 'id', 'note', 'period', 'subtitle', 'targetId', 'title', 'type']
- ✅ 数组含这两条条目（不去重、不合并，条目粒度不变） — 2
- ✅ 两条 period 均为 ["MENSTRUAL","LUTEAL"]（按枚举声明顺序、去重无重复项） — [['MENSTRUAL', 'LUTEAL'], ['MENSTRUAL', 'LUTEAL']]
- ✅ period 元素顺序符合 Period 枚举声明顺序 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL — [['MENSTRUAL', 'LUTEAL'], ['MENSTRUAL', 'LUTEAL']]
- ✅ 经期条目的 banner 与文案等于其自身配置（未串写） — ('经期描述', 'http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b028-m.png?Expires=1787924834&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE')
- ✅ 黄体期条目的 banner 与文案等于其自身配置（未串写） — ('黄体期描述', 'http://love-space-test-0524.oss-cn-hangzhou.aliyuncs.com/bound/b028-l.png?Expires=1787924834&OSSAccessKeyId=$OSS_ACCESS_KEY_ID&Signature=$OSS_SIGNATURE')
