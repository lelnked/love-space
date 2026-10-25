# TC-ai-IT-069 断言明细

> GET /api/admin/ai/generations/page 按未评价筛选 rating=NONE

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 200 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | 步骤3 仅 B | 通过 | ['8bc0d3be-80c4-4f67-96aa-cd512d0ea6d2'] | ['8bc0d3be-80c4-4f67-96aa-cd512d0ea6d2'] |
| 4 | B rating=null | 通过 | None | None |
| 5 | 步骤4 200 | 通过 | 200 | 200 |
| 6 | 步骤4 仅 A | 通过 | ['2ccbb344-e850-43fb-b0ac-eb0e7139d734'] | ['2ccbb344-e850-43fb-b0ac-eb0e7139d734'] |
| 7 | A rating=USABLE | 通过 | USABLE | USABLE |
| 8 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 9 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
| 10 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 11 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
