# TC-article-IT-008 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置 online=true 文章
- ✅ 状态码 = 200

## Step 2 下线
- ✅ 状态码 = 200

## Step 3 详情
- ✅ 状态码 = 200
- ✅ online = false

## Step 4 再上线并复查
- ✅ PUT 状态码 = 200
- ✅ 详情 online = true（可往返切换）
- ✅ 响应 schema 契约未声明，跳过
