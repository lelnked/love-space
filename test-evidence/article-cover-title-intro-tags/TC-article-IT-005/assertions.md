# TC-article-IT-005 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置栏目 A、B
- ✅ 状态码均 = 200

## Step 2 创建完整文章
- ✅ 状态码 = 200
- ✅ 请求体符合 ArticleUpsertRequest（image/title 必填，categoryIds uuid 数组，online boolean）

## Step 3 文章详情
- ✅ 状态码 = 200，Content-Type 含 application/json
- ✅ image.url 为签名 URL（http 开头）
- ✅ title = 海岛两日游 / subtitle = 附完整行程 / sortOrder = 1 / online = true
- ✅ categoryIds = [A, B] 两个栏目
- ✅ contentHtml 与提交一致：`<p>第一天出发</p><p>第二天返程</p>`
- ✅ （本次变更回归）未提交的新字段 coverTitle=null、intro=null、tags=[]，无字段映射回归
- ✅ 响应 schema 契约未声明，跳过
