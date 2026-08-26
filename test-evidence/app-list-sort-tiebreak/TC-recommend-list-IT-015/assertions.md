# TC-recommend-list-IT-015 断言明细

执行日期: 2026-08-26 ｜ 关联契约: api-spec.json#/paths/~1api~1app~1merchants~1page/get

结果: ✅ 通过（6/6 通过）

断言顺序: 状态码 → 响应头 → body 字段值 → 契约 schema

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 两次请求状态码均 200 | ✅ | `HTTP/1.1 200 / HTTP/1.1 200` |
| 2 | Content-Type 含 application/json | ✅ | `application/json` |
| 3 | 两次 totalElements 均为 3 | ✅ | `3 / 3` |
| 4 | 不带 recommendListId 时 content 按 weight 降序 | ✅ | `['乙2021653', '丙2021653', '甲2021653']`（乙2=9 → 丙2=5 → 甲2=1） |
| 5 | 带 recommendListId 时顺序不变（参数被忽略，与清单内顺序无关） | ✅ | `['乙2021653', '丙2021653', '甲2021653']` |
| 6 | content[*] 不含 recommendSortOrder 字段 | ✅ | `['address', 'id', 'logo', 'loveIndex', 'name', 'scores', 'tags']` |

## 契约 schema 校验

- `api-spec.json#/paths/~1api~1app~1merchants~1page/get` 无 responses schema；summary 明确「固定 weight DESC, createdAt DESC，不受推荐清单影响」，实测一致。
- 请求参数自检：`recommendListId` **不在**契约声明的 parameters 中，属用例刻意传入的多余参数（用于验证被忽略），非契约漂移。
- 未发现契约漂移。
