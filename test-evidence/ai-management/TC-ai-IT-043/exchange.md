# TC-ai-IT-043 请求/响应存证

> POST /api/app/ai/generations 场景不可用返回 409 且不写记录
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_67fa1756

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_67fa1756","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-4395-7dd8-8d46-2f9ccd76f620",
 "sceneId": "t_67fa1756",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:46.069801046Z",
 "updatedAt": "2026-10-24T17:51:46.069801046Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-4395-7dd8-8d46-2f9ccd76f620/models' \
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
  -d '{"sceneId":"t_67fa1756","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-43b1-799b-a9e0-1848820a02a3",
 "sceneId": "t_67fa1756",
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
 "createdAt": "2026-10-24T17:51:46.097557981Z",
 "updatedAt": "2026-10-24T17:51:46.097557981Z",
 "version": 0
}
```

## step 4（前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-43b1-799b-a9e0-1848820a02a3/test' \
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
 "testedAt": "2026-10-24T17:51:46.117501201Z"
}
```

## step 5（前置） 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-43b1-799b-a9e0-1848820a02a3' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-43b1-799b-a9e0-1848820a02a3",
 "sceneId": "t_67fa1756",
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
 "lastTestLatencyMs": 4,
 "lastTestAt": "2026-10-24T17:51:46.117501Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:46.097558Z",
 "updatedAt": "2026-10-24T17:51:46.097558Z",
 "version": 0
}
```

## step 6（前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-43b1-799b-a9e0-1848820a02a3/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 7（前置） 禁用 A

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-4395-7dd8-8d46-2f9ccd76f620/enabled' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"enabled":false}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 8（前置） 创建场景 t_e4ef7cf0

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_e4ef7cf0","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-43fc-7a37-b6a2-def5066ad2ce",
 "sceneId": "t_e4ef7cf0",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:46.172585943Z",
 "updatedAt": "2026-10-24T17:51:46.172585943Z"
}
```

## step 9（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-43fc-7a37-b6a2-def5066ad2ce/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 10（前置） 创建场景 t_543095d3

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_543095d3","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-4417-7118-a98f-3fb1859c03a6",
 "sceneId": "t_543095d3",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:46.199025549Z",
 "updatedAt": "2026-10-24T17:51:46.199025549Z"
}
```

## step 11（前置） 新建模板 模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_543095d3","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-4424-78bb-a381-d5ac8459b7d7",
 "sceneId": "t_543095d3",
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
 "createdAt": "2026-10-24T17:51:46.212500487Z",
 "updatedAt": "2026-10-24T17:51:46.212500487Z",
 "version": 0
}
```

## step 12 psql C 经 DB 置 active_template_id（未配主模型）

```sql
update loves_ai_scene set active_template_id='01a16f0a-4424-78bb-a381-d5ac8459b7d7' where id='01a16f0a-4417-7118-a98f-3fb1859c03a6' returning scene_id
```

```
t_543095d3
UPDATE 1
```

## step 13 提交生成 A 已禁用

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-98e87736' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_67fa1756","clientRequestId":"c-b6d545c5-8df1-488a-bb8c-55940ddc3e20","inputs":{"age":28}}'
```

HTTP 409（Content-Type: application/json）

```json
{
 "status": 409,
 "error": "Conflict",
 "message": "场景不可用",
 "path": "/api/app/ai/generations"
}
```

## step 14 提交生成 B 无生效模板

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-7dcf65a4' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_e4ef7cf0","clientRequestId":"c-c1a0754b-2de8-4bbd-b758-370752d362c3","inputs":{"age":28}}'
```

HTTP 409（Content-Type: application/json）

```json
{
 "status": 409,
 "error": "Conflict",
 "message": "场景不可用",
 "path": "/api/app/ai/generations"
}
```

## step 15 提交生成 C 无主模型

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-18263010' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_543095d3","clientRequestId":"c-dcf0efe8-ebb8-44c5-9163-5cb70cca4526","inputs":{"age":28}}'
```

HTTP 409（Content-Type: application/json）

```json
{
 "status": 409,
 "error": "Conflict",
 "message": "场景不可用",
 "path": "/api/app/ai/generations"
}
```

## step 16 psql 统计生成记录 client_request_id in ('c-b6d545c5-8df1-488a-bb8c-55940ddc3e20','c-c1a0754b-2de8-4bbd-b758-370752d362c3','c-dcf0efe8-ebb8-44c5-9163-5cb70cca4526')

```sql
select count(*) from loves_ai_generation where client_request_id in ('c-b6d545c5-8df1-488a-bb8c-55940ddc3e20','c-c1a0754b-2de8-4bbd-b758-370752d362c3','c-dcf0efe8-ebb8-44c5-9163-5cb70cca4526')
```

```
0
```
