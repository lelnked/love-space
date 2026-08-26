# TC-merchant-IT-007 断言明细

执行日期: 2026-08-26 ｜ 关联契约: ⚠️ 待补契约（api-spec.json 缺 /api/app/categories/page）

结果: ✅ 通过（5/5 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | body 为分页对象（content/page/size/totalElements/totalPages） | ✅ | `['content', 'page', 'size', 'totalElements', 'totalPages']` |
| 4 | 分类 A、B 均在结果中 | ✅ | `idx(分B)=0, idx(分A)=1` |
| 5 | 同 sortOrder=0 时后创建的 B 排在先创建的 A 之前 | ✅ | `['分B021653', '分A021653', '类-7a9988f5-4d17-41c3-863b-970dc2234936']` |

## 契约 schema 校验

- ⚠️ 用例「关联契约」标注为**待补契约**：`contracts/api-spec.json` 中不存在 `/api/app/categories/page` 条目，故本用例**跳过 schema 校验**，只做状态码与 body 字段断言（既有契约缺口，非本次执行失败项）。
- 未发现与现有契约条目的漂移。
