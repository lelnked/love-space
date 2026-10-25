# TC-ai-IT-065 断言明细

> GET /api/admin/ai/generations/page 不传日期默认最近 7 天

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 状态码 200 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | totalElements=2 | 通过 | 2 | 2 |
| 4 | content 为今天、6 天前（倒序），不含 8 天前 | 通过 | ['57249924-a8b9-423a-842d-d122edf03176', '7b0238c7-eaff-43a4-bbe9-71187f1215a4'] | ['57249924-a8b9-423a-842d-d122edf03176', '7b0238c7-eaff-43a4-bbe9-71187f1215a4'] |
| 5 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 6 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
