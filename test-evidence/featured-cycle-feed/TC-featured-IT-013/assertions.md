# TC-featured-IT-013 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-cycle-items~1page/get`：query 参数 phase∈Period、type∈FeaturedCycleItemType、page/size 为 integer，本用例使用 phase 与 size，符合声明。响应 schema 未声明，记跳过。

- ✅ 前置：FOLLICULAR 3 条（sortOrder 2/1/3 顺序创建）+ MENSTRUAL 1 条
- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ content 仅含 3 条（实际 3）
- ✅ content 全部为 FOLLICULAR（实际 ['FOLLICULAR', 'FOLLICULAR', 'FOLLICULAR']）
- ✅ sortOrder 依次为 1、2、3（实际 [1, 2, 3]）
- ✅ 不带 phase 状态码 = 200（实际 200）
- ✅ 返回全部 4 条（实际 totalElements=4，content=4）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅

## ⚠️ 契约漂移（不判失败，供人工确认）

`GET /api/admin/featured-cycle-items/page` 的 `size` query 参数在契约中声明为无约束 `integer`，
实测被服务端夹逼到 [20,30]：`size=1`/`size=2` → 响应 `size=20`，`size=100` → 响应 `size=30`。
本用例断言不依赖分页大小（数据量 4 条 < 20），故不影响结论；建议在 api-spec.json 中补 minimum/maximum
或在 spec 中明确夹逼口径。
