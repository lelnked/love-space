# TC-article-IT-017 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置（已设三字段的文章）
- ✅ 状态码 = 200，coverTitle=原封面标题 / intro=原引言 / tags=["原标签"]

## Step 2 PUT 空白值与含空白项的 tags
- ✅ 状态码 = 200
- ✅ 请求体符合 ArticleUpsertRequest

## Step 3 GET 详情
- ✅ coverTitle = null（纯空格按 null 存）
- ✅ intro = null（纯空格按 null 存）
- ✅ tags = ["甲","乙"]：空串项被剔除，保留项已 trim（" 甲 " → "甲"）
- ✅ 响应 schema 契约未声明，跳过
