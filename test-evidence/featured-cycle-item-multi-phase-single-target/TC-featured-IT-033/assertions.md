# TC-featured-IT-033 断言明细

执行日期: 2026-09-02

- ✅ Step 2: ACTIVITY 无 targetId 返回 400（非 500）　— 实际 400: {"status":400,"error":"Bad Request","message":"关联实体不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 2: ACTIVITY 无 targetId message 为中文业务错误　— '关联实体不能为空'
- ✅ Step 3: ROUTE 无 targetId 返回 400（非 500）　— 实际 400: {"status":400,"error":"Bad Request","message":"关联实体不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 3: ROUTE 无 targetId message 为中文业务错误　— '关联实体不能为空'
- ✅ Step 4: ARTICLE 无 targetId 返回 400（非 500）　— 实际 400: {"status":400,"error":"Bad Request","message":"关联实体不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 4: ARTICLE 无 targetId message 为中文业务错误　— '关联实体不能为空'
- ✅ PUT 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"关联实体不能为空","path":"/api/admin/featured-cycle-items/01a0622d-60a7-70f6-87db-67fbee57ff1b"}
- ✅ message 为中文业务错误　— '关联实体不能为空'
- ✅ 分页总数不变（三次创建均未落库）　— 基线 1 实际 1
- ✅ targetId 未被清空
- ✅ phases 保持原值未被清空　— ['MENSTRUAL', 'LUTEAL']
- ✅ 其余字段保持原值

结论: ✅ 通过（12/12 断言通过）
