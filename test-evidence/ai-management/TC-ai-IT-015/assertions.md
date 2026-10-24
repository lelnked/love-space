# TC-ai-IT-015 断言明细

> POST /api/admin/ai/prompt-templates 新建模板自动分配 seq

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 201 | 通过 | 201 | 201 |
| 2 | seq=3 | 通过 | 3 | 3 |
| 3 | tested=false | 通过 | false | false |
| 4 | active=false | 通过 | false | false |
| 5 | createdBy=admin | 通过 | admin | admin |
| 6 | variables 与提交一致 | 通过 | [{"name":"age","required":true,"description":"年龄"}] | [{"name":"age","required":true,"description":"年龄"}] |
| 7 | 契约字段 TemplateDetail | 通过 | id,sceneId,seq,name,systemPrompt,userPromptTemplate,variables,remark,tested,lastTestInputs,lastTestOutput,lastTestContent,lastTestModel,lastTestSuccess,lastTestError,lastTestLatencyMs,lastTestAt,lastTestBy,publishedAt,publishedBy,createdBy,updatedBy,createdAt,updatedAt,version,active | 一致 |
