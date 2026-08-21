# TC-featured-IT-018 断言明细

执行日期: 2026-08-20 ｜ 结果: ✅ 通过

前置清理：删除历史遗留的周期推荐条目，四分组初始为空。

| # | 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|---|
| 1 | 1 | 上架城市 + 路线（大使 online=true）+ OVULATION 上线 ROUTE 条目创建 | 均 200 | 均 200 | ✅ |
| 2 | 2 | 状态码 / Content-Type | 200 / application/json | 200 / application/json | ✅ |
| 3 | 2 | 条目在 OVULATION 分组 | 含 `01a01fba-7092-…` | 含 | ✅ |
| 4 | 3a | 大使下线 状态码 / online | 200 / false | 200 / false | ✅ |
| 5 | 3b | 状态码 | 200 | 200 | ✅ |
| 6 | 3b | 条目从 OVULATION 消失 | 不含 | OVULATION 为 `[]` | ✅ |
| 7 | 4a | 恢复大使上线 状态码 / online | 200 / true | 200 / true | ✅ |
| 8 | 4b | 条目重新出现在 OVULATION | 含 `01a01fba-7092-…` | 含 | ✅ |
| 9 | 全程 | 接口不因关联实体不可见报 5xx | 均 200 | 均 200 | ✅ |
| 10 | — | 契约 schema | `/api/app/featured-cycle-items` 无 responses schema | 无从比对 | ⚠️ 待补契约 |

首个失败点: 无
