# TC-article-IT-007 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 前置：栏目 A、B 与关联 [A] 的文章

- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）

## Step 2 更新文章

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 请求体符合 ArticleUpsertRequest schema

## Step 3 详情确认

- ✅ 状态码 = 200（实际 200）
- ✅ title = 更新后标题
- ✅ subtitle = 更新后副标题
- ✅ sortOrder = 9
- ✅ 关联栏目变为仅 B
