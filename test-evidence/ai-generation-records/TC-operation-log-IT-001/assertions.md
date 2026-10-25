# TC-operation-log-IT-001 断言明细

> 创建城市后异步产生 city:create 留痕

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 创建 200 | 通过 | 200 | 200 |
| 2 | 命中 city/create/admin 且 createdAt 非空 | 通过 | 命中 | {'id': '01a17341-df42-7583-9526-491483210f94', 'username': 'admin', 'module': 'city', 'action': 'create', 'target': None, 'createdAt': '2026-10-25T13:30:59.266278Z'} |
