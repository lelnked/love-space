# TC-operation-log-IT-016 请求/响应存证

> AI 测试连接与模板测试不产生留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_2ecbdbbf

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_2ecbdbbf","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-6764-7a5f-b10f-0c6187908609",
 "sceneId": "t_2ecbdbbf",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:55.236600545Z",
 "updatedAt": "2026-10-24T17:51:55.236600545Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-6764-7a5f-b10f-0c6187908609/models' \
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
  -d '{"sceneId":"t_2ecbdbbf","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-677d-7b28-b2d5-a4ae337234fc",
 "sceneId": "t_2ecbdbbf",
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
 "createdAt": "2026-10-24T17:51:55.261653005Z",
 "updatedAt": "2026-10-24T17:51:55.261653005Z",
 "version": 0
}
```

## step 4 ai-scene 基线

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-scene&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-6773-7462-864b-771c8c474bcf",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-6764-7a5f-b10f-0c6187908609",
   "createdAt": "2026-10-24T17:51:55.251229Z"
  },
  {
   "id": "01a16f0a-6767-72ea-a729-16d7bc8cfa91",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:55.239131Z"
  },
  {
   "id": "01a16f0a-673d-7738-aca5-64399f4f6f9d",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:55.197408Z"
  },
  {
   "id": "01a16f0a-6684-78b0-96d5-f2e5e9a08db4",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-6675-7301-8abc-88e0bdaeb498",
   "createdAt": "2026-10-24T17:51:55.012501Z"
  },
  {
   "id": "01a16f0a-6677-7a03-b580-8cd839146281",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.999587Z"
  },
  {
   "id": "01a16f0a-65fa-730f-a463-02e24e2a1e5f",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-65eb-7e91-abb9-34ddba7ef6b3",
   "createdAt": "2026-10-24T17:51:54.874144Z"
  },
  {
   "id": "01a16f0a-65ee-750d-831b-f8056cc9fcd4",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.862282Z"
  },
  {
   "id": "01a16f0a-6502-73c2-8340-54bd4c44989d",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-64f2-76e0-ab76-c52c584b328c",
   "createdAt": "2026-10-24T17:51:54.626181Z"
  },
  {
   "id": "01a16f0a-64f5-72b9-9fe9-81449e025ad2",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.613121Z"
  },
  {
   "id": "01a16f0a-6461-7a63-9293-6fbf16336325",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-6452-7987-ac3e-1024ee87d1a7",
   "createdAt": "2026-10-24T17:51:54.465604Z"
  },
  {
   "id": "01a16f0a-6455-795c-8780-5d41cdb33f9f",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.45354Z"
  },
  {
   "id": "01a16f0a-4c86-7168-b516-e1467b4d7988",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4c77-7056-8ba2-3819730b97ec",
   "createdAt": "2026-10-24T17:51:48.358036Z"
  },
  {
   "id": "01a16f0a-4c79-7f59-b47f-4f6303f25b8f",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.345916Z"
  },
  {
   "id": "01a16f0a-4c14-7a92-b316-795af83001ac",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4c05-7dca-95fa-534e16f6008f",
   "createdAt": "2026-10-24T17:51:48.244615Z"
  },
  {
   "id": "01a16f0a-4c08-760a-a573-06c4cb3210b4",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.232327Z"
  },
  {
   "id": "01a16f0a-4ba4-7a68-abbf-83042c9515ed",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4b96-7810-b4f9-73c44d887468",
   "createdAt": "2026-10-24T17:51:48.132606Z"
  },
  {
   "id": "01a16f0a-4b98-7f4f-a386-67fc64c516b6",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.12091Z"
  },
  {
   "id": "01a16f0a-4b37-7359-8f43-b2b40e1eeaad",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4b28-7363-b290-ade778dd0ac6",
   "createdAt": "2026-10-24T17:51:48.02317Z"
  },
  {
   "id": "01a16f0a-4b2a-7d39-9b98-347ebeb4985d",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.010761Z"
  },
  {
   "id": "01a16f0a-4b10-7b64-ae45-073ab2090212",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-enabled",
   "target": "01a16f0a-4a9b-7449-8178-fd38b217b615",
   "createdAt": "2026-10-24T17:51:47.984659Z"
  },
  {
   "id": "01a16f0a-4aaa-76f3-903c-ca1026cc3122",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4a9b-7449-8178-fd38b217b615",
   "createdAt": "2026-10-24T17:51:47.882379Z"
  },
  {
   "id": "01a16f0a-4a9d-7d82-8aff-ec74e092de1e",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.869787Z"
  },
  {
   "id": "01a16f0a-4a25-7682-811f-0e1626ab359e",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4a15-7bb5-9090-0c7ca46d0fae",
   "createdAt": "2026-10-24T17:51:47.749353Z"
  },
  {
   "id": "01a16f0a-4a18-74b2-a519-9728fa9ea87e",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.736249Z"
  },
  {
   "id": "01a16f0a-49d4-7282-a594-843b19f6d93e",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-49c4-7f3a-a87f-67939aeb36e9",
   "createdAt": "2026-10-24T17:51:47.668114Z"
  },
  {
   "id": "01a16f0a-49c7-787d-876d-045af71b2f7f",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.655489Z"
  },
  {
   "id": "01a16f0a-493b-7c00-b229-4c38d490995b",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-492c-75df-8cf8-5ecc8da2c95e",
   "createdAt": "2026-10-24T17:51:47.515707Z"
  },
  {
   "id": "01a16f0a-492f-7389-b831-99d5e69baac2",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.503157Z"
...(截断)
```

