# TC-ai-IT-029 断言明细

> POST …/publish 发布切换生效模板

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 步骤3 204 | 通过 | 204 | 204 |
| 2 | activeTemplate.seq=4 | 通过 | 4 | {"id":"01a16f0a-3462-783e-af98-27b57ee90ffc","seq":4,"name":"V4","publishedAt":"2026-10-24T17:51:42.278067Z","publishedBy":"admin"} |
| 3 | activeTemplate.publishedBy=admin | 通过 | admin | admin |
| 4 | V3 active=false | 通过 | false | false |
| 5 | V4 active=true | 通过 | true | true |
| 6 | V4 publishedBy=admin、publishedAt 非空 | 通过 | admin + 非空 | admin 2026-10-24T17:51:42.278067Z |
