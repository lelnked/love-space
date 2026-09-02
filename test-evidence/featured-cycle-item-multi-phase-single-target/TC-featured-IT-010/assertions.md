# TC-featured-IT-010 断言明细

执行日期: 2026-09-02

- ✅ Step 2: ROUTE 缺 subtitle 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"副标题不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 2: ROUTE 缺 subtitle message 为中文业务错误　— '副标题不能为空'
- ✅ Step 3: ACTIVITY 缺 description 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"推荐说明不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 3: ACTIVITY 缺 description message 为中文业务错误　— '推荐说明不能为空'
- ✅ Step 4: ARTICLE 缺 banner 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"banner 图片不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 4: ARTICLE 缺 banner message 为中文业务错误　— 'banner 图片不能为空'
- ✅ Step 5: 缺 type 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"内容类型不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 5: 缺 type message 为中文业务错误　— '内容类型不能为空'
- ✅ 条目均未创建（分页总数不变 0）　— 实际 0

结论: ✅ 通过（9/9 断言通过）
