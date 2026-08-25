# TC-city-IT-004 assertions

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 登录状态码 | 200 | 200 | 通过 |
| 2 | 前置建城状态码 | 200 | 200 | 通过 |
| 3 | GET /api/app/cities 状态码 | 200 | 200 | 通过 |
| 4 | Content-Type | application/json | application/json | 通过 |
| 5 | 列表中存在目标城市 id | 存在 | 存在 | 通过 |
| 6 | 该项 `editorNote` | "山与湖之间的浪漫" | "山与湖之间的浪漫" | 通过 |
| 7 | 契约 schema（api-spec.json#/paths/~1api~1app~1cities/get，200 响应项 id/chineseName/englishName/chineseProvince/englishProvince/backgroundImage{id,url}/editorNote） | 符合 | 符合 | 通过 |

结论：✅ 通过。
