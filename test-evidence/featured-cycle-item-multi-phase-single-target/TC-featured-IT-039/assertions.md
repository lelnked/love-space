# TC-featured-IT-039 断言明细

执行日期: 2026-09-02

- ✅ 创建返回 200　— 实际 200: {"id":"01a0622d-60ed-7a75-a960-4b35d10193e0","phases":["MENSTRUAL","LUTEAL"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622d-60d8-708b-8e27-746792c0f2bc","relatedTitle":"route-m9p5","
- ✅ phases 按枚举声明顺序归一为 ["MENSTRUAL","LUTEAL"]（非传入顺序）　— ['MENSTRUAL', 'LUTEAL']
- ✅ MENSTRUAL 分页含该条目
- ✅ LUTEAL 分页含该条目（一条条目同时归属多个周期）
- ✅ 含重复值创建返回 200　— 实际 200: {"id":"01a0622d-6101-73d7-ab9a-f3cb5b94ef9a","phases":["MENSTRUAL","LUTEAL"],"type":"ROUTE","sortOrder":0,"online":false,"targetId":"01a0622d-60e6-79d8-a5b4-ed1223b81053","relatedTitle":"route-m9p8","
- ✅ 重复值已去重，phases=["MENSTRUAL","LUTEAL"] 长度 2　— ['MENSTRUAL', 'LUTEAL']

结论: ✅ 通过（6/6 断言通过）
