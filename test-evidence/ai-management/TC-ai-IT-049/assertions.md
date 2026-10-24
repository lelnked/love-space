# TC-ai-IT-049 断言明细

> 选填变量未传时生成记录 inputs 记 null

**结论**: 通过

| # | 断言 | 结果 | 期望 | 实际 |
|---|---|---|---|---|
| 1 | inputs 含 user_input 且为 null | 通过 | {"age":28,"user_input":null} | {"age":28,"user_input":null} |
| 2 | user 内容中该变量替换为空串 | 通过 | 年龄28，补充[] | 年龄28，补充[] |
