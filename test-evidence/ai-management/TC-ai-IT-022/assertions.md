# TC-ai-IT-022 断言明细

> GET /api/admin/ai/prompt-templates/page 按 seq 倒序且仅生效项 active

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | seq 顺序 3,2,1 | 通过 | 3,2,1 | [3,2,1] |
| 3 | 仅 V2 active=true | 通过 | [2] | [2] |
| 4 | 条目含规定字段 | 通过 | id,seq,name,remark,active,tested,lastTestAt,publishedAt,publishedBy,updatedBy,updatedAt | ["id","seq","name","remark","active","tested","lastTestAt","publishedAt","publishedBy","updatedBy","updatedAt"] |
| 5 | 契约字段 TemplateItem | 通过 | id,seq,name,remark,active,tested,lastTestAt,publishedAt,publishedBy,updatedBy,updatedAt | 一致 |
| 6 | 步骤4 404 | 通过 | 404 | 404 |
