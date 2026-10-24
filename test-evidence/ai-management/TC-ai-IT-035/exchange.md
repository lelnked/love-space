# TC-ai-IT-035 请求/响应存证

> 测试模板不产生留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_d3b16183

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_d3b16183","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-377a-7186-9c51-8ff06b60d694",
 "sceneId": "t_d3b16183",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:42.970046465Z",
 "updatedAt": "2026-10-24T17:51:42.970046465Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-377a-7186-9c51-8ff06b60d694/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 3（前置） 新建模板 模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_d3b16183","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-3797-70da-9477-a64512fc25b6",
 "sceneId": "t_d3b16183",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "年龄{{age}}",
 "variables": [
  {
   "name": "age",
   "required": true,
   "description": "年龄"
  }
 ],
 "remark": null,
 "active": false,
 "tested": false,
 "lastTestInputs": null,
 "lastTestOutput": null,
 "lastTestContent": null,
 "lastTestModel": null,
 "lastTestSuccess": null,
 "lastTestError": null,
 "lastTestLatencyMs": null,
 "lastTestAt": null,
 "lastTestBy": null,
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:42.999014145Z",
 "updatedAt": "2026-10-24T17:51:42.999014145Z",
 "version": 0
}
```

## step 4 基线

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-prompt&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-3799-7feb-a4ef-b39ecffb6b22",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:43.001955Z"
  },
  {
   "id": "01a16f0a-374b-7209-95fc-631e26406345",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-371b-7373-909e-e8854d534476",
   "createdAt": "2026-10-24T17:51:42.923083Z"
  },
  {
   "id": "01a16f0a-371e-71df-93f7-82803de1a1ab",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.878075Z"
  },
  {
   "id": "01a16f0a-36ae-7f17-9d99-016dfb42bec1",
   "username": "admin",
   "module": "ai-prompt",
   "action": "update",
   "target": "01a16f0a-369a-71da-a91b-0119d84aa682",
   "createdAt": "2026-10-24T17:51:42.766904Z"
  },
  {
   "id": "01a16f0a-369d-7c4e-b0ec-db6c5e65adb3",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.749726Z"
  },
  {
   "id": "01a16f0a-3665-7cae-ad2f-791bc7c31562",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3636-73fb-9dae-8a3ae816f2cf",
   "createdAt": "2026-10-24T17:51:42.693736Z"
  },
  {
   "id": "01a16f0a-3639-784d-9955-449f9352f630",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.649472Z"
  },
  {
   "id": "01a16f0a-35e0-788a-9c77-ae3a55a5400f",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.560479Z"
  },
  {
   "id": "01a16f0a-35b3-7e26-a57e-596747236f6e",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3517-7678-abe2-9a3728acaf7f",
   "createdAt": "2026-10-24T17:51:42.515837Z"
  },
  {
   "id": "01a16f0a-3597-7106-8740-1eb9664e803b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3536-79ee-a59d-5b732a8878ce",
   "createdAt": "2026-10-24T17:51:42.487003Z"
  },
  {
   "id": "01a16f0a-3568-7bf2-911d-69f966aa9437",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3517-7678-abe2-9a3728acaf7f",
   "createdAt": "2026-10-24T17:51:42.440686Z"
  },
  {
   "id": "01a16f0a-353a-7062-b828-3d63de35047b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.393968Z"
  },
  {
   "id": "01a16f0a-352a-79c0-9939-111a2f197b2b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.378561Z"
  },
  {
   "id": "01a16f0a-351a-797f-9f47-44ad37d64b25",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.362532Z"
  },
  {
   "id": "01a16f0a-34c9-7d1f-b3c5-f7d20cde055f",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3462-783e-af98-27b57ee90ffc",
   "createdAt": "2026-10-24T17:51:42.281743Z"
  },
  {
   "id": "01a16f0a-3499-7329-b913-d72ad94990bd",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3451-76e8-93a7-f5795cb3fb64",
   "createdAt": "2026-10-24T17:51:42.233138Z"
  },
  {
   "id": "01a16f0a-3466-73e4-8552-1cb411660f02",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.182196Z"
  },
  {
   "id": "01a16f0a-3455-7586-9a88-b8a88dff2ea0",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.165276Z"
  },
  {
   "id": "01a16f0a-3445-7d7e-bc14-71e05bef45d2",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.149786Z"
  },
  {
   "id": "01a16f0a-3435-725d-bc23-f0def79d7d19",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.133092Z"
  },
  {
   "id": "01a16f0a-33d0-7a57-91bb-2b0c2c8fcb27",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.032604Z"
  },
  {
   "id": "01a16f0a-3395-7f1e-88ad-322f46a154d2",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.973886Z"
  },
  {
   "id": "01a16f0a-3311-7daf-9905-db5dacca4465",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.8418Z"
  },
  {
   "id": "01a16f0a-32c1-7886-b926-633738bb63d8",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.761368Z"
  },
  {
   "id": "01a16f0a-324d-75b6-94b6-32bdef7e4ac4",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.645305Z"
  },
  {
   "id": "01a16f0a-31fa-7c4c-b504-b1c5fe9e8b8f",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.562713Z"
  },
  {
   "id": "01a16f0a-31ad-714f-9fb4-feed7cd4025b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3166-7241-94c6-64b8210a799d",
   "createdAt": "2026-10-24T17:51:41.485013Z"
  },
  {
   "id": "01a16f0a-317b-7ab3-8421-1f3afe7dc9e1",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.435618Z"
  },
  {
   "id": "01a16f0a-316a-789f-9fe4-a1ea532da9af",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.41
...(截断)
```

