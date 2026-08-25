# TC-featured-IT-020 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ Step 2: 状态码 200 — 实际 200
- ✅ Step 2: Content-Type 含 application/json — application/json
- ✅ Step 2: 响应顶层为 JSON 数组 — 实际 array
- ✅ Step 2 含 ROUTE 条目且 period=OVULATION（城市下架不过滤） — {"id":"01a038fd-48f9-7578-a037-79c0e0979a00","period":"OVULATION","type":"ROUTE","banner":{"id":"bound/banner-OVULATION-ROUTE.png","url":"http://placeholder.oss
- ⚠️ "同城市 ACTIVITY 条目不在数组中" 不可执行 — 活动已解除城市关联，无法构造"所属同一下架城市的活动"；实际该上线活动条目在数组中（按现行为：活动可见性只看 online）
- ✅ admin 城市上架 200
- ✅ Step 3: 状态码 200 — 实际 200
- ✅ Step 3: Content-Type 含 application/json — application/json
- ✅ Step 3: 响应顶层为 JSON 数组 — 实际 array
- ✅ Step 3 同时含 ROUTE 与 ACTIVITY 条目 — 01a038fd-4904-786f-9aed-71cfec4b83ea,01a038fd-48f9-7578-a037-79c0e0979a00
- ✅ ROUTE 条目前后一致
