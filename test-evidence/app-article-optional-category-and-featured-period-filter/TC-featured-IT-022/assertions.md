# TC-featured-IT-022 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ type=ROUTE: 状态码 200 — 实际 200
- ✅ type=ROUTE: Content-Type 含 application/json — application/json
- ✅ type=ROUTE: 响应顶层为 JSON 数组 — 实际 array
- ✅ 响应体为 [] — []
