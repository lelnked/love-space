# TC-ai-IT-071 断言明细

> GET /api/admin/ai/generations/page 场景 + 状态组合筛选且按时间倒序

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 200 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | 步骤3 3 条且 -1h,-2h,-3h | 通过 | ['e97b3450-0ccb-4542-bd99-3b621d42a859', 'ebb8be7f-891c-4ead-87e7-99b80ef4783b', 'e9221706-ac00-412e-8119-84625c845ca7'] | ['e97b3450-0ccb-4542-bd99-3b621d42a859', 'ebb8be7f-891c-4ead-87e7-99b80ef4783b', 'e9221706-ac00-412e-8119-84625c845ca7'] |
| 4 | 步骤3 均 sceneId=S、status=SUCCESS | 通过 | 全部满足 | [('gr_3f05e38d', 'SUCCESS'), ('gr_3f05e38d', 'SUCCESS'), ('gr_3f05e38d', 'SUCCESS')] |
| 5 | 步骤4 200 | 通过 | 200 | 200 |
| 6 | 步骤4 1 条 PROCESSING | 通过 | ['36506149-c8cb-444a-ac69-ce5f9b5ce051'] | ['36506149-c8cb-444a-ac69-ce5f9b5ce051'] |
| 7 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 8 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
| 9 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 10 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
