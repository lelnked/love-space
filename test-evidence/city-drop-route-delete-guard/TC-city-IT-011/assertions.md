# TC-city-IT-011 assertions

| # | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | 状态码 | 200 | 200 | 通过 |
| 2 | Content-Type | application/json | application/json | 通过 |
| 3 | `id` | 目标城市 id | 一致 | 通过 |
| 4 | `chineseName`/`englishName`/`chineseProvince`/`englishProvince` | 均存在且与创建值一致 | 一致 | 通过 |
| 5 | `backgroundImage` 为 `{id,url}` 对象 | 是 | 是 | 通过 |
| 6 | `editorNote` | "山与湖之间的浪漫" | 一致 | 通过 |
| 7 | 字段集与列表项一致（无多余/缺失） | 一致 | 一致（7 字段） | 通过 |
| 8 | 契约 schema（api-spec.json#/paths/~1api~1app~1cities~1{id}/get） | 符合 | 符合 | 通过 |

结论：✅ 通过。（本轮以运行中的 app 实例经 HTTP 实跑复核，与 `CityReadIT#detailReturnsOnlineCity` 同语义。）
