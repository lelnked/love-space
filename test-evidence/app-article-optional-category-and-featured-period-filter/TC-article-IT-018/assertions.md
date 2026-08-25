# TC-article-IT-018 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ 文章列表: 状态码 200 — 实际 200
- ✅ 文章列表: Content-Type 含 application/json — application/json
- ✅ 文章列表: 响应顶层为 JSON 数组 — 实际 array
- ✅ 列表含甲与乙
- ✅ 甲 coverTitle=封面甲 — 封面甲
- ✅ 甲 tags=["约会"] — ["约会"]
- ✅ 乙 coverTitle 回落为 文章乙 — 文章乙
- ✅ 乙 tags=[] — []
- ✅ 两项均含 image、title、subtitle
