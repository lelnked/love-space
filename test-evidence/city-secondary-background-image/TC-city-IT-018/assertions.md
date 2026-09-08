# TC-city-IT-018 断言明细

| # | 断言 | 结果 |
|---|---|---|
| 1 | GET /api/app/cities 状态码 = 200 | ✅ |
| 2 | 响应头 Content-Type 含 application/json | ✅ |
| 3 | 已配置城市（92331558）`secondaryBackgroundImage` 为 `{id,url}` 结构 | ✅ |
| 4 | 其 `id` = `bound/bg-second-92331558.jpg`，与 admin 侧落库值一致 | ✅ |
| 5 | 其 `url` 为签名地址（含 Expires / OSSAccessKeyId / Signature） | ✅ |
| 6 | 未配置城市（1812123）`secondaryBackgroundImage` = null | ✅ |
| 7 | 两个城市的 `backgroundImage` 行为不变，仍为 `{id,url}` 签名结构 | ✅ |
| 8 | 契约核对 `api-spec.json#/paths/~1api~1app~1cities/get`：字段与实现一致 | ✅ |

结论：✅ 通过（8/8）
备注：执行前需修正 app 的 `ALIYUN_OSS_BUCKET`（原为非法值 `x`），详见 exchange.md 环境说明。
