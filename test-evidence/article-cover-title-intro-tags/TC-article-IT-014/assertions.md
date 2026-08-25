# TC-article-IT-014 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置（含图片富文本的可见文章）
- ✅ 状态码 = 200

## Step 2 GET /api/app/articles/{id}
- ✅ 状态码 = 200，Content-Type 含 application/json
- ✅ 含 image（签名 URL）、title = IT014文章、subtitle = IT014副标题
- ✅ contentHtml 文本与后台保存一致（`<p>段落甲</p>`、`<p>段落乙</p>`）
- ✅ img src 已替换为签名 URL（bound/it014-p1.png，http 开头）
- ✅ （本次变更回归）详情字段集含新增 intro、tags，未破坏既有字段
- ✅ 响应 schema 契约未声明，跳过
