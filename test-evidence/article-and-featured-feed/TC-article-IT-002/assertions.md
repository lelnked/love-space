# TC-article-IT-002 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 缺 name

- ✅ 状态码 = 400（实际 400）
- ✅ 响应头 Content-Type 含 application/json
- ✅ message 为中文业务错误

## Step 2 缺 icon

- ✅ 状态码 = 400（实际 400）
- ✅ 响应头 Content-Type 含 application/json
- ✅ message 为中文业务错误

## 副作用检查

- ✅ 栏目数不变（仍为 1）
