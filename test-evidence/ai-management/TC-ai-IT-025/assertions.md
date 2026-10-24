# TC-ai-IT-025 断言明细

> POST /api/admin/ai/prompt-templates/{id}/test 存在未声明变量返回 400

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 400 | 通过 | 400 | 400 |
| 2 | message 含 mood 与「输入模板」 | 通过 | 含 mood、输入模板 | 输入模板引用了未声明变量 {{mood}} |
| 3 | 未调用模型（lastTestAt 仍为空） | 通过 | null | null |
