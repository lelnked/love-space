# TC-ai-IT-083 断言明细

> PUT /api/admin/ai/generations/{id}/rating 失败记录可以评价

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 前置 app 生成 200 FORMAT_ERROR | 通过 | 200 FORMAT_ERROR | (200, 'FORMAT_ERROR') |
| 2 | 步骤3 204 | 通过 | 204 | (204, None) |
| 3 | status=FORMAT_ERROR、rating=UNUSABLE、ratingTags=[FORMAT_ERROR] | 通过 | ('FORMAT_ERROR', 'UNUSABLE', ['FORMAT_ERROR']) | ('FORMAT_ERROR', 'UNUSABLE', ['FORMAT_ERROR']) |
