# TC-featured-IT-024 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ period=MENSTRUAL: 状态码 200 — 实际 200
- ✅ period=MENSTRUAL: Content-Type 含 application/json — application/json
- ✅ period=MENSTRUAL: 响应顶层为 JSON 数组 — 实际 array
- ✅ 恰含 2 条且 period 均 MENSTRUAL — 01a038fd-4ab2-7df5-8321-d8357f9bcf86,01a038fd-4aa8-764c-9350-da73ef4aad1f
- ✅ 不含 FOLLICULAR 条目
- ✅ period=FOLLICULAR: 状态码 200 — 实际 200
- ✅ period=FOLLICULAR: Content-Type 含 application/json — application/json
- ✅ period=FOLLICULAR: 响应顶层为 JSON 数组 — 实际 array
- ✅ 恰含 1 条且 period=FOLLICULAR — [{"id":"01a038fd-4abd-7fd5-a821-74441227b3a4","period":"FOLLICULAR","type":"ARTICLE","banner":{"id":"bound/banner-FOLLICULAR-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/b
