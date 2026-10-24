# TC-ai-IT-011 断言明细

> PUT /api/admin/ai/scenes/{id}/models 主备相同返回 400

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 400 | 通过 | 400 | 400 |
| 2 | message 中文 | 通过 | 中文 | 备用模型不能与主模型相同 |
| 3 | 模型配置与前置一致 | 通过 | mock-fenced/null | mock-fenced/null |
