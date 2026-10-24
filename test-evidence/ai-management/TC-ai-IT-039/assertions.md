# TC-ai-IT-039 断言明细

> 生成成功写完整生成记录与调用明细

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | 记录存在 | 通过 | 1 条 | {"status":"SUCCESS","attempt_count":1,"completed_at":"2026-10-24T17:51:45.617747+00:00","template_id":"01a16f0a-4188-7821-9452-e48bd22ec494","template_seq":1,"anonymous_user_id":"anon-a88dcd14"} |
| 2 | status=SUCCESS | 通过 | SUCCESS | SUCCESS |
| 3 | attempt_count=1 | 通过 | 1 | 1 |
| 4 | completed_at 非空 | 通过 | 非空 | 2026-10-24T17:51:45.617747+00:00 |
| 5 | template_id/seq 为生效模板 | 通过 | 01a16f0a-4188-7821-9452-e48bd22ec494/1 | 01a16f0a-4188-7821-9452-e48bd22ec494/1 |
| 6 | anonymous_user_id 与请求头一致 | 通过 | anon-a88dcd14 | anon-a88dcd14 |
| 7 | 明细 1 条 seq=1 INITIAL | 通过 | 1\|INITIAL | 1\|INITIAL |
