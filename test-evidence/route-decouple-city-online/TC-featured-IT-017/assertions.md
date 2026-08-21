# TC-featured-IT-017 断言明细（回归）

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

前置清理：删除历史遗留的周期推荐条目，四分组初始为空。

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1g | 前置：MENSTRUAL 含 ACTIVITY + ARTICLE 两条 | 2 条 | 2 条 | ✅ |
| 2 | 2b | 活动下线后 ACTIVITY 条目消失 | 不含 `…7d98…` | 不含 | ✅ |
| 3 | 2b | 未受影响的 ARTICLE 条目仍在 | 含 `…7dde…` | 含 | ✅ |
| 4 | 3c | 活动恢复上线但所属城市下架后，ACTIVITY 条目仍不下发 | 不含 | 不含 | ✅ |
| 5 | 3c | ARTICLE 条目仍在 | 含 | 含 | ✅ |
| 6 | 4c | 文章下线后 ARTICLE 条目消失 | 不含 `…7dde…` | 不含 | ✅ |
| 7 | 4c | 城市恢复上架后 ACTIVITY 条目重新出现 | 含 `…7d98…` | 含 | ✅ |
| 8 | 5b | 删除文章 状态码 | 200 | 200（空响应体） | ✅ |
| 9 | 5c | 关联文章被删后 ARTICLE 条目不下发 | 不含 | 不含 | ✅ |
| 10 | 全程 | 接口不因关联实体缺失报 5xx | 每次均 200 | 均 200 | ✅ |
| 11 | 全程 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 12 | — | 契约 schema | `/api/app/featured-cycle-items` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
