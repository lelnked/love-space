# TC-article-IT-010 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 创建含 2 img 的富文本文章

- ✅ 状态码 = 200（实际 200）

## Step 2 详情确认签名替换

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ contentHtml 文本部分与提交一致
- ✅ 2 个 img src 均为签名 URL（http 开头）
- ✅ 无裸 objectKey src（存储层已存 bound objectKey，读时替换）

## Step 3 无 img 的 HTML 原样往返

- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）
- ✅ 无 img 的 HTML 原样往返
