# TC-featured-IT-027 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ UNKNOWN 返回 400 — 实际 400
- ✅ 小写 menstrual 返回 400 — 实际 400
