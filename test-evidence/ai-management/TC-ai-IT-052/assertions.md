# TC-ai-IT-052 断言明细

> PROCESSING 超时记录就地改为 TIMEOUT

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 | 通过 | 200 | 200 |
| 2 | status=TIMEOUT | 通过 | TIMEOUT | TIMEOUT |
| 3 | content=null | 通过 | null | null |
| 4 | 记录 TIMEOUT 且 completed_at 非空 | 通过 | TIMEOUT\|true | TIMEOUT\|true |
