# TC-featured-IT-041 断言明细

执行日期: 2026-09-02

- ✅ 首条创建返回 200　— 实际 200: {"id":"01a0622d-615c-7a2f-95ea-cf9f3f6ca637","phases":["MENSTRUAL"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622d-6144-7a53-8649-a2ac06a50c61","relatedTitle":"act-m9p12","title":
- ✅ 返回 400（非 500）　— 实际 400: {"status":400,"error":"Bad Request","message":"该活动已存在周期推荐","path":"/api/admin/featured-cycle-items"}
- ✅ message 为「该活动已存在周期推荐」　— '该活动已存在周期推荐'
- ✅ 返回 400　— 实际 400
- ✅ message 为「该路线已存在周期推荐」　— '该路线已存在周期推荐'
- ✅ 返回 400　— 实际 400
- ✅ message 为「该文章已存在周期推荐」　— '该文章已存在周期推荐'
- ✅ 分页总数不变，重复条目均未新增　— 基线 3 实际 3

结论: ✅ 通过（8/8 断言通过）
