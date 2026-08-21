# TC-city-IT-010 断言明细

执行日期: 2026-08-20 ｜ 结果: ✅ 通过
（承接 TC-city-IT-009 的城市 `01a01fb5-7729-784e-a42d-c718c64ce655` 与路线 `01a01fb5-77a2-7512-87b1-1dda01ba1f0e`）

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 2 | DELETE 路线 状态码 | 200 | 200（空响应体） | ✅ |
| 2 | 3 | DELETE 城市 状态码 | 200（无路线引用后放行） | 200（空响应体） | ✅ |
| 3 | 4 | GET 已删城市 状态码 | 400 | 400 | ✅ |
| 4 | 4 | body `message` 中文业务错误（admin 端「资源不存在」口径） | 中文 | "城市不存在：01a01fb5-7729-…" | ✅ |
| 5 | 4 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 6 | — | 契约 schema | api-spec.json 中 `/api/admin/cities/{id}` 无 `delete` operation | 无从比对 | ⚠️ 待补契约（用例已标注） |

首个失败点: 无
