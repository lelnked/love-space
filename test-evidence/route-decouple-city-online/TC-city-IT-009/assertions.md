# TC-city-IT-009 断言明细

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1 | 前置：城市 / 大使 / 路线创建 | 均 200 | 均 200 | ✅ |
| 2 | 2 | DELETE 城市 状态码 | 400 | 400 | ✅ |
| 3 | 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 4 | 2 | body `message` 为中文业务错误且提示先处理路线 | 含「路线」「先删除」语义 | "该城市下仍有路线，请先删除这些路线后再删除城市：01a01fb5-7729-…" | ✅ |
| 5 | 2 | body `error` / `status` | Bad Request / 400 | Bad Request / 400 | ✅ |
| 6 | 3 | GET 城市详情 状态码 | 200（未被删除） | 200 | ✅ |
| 7 | 3 | body 字段完整（chineseName/online 等） | "待删城009" / true | 一致 | ✅ |
| 8 | — | 契约 schema | api-spec.json 中 `/api/admin/cities/{id}` 无 `delete` operation | 无从比对 | ⚠️ 待补契约（用例已标注） |

首个失败点: 无
