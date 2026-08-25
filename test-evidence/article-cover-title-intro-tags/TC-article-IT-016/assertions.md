# TC-article-IT-016 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 创建（只含 image/title/categoryIds/online）
- ✅ 状态码 = 200
- ✅ 请求体符合 ArticleUpsertRequest（coverTitle/intro/tags 均为可选）

## Step 2 GET 详情
- ✅ 状态码 = 200
- ✅ coverTitle = null（admin 详情不回落 title）
- ✅ intro = null
- ✅ tags = []，jq type = array（空数组而非 null）

## Step 3 GET /api/admin/articles/page 列表项
- ✅ 状态码 = 200
- ✅ coverTitle = null（admin 列表不回落）
- ✅ intro = null
- ✅ tags = []，type = array
- ✅ 列表项字段集 = [categoryIds,coverTitle,createdAt,id,image,intro,online,sortOrder,subtitle,tags,title,updatedAt]
- ✅ 响应 schema 契约未声明，跳过
