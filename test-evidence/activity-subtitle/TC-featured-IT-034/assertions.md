# TC-featured-IT-034 断言结果

活动 `01a0608e-9b39-7d5c-8cfa-5fb3fedd3879` / 条目 `01a06090-346f-7598-a210-ae6e36b7f5ab`

| 步骤 | 断言 | 期望 | 实际 | 结果 |
|---|---|---|---|---|
| 2 | 状态码 | 200 | 200 | ✅ |
| 2 | Content-Type | application/json | application/json | ✅ |
| 2 | target 非 null | 非 null | 非 null | ✅ |
| 2 | target.id = 活动 id | 01a0608e-9b39-7d5c-8cfa-5fb3fedd3879 | 01a0608e-9b39-7d5c-8cfa-5fb3fedd3879 | ✅ |
| 2 | target.title = 活动标题 | 副标题活动A-0902 | 副标题活动A-0902 | ✅ |
| 2 | target.subtitle 取自活动实体 | 山野轻装 | '山野轻装' | ✅ |
| 2 | target.cover 为首图签名 URL 对象 | {id,url} 且 url 含 Signature/Expires | id=bound/a024-A.png，url 含 Signature/Expires | ✅ |
| 2 | target.level = 活动难度等级 | L2 | L2 | ✅ |
| 2 | 条目自身 description 仍为手填推荐说明 | 周期推荐说明-034 | '周期推荐说明-034' | ✅ |
| 契约 | ActivityTarget 形状 {id,title,subtitle,cover,level} | 5 键 | 实际键 ['cover', 'id', 'level', 'subtitle', 'title'] | ✅ |

结论：✅ 通过。ACTIVITY 条目的 `target` 已扩出 `subtitle` 且取自活动实体，其余基础信息不变。
