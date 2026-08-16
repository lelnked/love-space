# TC-featured-IT-004 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 1 前置：另一城市 B

- ✅ 状态码 = 200（实际 200）

## Step 2 更新条目（cityId 传城市 B）

- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 请求体符合 FeaturedItemUpsertRequest schema

## Step 3 详情确认

- ✅ 状态码 = 200（实际 200）
- ✅ description 更新生效
- ✅ banner 为新图签名 URL（bound/feat004-new-）
- ✅ 关联城市仍为城市 A（cityId 变更被忽略）
