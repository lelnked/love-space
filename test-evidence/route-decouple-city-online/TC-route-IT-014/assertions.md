# TC-route-IT-014 断言明细（回归）

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 2 | 状态码 | 200 | 200 | ✅ |
| 2 | 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 3 | 2 | `images` 为签名 URL 列表 | 2 张，均带 Signature | 2 张 | ✅ |
| 4 | 2 | `spots` 顺序 | S1→S2 | "S1 江畔步道"→"S2 咖啡小馆" | ✅ |
| 5 | 2 | 每个 spot 含 name / image(签名 URL) / introduction | 均非空 | 均非空 | ✅ |
| 6 | 2 | `ambassador.name` | "路线大使014" | 一致 | ✅ |
| 7 | 2 | `ambassador.avatar.url` 为签名 URL | 带 Signature | 是 | ✅ |
| 8 | 2 | `ambassador.tags` 顺序 | ["向导","咖啡"] | 一致 | ✅ |
| 9 | 2 | 新增字段 `cityName` 不破坏既有字段 | 既有字段齐全 | 齐全，`cityName`="详情城014" | ✅ |
| 10 | — | 契约 schema | `/api/app/routes/{id}` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
