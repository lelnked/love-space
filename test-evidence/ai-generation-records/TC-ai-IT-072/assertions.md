# TC-ai-IT-072 断言明细

> GET /api/admin/ai/generations/page requestId / templateId / model 精确匹配

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 200 且仅 R1 | 通过 | ['2b20d613-b771-4f0a-abe9-2c0d3151fb84'] | (200, ['2b20d613-b771-4f0a-abe9-2c0d3151fb84']) |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 4 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
| 5 | 步骤4 200 空页 | 通过 | 空页 | (200, []) |
| 6 | 步骤5 仅 R2 | 通过 | ['377cdb72-a6bf-45a9-9e0e-d0da0c86af96'] | (200, ['377cdb72-a6bf-45a9-9e0e-d0da0c86af96']) |
| 7 | 步骤6 仅 R1 | 通过 | ['2b20d613-b771-4f0a-abe9-2c0d3151fb84'] | (200, ['2b20d613-b771-4f0a-abe9-2c0d3151fb84']) |
| 8 | 步骤7 空页（精确匹配） | 通过 | 空页 | (200, []) |
