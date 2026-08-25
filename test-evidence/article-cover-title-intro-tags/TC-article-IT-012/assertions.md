# TC-article-IT-012 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 上线时 app 列表可见
- ✅ 状态码 = 200
- ✅ 列表含该文章 id

## Step 2 admin 下线
- ✅ 状态码 = 200

## Step 3 下线后 app 列表
- ✅ 状态码 = 200
- ✅ 列表不含该文章 id

## Step 4 下线后 app 详情
- ✅ 状态码 = 404
- ✅ body 含 message: article not found
- ✅ 响应 schema 契约未声明，跳过
