# TC-featured-IT-037 断言结果

活动 `01a06090-6abb-746e-b621-959ac912e3f4` / 条目 `01a06090-6aec-7c45-aabc-2cabfd9000c6`

| 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 2 | 状态码 | 200 | 200 | ✅ |
| 2 | 条目仍被下发 | 命中 1 条 | 命中 1 条 | ✅ |
| 2 | target 非 null | 非 null | 非 null | ✅ |
| 2 | target.cover | null | None | ✅ |
| 2 | target.id 正常有值 | 01a06090-6abb-746e-b621-959ac912e3f4 | 01a06090-6abb-746e-b621-959ac912e3f4 | ✅ |
| 2 | target.title 正常有值 | 无图活动-0902 | 无图活动-0902 | ✅ |
| 2 | ActivityTarget 形状变更后回归 | 含新增 subtitle 键且为 null（该活动未填） | subtitle=None | ✅ |

结论：✅ 通过。ActivityTarget 增 `subtitle` 后，无图活动的 `cover=null` 且条目仍正常下发。
