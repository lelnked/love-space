# TC-ai-IT-101 断言明细

> 场景开启深度思考且切备用时参数快照 thinkingEnabled=true

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 步骤2 返回 200 status=SUCCESS | 通过 | (200, 'SUCCESS') |
| 2 | 步骤3 返回 200 | 通过 | 200 |
| 3 | requestParams.thinkingEnabled=true | 通过 | True |
| 4 | finalModel=mock-ok | 通过 | mock-ok |
| 5 | attempts 依次 INITIAL/RETRY/FALLBACK | 通过 | ['INITIAL', 'RETRY', 'FALLBACK'] |
| 6 | FALLBACK 条 model=mock-ok | 通过 | mock-ok |

**结论**: 通过
