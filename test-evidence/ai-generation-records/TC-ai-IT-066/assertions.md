# TC-ai-IT-066 断言明细

> GET /api/admin/ai/generations/page 日期区间含两端整日（北京时间）

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 200 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | totalElements=2 | 通过 | 2 | 2 |
| 4 | 仅 10-21 23:59:50、10-20 00:00:05（10-21 在前） | 通过 | ['4cfcc790-9b61-4210-a40b-4501e5147071', 'efb51be2-144d-4f34-8531-04824ee74355'] | ['4cfcc790-9b61-4210-a40b-4501e5147071', 'efb51be2-144d-4f34-8531-04824ee74355'] |
| 5 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 6 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
