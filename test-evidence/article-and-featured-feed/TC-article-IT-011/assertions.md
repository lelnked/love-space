# TC-article-IT-011 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 前置：栏目 A(sortOrder=2)、B(sortOrder=1)，B 下两篇上线文章(sortOrder=3 与 1)

- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）

## Step 2 app 栏目列表

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 列表按 sortOrder 升序
- ✅ B（权重1）排在 A（权重2）之前
- ✅ 每项含名称与 icon 签名 URL

## Step 3 app 文章列表

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 仅返回 B 下两篇上线文章
- ✅ 按 sortOrder 升序（权重 1 在前）
- ✅ 每项含图片签名 URL、标题、副标题
