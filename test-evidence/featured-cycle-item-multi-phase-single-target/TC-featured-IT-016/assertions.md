# TC-featured-IT-016 断言明细

执行日期: 2026-09-02

- ✅ 返回 200　— 实际 200
- ✅ 响应顶层为 JSON 数组（非按周期分组的对象）　— list
- ✅ 数组恰含 2 条　— 实际 2
- ✅ ACTIVITY 条目 period=["MENSTRUAL"]、targetId=活动 id　— ['MENSTRUAL']
- ✅ ARTICLE 条目 period=["OVULATION"]、targetId=文章 id　— ['OVULATION']
- ✅ period 字段名与形状不变（数组）
- ✅ 响应不再出现 activityId/routeId/articleId
- ✅ 每条 banner 为签名 URL
- ✅ 不含下线条目

结论: ✅ 通过（9/9 断言通过）
