# TC-featured-IT-013 断言明细

执行日期: 2026-09-02

- ✅ 返回 200
- ✅ content 恰含 X1/X2/X3（phases 包含语义）　— ['01a0622c-33f5-7a94-98f3-fc6cbb2c34c5', '01a0622c-33ee-7f44-b942-ea049997b6e6', '01a0622c-33fc-7958-aa5f-4ff42e09536a']
- ✅ sortOrder 依次 1、2、3　— [1, 2, 3]
- ✅ 每项 phases 为数组
- ✅ X2 的 phases 为 ["FOLLICULAR","LUTEAL"]　— ['FOLLICULAR', 'LUTEAL']
- ✅ 恰含 X2 一条（多周期条目在每个周期都出现）　— ['01a0622c-33f5-7a94-98f3-fc6cbb2c34c5']
- ✅ 恰含 Y 一条，不含 X1/X2/X3　— ['01a0622c-3402-7c0a-b79c-a9e99d66bbbb']

结论: ✅ 通过（7/7 断言通过）
