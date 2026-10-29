# TC-ai-IT-075 断言明细

> GET /api/admin/ai/generations/{id} 详情含调用明细且查看不留痕

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 前置生成 SUCCESS | 通过 | (200, 'SUCCESS') |
| 2 | 返回 200 | 通过 | 200 |
| 3 | status=SUCCESS | 通过 | SUCCESS |
| 4 | attemptCount=2 | 通过 | 2 |
| 5 | finalModel=mock-flaky | 通过 | mock-flaky |
| 6 | templateName=模板 / sceneName=周期建议 | 通过 | ('模板', '周期建议') |
| 7 | 含全部记录字段 | 通过 | [] |
| 8 | requestParams 含 thinkingEnabled（本 change 新增键） | 通过 | ['backupModel', 'maxRetries', 'maxTokens', 'primaryModel', 'temperature', 'thinkingEnabled', 'timeoutSeconds', 'topP'] |
| 9 | attempts 2 条 seq 升序 INITIAL/RETRY | 通过 | [(1, 'INITIAL'), (2, 'RETRY')] |
| 10 | seq1 含解析/校验错误 | 通过 | (None, "Unrecognized token 'not': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]") |
| 11 | seq2 成功 | 通过 | SUCCESS |
| 12 | 每条含 model/status/rawResponse/startedAt/finishedAt/latencyMs | 通过 |  |
| 13 | totalElements 与基线一致（查看不留痕） | 通过 | (8, 8) |

**结论**: 通过
