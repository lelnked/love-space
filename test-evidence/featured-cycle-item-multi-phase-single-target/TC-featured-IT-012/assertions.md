# TC-featured-IT-012 断言明细

执行日期: 2026-09-02

- ✅ 更新返回 200　— 实际 200: {"id":"01a0622c-33b1-75e6-acb9-0bb074eb897a","phases":["FOLLICULAR","OVULATION"],"type":"ACTIVITY","sortOrder":0,"online":false,"targetId":"01a0622c-33a7-7175-82e2-cc13602b39ed","relatedTitle":"act-m9
- ✅ phases 已更新且按枚举声明顺序　— ['FOLLICULAR', 'OVULATION']
- ✅ type 仍为 ACTIVITY（传入值被忽略）　— ACTIVITY
- ✅ targetId 仍为该活动 id
- ✅ description 已更新
- ✅ title 未被写入（仍为 null）　— None
- ✅ 返回 400　— 实际 400: {"status":400,"error":"Bad Request","message":"关联活动不存在：01a0622c-33ab-7c20-aa16-ff6be2f7576f","path":"/api/admin/featured-cycle-items/01a0622c-33b1-75e6-acb9-0bb074eb897a"}
- ✅ message 为「关联活动不存在」（按持久化类型 ACTIVITY 分派）　— '关联活动不存在：01a0622c-33ab-7c20-aa16-ff6be2f7576f'

结论: ✅ 通过（8/8 断言通过）
