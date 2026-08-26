# TC-merchant-IT-009 断言明细

执行日期: 2026-08-26 ｜ 关联契约: ⚠️ 待补契约（api-spec.json 缺 /api/app/merchants/{merchantId}/reviews）

结果: ✅ 通过（6/6 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 状态码 200 | ✅ | `HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | body 为数组且含 2 条 | ✅ | `len=2` |
| 4 | 两条 sortOrder 同为 0（并列） | ✅ | `DB: 评A=0, 评B=0` |
| 5 | 同序号按 createdAt 倒序：后创建的 B 排在 A 之前 | ✅ | `['评B', '评A']` |
| 6 | 每项仅含 nickname/title/content | ✅ | `['content', 'nickname', 'title']` |

## 契约 schema 校验

- ⚠️ 用例「关联契约」标注为**待补契约**：`contracts/api-spec.json` 中不存在 `/api/app/merchants/{merchantId}/reviews` 条目，故本用例**跳过 schema 校验**，只做状态码与 body 字段断言（既有契约缺口，非本次执行失败项）。
- 未发现与现有契约条目的漂移。
