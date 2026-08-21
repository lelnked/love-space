# TC-featured-IT-018 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1app~1featured-cycle-items/get`：无请求体/无参数；契约未声明响应 schema，记跳过。

- ✅ 前置就绪：上架城市 + online 大使 + 路线 + OVULATION 上线 ROUTE 条目（id=01a01f6c-2c59-76dd-babd-44a72b1155ca）
- ✅ 步骤2 状态码 = 200（实际 200）
- ✅ 步骤2 该条目在 OVULATION 分组（实际 ['01a01f6c-2c59-76dd-babd-44a72b1155ca']）
- ✅ 步骤3 状态码 = 200（实际 200）
- ✅ 步骤3 该条目已从 OVULATION 分组消失（大使下线连带隐藏，实际 []）
- ✅ 步骤4 状态码 = 200（实际 200）
- ✅ 步骤4 该条目已从 OVULATION 分组消失（城市下架，实际 []）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
