# TC-featured-IT-042 断言明细

执行日期: 2026-09-02

- ✅ 前置条目创建 200　— 实际 200
- ✅ 置 online=false 返回 200
- ✅ 返回 400（唯一约束与上下线状态无关）　— 实际 400: {"status":400,"error":"Bad Request","message":"该文章已存在周期推荐","path":"/api/admin/featured-cycle-items"}
- ✅ message 为「该文章已存在周期推荐」　— '该文章已存在周期推荐'
- ✅ 删除占位条目返回 200　— 实际 200
- ✅ 重试返回 200（占位条目删除后该实体重新可用）　— 实际 200: {"id":"01a0622d-61b9-769d-b23c-b3a00b3edb69","phases":["FOLLICULAR"],"type":"ARTICLE","sortOrder":0,"online":false,"targetId":"01a0622d-6193-78fa-a44c-dd81610cfd3c","relatedTitle":"art-m9p21","title":
- ✅ 详情 phases 为 ["FOLLICULAR"]　— ['FOLLICULAR']

结论: ✅ 通过（7/7 断言通过）