## step 5 ai-prompt 基线

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-prompt&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-6780-76eb-bc6a-46210d6142b5",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:55.26439Z"
  },
  {
   "id": "01a16f0a-66bd-7651-b99a-8e3c1cea1c67",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-6691-7d68-802c-710f2d7045a3",
   "createdAt": "2026-10-24T17:51:55.069342Z"
  },
  {
   "id": "01a16f0a-6694-791b-8401-e591838bc6b1",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:55.028527Z"
  },
  {
   "id": "01a16f0a-6631-7079-8748-13ea1b6a5aa6",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-6605-7811-9415-3aef93d72c7d",
   "createdAt": "2026-10-24T17:51:54.928984Z"
  },
  {
   "id": "01a16f0a-6608-74c8-bb74-dd0bf74ba868",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.88826Z"
  },
  {
   "id": "01a16f0a-6539-7368-a32d-6554130db874",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-650c-771c-b769-c7e983892658",
   "createdAt": "2026-10-24T17:51:54.681168Z"
  },
  {
   "id": "01a16f0a-650f-71d2-ae84-0d4a7d44d0e0",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.639064Z"
  },
  {
   "id": "01a16f0a-6498-705e-a366-6c15e2fe84d7",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-646c-76a2-885d-ccc2eaf13d77",
   "createdAt": "2026-10-24T17:51:54.519978Z"
  },
  {
   "id": "01a16f0a-646f-74d6-98f2-f907d301d690",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.479258Z"
  },
  {
   "id": "01a16f0a-50a4-798a-a8fb-22f4661240a3",
   "username": "admin",
   "module": "ai-prompt",
   "action": "update",
   "target": "01a16f0a-4c91-789f-b081-458e7de315e2",
   "createdAt": "2026-10-24T17:51:49.412551Z"
  },
  {
   "id": "01a16f0a-4c94-786d-865c-ce3e176df7a7",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.372478Z"
  },
  {
   "id": "01a16f0a-4c4d-7acb-b04f-15bef77f7bd8",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4c1e-7e80-a4a6-6e2c222346ad",
   "createdAt": "2026-10-24T17:51:48.301622Z"
  },
  {
   "id": "01a16f0a-4c21-7ddf-8968-4b2d0e33f822",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.2578Z"
  },
  {
   "id": "01a16f0a-4bdb-7495-b188-fcd71a8847f9",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4bae-79d0-9813-a15d4096bd97",
   "createdAt": "2026-10-24T17:51:48.187239Z"
  },
  {
   "id": "01a16f0a-4bb1-78b0-82ca-fdcd158dfda2",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.1455Z"
  },
  {
   "id": "01a16f0a-4b6f-71d3-8542-7b5e4f9180fb",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4b41-7f9a-a699-4b17f141951e",
   "createdAt": "2026-10-24T17:51:48.079071Z"
  },
  {
   "id": "01a16f0a-4b45-72c0-9d4d-3bd8a665601a",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.037121Z"
  },
  {
   "id": "01a16f0a-4ae4-72e2-8a8f-333627ae281b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4ab5-78d0-a3a2-af3028962c56",
   "createdAt": "2026-10-24T17:51:47.940134Z"
  },
  {
   "id": "01a16f0a-4ab8-75dc-8391-2b71bea39909",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.896318Z"
  },
  {
   "id": "01a16f0a-4a5c-7f6b-b057-d3fd6b648b19",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4a2f-76fd-82fa-d2f6cd39cdca",
   "createdAt": "2026-10-24T17:51:47.804914Z"
  },
  {
   "id": "01a16f0a-4a32-70a4-8c85-9c72d13d1a70",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.761993Z"
  },
  {
   "id": "01a16f0a-4a0a-78d3-82c5-98744e805739",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-49df-7208-88d4-f56497073ce6",
   "createdAt": "2026-10-24T17:51:47.722511Z"
  },
  {
   "id": "01a16f0a-49e1-7d18-8654-a814db50b07a",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.681753Z"
  },
  {
   "id": "01a16f0a-4975-7314-8c01-802a59832509",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4946-7293-a9a5-68d81904ef00",
   "createdAt": "2026-10-24T17:51:47.573141Z"
  },
  {
   "id": "01a16f0a-4949-7391-8481-cae2dc1f9336",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.529175Z"
  },
  {
   "id": "01a16f0a-48ee-7b1a-8f02-12db57166ff1",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-48c2-7a40-93fd-7ddee597d9f0",
   "createdAt": "2026-10-24T17:51:47.438659Z"
  },
  {
   "id": "01a16f0a-48c5-76ea-9ab6-11cfb031bcbf",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.39739Z"
  },
  {
   "id": "01a16f0a-483e-72dd-b6b2-64de16ea27bf",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4812-7770-834b-27e248ca17bc",
   "createdAt": "2026-10-24T17:51:47.262134Z"
  },
  {
   "id": "
...(截断)
```

## step 6 测试连接 model-a(mock-ok)

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/models/test-connection' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"model":"mock-ok"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "ok": true,
 "latencyMs": 3,
 "message": "可达"
}
```

## step 7 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-677d-7b28-b2d5-a4ae337234fc/test' \
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
 "latencyMs": 3,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-24T17:51:55.323577318Z"
}
```

## step 8 ai-scene 2s 后

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-scene&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-6773-7462-864b-771c8c474bcf",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-6764-7a5f-b10f-0c6187908609",
   "createdAt": "2026-10-24T17:51:55.251229Z"
  },
  {
   "id": "01a16f0a-6767-72ea-a729-16d7bc8cfa91",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:55.239131Z"
  },
  {
   "id": "01a16f0a-673d-7738-aca5-64399f4f6f9d",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:55.197408Z"
  },
  {
   "id": "01a16f0a-6684-78b0-96d5-f2e5e9a08db4",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-6675-7301-8abc-88e0bdaeb498",
   "createdAt": "2026-10-24T17:51:55.012501Z"
  },
  {
   "id": "01a16f0a-6677-7a03-b580-8cd839146281",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.999587Z"
  },
  {
   "id": "01a16f0a-65fa-730f-a463-02e24e2a1e5f",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-65eb-7e91-abb9-34ddba7ef6b3",
   "createdAt": "2026-10-24T17:51:54.874144Z"
  },
  {
   "id": "01a16f0a-65ee-750d-831b-f8056cc9fcd4",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.862282Z"
  },
  {
   "id": "01a16f0a-6502-73c2-8340-54bd4c44989d",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-64f2-76e0-ab76-c52c584b328c",
   "createdAt": "2026-10-24T17:51:54.626181Z"
  },
  {
   "id": "01a16f0a-64f5-72b9-9fe9-81449e025ad2",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.613121Z"
  },
  {
   "id": "01a16f0a-6461-7a63-9293-6fbf16336325",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-6452-7987-ac3e-1024ee87d1a7",
   "createdAt": "2026-10-24T17:51:54.465604Z"
  },
  {
   "id": "01a16f0a-6455-795c-8780-5d41cdb33f9f",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.45354Z"
  },
  {
   "id": "01a16f0a-4c86-7168-b516-e1467b4d7988",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4c77-7056-8ba2-3819730b97ec",
   "createdAt": "2026-10-24T17:51:48.358036Z"
  },
  {
   "id": "01a16f0a-4c79-7f59-b47f-4f6303f25b8f",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.345916Z"
  },
  {
   "id": "01a16f0a-4c14-7a92-b316-795af83001ac",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4c05-7dca-95fa-534e16f6008f",
   "createdAt": "2026-10-24T17:51:48.244615Z"
  },
  {
   "id": "01a16f0a-4c08-760a-a573-06c4cb3210b4",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.232327Z"
  },
  {
   "id": "01a16f0a-4ba4-7a68-abbf-83042c9515ed",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4b96-7810-b4f9-73c44d887468",
   "createdAt": "2026-10-24T17:51:48.132606Z"
  },
  {
   "id": "01a16f0a-4b98-7f4f-a386-67fc64c516b6",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.12091Z"
  },
  {
   "id": "01a16f0a-4b37-7359-8f43-b2b40e1eeaad",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4b28-7363-b290-ade778dd0ac6",
   "createdAt": "2026-10-24T17:51:48.02317Z"
  },
  {
   "id": "01a16f0a-4b2a-7d39-9b98-347ebeb4985d",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.010761Z"
  },
  {
   "id": "01a16f0a-4b10-7b64-ae45-073ab2090212",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-enabled",
   "target": "01a16f0a-4a9b-7449-8178-fd38b217b615",
   "createdAt": "2026-10-24T17:51:47.984659Z"
  },
  {
   "id": "01a16f0a-4aaa-76f3-903c-ca1026cc3122",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4a9b-7449-8178-fd38b217b615",
   "createdAt": "2026-10-24T17:51:47.882379Z"
  },
  {
   "id": "01a16f0a-4a9d-7d82-8aff-ec74e092de1e",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.869787Z"
  },
  {
   "id": "01a16f0a-4a25-7682-811f-0e1626ab359e",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4a15-7bb5-9090-0c7ca46d0fae",
   "createdAt": "2026-10-24T17:51:47.749353Z"
  },
  {
   "id": "01a16f0a-4a18-74b2-a519-9728fa9ea87e",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.736249Z"
  },
  {
   "id": "01a16f0a-49d4-7282-a594-843b19f6d93e",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-49c4-7f3a-a87f-67939aeb36e9",
   "createdAt": "2026-10-24T17:51:47.668114Z"
  },
  {
   "id": "01a16f0a-49c7-787d-876d-045af71b2f7f",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.655489Z"
  },
  {
   "id": "01a16f0a-493b-7c00-b229-4c38d490995b",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-492c-75df-8cf8-5ecc8da2c95e",
   "createdAt": "2026-10-24T17:51:47.515707Z"
  },
  {
   "id": "01a16f0a-492f-7389-b831-99d5e69baac2",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.503157Z"
...(截断)
```

