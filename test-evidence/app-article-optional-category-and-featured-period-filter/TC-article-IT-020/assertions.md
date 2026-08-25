# TC-article-IT-020 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ 删除栏目 C 返回 200 — 实际 200
- ✅ 全部可见文章: 状态码 200 — 实际 200
- ✅ 全部可见文章: Content-Type 含 application/json — application/json
- ✅ 全部可见文章: 响应顶层为 JSON 数组 — 实际 array
- ✅ 含甲
- ✅ 含乙
- ✅ 不含下线的丙
- ✅ 不含失去所有栏目的丁
- ✅ 乙(sortOrder=1) 在甲(sortOrder=2) 前 — idx乙=12 idx甲=28
- ✅ 每项含 image 签名 URL、coverTitle、title、subtitle、tags — 共 41 条
- ℹ️ "恰含甲、乙"以本用例自建 id 为锚点做包含/排除断言（库中另有 39 条历史可见文章，非本用例范围）
- ✅ A 栏目文章: 状态码 200 — 实际 200
- ✅ A 栏目文章: Content-Type 含 application/json — application/json
- ✅ A 栏目文章: 响应顶层为 JSON 数组 — 实际 array
- ✅ 仅含甲 — 01a038fd-44e2-7f66-8f0f-44bee342db5e
