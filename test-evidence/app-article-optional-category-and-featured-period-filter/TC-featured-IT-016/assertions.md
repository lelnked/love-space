# TC-featured-IT-016 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ 周期推荐: 状态码 200 — 实际 200
- ✅ 周期推荐: Content-Type 含 application/json — application/json
- ✅ 周期推荐: 响应顶层为 JSON 数组 — 实际 array
- ✅ 无 MENSTRUAL/FOLLICULAR/OVULATION/LUTEAL 分组键
- ✅ 数组恰含 2 条 — 实际 2
- ✅ MENSTRUAL 条目: period=MENSTRUAL、type=ACTIVITY、activityId 非空 — {"id":"01a038fd-4563-7601-879c-40c8f628ef67","period":"MENSTRUAL","type":"ACTIVITY","banner":{"id":"bound/banner-MENSTRUAL-ACTIVITY.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/ba
- ✅ OVULATION 条目: period=OVULATION、type=ARTICLE、articleId 非空 — {"id":"01a038fd-4577-7683-9167-91acaf3d57e6","period":"OVULATION","type":"ARTICLE","banner":{"id":"bound/banner-OVULATION-ARTICLE.png","url":"http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/bann
- ✅ 每条 banner 为 http 开头签名 URL — http://placeholder.oss-cn-hangzhou.aliyuncs.com/bound/banner-OVULATION-ARTICLE.p
- ✅ 不含 LUTEAL 下线条目
- ✅ period 取值在契约枚举内
