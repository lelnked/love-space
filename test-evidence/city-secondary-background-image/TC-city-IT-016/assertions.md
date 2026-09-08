# TC-city-IT-016 断言明细

| # | 断言 | 结果 |
|---|---|---|
| 1 | step 1 创建返回 200，两图字段均落库 | ✅ |
| 2 | step 2 PUT 返回 200 | ✅ |
| 3 | step 2 详情 `secondaryBackgroundImage.id` = `bound/bg-second-new-169523.webp`（提交 `images/bg-second-new-169523.webp`，validateAndBind 转为 bound/ 前缀，同 backgroundImage 口径） | ✅ |
| 4 | step 2 详情 `backgroundImage.id` 仍为 `bound/bg-primary-169523.png`（未被更新第二背景图波及） | ✅ |
| 5 | step 3 PUT `secondaryBackgroundImage: null` 返回 200 | ✅ |
| 6 | step 3 详情 `secondaryBackgroundImage` = null | ✅ |
| 7 | step 3 详情 `backgroundImage.id` 仍为 `bound/bg-primary-169523.png`（清空第二背景图不影响主背景图） | ✅ |
| 8 | `updatedAt` 随两次 PUT 递增，`createdAt` 不变 | ✅ |
| 9 | 契约核对 `api-spec.json#/paths/~1api~1admin~1cities~1{id}/put`：与实现一致 | ✅ |

结论：✅ 通过（9/9）
