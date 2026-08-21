# TC-featured-IT-003 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1admin~1featured-items~1{id}~1online/put`：契约未声明请求体/响应 schema，按 summary 语义提交 `{"online": bool}`，记跳过。

- ✅ 前置创建 状态码 = 200（实际 200），online = true
- ✅ 下线 状态码 = 200（实际 200）
- ✅ 详情 online = false（实际 False）
- ✅ 上线 状态码 = 200（实际 200）
- ✅ 详情 online = true（可往返切换，实际 True）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
