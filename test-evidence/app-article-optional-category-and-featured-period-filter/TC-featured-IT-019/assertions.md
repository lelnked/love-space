# TC-featured-IT-019 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ 周期推荐: 状态码 200 — 实际 200
- ✅ 周期推荐: Content-Type 含 application/json — application/json
- ✅ 周期推荐: 响应顶层为 JSON 数组 — 实际 array
- ✅ 5 条且 period 均 MENSTRUAL — 实际 5
- ✅ 按 sortOrder 1,1,1,2,3 升序 — 实际 1,1,1,2,3
- ✅ sortOrder=2、3 位置正确
- ✅ 三个 sortOrder=1 按 createdAt 倒序（第5建, 第4建, 第2建） — 实际顺序=条目019-第5建 > 条目019-第4建 > 条目019-第2建
