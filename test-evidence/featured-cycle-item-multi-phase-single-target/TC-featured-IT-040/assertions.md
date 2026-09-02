# TC-featured-IT-040 断言明细

执行日期: 2026-09-02

- ✅ Step 2: phases=[] 空数组 返回 400（非 500）　— 实际 400: {"status":400,"error":"Bad Request","message":"投放周期不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 2: phases=[] 空数组 message 为中文业务错误（至少选择一个周期口径）　— '投放周期不能为空'
- ✅ Step 3: 完全不带 phases 字段 返回 400（非 500）　— 实际 400: {"status":400,"error":"Bad Request","message":"投放周期不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 3: 完全不带 phases 字段 message 为中文业务错误（至少选择一个周期口径）　— '投放周期不能为空'
- ✅ Step 4: phases=null 返回 400（非 500）　— 实际 400: {"status":400,"error":"Bad Request","message":"投放周期不能为空","path":"/api/admin/featured-cycle-items"}
- ✅ Step 4: phases=null message 为中文业务错误（至少选择一个周期口径）　— '投放周期不能为空'
- ✅ PUT 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"投放周期不能为空","path":"/api/admin/featured-cycle-items/01a0622d-611e-75f1-9fe0-a1fa565a8e12"}
- ✅ message 为中文业务错误　— '投放周期不能为空'
- ✅ 分页总数不变（未静默落库为空数组）　— 基线 1 实际 1
- ✅ 被 PUT 条目 phases 保持原值未被清空　— ['MENSTRUAL', 'OVULATION']

结论: ✅ 通过（10/10 断言通过）
