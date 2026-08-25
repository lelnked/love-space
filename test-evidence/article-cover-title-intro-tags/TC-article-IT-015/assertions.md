# TC-article-IT-015 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 登录
- ✅ 状态码 = 200
- ✅ token 为三段式 JWT

## Step 2 前置栏目
- ✅ 状态码 = 200

## Step 3 创建带 coverTitle/intro/tags 的文章
- ✅ 状态码 = 200，Content-Type 含 application/json
- ✅ 请求体符合 ArticleUpsertRequest（coverTitle/intro string，tags string 数组）

## Step 4 GET 详情
- ✅ 状态码 = 200
- ✅ title = 详情页标题
- ✅ coverTitle = 封面标题（返回原值，不回落）
- ✅ subtitle = 副标题
- ✅ intro = 这是引言
- ✅ tags = ["约会","周末"]（顺序保持）
- ✅ 五个字段互不覆盖
- ✅ 响应 schema 契约未声明，跳过
