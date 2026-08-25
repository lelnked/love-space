# TC-article-IT-001 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 登录
- ✅ 状态码 = 200
- ✅ Content-Type 含 application/json
- ✅ token 为三段式 JWT

## Step 2 创建栏目
- ✅ 状态码 = 200
- ✅ Content-Type 含 application/json
- ✅ 请求体符合 ArticleCategoryUpsertRequest（name/icon 必填，sortOrder integer）
- ✅ name = 行程攻略
- ✅ icon.url 为签名 URL（http 开头），非裸 objectKey（id=bound/it001-icon.png）
- ✅ sortOrder = 1

## Step 3 列表确认
- ✅ 状态码 = 200
- ✅ 列表含新建栏目 id
- ✅ 响应 schema 契约未声明，跳过
