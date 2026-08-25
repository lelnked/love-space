# TC-article-IT-013 断言明细

执行日期: 2026-08-25 ｜ 断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。
契约 contracts/api-spec.json 对 article 相关 operation 仅声明了请求体 schema、未声明响应 schema，故响应 schema 校验统一记「契约未声明，跳过」。

## Step 1 删除栏目前 app 详情
- ✅ 状态码 = 200

## Step 2 admin 删除唯一关联栏目 A
- ✅ 状态码 = 200

## Step 3 删除后 app 详情
- ✅ 状态码 = 404（可见性 = online ∧ 至少关联一个仍存在的栏目）
- ✅ 响应 schema 契约未声明，跳过
