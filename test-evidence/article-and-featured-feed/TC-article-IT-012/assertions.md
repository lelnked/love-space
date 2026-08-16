# TC-article-IT-012 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 前置：app 端列表可见

- ✅ 状态码 = 200（实际 200）
- ✅ 下线前列表含该文章

## Step 2 admin 下线

- ✅ 状态码 = 200（实际 200）

## Step 3 app 列表不可见

- ✅ 状态码 = 200（实际 200）
- ✅ 下线后列表不含该文章

## Step 4 app 详情 404

- ✅ 状态码 = 404（实际 404）
