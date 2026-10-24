# TC-ai-IT-045 断言明细

> POST /api/app/ai/generations 请求头与 body 参数校验

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 不带 X-Anonymous-Id → 400 | 通过 | 400 | 400 |
| 2 | X-Anonymous-Id 129 字符 → 400 | 通过 | 400 | 400 |
| 3 | 缺 clientRequestId → 400 | 通过 | 400 | 400 |
| 4 | clientRequestId 65 字符 → 400 | 通过 | 400 | 400 |
| 5 | appVersion 33 字符 → 400 | 通过 | 400 | 400 |
| 6 | 缺 sceneId → 400 | 通过 | 400 | 400 |
| 7 | 均不写生成记录 | 通过 | 0 | 0 |
