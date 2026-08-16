# TC-article-IT-003 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 2 更新栏目

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 请求体符合 ArticleCategoryUpsertRequest schema

## Step 3 列表确认

- ✅ 状态码 = 200（实际 200）
- ✅ 该栏目 name = 美食攻略
- ✅ 该栏目 sortOrder = 5
- ✅ icon 为新图签名 URL（bound/it003-icon-）
