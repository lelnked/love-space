# TC-article-IT-014 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 前置：可见文章，contentHtml 含图片与文本

- ✅ 状态码 = 200（实际 200）

## Step 2 app 详情

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 含图片字段（签名 URL）
- ✅ 含标题、副标题
- ✅ contentHtml 文本与后台保存一致
- ✅ img src 已替换为签名 URL
