# TC-featured-IT-021 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ type=ARTICLE: 状态码 200 — 实际 200
- ✅ type=ARTICLE: Content-Type 含 application/json — application/json
- ✅ type=ARTICLE: 响应顶层为 JSON 数组 — 实际 array
- ✅ 仅含 ARTICLE 条目（period=MENSTRUAL、type=ARTICLE、articleId 非空） — [{"id":"01a038fd-49df-7768-b4a9-6d66165bd80f","period":"MENSTRUAL","type":"ARTICLE","banner":{"id":"bound/banner-MENSTRUAL-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/ban
- ✅ 不含 ACTIVITY/ROUTE 条目
- ✅ 不带参数: 状态码 200 — 实际 200
- ✅ 不带参数: Content-Type 含 application/json — application/json
- ✅ 不带参数: 响应顶层为 JSON 数组 — 实际 array
- ✅ 含全部 3 条 — 01a038fd-49df-7768-b4a9-6d66165bd80f,01a038fd-49d4-74fd-b88c-d4c42448d409,01a038fd-49c9-7841-8590-f22ab02b6776
