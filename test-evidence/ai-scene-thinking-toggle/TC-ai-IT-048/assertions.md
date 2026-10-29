# TC-ai-IT-048 断言明细

> 重试与切备用逐条写调用明细

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 响应 200 status=SUCCESS | 通过 | (200, 'SUCCESS') |
| 2 | 明细 3 条 seq 1/2/3 INITIAL/RETRY/FALLBACK | 通过 | [('1', 'INITIAL'), ('2', 'RETRY'), ('3', 'FALLBACK')] |
| 3 | 第 3 条 model 为备用 mock-ok | 通过 | mock-ok |
| 4 | attempt_count=3 | 通过 | 3 |
| 5 | final_model=mock-ok | 通过 | mock-ok |
| 6 | tokens 为各次合计 | 通过 | ('12', '6') |

**结论**: 通过
