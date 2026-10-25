# TC-ai-IT-081 断言明细

> PUT /api/admin/ai/generations/{id}/rating 已评价记录不能清除评价

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | {"rating":null} → 400 | 通过 | 400 | (400, {'status': 400, 'error': 'Bad Request', 'message': '评价不能为空', 'path': '/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840/rating'}) |
| 2 | {} → 400 | 通过 | 400 | (400, {'status': 400, 'error': 'Bad Request', 'message': '评价不能为空', 'path': '/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840/rating'}) |
| 3 | {"rating":"NONE"} → 400 | 通过 | 400 | (400, {'status': 400, 'error': 'Bad Request', 'message': 'Malformed request body', 'path': '/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840/rating'}) |
| 4 | 仍 rating=USABLE，ratedBy/ratedAt 未变 | 通过 | ('USABLE', 'admin', '2026-10-25T12:30:20.062257Z') | ('USABLE', 'admin', '2026-10-25T12:30:20.062257Z') |
