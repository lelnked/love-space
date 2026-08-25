# TC-article-IT-007 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置（栏目 A/B + 关联 [A] 的文章）
- ✅ 状态码均 = 200

## Step 2 PUT 更新
- ✅ 状态码 = 200
- ✅ 请求体符合 ArticleUpsertRequest

## Step 3 详情校验
- ✅ 状态码 = 200
- ✅ title = IT007新标题 / subtitle = 新副标题 / sortOrder = 9
- ✅ categoryIds 仅剩 B
- ✅ 响应 schema 契约未声明，跳过
