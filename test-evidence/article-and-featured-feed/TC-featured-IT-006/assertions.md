# TC-featured-IT-006 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 前置：两条上线推荐（先后创建）与一条下线推荐

- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）

## Step 2 app 信息流

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 仅含两条上线条目
- ✅ 不含下线条目
- ✅ 按创建时间倒序（条目 2 在前）
- ✅ 每项含 banner 签名 URL、推荐说明
- ✅ 每项含关联城市数据（id 与名称）