## step 9 ai-prompt 2s 后

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-prompt&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-6780-76eb-bc6a-46210d6142b5",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:55.26439Z"
  },
  {
   "id": "01a16f0a-66bd-7651-b99a-8e3c1cea1c67",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-6691-7d68-802c-710f2d7045a3",
   "createdAt": "2026-10-24T17:51:55.069342Z"
  },
  {
   "id": "01a16f0a-6694-791b-8401-e591838bc6b1",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:55.028527Z"
  },
  {
   "id": "01a16f0a-6631-7079-8748-13ea1b6a5aa6",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-6605-7811-9415-3aef93d72c7d",
   "createdAt": "2026-10-24T17:51:54.928984Z"
  },
  {
   "id": "01a16f0a-6608-74c8-bb74-dd0bf74ba868",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.88826Z"
  },
  {
   "id": "01a16f0a-6539-7368-a32d-6554130db874",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-650c-771c-b769-c7e983892658",
   "createdAt": "2026-10-24T17:51:54.681168Z"
  },
  {
   "id": "01a16f0a-650f-71d2-ae84-0d4a7d44d0e0",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.639064Z"
  },
  {
   "id": "01a16f0a-6498-705e-a366-6c15e2fe84d7",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-646c-76a2-885d-ccc2eaf13d77",
   "createdAt": "2026-10-24T17:51:54.519978Z"
  },
  {
   "id": "01a16f0a-646f-74d6-98f2-f907d301d690",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.479258Z"
  },
  {
   "id": "01a16f0a-50a4-798a-a8fb-22f4661240a3",
   "username": "admin",
   "module": "ai-prompt",
   "action": "update",
   "target": "01a16f0a-4c91-789f-b081-458e7de315e2",
   "createdAt": "2026-10-24T17:51:49.412551Z"
  },
  {
   "id": "01a16f0a-4c94-786d-865c-ce3e176df7a7",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.372478Z"
  },
  {
   "id": "01a16f0a-4c4d-7acb-b04f-15bef77f7bd8",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4c1e-7e80-a4a6-6e2c222346ad",
   "createdAt": "2026-10-24T17:51:48.301622Z"
  },
  {
   "id": "01a16f0a-4c21-7ddf-8968-4b2d0e33f822",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.2578Z"
  },
  {
   "id": "01a16f0a-4bdb-7495-b188-fcd71a8847f9",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4bae-79d0-9813-a15d4096bd97",
   "createdAt": "2026-10-24T17:51:48.187239Z"
  },
  {
   "id": "01a16f0a-4bb1-78b0-82ca-fdcd158dfda2",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.1455Z"
  },
  {
   "id": "01a16f0a-4b6f-71d3-8542-7b5e4f9180fb",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4b41-7f9a-a699-4b17f141951e",
   "createdAt": "2026-10-24T17:51:48.079071Z"
  },
  {
   "id": "01a16f0a-4b45-72c0-9d4d-3bd8a665601a",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.037121Z"
  },
  {
   "id": "01a16f0a-4ae4-72e2-8a8f-333627ae281b",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4ab5-78d0-a3a2-af3028962c56",
   "createdAt": "2026-10-24T17:51:47.940134Z"
  },
  {
   "id": "01a16f0a-4ab8-75dc-8391-2b71bea39909",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.896318Z"
  },
  {
   "id": "01a16f0a-4a5c-7f6b-b057-d3fd6b648b19",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4a2f-76fd-82fa-d2f6cd39cdca",
   "createdAt": "2026-10-24T17:51:47.804914Z"
  },
  {
   "id": "01a16f0a-4a32-70a4-8c85-9c72d13d1a70",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.761993Z"
  },
  {
   "id": "01a16f0a-4a0a-78d3-82c5-98744e805739",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-49df-7208-88d4-f56497073ce6",
   "createdAt": "2026-10-24T17:51:47.722511Z"
  },
  {
   "id": "01a16f0a-49e1-7d18-8654-a814db50b07a",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.681753Z"
  },
  {
   "id": "01a16f0a-4975-7314-8c01-802a59832509",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4946-7293-a9a5-68d81904ef00",
   "createdAt": "2026-10-24T17:51:47.573141Z"
  },
  {
   "id": "01a16f0a-4949-7391-8481-cae2dc1f9336",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.529175Z"
  },
  {
   "id": "01a16f0a-48ee-7b1a-8f02-12db57166ff1",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-48c2-7a40-93fd-7ddee597d9f0",
   "createdAt": "2026-10-24T17:51:47.438659Z"
  },
  {
   "id": "01a16f0a-48c5-76ea-9ab6-11cfb031bcbf",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.39739Z"
  },
  {
   "id": "01a16f0a-483e-72dd-b6b2-64de16ea27bf",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a16f0a-4812-7770-834b-27e248ca17bc",
   "createdAt": "2026-10-24T17:51:47.262134Z"
  },
  {
   "id": "
...(截断)
```
