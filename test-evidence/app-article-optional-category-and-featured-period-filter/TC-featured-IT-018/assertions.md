# TC-featured-IT-018 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ Step 2: 初始: 200 且为数组 — 实际 200
- ✅ Step 2 含该条目且 period=OVULATION、type=ROUTE、routeId 非空 — {"id":"01a038fd-4700-7dc1-b9d2-b3457d8afd71","period":"OVULATION","type":"ROUTE","banner":{"id":"bound/banner-OVULATION-ROUTE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-O
- ✅ admin 大使下线 200
- ✅ Step 3: 大使下线后: 200 且为数组 — 实际 200
- ✅ Step 3 不含该条目
- ✅ admin 大使恢复上线 200
- ✅ Step 4: 大使恢复后: 200 且为数组 — 实际 200
- ✅ Step 4 条目重新出现 — 01a038fd-4700-7dc1-b9d2-b3457d8afd71
