# TC-featured-IT-023 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ 返回 400 — 实际 400
- ✅ 契约: UNKNOWN 不在 FeaturedCycleItemType 枚举内
