# TC-ai-IT-049 请求/响应存证

> 选填变量未传时生成记录 inputs 记 null
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_7ba088f4

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_7ba088f4","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-4773-79a3-8bb1-543ca63e21bf",
 "sceneId": "t_7ba088f4",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:47.059544848Z",
 "updatedAt": "2026-10-24T17:51:47.059544848Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-4773-79a3-8bb1-543ca63e21bf/models' \
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
  -d '{"sceneId":"t_7ba088f4","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}，补充[{{user_input}}]","variables":[{"name":"age","required":true,"description":"年龄"},{"name":"user_input","required":false,"description":"补充"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-478e-7487-924e-e9380de87e07",
 "sceneId": "t_7ba088f4",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "年龄{{age}}，补充[{{user_input}}]",
 "variables": [
  {
   "name": "age",
   "required": true,
   "description": "年龄"
  },
  {
   "name": "user_input",
   "required": false,
   "description": "补充"
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
 "createdAt": "2026-10-24T17:51:47.086240498Z",
 "updatedAt": "2026-10-24T17:51:47.086240498Z",
 "version": 0
}
```

## step 4（前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-478e-7487-924e-e9380de87e07/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{"age":28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"年龄28，补充[]\"}",
 "content": {
  "echo": "年龄28，补充[]"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 3,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-24T17:51:47.103566832Z"
}
```

## step 5（前置） 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-478e-7487-924e-e9380de87e07' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-478e-7487-924e-e9380de87e07",
 "sceneId": "t_7ba088f4",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "年龄{{age}}，补充[{{user_input}}]",
 "variables": [
  {
   "name": "age",
   "required": true,
   "description": "年龄"
  },
  {
   "name": "user_input",
   "required": false,
   "description": "补充"
  }
 ],
 "remark": null,
 "active": false,
 "tested": true,
 "lastTestInputs": {
  "age": 28
 },
 "lastTestOutput": "{\"echo\":\"年龄28，补充[]\"}",
 "lastTestContent": {
  "echo": "年龄28，补充[]"
 },
 "lastTestModel": "mock-ok",
 "lastTestSuccess": true,
 "lastTestError": null,
 "lastTestLatencyMs": 3,
 "lastTestAt": "2026-10-24T17:51:47.103567Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:47.08624Z",
 "updatedAt": "2026-10-24T17:51:47.08624Z",
 "version": 0
}
```

## step 6（前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-478e-7487-924e-e9380de87e07/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 7 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-0fdc3ba0' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_7ba088f4","clientRequestId":"c-16d2409e-4611-4ef1-b8ff-0d98bf37cdbb","inputs":{"age":28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a16f0a-47cc-7313-b404-07f12f843d25",
 "clientRequestId": "c-16d2409e-4611-4ef1-b8ff-0d98bf37cdbb",
 "sceneId": "t_7ba088f4",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28，补充[]"
 }
}
```

## step 8 psql inputs / request_messages

```sql
select json_build_object('i', inputs, 'm', request_messages) from loves_ai_generation where id='01a16f0a-47cc-7313-b404-07f12f843d25'
```

```
{"i" : {"age": 28, "user_input": null}, "m" : [{"role": "system", "content": "你是助手，只输出 JSON"}, {"role": "user", "content": "年龄28，补充[]"}]}
```
