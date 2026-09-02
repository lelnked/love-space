# TC-featured-IT-044 断言明细

执行日期: 2026-09-02

- ✅ 返回 400（非 500）　— 实际 400: {"status":400,"error":"Bad Request","message":"该活动已存在周期推荐","path":"/api/admin/featured-cycle-items/01a0622d-6237-733d-85f4-0425587ec109"}
- ✅ message 为「该活动已存在周期推荐」　— '该活动已存在周期推荐'
- ✅ CA 的 targetId 仍为活动 A
- ✅ CA 其余字段未被部分写入（更新整体回滚）　— phases=['MENSTRUAL'] description=CA 文案
- ✅ CB 不受影响

结论: ✅ 通过（5/5 断言通过）
