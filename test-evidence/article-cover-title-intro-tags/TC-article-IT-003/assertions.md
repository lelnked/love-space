# TC-article-IT-003 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置创建栏目
- ✅ 状态码 = 200，sortOrder=1，icon=bound/it003-icon-old.png

## Step 2 PUT 更新
- ✅ 状态码 = 200
- ✅ 请求体符合 ArticleCategoryUpsertRequest

## Step 3 列表校验
- ✅ 状态码 = 200
- ✅ name = IT003美食攻略
- ✅ sortOrder = 5
- ✅ icon 为新图 bound/it003-icon-new.png 的签名 URL（http 开头）
- ✅ 响应 schema 契约未声明，跳过
