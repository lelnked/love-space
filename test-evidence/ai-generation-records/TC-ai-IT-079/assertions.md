# TC-ai-IT-079 断言明细

> PUT /api/admin/ai/generations/{id}/rating remark 恰 500 字符与全部标签通过

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 204 | 通过 | 204 | (204, None) |
| 2 | ratingTags 为 8 个标签 | 通过 | ['IGNORED_INPUT', 'WEAK_CYCLE_LINK', 'BAD_TONE', 'VAGUE', 'INAPPLICABLE_ADVICE', 'OVER_MEDICALIZED', 'FORMAT_ERROR', 'OTHER'] | ['IGNORED_INPUT', 'WEAK_CYCLE_LINK', 'BAD_TONE', 'VAGUE', 'INAPPLICABLE_ADVICE', 'OVER_MEDICALIZED', 'FORMAT_ERROR', 'OTHER'] |
| 3 | ratingRemark 长度 500 且一致 | 通过 | len=500 一致 | 500 |
