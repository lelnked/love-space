# TC-ai-IT-070 断言明细

> GET /api/admin/ai/generations/page 按问题标签筛选

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 200 | 通过 | 200 | 200 |
| 2 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 3 | 步骤3 仅 A | 通过 | ['ab6f4d1d-dbc9-48f0-a5ac-3df66ad74050'] | ['ab6f4d1d-dbc9-48f0-a5ac-3df66ad74050'] |
| 4 | 步骤4 200 | 通过 | 200 | 200 |
| 5 | 步骤4 A、B 两条 | 通过 | ['60ba3e9a-1e86-4d2a-bc43-7e04c22783c0', 'ab6f4d1d-dbc9-48f0-a5ac-3df66ad74050'] | ['60ba3e9a-1e86-4d2a-bc43-7e04c22783c0', 'ab6f4d1d-dbc9-48f0-a5ac-3df66ad74050'] |
| 6 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 7 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
| 8 | 契约: PageResponse 键 | 通过 | ['content', 'page', 'size', 'totalElements', 'totalPages'] | ['content', 'page', 'size', 'totalElements', 'totalPages'] |
| 9 | 契约: GenerationItem 键集合 | 通过 | 11 键 | 一致 |
