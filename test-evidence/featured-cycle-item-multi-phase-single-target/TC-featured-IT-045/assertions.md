# TC-featured-IT-045 断言明细

执行日期: 2026-09-02

- ✅ 返回 200
- ✅ content 含全部 3 条（不做周期过滤）　— ['01a0622d-6276-7a95-8c1f-cdfdbd1bf6c9', '01a0622d-627d-77c5-a7c9-d15ebc82c254', '01a0622d-6271-727d-8637-31ad84277b22']
- ✅ 每项均带 phases 数组字段
- ✅ 多周期那条 phases 为 ["FOLLICULAR","LUTEAL"]（枚举声明顺序）　— ['FOLLICULAR', 'LUTEAL']
- ✅ 按 sortOrder 升序、同 sortOrder 按创建时间倒序　— ['01a0622d-6276-7a95-8c1f-cdfdbd1bf6c9', '01a0622d-627d-77c5-a7c9-d15ebc82c254', '01a0622d-6271-727d-8637-31ad84277b22']

结论: ✅ 通过（5/5 断言通过）
