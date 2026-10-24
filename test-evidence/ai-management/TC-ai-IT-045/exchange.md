# TC-ai-IT-045 请求/响应存证

> POST /api/app/ai/generations 请求头与 body 参数校验
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_4eb41c38

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_4eb41c38","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-44fb-7d1f-beb0-a52818e5d0a8",
 "sceneId": "t_4eb41c38",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:46.427740355Z",
 "updatedAt": "2026-10-24T17:51:46.427740355Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-44fb-7d1f-beb0-a52818e5d0a8/models' \
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
  -d '{"sceneId":"t_4eb41c38","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-4516-73d7-a89c-76ae93e4e9e2",
 "sceneId": "t_4eb41c38",
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
 "createdAt": "2026-10-24T17:51:46.454144196Z",
 "updatedAt": "2026-10-24T17:51:46.454144196Z",
 "version": 0
}
```

## step 4（前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-4516-73d7-a89c-76ae93e4e9e2/test' \
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
 "testedAt": "2026-10-24T17:51:46.472525128Z"
}
```

## step 5（前置） 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-4516-73d7-a89c-76ae93e4e9e2' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-4516-73d7-a89c-76ae93e4e9e2",
 "sceneId": "t_4eb41c38",
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
 "lastTestAt": "2026-10-24T17:51:46.472525Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:46.454144Z",
 "updatedAt": "2026-10-24T17:51:46.454144Z",
 "version": 0
}
```

## step 6（前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-4516-73d7-a89c-76ae93e4e9e2/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 7 不带 X-Anonymous-Id

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_4eb41c38","clientRequestId":"c-ab001e5b-5050-4b3a-b89d-607fbc19de18","inputs":{"age":28}}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "缺少请求头 X-Anonymous-Id",
 "path": "/api/app/ai/generations"
}
```

## step 8 X-Anonymous-Id 129 字符

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_4eb41c38","clientRequestId":"c-1f14f2d2-5121-46fd-ba7a-53f4241e520d","inputs":{"age":28}}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "X-Anonymous-Id 长度不能超过 128 个字符",
 "path": "/api/app/ai/generations"
}
```

## step 9 缺 clientRequestId

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-3b631ecb' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_4eb41c38","inputs":{"age":28}}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "clientRequestId: clientRequestId 不能为空",
 "path": "/api/app/ai/generations"
}
```

## step 10 clientRequestId 65 字符

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-3b631ecb' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_4eb41c38","clientRequestId":"ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc","inputs":{"age":28}}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "clientRequestId: clientRequestId 长度不能超过 64 个字符",
 "path": "/api/app/ai/generations"
}
```

## step 11 appVersion 33 字符

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-3b631ecb' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_4eb41c38","clientRequestId":"c-d408306f-21ea-42f3-b602-8defc273a6ea","appVersion":"999999999999999999999999999999999","inputs":{"age":28}}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "appVersion: appVersion 长度不能超过 32 个字符",
 "path": "/api/app/ai/generations"
}
```

## step 12 缺 sceneId

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-3b631ecb' \
  -H 'Content-Type: application/json' \
  -d '{"clientRequestId":"c-b0d5fc74-101a-4e7c-bb8d-3063815a8381","inputs":{"age":28}}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "sceneId: sceneId 不能为空",
 "path": "/api/app/ai/generations"
}
```

## step 13 psql 统计生成记录 client_request_id in ('c-ab001e5b-5050-4b3a-b89d-607fbc19de18','c-1f14f2d2-5121-46fd-ba7a-53f4241e520d','ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc','c-d408306f-21ea-42f3-b602-8defc273a6ea','c-b0d5fc74-101a-4e7c-bb8d-3063815a8381') or anonymous_user_id in ('anon-3b631ecb', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa') or scene_id='t_4eb41c38'

```sql
select count(*) from loves_ai_generation where client_request_id in ('c-ab001e5b-5050-4b3a-b89d-607fbc19de18','c-1f14f2d2-5121-46fd-ba7a-53f4241e520d','ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc','c-d408306f-21ea-42f3-b602-8defc273a6ea','c-b0d5fc74-101a-4e7c-bb8d-3063815a8381') or anonymous_user_id in ('anon-3b631ecb', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa') or scene_id='t_4eb41c38'
```

```
0
```
