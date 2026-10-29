# TC-ai-IT-100 断言明细

> 场景关闭深度思考时生成参数快照 thinkingEnabled=false

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 前置场景 thinkingEnabled=false | 通过 |  |
| 2 | 步骤2 返回 200 status=SUCCESS | 通过 | (200, 'SUCCESS') |
| 3 | 步骤3 返回 200 | 通过 | 200 |
| 4 | Content-Type 含 application/json | 通过 | application/json |
| 5 | requestParams.thinkingEnabled 键存在且为布尔 false | 通过 | False |

**结论**: 通过
