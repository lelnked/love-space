# TC-article-IT-011 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ 栏目列表: 状态码 200 — 实际 200
- ✅ 栏目列表: Content-Type 含 application/json — application/json
- ✅ 栏目列表: 响应顶层为 JSON 数组 — 实际 array
- ✅ 栏目列表含 A 与 B — idxA=37 idxB=15
- ✅ B(sortOrder=1) 在 A(sortOrder=2) 前 — idxB=15 idxA=37
- ✅ 整体按 sortOrder 升序 — 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,3,5,5,5,5,5,5
- ✅ 每项含名称与 icon 签名 URL — {"id":"bound/cat011B.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/cat011B.png?Expires=1787664317&OS
- ✅ 文章列表: 状态码 200 — 实际 200
- ✅ 文章列表: Content-Type 含 application/json — application/json
- ✅ 文章列表: 响应顶层为 JSON 数组 — 实际 array
- ✅ 列表恰含 B 下两篇 — 01a038fd-43f5-79ec-ad6f-e37878320497,01a038fd-43e2-70c9-96f5-f1e085e84393
- ✅ sortOrder=1 在前 — 01a038fd-43f5-79ec-ad6f-e37878320497,01a038fd-43e2-70c9-96f5-f1e085e84393
- ✅ 每项含 image 签名 URL、coverTitle、title、subtitle、tags — {"id":"01a038fd-43f5-79ec-ad6f-e37878320497","image":{"id":"bound/art-2.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/art-2.png?Expires=1787664317&OSSAccessKeyId=placeholder&Signat
