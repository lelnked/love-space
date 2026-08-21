# TC-featured-IT-011 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-cycle-items/post`：本用例刻意提交不合规请求体以验证服务端分派校验，请求体自检记为「有意违规」。响应 schema 契约未声明，记跳过。

- ✅ 前置分页 状态码 = 200，totalElements = 0
- ✅ type=ACTIVITY activityId 不存在 → 状态码 = 400（实际 400）
- ✅ type=ACTIVITY activityId 不存在 → message 为中文业务错误：'关联活动不存在：6986f09c-3a75-49ef-b31c-0108c07b4f3a'
- ✅ type=ACTIVITY activityId 不存在 → message 指出关联实体不存在（含「不存在」）
- ✅ type=ROUTE routeId 不存在 → 状态码 = 400（实际 400）
- ✅ type=ROUTE routeId 不存在 → message 为中文业务错误：'关联路线不存在：6986f09c-3a75-49ef-b31c-0108c07b4f3a'
- ✅ type=ROUTE routeId 不存在 → message 指出关联实体不存在（含「不存在」）
- ✅ type=ARTICLE articleId 不存在 → 状态码 = 400（实际 400）
- ✅ type=ARTICLE articleId 不存在 → message 为中文业务错误：'关联文章不存在：6986f09c-3a75-49ef-b31c-0108c07b4f3a'
- ✅ type=ARTICLE articleId 不存在 → message 指出关联实体不存在（含「不存在」）
- ✅ 条目均未创建：totalElements 仍为 0（实际 0）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
