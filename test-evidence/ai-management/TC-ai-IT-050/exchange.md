# TC-ai-IT-050 请求/响应存证

> 重复提交返回原结果且不再调用模型
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

> 备注: 假方舟无调用计数接口；以「该生成的调用明细条数不变」作为不再调用模型的代理证据（每次调用模型都会写一条 attempt）。

## step 1（前置） 创建场景 t_8c38362c

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_8c38362c","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-47f8-7446-935c-987e128c258c",
 "sceneId": "t_8c38362c",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:47.192187217Z",
 "updatedAt": "2026-10-24T17:51:47.192187217Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-47f8-7446-935c-987e128c258c/models' \
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
  -d '{"sceneId":"t_8c38362c","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-4812-7770-834b-27e248ca17bc",
 "sceneId": "t_8c38362c",
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
 "createdAt": "2026-10-24T17:51:47.218422981Z",
 "updatedAt": "2026-10-24T17:51:47.218422981Z",
 "version": 0
}
```

## step 4（前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-4812-7770-834b-27e248ca17bc/test' \
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
 "testedAt": "2026-10-24T17:51:47.235410567Z"
}
```

## step 5（前置） 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-4812-7770-834b-27e248ca17bc' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-4812-7770-834b-27e248ca17bc",
 "sceneId": "t_8c38362c",
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
 "tested": true,
 "lastTestInputs": {
  "age": 28
 },
 "lastTestOutput": "{\"echo\":\"年龄28\"}",
 "lastTestContent": {
  "echo": "年龄28"
 },
 "lastTestModel": "mock-ok",
 "lastTestSuccess": true,
 "lastTestError": null,
 "lastTestLatencyMs": 3,
 "lastTestAt": "2026-10-24T17:51:47.235411Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:47.218423Z",
 "updatedAt": "2026-10-24T17:51:47.218423Z",
 "version": 0
}
```

## step 6（前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-4812-7770-834b-27e248ca17bc/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 7（前置） 首次提交

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-dc942e8a' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_8c38362c","clientRequestId":"c-1-6f870c62","inputs":{"age":28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a16f0a-484d-7d1b-a12e-8c9dc0199379",
 "clientRequestId": "c-1-6f870c62",
 "sceneId": "t_8c38362c",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 8 psql 首次后调用明细数

```sql
select count(*) from loves_ai_generation_attempt where generation_id='01a16f0a-484d-7d1b-a12e-8c9dc0199379'
```

```
1
```

## step 9 重复提交

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-dc942e8a' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_8c38362c","clientRequestId":"c-1-6f870c62","inputs":{"age":28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a16f0a-484d-7d1b-a12e-8c9dc0199379",
 "clientRequestId": "c-1-6f870c62",
 "sceneId": "t_8c38362c",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 10 psql 统计生成记录 client_request_id='c-1-6f870c62'

```sql
select count(*) from loves_ai_generation where client_request_id='c-1-6f870c62'
```

```
1
```

## step 11 psql 调用明细数

```sql
select count(*) from loves_ai_generation_attempt where generation_id='01a16f0a-484d-7d1b-a12e-8c9dc0199379'
```

```
1
```
