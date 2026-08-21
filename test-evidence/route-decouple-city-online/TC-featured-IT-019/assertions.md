# TC-featured-IT-019 断言明细（回归）

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

前置清理：删除历史遗留的周期推荐条目，四分组初始为空。
夹具：MENSTRUAL 下按 sortOrder 2、1、3 顺序建三条，再建两条 sortOrder=1（1A 先、1B 后），共 5 条，故 sortOrder=1 者共 3 条。

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1 | 5 个条目创建 | 均 200 | 均 200 | ✅ |
| 2 | 2 | 状态码 / Content-Type | 200 / application/json | 200 / application/json | ✅ |
| 3 | 2 | MENSTRUAL 条目数 | 5 | 5 | ✅ |
| 4 | 2 | 按 sortOrder 升序 | 1,1,1,2,3 | 1,1,1,2,3 | ✅ |
| 5 | 2 | sortOrder 并列时按 createdAt 倒序（后创建的在前） | 1B → 1A → 排序条目-1 | 并列条目-1B、并列条目-1A、排序条目-1 | ✅ |
| 6 | — | 契约 schema | `/api/app/featured-cycle-items` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
