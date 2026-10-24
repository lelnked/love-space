# TC-ai-IT-058 断言明细

> 去重命中不占用额度

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤2 200 与原结果 | 通过 | {"requestId":"01a16f0a-66d4-7ddd-b1cc-e9a037b2aaee","clientRequestId":"c-1-80eef145","sceneId":"t_73547bf6","status":"SUCCESS","content":{"echo":"年龄28"}} | {"requestId":"01a16f0a-66d4-7ddd-b1cc-e9a037b2aaee","clientRequestId":"c-1-80eef145","sceneId":"t_73547bf6","status":"SUCCESS","content":{"echo":"年龄28"}} |
| 2 | 步骤3 429 | 通过 | 429 | 429 |
| 3 |   message=今日生成次数已达上限 | 通过 | 今日生成次数已达上限 | 今日生成次数已达上限 |

## 备注

- 默认额度 50（非 1）：先真实生成 1 条 SUCCESS（c-1-<后缀>），再直插 49 条今日记录使该匿名 ID 达上限，等价于用例「今日已有上限条数且含 c-1」；执行后删除填充记录。
