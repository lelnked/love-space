# TC-city-IT-001 断言明细（回归）

| # | 断言 | 结果 |
|---|---|---|
| 1 | 登录返回 200，token 为三段式 JWT | ✅ |
| 2 | 创建返回 200 | ✅ |
| 3 | 详情返回 200，`editorNote` 与提交值逐字一致 | ✅ |
| 4 | 不提交任何图片字段时 `backgroundImage` 与新增的 `secondaryBackgroundImage` 均为 null，新列未破坏既有创建路径 | ✅ |

结论：✅ 通过（4/4）
