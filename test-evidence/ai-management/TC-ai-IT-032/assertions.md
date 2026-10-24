# TC-ai-IT-032 断言明细

> POST …/publish 禁用场景也可发布

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 204 | 通过 | 204 | 204 |
| 2 | activeTemplate 指向该模板 | 通过 | 01a16f0a-3636-73fb-9dae-8a3ae816f2cf | {"id":"01a16f0a-3636-73fb-9dae-8a3ae816f2cf","seq":1,"name":"模板","publishedAt":"2026-10-24T17:51:42.690467Z","publishedBy":"admin"} |
| 3 | enabled 仍为 false | 通过 | false | false |
