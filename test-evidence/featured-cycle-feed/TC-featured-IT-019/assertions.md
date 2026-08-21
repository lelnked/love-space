# TC-featured-IT-019 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1app~1featured-cycle-items/get`：无请求体/无参数；契约未声明响应 schema，记跳过。

注：用例原文写「另建两个 sortOrder 同为 1 的条目」，与步骤 1 已有的 sortOrder=1 条目合计为 3 个同序号条目，故排序断言按 1、1、1、2、3 核对，同序号内按 createdAt 倒序。

- ✅ 前置：按 sortOrder 2(A)、1(B)、3(C) 顺序创建三条，另建两条 sortOrder=1（D、E，E 最后创建）
- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ MENSTRUAL 分组共 5 条（实际 5）
- ✅ 组内按 sortOrder 升序 1、1、1、2、3（实际 [1, 1, 1, 2, 3]，条目标记 ['E', 'D', 'B', 'A', 'C']）
- ✅ 三个 sortOrder=1 的条目按 createdAt 倒序（后创建的在前）：期望 E、D、B（实际 ['E', 'D', 'B']）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
