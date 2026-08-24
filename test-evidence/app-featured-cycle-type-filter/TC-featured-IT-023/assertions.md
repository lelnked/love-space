# TC-featured-IT-023 断言明细

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | HTTP 状态码 | 400 | 400 | ✅ |
| 2 | Content-Type | application/json | application/json | ✅ |
| 3 | 不返回 200 / 不静默忽略参数 | 非 200 且非分组体 | 400 错误体，无四周期分组 | ✅ |
| 4 | 错误体含参数名提示 | 指明 type | `Invalid value for parameter 'type': UNKNOWN` | ✅ |
| 5 | 契约 schema 校验 | — | 该 operation 未声明 responses schema，跳过 | ⏭ |

结论: ✅ 通过
