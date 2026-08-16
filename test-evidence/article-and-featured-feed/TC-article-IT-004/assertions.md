# TC-article-IT-004 断言明细（2026-08-16 复测，修复后）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。请求体已按 ArticleCategoryUpsertRequest / ArticleUpsertRequest 自检通过。

## Step 1-3 前置：栏目 A、B 与关联双栏目的文章

- ✅ 创建栏目 A 状态码 = 200（实际 200）
- ✅ 创建栏目 B 状态码 = 200（实际 200）
- ✅ 创建文章状态码 = 200（实际 200），categoryIds = [A, B]

## Step 4 删除栏目 A

- ✅ 状态码 = 200（实际 200，物理删除）

## Step 5 栏目列表确认

- ✅ 状态码 = 200（实际 200），Content-Type 含 application/json
- ✅ 栏目 A（01a00b9d-70f6-754b-8cfe-c7f5225c9071）已不在列表
- ✅ 栏目 B（01a00b9d-7133-7997-9046-7d373253476e）仍在列表

## Step 6 文章详情确认（本次修复验证点）

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 文章记录仍存在（id = 01a00b9d-7168-7db8-b8b9-834757b8e32b，title/online/sortOrder 未变）
- ✅ 关联栏目仅剩 B：categoryIds = ["01a00b9d-7133-7997-9046-7d373253476e"]，不再含已删除的 A（上轮失败点，已修复）

## Step 7 存储不回写核验

- ✅ test 库 loves_article.category_ids 仍为 [A, B]（存储未回写，过滤仅在查询端）

## 契约 schema

- ⏭️ 响应 schema：契约未声明，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
