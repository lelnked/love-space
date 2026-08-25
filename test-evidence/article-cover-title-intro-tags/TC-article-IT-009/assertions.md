# TC-article-IT-009 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 前置文章
- ✅ 状态码 = 200

## Step 2 DELETE
- ✅ 状态码 = 200（物理删除）

## Step 3 再查详情
- ✅ 状态码 = 400（admin 端「资源不存在」全局口径）
- ✅ message =「文章不存在：{id}」为中文业务错误

## Step 4 分页列表
- ✅ 状态码 = 200
- ✅ 列表不含被删除文章 id
- ✅ 响应 schema 契约未声明，跳过
