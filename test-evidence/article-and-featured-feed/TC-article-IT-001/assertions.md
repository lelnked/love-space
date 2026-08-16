# TC-article-IT-001 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 登录

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ token 为三段式 JWT

## Step 2 创建栏目

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 请求体符合 ArticleCategoryUpsertRequest schema（name/icon 必填，sortOrder integer）
- ✅ name = 行程攻略
- ✅ icon 为签名 URL（http 开头）
- ✅ icon 非裸 objectKey（url 指向 bound key）
- ✅ sortOrder = 1

## Step 3 列表确认

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 列表含新建栏目 01a00b98-4acc-787f-9be2-c8d843d7ec05
- ✅ 响应 schema 契约未声明，跳过
