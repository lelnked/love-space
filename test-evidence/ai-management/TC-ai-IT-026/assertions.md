# TC-ai-IT-026 断言明细

> POST /api/admin/ai/prompt-templates/{id}/test 必填变量缺失返回 400

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | inputs={} → 400 | 通过 | 400 | 400 |
| 2 |   message 含 age | 通过 | 含 age | 缺少必填变量：age |
| 3 | inputs={"age":null} → 400 | 通过 | 400 | 400 |
| 4 |   message 含 age | 通过 | 含 age | 缺少必填变量：age |
| 5 | last_test_* 与前置一致 | 通过 | 2026-10-24 17:51:41.859247+00\|{"age": 30}\|{"echo":"年龄30"}\|t\|admin\|t | 2026-10-24 17:51:41.859247+00\|{"age": 30}\|{"echo":"年龄30"}\|t\|admin\|t |
