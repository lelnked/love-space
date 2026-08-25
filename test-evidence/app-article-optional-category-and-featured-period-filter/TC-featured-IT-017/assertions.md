# TC-featured-IT-017 断言明细

结果: ✅ 通过

契约: contracts/api-spec.json 对应 operation 仅声明 parameters（无 responses/schema），body 契约 schema 校验无可依据，仅按用例预期结果与参数枚举校验。

- ✅ Step 1: 初始: 200 且为数组 — 实际 200
- ✅ 初始含两条且 period 均 MENSTRUAL — 01a038fd-4607-7c95-93f5-d8390d15d7ba,01a038fd-45fb-7b1c-95f5-d84ee22ce8c7
- ✅ admin 活动下线 200
- ✅ Step 2: 活动下线后: 200 且为数组 — 实际 200
- ✅ Step 2 不含 ACTIVITY 条目、仍含 ARTICLE 条目 — 01a038fd-4607-7c95-93f5-d8390d15d7ba
- ✅ admin 活动恢复上线 200
- ⚠️ Step 3 "所属城市下架" 不可执行 — 活动已解除城市关联（ActivityUpsertRequest 无 cityId、app 侧可见性只看活动 online），living spec "所属城市上架" 措辞滞后，跳过该子步骤
- ✅ Step 3: 活动恢复后: 200 且为数组 — 实际 200
- ✅ Step 3 恢复后两条均在 — 01a038fd-4607-7c95-93f5-d8390d15d7ba,01a038fd-45fb-7b1c-95f5-d84ee22ce8c7
- ✅ admin 文章下线 200
- ✅ Step 4: 文章下线后: 200 且为数组 — 实际 200
- ✅ Step 4 不含 ARTICLE 条目、仍含 ACTIVITY 条目 — 01a038fd-45fb-7b1c-95f5-d84ee22ce8c7
- ✅ admin 文章恢复上线 200
- ✅ admin 删除文章 200
- ✅ Step 5: 文章删除后: 200 且为数组 — 实际 200
- ✅ Step 5 不含 ARTICLE 条目、仍含 ACTIVITY 条目，未报 500 — 01a038fd-45fb-7b1c-95f5-d84ee22ce8c7
