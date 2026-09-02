# TC-activity-IT-024 断言结果

活动 A: `01a0608e-9b39-7d5c-8cfa-5fb3fedd3879`（subtitle=山野轻装）／活动 B: `01a0608e-9b58-7503-95ee-0dea4ec91bc1`（未填 subtitle）

| 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 2 | app 列表状态码 | 200 | 200 | ✅ |
| 2 | Content-Type | application/json | application/json | ✅ |
| 2 | 列表中 A 的 subtitle | 山野轻装 | '山野轻装' | ✅ |
| 2 | 列表中 B 的 subtitle 键存在且为 null（不回落为 title） | 键存在 / null | 键存在=True / None（title='无副标题活动B-0902'） | ✅ |
| 3 | A 详情状态码 | 200 | 200 | ✅ |
| 3 | A 详情 subtitle | 山野轻装 | '山野轻装' | ✅ |
| 4 | B 详情状态码 | 200 | 200 | ✅ |
| 4 | B 详情 subtitle 键存在且为 null | 键存在 / null | 键存在=True / None | ✅ |

结论：✅ 通过。admin 写入 → app 列表/详情读出的跨端副标题链路贯通，未填时键存在且为 null，不回落为 title。
