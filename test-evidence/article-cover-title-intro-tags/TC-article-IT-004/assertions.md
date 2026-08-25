# TC-article-IT-004 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置（栏目 A/B + 关联双栏目的文章）
- ✅ 三次创建状态码均 = 200

## Step 2 DELETE 栏目 A
- ✅ 状态码 = 200（物理删除）

## Step 3 栏目列表
- ✅ 状态码 = 200
- ✅ 列表已不含 A 的 id

## Step 4 文章详情
- ✅ 状态码 = 200（文章记录仍存在）
- ✅ categoryIds 仅剩 B（不含 A）——详情按「仍存在的栏目」过滤展示
- ✅ 响应 schema 契约未声明，跳过
