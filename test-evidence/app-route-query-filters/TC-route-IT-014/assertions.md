# TC-route-IT-014 断言明细

> 契约: `api-spec.json#/paths/~1api~1app~1routes~1{id}/get`；未声明 responses/schema，schema 校验跳过。

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 状态码 | 200 | 200 | ✅ |
| 2 | 响应头 Content-Type | 含 application/json | application/json | ✅ |
| 3 | 路线图片列表为签名 URL | 2 张且均含 Signature | 2 张，均签名 | ✅ |
| 4 | 缩略图签名 URL | 非空 | 含 Signature | ✅ |
| 5 | 地点顺序 | S1 → S2 | [S1, S2] | ✅ |
| 6 | 每个地点含名称/图片/介绍 | 齐全 | S1/介绍1/签名图；S2/介绍2/签名图 | ✅ |
| 7 | 大使名称 | 详情大使014 | 详情大使014 | ✅ |
| 8 | 大使头像签名 URL | 非空 | 含 Signature | ✅ |
| 9 | 大使标签 | ["古着","咖啡"] | 一致 | ✅ |
| 10 | cityName / city 对象 | 详情城014 | cityName=详情城014，city={id,name} 一致 | ✅ |
| 11 | 契约 schema | — | 契约未声明响应 schema | ⏭ 跳过 |

**结论**: ✅ 通过（RouteQueryService 重构后详情口径未变）
