# TC-featured-IT-027 断言明细

执行日期: 2026-09-02

- ✅ 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"Invalid value for parameter 'period': UNKNOWN","path":"/api/app/featured-cycle-items"}
- ✅ 返回 400，不静默忽略该参数　— 实际 400: {"status":400,"error":"Bad Request","message":"Invalid value for parameter 'period': menstrual","path":"/api/app/featured-cycle-items"}

结论: ✅ 通过（2/2 断言通过）
