# TC-featured-IT-016 断言明细（回归）

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

前置清理：删除历史遗留的周期推荐条目，四分组初始为空。

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1 | 夹具创建（城市/活动/栏目/文章/三个条目） | 均 200 | 均 200 | ✅ |
| 2 | 2 | 状态码 / Content-Type | 200 / application/json | 200 / application/json | ✅ |
| 3 | 2 | 四个分组键齐全 | MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL | 四键齐全 | ✅ |
| 4 | 2 | FOLLICULAR 为空数组（不缺键） | `[]` | `[]` | ✅ |
| 5 | 2 | MENSTRUAL 1 条，type=ACTIVITY | 1 条 | 1 条，ACTIVITY | ✅ |
| 6 | 2 | MENSTRUAL 条目含 banner 签名 URL 与 `activityId` | 非空 | 均非空 | ✅ |
| 7 | 2 | OVULATION 1 条，type=ARTICLE，含 `articleId` | 1 条 | 1 条，ARTICLE，articleId 匹配 | ✅ |
| 8 | 2 | LUTEAL 为空数组（下线条目不下发） | `[]` | `[]` | ✅ |
| 9 | — | 契约 schema | `/api/app/featured-cycle-items` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
