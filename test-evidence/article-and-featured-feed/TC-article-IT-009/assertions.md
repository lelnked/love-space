# TC-article-IT-009 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 前置：一篇文章

- ✅ 状态码 = 200（实际 200）

## Step 2 删除

- ✅ 状态码 = 200（实际 200）

## Step 3 再查详情

- ✅ 状态码 = 400（实际 400）
- ✅ 响应头 Content-Type 含 application/json
- ✅ message 为中文业务错误（资源不存在口径）

## Step 4 分页确认

- ✅ 状态码 = 200（实际 200）
- ✅ 分页列表不再出现该文章
