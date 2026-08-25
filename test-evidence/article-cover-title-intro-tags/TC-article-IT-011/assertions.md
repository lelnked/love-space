# TC-article-IT-011 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置（栏目 A sortOrder=2、B sortOrder=1；B 下两篇上线文章 sortOrder=3 与 1）
- ✅ 创建状态码均 = 200

## Step 2 GET /api/app/article-categories
- ✅ 状态码 = 200，Content-Type 含 application/json
- ✅ 整体按 sortOrder 升序（`[.sortOrder] == sort`）
- ✅ B 排在 A 之前
- ✅ 每项含 name 与 icon 签名 URL（http 开头）

## Step 3 GET /api/app/articles?categoryId={B}
- ✅ 状态码 = 200，Content-Type 含 application/json
- ✅ 按 sortOrder 升序：IT011权重1 在前、IT011权重3 在后
- ✅ 每项含 image（签名 URL）、title、subtitle
- ✅ （MODIFIED）每项含 coverTitle：分别为 IT011封面1、IT011封面3
- ✅ （MODIFIED）每项含 tags：["周末","户外"]、["约会"]
- ✅ 列表项字段集 = [coverTitle,id,image,subtitle,tags,title]
- ✅ 响应 schema 契约未声明，跳过
