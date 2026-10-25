# TC-ai-IT-084 断言明细

> PUT /api/admin/ai/generations/{id}/rating 参数非法返回 400 且评价不变

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | a 缺 rating → 400 | 通过 | 400 | (400, {'status': 400, 'error': 'Bad Request', 'message': '评价不能为空', 'path': '/api/admin/ai/generations/c8007dba-a8cb-4f3a-bd35-dcb9432615cc/rating'}) |
| 2 | b rating=GOOD → 400 | 通过 | 400 | (400, {'status': 400, 'error': 'Bad Request', 'message': 'Malformed request body', 'path': '/api/admin/ai/generations/c8007dba-a8cb-4f3a-bd35-dcb9432615cc/rating'}) |
| 3 | c 重复标签 → 400 | 通过 | 400 | (400, {'status': 400, 'error': 'Bad Request', 'message': '问题标签不能重复', 'path': '/api/admin/ai/generations/c8007dba-a8cb-4f3a-bd35-dcb9432615cc/rating'}) |
| 4 | d 未知标签 → 400 | 通过 | 400 | (400, {'status': 400, 'error': 'Bad Request', 'message': 'Malformed request body', 'path': '/api/admin/ai/generations/c8007dba-a8cb-4f3a-bd35-dcb9432615cc/rating'}) |
| 5 | e remark 501 → 400 | 通过 | 400 | (400, {'status': 400, 'error': 'Bad Request', 'message': '备注长度不能超过 500 个字符', 'path': '/api/admin/ai/generations/c8007dba-a8cb-4f3a-bd35-dcb9432615cc/rating'}) |
| 6 | 评价不变 | 通过 | ('NEEDS_IMPROVEMENT', ['OTHER'], '原备注', 'admin', '2026-10-25T12:30:22.393469Z') | ('NEEDS_IMPROVEMENT', ['OTHER'], '原备注', 'admin', '2026-10-25T12:30:22.393469Z') |
