# TC-featured-IT-025 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ period+type: 状态码 200 — 实际 200
- ✅ period+type: Content-Type 含 application/json — application/json
- ✅ period+type: 响应顶层为 JSON 数组 — 实际 array
- ✅ 恰含 1 条: period=MENSTRUAL、type=ARTICLE、articleId 非空 — [{"id":"01a038fd-4b3e-74b1-9f40-77e16520eb1c","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/ban
- ✅ 不含 MENSTRUAL ACTIVITY 与 FOLLICULAR ARTICLE
