# TC-article-IT-010 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 创建含 2 个 img 的富文本
- ✅ 状态码 = 200
- ✅ 请求体符合 ArticleUpsertRequest

## Step 2 详情
- ✅ 状态码 = 200
- ✅ 文本部分与提交一致（`<p>段落一</p>`、`<p>段落二</p>`）
- ✅ 2 个 img 的 src 均替换为签名 URL（http 开头、含 bound/it010-p1.png 与 bound/it010-p2.png，非裸 objectKey）

## Step 3 改为纯文本 HTML
- ✅ PUT 状态码 = 200
- ✅ 详情 contentHtml = `<p>只剩纯文本</p>`，无 img 的 HTML 原样往返不报错
- ✅ 响应 schema 契约未声明，跳过
