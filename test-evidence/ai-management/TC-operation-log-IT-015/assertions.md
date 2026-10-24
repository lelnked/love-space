# TC-operation-log-IT-015 断言明细

> 创建 AI 场景后异步产生 ai-scene:create 留痕

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 201 | 通过 | 201 | 201 |
| 2 | 命中 module=ai-scene action=create username=admin 新记录 | 通过 | 命中 | {"id":"01a16f0a-673d-7738-aca5-64399f4f6f9d","username":"admin","module":"ai-scene","action":"create","target":null,"createdAt":"2026-10-24T17:51:55.197408Z"} |