## step 5 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-3797-70da-9477-a64512fc25b6/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{"age":28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"年龄28\"}",
 "content": {
  "echo": "年龄28"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 4,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-24T17:51:43.031876302Z"
}
```

## step 6 2s 后再次查询

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-prompt&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-3799-7feb-a4ef-b39ecffb6b22",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:43.001955Z"
  },
  {
   "id": "01a16f0a-374b-7209-95fc-631e26406345",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-371b-7373-909e-e8854d534476",
   "createdAt": "2026-10-24T17:51:42.923083Z"
  },
  {
   "id": "01a16f0a-371e-71df-93f7-82803de1a1ab",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.878075Z"
  },
  {
   "id": "01a16f0a-36ae-7f17-9d99-016dfb42bec1",
   "username": "admin",
   "module": "ai-prompt",
   "action": "update",
   "target": "01a16f0a-369a-71da-a91b-0119d84aa682",
   "createdAt": "2026-10-24T17:51:42.766904Z"
  },
  {
   "id": "01a16f0a-369d-7c4e-b0ec-db6c5e65adb3",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.749726Z"
  },
  {
   "id": "01a16f0a-3665-7cae-ad2f-791bc7c31562",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3636-73fb-9dae-8a3ae816f2cf",
   "createdAt": "2026-10-24T17:51:42.693736Z"
  },
  {
   "id": "01a16f0a-3639-784d-9955-449f9352f630",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.649472Z"
  },
  {
   "id": "01a16f0a-35e0-788a-9c77-ae3a55a5400f",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.560479Z"
  },
  {
   "id": "01a16f0a-35b3-7e26-a57e-596747236f6e",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3517-7678-abe2-9a3728acaf7f",
   "createdAt": "2026-10-24T17:51:42.515837Z"
  },
  {
   "id": "01a16f0a-3597-7106-8740-1eb9664e803b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3536-79ee-a59d-5b732a8878ce",
   "createdAt": "2026-10-24T17:51:42.487003Z"
  },
  {
   "id": "01a16f0a-3568-7bf2-911d-69f966aa9437",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3517-7678-abe2-9a3728acaf7f",
   "createdAt": "2026-10-24T17:51:42.440686Z"
  },
  {
   "id": "01a16f0a-353a-7062-b828-3d63de35047b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.393968Z"
  },
  {
   "id": "01a16f0a-352a-79c0-9939-111a2f197b2b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.378561Z"
  },
  {
   "id": "01a16f0a-351a-797f-9f47-44ad37d64b25",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.362532Z"
  },
  {
   "id": "01a16f0a-34c9-7d1f-b3c5-f7d20cde055f",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3462-783e-af98-27b57ee90ffc",
   "createdAt": "2026-10-24T17:51:42.281743Z"
  },
  {
   "id": "01a16f0a-3499-7329-b913-d72ad94990bd",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3451-76e8-93a7-f5795cb3fb64",
   "createdAt": "2026-10-24T17:51:42.233138Z"
  },
  {
   "id": "01a16f0a-3466-73e4-8552-1cb411660f02",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.182196Z"
  },
  {
   "id": "01a16f0a-3455-7586-9a88-b8a88dff2ea0",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.165276Z"
  },
  {
   "id": "01a16f0a-3445-7d7e-bc14-71e05bef45d2",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.149786Z"
  },
  {
   "id": "01a16f0a-3435-725d-bc23-f0def79d7d19",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.133092Z"
  },
  {
   "id": "01a16f0a-33d0-7a57-91bb-2b0c2c8fcb27",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:42.032604Z"
  },
  {
   "id": "01a16f0a-3395-7f1e-88ad-322f46a154d2",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.973886Z"
  },
  {
   "id": "01a16f0a-3311-7daf-9905-db5dacca4465",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.8418Z"
  },
  {
   "id": "01a16f0a-32c1-7886-b926-633738bb63d8",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.761368Z"
  },
  {
   "id": "01a16f0a-324d-75b6-94b6-32bdef7e4ac4",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.645305Z"
  },
  {
   "id": "01a16f0a-31fa-7c4c-b504-b1c5fe9e8b8f",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.562713Z"
  },
  {
   "id": "01a16f0a-31ad-714f-9fb4-feed7cd4025b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-3166-7241-94c6-64b8210a799d",
   "createdAt": "2026-10-24T17:51:41.485013Z"
  },
  {
   "id": "01a16f0a-317b-7ab3-8421-1f3afe7dc9e1",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.435618Z"
  },
  {
   "id": "01a16f0a-316a-789f-9fe4-a1ea532da9af",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:41.41
...(截断)
```
