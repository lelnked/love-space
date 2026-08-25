# TC-article-IT-002 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 缺 name
- ✅ 状态码 = 400
- ✅ message = 「栏目名称不能为空」（中文业务错误）

## Step 2 缺 icon
- ✅ 状态码 = 400
- ✅ message = 「栏目 icon 不能为空」（中文业务错误）

## Step 3 列表确认未创建
- ✅ 状态码 = 200
- ✅ 列表中无本用例提交的栏目（两次请求均未落库）
- ✅ 响应 schema 契约未声明，跳过
