# TC-ai-IT-067 断言明细

> GET /api/admin/ai/generations/page 只传一端按单边过滤

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 状态码 200 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | 步骤3 返回 10-22 00:00:00、10-21 23:59:50 | 通过 | ['eb6d8276-188b-4ed4-a95d-494ea9cb7e03', 'ad848a61-88ff-4604-a80a-2582227a6f08'] | ['eb6d8276-188b-4ed4-a95d-494ea9cb7e03', 'ad848a61-88ff-4604-a80a-2582227a6f08'] |
| 4 | 步骤4 状态码 200 | 通过 | 200 | 200 |
| 5 | 步骤4 返回 10-20 00:00:05、10-19 23:59:59 | 通过 | ['8cf9e1db-deda-4651-96b4-efc3386265b6', '0fba6d8a-efd8-4750-9b5c-32517eb1145f'] | ['8cf9e1db-deda-4651-96b4-efc3386265b6', '0fba6d8a-efd8-4750-9b5c-32517eb1145f'] |
| 6 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 7 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
| 8 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 9 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
