# TC-ai-IT-010 断言明细

> PUT /api/admin/ai/scenes/{id}/models 模型不在白名单返回 400

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | {"primaryModel":"model-x"} → 400 | 通过 | 400 | 400 |
| 2 |   message 中文 | 通过 | 中文 | 主模型不在可选模型列表中：model-x |
| 3 | {"primaryModel":"mock-ok","backupModel":"model-x"} → 400 | 通过 | 400 | 400 |
| 4 |   message 中文 | 通过 | 中文 | 备用模型不在可选模型列表中：model-x |
| 5 | {} → 400 | 通过 | 400 | 400 |
| 6 |   message 中文 | 通过 | 中文 | 主模型不能为空 |
| 7 | 模型未变 primary=mock-ok | 通过 | mock-ok/null | mock-ok/null |
