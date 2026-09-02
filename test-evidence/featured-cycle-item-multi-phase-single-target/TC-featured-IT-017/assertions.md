# TC-featured-IT-017 断言明细

执行日期: 2026-09-02

- ✅ 初始数组含 ACTIVITY/ARTICLE 两条　— ['01a0622e-c4bf-7ccf-a5f3-22d1914f6999', '01a0622e-c4bc-76c0-8be5-d9e4df503922']
- ✅ period 均为 ["MENSTRUAL"]
- ✅ 返回 200
- ✅ 数组不含该 ACTIVITY 条目　— ['01a0622e-c4bf-7ccf-a5f3-22d1914f6999']
- ✅ 未受影响的 ARTICLE 条目仍在
- ✅ ACTIVITY 条目重新出现
- ✅ 数组不含该 ARTICLE 条目　— ['01a0622e-c4bc-76c0-8be5-d9e4df503922']
- ✅ ACTIVITY 条目仍在
- ✅ 删除文章返回 200　— 实际 200: 
- ✅ 接口仍返回 200，不因关联实体缺失报 500　— 实际 200
- ✅ 数组不含该 ARTICLE 条目　— ['01a0622e-c4bc-76c0-8be5-d9e4df503922']
- ✅ ACTIVITY 条目仍在

结论: ✅ 通过（12/12 断言通过）
