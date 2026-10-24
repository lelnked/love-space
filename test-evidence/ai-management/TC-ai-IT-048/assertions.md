# TC-ai-IT-048 断言明细

> 重试与切备用逐条写调用明细

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 200 SUCCESS | 通过 | 200 SUCCESS | 200 SUCCESS |
| 2 | 明细 3 条 seq 1/2/3 | 通过 | 1,2,3 | ["1","2","3"] |
| 3 | trigger_reason INITIAL/RETRY/FALLBACK | 通过 | INITIAL,RETRY,FALLBACK | ["INITIAL","RETRY","FALLBACK"] |
| 4 | 第 3 条 model 为备用 | 通过 | mock-ok | mock-ok |
| 5 | attempt_count=3 | 通过 | 3 | 3 |
| 6 | final_model=备用 | 通过 | mock-ok | mock-ok |
| 7 | tokens 为各次合计 | 通过 | 12/6 | 12/6 |

## 备注

- 主模型映射 mock-429（两次均 429），备用 mock-ok。
