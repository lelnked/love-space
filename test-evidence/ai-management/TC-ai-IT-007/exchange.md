# TC-ai-IT-007 请求/响应存证

> GET /api/admin/ai/scenes/options 全量下拉与详情 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_ae5b9fbc

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_ae5b9fbc","name":"下拉启用"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2125-757d-b527-435105325502",
 "sceneId": "t_ae5b9fbc",
 "name": "下拉启用",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.253285891Z",
 "updatedAt": "2026-10-24T17:51:37.253285891Z"
}
```

## step 2（前置） 创建场景 t_59ea11af

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_59ea11af","name":"下拉禁用"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2135-702b-a7e9-431492c4d700",
 "sceneId": "t_59ea11af",
 "name": "下拉禁用",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.268950784Z",
 "updatedAt": "2026-10-24T17:51:37.268950784Z"
}
```

## step 3（前置） 禁用场景 t_59ea11af

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-2135-702b-a7e9-431492c4d700/enabled' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"enabled":false}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 4 GET options

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/options' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
[
 {
  "id": "01a16f0a-2135-702b-a7e9-431492c4d700",
  "sceneId": "t_59ea11af",
  "name": "下拉禁用",
  "enabled": false
 },
 {
  "id": "01a16f0a-2125-757d-b527-435105325502",
  "sceneId": "t_ae5b9fbc",
  "name": "下拉启用",
  "enabled": true
 },
 {
  "id": "01a16f0a-20ca-7131-9a4f-bcf2d2a653ed",
  "sceneId": "mood_13909147",
  "name": "情绪回应-13909147",
  "enabled": false
 },
 {
  "id": "01a16f0a-20ba-731e-a363-6eb8f29e6812",
  "sceneId": "cycle_13909147",
  "name": "周期建议",
  "enabled": true
 },
 {
  "id": "01a16f0a-2070-7033-be03-6a2b38231328",
  "sceneId": "cycle_advice_19eabb3a",
  "name": "周期建议-改",
  "enabled": true
 },
 {
  "id": "01a16f0a-204c-78cc-be3e-818c312d7318",
  "sceneId": "x380f660fyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy",
  "name": "边界64",
  "enabled": true
 },
 {
  "id": "01a16f0a-203c-7d05-abb8-0996436dc47f",
  "sceneId": "z0",
  "name": "边界2",
  "enabled": true
 },
 {
  "id": "01a16f0a-1f31-77d4-ab39-b89fc5a8f487",
  "sceneId": "t_4b27b399",
  "name": "周期建议",
  "enabled": true
 },
 {
  "id": "01a16f0a-1f14-7ab3-b418-bd4622371eda",
  "sceneId": "cycle_advice_55c74ed7",
  "name": "周期建议",
  "enabled": true
 },
 {
  "id": "01a16f09-f724-7ae0-b492-6ee844fa10b6",
  "sceneId": "t_b9c1919c",
  "name": "周期建议",
  "enabled": true
 },
 {
  "id": "01a16f09-f6ec-77c9-b35b-0db40c6017ce",
  "sceneId": "cycle_advice_946e3298",
  "name": "周期建议",
  "enabled": true
 },
 {
  "id": "01a16f01-26d1-714a-b3c5-8992ca2186f6",
  "sceneId": "t_011b9845d41f",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-26c1-7410-8702-704c178be11e",
  "sceneId": "t_f6150df7e870",
  "name": "周期建议",
  "enabled": true
 },
 {
  "id": "01a16f01-26a5-7981-b595-8fa89d48f6c5",
  "sceneId": "t_6e39be5fc55d",
  "name": "新名称",
  "enabled": true
 },
 {
  "id": "01a16f01-2684-7cdf-9e05-02a89db04a17",
  "sceneId": "t_e7906151e315",
  "name": "测试场景",
  "enabled": false
 },
 {
  "id": "01a16f01-2669-77b9-b9c9-65efe8a5af49",
  "sceneId": "t_3b4ac6bd70d8_off",
  "name": "禁用场景",
  "enabled": false
 },
 {
  "id": "01a16f01-2663-77cf-bac7-4c8fb4cce170",
  "sceneId": "t_3b4ac6bd70d8_on",
  "name": "启用场景",
  "enabled": true
 },
 {
  "id": "01a16f01-262a-7735-aa30-9c5e38dd42ea",
  "sceneId": "t_9e12a89495e5",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-25fc-7f5e-8998-d2bb5986a044",
  "sceneId": "t_b000a460ad6c",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-25ef-7091-b5e0-0a025808e1f2",
  "sceneId": "t_4f484bb98321",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-25e0-702b-8236-7f42ef0b1820",
  "sceneId": "t_b590cb98e5a6",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-25ca-72fc-a299-d6fc823abd53",
  "sceneId": "t_732c55c4493e",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-25b2-754c-90ac-233faee1563e",
  "sceneId": "t_8110a2dd6915",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-2597-7a16-a979-170a414c1ab3",
  "sceneId": "t_8197053b7e41",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-256d-7da4-9f2c-239c38345adb",
  "sceneId": "t_6b988b8536a6",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-253e-71e8-b246-e58f766d9c8b",
  "sceneId": "t_7b1086a94574",
  "name": "测试场景",
  "enabled": false
 },
 {
  "id": "01a16f01-251b-74f0-93ce-d37e76df4f92",
  "sceneId": "t_b83d9f85f4e5",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-24f0-7bc8-9c18-88625b3e2799",
  "sceneId": "t_9bc86934e492",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-24d1-751a-bf6b-08b52ed66b33",
  "sceneId": "t_4f5c31b92da0",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-2482-7299-9d70-ada98291a32e",
  "sceneId": "t_e2527f4c2b91",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-245e-7381-9133-2a5436307363",
  "sceneId": "t_91f72551566f",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-242e-74af-a2cc-58cee59fc88b",
  "sceneId": "t_eb6e5c0d14d0",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-240b-7447-b346-f19cb229884e",
  "sceneId": "t_952e5027fc15",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-23e3-7366-a399-13551df378ab",
  "sceneId": "t_027a2b69cd76",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-238e-7abd-89b9-99c71258d4ea",
  "sceneId": "t_1c22494eba13",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-233a-7f3b-899c-d2edba9f32cc",
  "sceneId": "t_d5c03eb71278",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-22b8-76f5-890a-2ae3fc1e36d4",
  "sceneId": "t_e13d2d2be004",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-2295-70b6-972f-0d0b7cc85236",
  "sceneId": "t_687b7936095d",
  "name": "x",
  "enabled": true
 },
 {
  "id": "01a16f01-1fd7-77b4-8ba3-8f9bf5d1d760",
  "sceneId": "t_2b930d2cd03a",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-1fa4-7f61-942a-8936ee8fd8cf",
  "sceneId": "t_49ff51a29f1e",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-1f8e-7e37-a635-eecd06dbcc08",
  "sceneId": "t_9d3efdaa351c",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-1f7c-7fdb-ac92-6a0a3896a84e",
  "sceneId": "t_5df5bacd6e7a",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16f01-1f18-798b-acf6-b115ddd76e89",
  "sceneId": "t_9533ed281726",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16ef4-bb4b-7490-bfc9-23492bd25f6d",
  "sceneId": "t_1cae934a079c",
  "name": "测试场景",
  "enabled": true
 },
 {
  "id": "01a16ef4-bb3b-7fd0-870b-eb4163a78223",
  "sceneId": "t_ab802b285837",
  "name": "周期建议",
  "enabled": true
 },
 {
  "id": "01a16ef4-bb1d-7fc1-a6cd-d7651e5f3967",
  "sceneId": "t_604c5ece85f8",
  "name": "新名称",
  "enabled": true
 },
 {
  "id": "01a16ef4-bb06-7a4f-8484-fda8eb61f4c8",
  "sceneId": "t_632b2fe1b88e",
  "name": "测试场景",
  "enabled": false
 },
 {
  "id": "01a16ef4-baea-72c0-9922-c2dc5e85068a",
  "sceneId": "t_d0ecb550e137_off",
  "name": "禁用场景",
  "enabled": false
 },
 {
  "id": "01a16ef4-bae5-7ea8-bbab-18de0bf52120",
  "sceneId": "t_d0ecb550e1
...(截断)
```

## step 5 不存在 uuid 详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/1ff7acff-d70f-46ee-8164-93b2a0c5ff5b' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "场景不存在：1ff7acff-d70f-46ee-8164-93b2a0c5ff5b",
 "path": "/api/admin/ai/scenes/1ff7acff-d70f-46ee-8164-93b2a0c5ff5b"
}
```
