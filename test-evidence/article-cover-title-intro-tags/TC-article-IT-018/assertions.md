# TC-article-IT-018 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置（甲设 coverTitle+tags；乙不设）
- ✅ 两次创建状态码 = 200

## Step 2 GET /api/app/articles?categoryId={id}
- ✅ 状态码 = 200，Content-Type 含 application/json
- ✅ 甲：coverTitle = 封面甲，tags = ["约会"]
- ✅ 乙：coverTitle = 文章乙（未设时回落 title）
- ✅ 乙：tags = []，type = array
- ✅ 两项均含 image（签名 URL）、title、subtitle
- ✅ 响应 schema 契约未声明，跳过
