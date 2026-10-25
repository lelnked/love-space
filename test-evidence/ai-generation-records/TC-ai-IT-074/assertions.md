# TC-ai-IT-074 断言明细

> GET /api/admin/ai/generations/page 列表条目仅十一个字段、不含正文

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 app 生成 200 SUCCESS | 通过 | 200 SUCCESS | (200, 'SUCCESS') |
| 2 | 状态码 200 | 通过 | 200 | 200 |
| 3 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 4 | 分页键 {content,page,size,totalElements,totalPages} | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 5 | page=1 | 通过 | 1 | 1 |
| 6 | 条目键集合恰为 11 字段 | 通过 | ['anonymousUserId', 'createdAt', 'finalModel', 'id', 'latencyMs', 'rating', 'sceneId', 'sceneName', 'status', 'templateId', 'templateSeq'] | ['anonymousUserId', 'createdAt', 'finalModel', 'id', 'latencyMs', 'rating', 'sceneId', 'sceneName', 'status', 'templateId', 'templateSeq'] |
| 7 | 无 inputs/requestMessages/rawOutput/content | 通过 | 无 | [] |
| 8 | sceneName=场景当前名称 | 通过 | 生成记录场景S | 生成记录场景S |
| 9 | finalModel=mock-ok | 通过 | mock-ok | mock-ok |
| 10 | rating=null | 通过 | None | None |
