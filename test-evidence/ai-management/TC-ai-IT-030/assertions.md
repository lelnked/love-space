# TC-ai-IT-030 断言明细

> POST …/publish 发布旧版本即回退

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 204 | 通过 | 204 | 204 |
| 2 | activeTemplate.seq=1 | 通过 | 1 | {"id":"01a16f0a-3517-7678-abe2-9a3728acaf7f","seq":1,"name":"V1","publishedAt":"2026-10-24T17:51:42.511942Z","publishedBy":"admin"} |
