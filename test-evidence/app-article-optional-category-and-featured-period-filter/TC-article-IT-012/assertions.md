# TC-article-IT-012 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ 下线前列表: 状态码 200 — 实际 200
- ✅ 下线前列表: Content-Type 含 application/json — application/json
- ✅ 下线前列表: 响应顶层为 JSON 数组 — 实际 array
- ✅ 下线前列表含该文章
- ✅ admin 下线返回 200 — 实际 200
- ✅ 下线后列表: 状态码 200 — 实际 200
- ✅ 下线后列表: Content-Type 含 application/json — application/json
- ✅ 下线后列表: 响应顶层为 JSON 数组 — 实际 array
- ✅ 下线后列表不含该文章
- ✅ 详情返回 404 — 实际 404
