# TC-article-IT-019 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置（甲设 intro+tags；乙不设）
- ✅ 两次创建状态码 = 200

## Step 2 GET app 详情（已设）
- ✅ 状态码 = 200，Content-Type 含 application/json
- ✅ intro = 这是引言
- ✅ tags = ["恋爱","指南"]

## Step 3 GET app 详情（未设）
- ✅ 状态码 = 200
- ✅ intro = null
- ✅ tags = []，type = array（空数组而非 null）
- ✅ 响应 schema 契约未声明，跳过
