# TC-route-IT-012 断言明细（回归）

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1 | 前置：上架城市 + 上线大使 + sortOrder 5/1/3 三条路线创建 | 均 200 | 均 200 | ✅ |
| 2 | 2 | 状态码 | 200 | 200 | ✅ |
| 3 | 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 4 | 2 | 返回该城市全部可见路线 | 3 条 | 3 条 | ✅ |
| 5 | 2 | 按 sortOrder 升序 | 1→3→5 | 1→3→5 | ✅ |
| 6 | 2 | 每项含 thumbnail 签名 URL | 非空且带 Signature | 3 项均有 | ✅ |
| 7 | 2 | 每项含主标题 title | 非空 | 排序路线012-1/3/5 | ✅ |
| 8 | 2 | 每项含 ambassadorName | "路线大使012" | 一致 | ✅ |
| 9 | — | 契约 schema | `/api/app/routes` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
