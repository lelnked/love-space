# TC-ai-IT-039 断言明细

> 生成成功写完整生成记录与调用明细

| # | 断言 | 结果 | 实际 |
|---|---|---|---|
| 1 | 生成 200 SUCCESS | 通过 | (200, 'SUCCESS') |
| 2 | 生成记录 1 条 | 通过 | 1 |
| 3 | status=SUCCESS | 通过 | SUCCESS |
| 4 | attempt_count=1 | 通过 | 1 |
| 5 | completed_at 非空 | 通过 | t |
| 6 | template_id/template_seq 为生效模板 | 通过 | ('01a1865e-8d71-7493-9845-d4c6528e7ed4', '1') |
| 7 | anonymous_user_id 与请求头一致 | 通过 | anon-a5770d7a |
| 8 | 调用明细 1 条 seq=1 INITIAL | 通过 | [['1', 'INITIAL']] |

**结论**: 通过
