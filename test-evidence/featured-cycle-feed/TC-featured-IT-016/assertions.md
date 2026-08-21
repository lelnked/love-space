# TC-featured-IT-016 断言明细（2026-08-20）

断言顺序：状态码 → 响应头 → body 字段 → 契约 schema。

契约 `#/paths/~1api~1app~1featured-cycle-items/get`（summary：App 周期推荐（四周期分组恒在，条目上线∧关联实体可见，组内 sortOrder 升序））：无请求体/无参数，契约未声明响应 schema，记跳过。

- ✅ 前置数据就绪：MENSTRUAL 上线 ACTIVITY、OVULATION 上线 ARTICLE、LUTEAL 下线条目、FOLLICULAR 无条目
- ✅ 状态码 = 200（实际 200）
- ✅ 响应头 Content-Type 含 application/json
- ✅ 响应含四个周期键（实际 ['MENSTRUAL', 'FOLLICULAR', 'OVULATION', 'LUTEAL']）
- ✅ FOLLICULAR 为空数组、不缺键（实际 []）
- ✅ MENSTRUAL 1 条（实际 1）
- ✅ OVULATION 1 条（实际 1）
- ✅ LUTEAL 为空数组（下线条目不下发，实际 []）
- ✅ MENSTRUAL 条目含 type=ACTIVITY（实际 'ACTIVITY'）
- ✅ MENSTRUAL 条目 banner 为签名 URL（实际 {'id': 'bound/58e61a27-ccab-41a6-b147-915f725e3c99.png', 'url': 'https）
- ✅ MENSTRUAL 条目含关联实体 id activityId=01a01f6b-51fc-72d2-bdd2-878755212665（实际 '01a01f6b-51fc-72d2-bdd2-878755212665'）
- ✅ OVULATION 条目含 type=ARTICLE（实际 'ARTICLE'）
- ✅ OVULATION 条目 banner 为签名 URL
- ✅ OVULATION 条目含关联实体 id articleId=01a01f6b-5203-79fd-b344-1180a4fc1237（实际 '01a01f6b-5203-79fd-b344-1180a4fc1237'）
- ⏭️ 响应 schema：契约未声明响应体，跳过（无漂移判定依据）

结论：全部断言通过 → ✅
