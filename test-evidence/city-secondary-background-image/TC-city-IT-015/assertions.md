# TC-city-IT-015 断言明细

| # | 断言 | 结果 |
|---|---|---|
| 1 | step 2 POST /api/admin/cities 状态码 = 200 | ✅ |
| 2 | 响应头 Content-Type 含 application/json | ✅ |
| 3 | step 3 GET 详情状态码 = 200 | ✅ |
| 4 | 详情 `secondaryBackgroundImage` 为对象且含 `id`/`url` 两字段 | ✅ |
| 5 | 详情 `secondaryBackgroundImage.id` = 提交值 `bound/bg-second-92331558.jpg` | ✅ |
| 6 | 详情 `secondaryBackgroundImage.url` 为该 objectKey 的签名地址（含 Expires/Signature） | ✅ |
| 7 | 详情 `backgroundImage.id` = 提交值 `bound/bg-primary-92331558.png`（未被第二背景图覆盖） | ✅ |
| 8 | 两字段的 `id` 互不相同，互不覆盖 | ✅ |
| 9 | 契约核对 `api-spec.json#/paths/~1api~1admin~1cities/post`：summary 已声明 secondaryBackgroundImage 为可空 OSS objectKey，与实现一致 | ✅ |

结论：✅ 通过（9/9）
