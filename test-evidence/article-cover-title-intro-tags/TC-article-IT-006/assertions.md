# TC-article-IT-006 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 缺 title
- ✅ 状态码 = 400，message =「文章标题不能为空」

## Step 2 缺 image
- ✅ 状态码 = 400，message =「文章图片不能为空」

## Step 3 categoryIds 含不存在 UUID
- ✅ 状态码 = 400，message =「关联栏目不存在：00000000-0000-4000-8000-000000000999」

## Step 4 分页列表
- ✅ 状态码 = 200
- ✅ 三次提交的文章均未创建（列表中无对应 title）
- ✅ 响应 schema 契约未声明，跳过
