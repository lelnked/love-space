# TC-featured-IT-003 断言明细

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。契约（contracts/api-spec.json）仅声明请求体 schema、未声明响应 schema，响应 schema 校验记「契约未声明，跳过」。


## Step 2 下线

- ✅ 状态码 = 200（实际 200）

## Step 3 详情 online=false

- ✅ 状态码 = 200（实际 200）
- ✅ 详情 online = false

## Step 4 重新上线并确认

- ✅ 状态码 = 200（实际 200）
- ✅ 状态码 = 200（实际 200）
- ✅ 详情 online = true（可往返切换）
