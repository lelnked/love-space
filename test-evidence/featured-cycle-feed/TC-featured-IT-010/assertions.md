# TC-featured-IT-010 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-cycle-items/post`：本用例刻意提交不合规请求体以验证服务端分派校验，请求体自检记为「有意违规」。响应 schema 契约未声明，记跳过。

- ✅ 前置分页 状态码 = 200，totalElements = 0
- ✅ type=ROUTE 缺 subtitle → 状态码 = 400（实际 400）
- ✅ type=ROUTE 缺 subtitle → message 为中文业务错误：'副标题不能为空'
- ✅ type=ACTIVITY 缺 description → 状态码 = 400（实际 400）
- ✅ type=ACTIVITY 缺 description → message 为中文业务错误：'推荐说明不能为空'
- ✅ type=ARTICLE 缺 banner → 状态码 = 400（实际 400）
- ✅ type=ARTICLE 缺 banner → message 为中文业务错误：'banner 图片不能为空'
- ✅ 缺 phase → 状态码 = 400（实际 400）
- ✅ 缺 phase → message 为中文业务错误：'所属周期不能为空'
- ✅ 缺 type → 状态码 = 400（实际 400）
- ✅ 缺 type → message 为中文业务错误：'内容类型不能为空'
- ✅ 条目均未创建：totalElements 仍为 0（实际 0）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
