# TC-article-IT-005 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 前置：栏目 A、B

- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）

## Step 2 创建完整文章

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 请求体符合 ArticleUpsertRequest schema（image/title 必填）

## Step 3 详情确认

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ image 为签名 URL
- ✅ title = 海岛两日游
- ✅ subtitle = 附完整行程
- ✅ sortOrder = 1
- ✅ online = true
- ✅ 关联栏目为 A、B 两个
- ✅ contentHtml 文本原样保存
