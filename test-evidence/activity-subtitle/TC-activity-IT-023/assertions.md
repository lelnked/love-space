# TC-activity-IT-023 断言结果

活动 ID: `01a0608e-531e-78f2-bb62-23c025fc49f4`

| 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 1 | POST 状态码 | 200 | 200 | ✅ |
| 1 | 响应 subtitle | 一日徒步 | '一日徒步' | ✅ |
| 2 | GET 状态码 | 200 | 200 | ✅ |
| 2 | 响应 subtitle | 一日徒步 | '一日徒步' | ✅ |
| 3 | PUT 状态码 | 200 | 200 | ✅ |
| 3 | PUT/GET 响应 subtitle | 两日徒步 | '两日徒步' / '两日徒步' | ✅ |
| 4 | PUT（不带 subtitle）状态码 | 200（不报 400） | 200 | ✅ |
| 4 | PUT/GET 响应 subtitle | null | None / None | ✅ |
| 5 | PUT（subtitle=""）状态码 | 200 | 200 | ✅ |
| 5 | PUT/GET 响应 subtitle | ""（原样保存，不 trim 归一） | '' / '' | ✅ |
| 6 | GET /page 状态码 | 200 | 200 | ✅ |
| 6 | 列表项含 subtitle 字段 | 键存在 | 键存在，值 '' | ✅ |
| 契约 | 请求体符合 ActivityUpsertRequest（subtitle: string 选填） | 符合 | 符合 | ✅ |

结论：✅ 通过。副标题可写、可改、可清空（省略字段→null / 空串→""），详情与列表均下发 `subtitle`。
