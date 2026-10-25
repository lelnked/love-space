# TC-ai-IT-075 断言明细

> GET /api/admin/ai/generations/{id} 详情含调用明细且查看不留痕

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 app 生成 200 SUCCESS | 通过 | 200 SUCCESS | (200, 'SUCCESS') |
| 2 | 状态码 200 | 通过 | 200 | 200 |
| 3 | Content-Type 含 application/json | 通过 | application/json | application/json |
| 4 | status=SUCCESS | 通过 | SUCCESS | SUCCESS |
| 5 | attemptCount=2 | 通过 | 2 | 2 |
| 6 | finalModel=mock-flaky | 通过 | mock-flaky | mock-flaky |
| 7 | templateName=生效模板名称 | 通过 | 模板-gr_bc04b5dc | 模板-gr_bc04b5dc |
| 8 | sceneName=场景名称 | 通过 | flaky场景 | flaky场景 |
| 9 | 含记录正文字段 | 通过 | ['clientRequestId', 'appVersion', 'inputs', 'requestMessages', 'requestParams', 'rawOutput', 'content', 'promptTokens', 'completionTokens', 'latencyMs', 'createdAt', 'completedAt'] | 全部存在 |
| 10 | attempts 2 条按 seq 升序 | 通过 | [1, 2] | [1, 2] |
| 11 | seq1 INITIAL 含解析/校验错误 | 通过 | INITIAL + 错误信息 | ('INITIAL', None, '模型输出不是合法 JSON', "Unrecognized token 'not': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]") |
| 12 | seq2 RETRY 成功 | 通过 | RETRY/SUCCESS | ('RETRY', 'SUCCESS') |
| 13 | 每条含 model/status/rawResponse/起止时间/latencyMs | 通过 | 非空 | [{'model': True, 'status': True, 'rawResponse': True, 'startedAt': True, 'finishedAt': True, 'latencyMs': True}, {'model': True, 'status': True, 'rawResponse': True, 'startedAt': True, 'finishedAt': True, 'latencyMs': True}] |
| 14 | 契约: GenerationDetail 键集合 | 通过 | 30 键（见 api-spec description） | 一致 |
| 15 | 查看不留痕（totalElements 与基线一致） | 通过 | 7 | 7 |
