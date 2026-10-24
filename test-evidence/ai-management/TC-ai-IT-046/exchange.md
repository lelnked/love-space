# TC-ai-IT-046 请求/响应存证

> POST /api/app/ai/generations API Key 无效返回 401
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_5bb60e13

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_5bb60e13","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-45ac-781a-8245-8a5050cc6d50",
 "sceneId": "t_5bb60e13",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:46.604467631Z",
 "updatedAt": "2026-10-24T17:51:46.604467631Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-45ac-781a-8245-8a5050cc6d50/models' \
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
  -d '{"sceneId":"t_5bb60e13","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-45c6-7027-ab5c-d26048a80c5c",
 "sceneId": "t_5bb60e13",
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
 "createdAt": "2026-10-24T17:51:46.629964939Z",
 "updatedAt": "2026-10-24T17:51:46.629964939Z",
 "version": 0
}
```

## step 4（前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-45c6-7027-ab5c-d26048a80c5c/test' \
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
 "testedAt": "2026-10-24T17:51:46.647653306Z"
}
```

## step 5（前置） 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-45c6-7027-ab5c-d26048a80c5c' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-45c6-7027-ab5c-d26048a80c5c",
 "sceneId": "t_5bb60e13",
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
 "lastTestAt": "2026-10-24T17:51:46.647653Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:46.629965Z",
 "updatedAt": "2026-10-24T17:51:46.629965Z",
 "version": 0
}
```

## step 6（前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-45c6-7027-ab5c-d26048a80c5c/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 7 X-API-Key: wrong-key

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H 'X-API-Key: wrong-key' \
  -H 'X-Anonymous-Id: anon-A' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_5bb60e13","clientRequestId":"c-65964b2c-dae2-4deb-9b28-8cb1c44318ab","inputs":{"age":28}}'
```

HTTP 401（Content-Type: application/problem+json）

```json
{
 "detail": "Invalid or missing API key",
 "instance": "/api/app/ai/generations",
 "status": 401,
 "title": "Unauthorized"
}
```

## step 8 不带 X-API-Key

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H 'X-Anonymous-Id: anon-A' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_5bb60e13","clientRequestId":"c-f7bad9f1-e107-4080-a99b-afb1c5678b12","inputs":{"age":28}}'
```

HTTP 401（Content-Type: application/problem+json）

```json
{
 "detail": "Invalid or missing API key",
 "instance": "/api/app/ai/generations",
 "status": 401,
 "title": "Unauthorized"
}
```

## step 9 psql 统计生成记录 scene_id='t_5bb60e13'

```sql
select count(*) from loves_ai_generation where scene_id='t_5bb60e13'
```

```
0
```
