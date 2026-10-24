# TC-ai-IT-041 断言明细

> POST /api/app/ai/generations 已处理的失败返回 200

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | status=FORMAT_ERROR | 通过 | FORMAT_ERROR | FORMAT_ERROR |
| 3 | content=null | 通过 | null | null |
| 4 | 记录 FORMAT_ERROR 且 completed_at 非空 | 通过 | FORMAT_ERROR\|true | FORMAT_ERROR\|true |
| 5 | 补测 200 + content=null + 失败态 | 通过 | 200 / null / 非 SUCCESS | 200 EMPTY |

## 备注

- 假方舟只有一个「必返回非 JSON」的模型（mock-format-error），主备不能相同；故主断言用 主=mock-format-error、无备用 → FORMAT_ERROR。另以 备用=mock-empty 补测「配了备用且主备均失败」时同样 200 + content=null（终态取备用结果）。
