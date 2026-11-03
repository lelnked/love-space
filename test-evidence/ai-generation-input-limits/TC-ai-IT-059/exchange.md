# TC-ai-IT-059 请求/响应存证

> 单个变量 2001 字符返回 400
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-11-03　执行器: api-test-runner（change ai-generation-input-limits）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 （前置） 创建场景 t_646f4930

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_646f4930","name":"输入长度限制"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1a06a-a5ff-79a6-b6ee-2e71a662b5b8",
 "sceneId": "t_646f4930",
 "name": "输入长度限制",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-11-03T07:58:26.291370518Z",
 "updatedAt": "2026-11-03T07:58:26.291370518Z"
}
```

## step 2 （前置） 配置模型 mock-ok

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a1a06a-a5ff-79a6-b6ee-2e71a662b5b8/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 3 （前置） 新建模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_646f4930","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"补充[{{user_input}}]","variables":[{"name":"user_input","required":false}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1a06a-a65b-7ad7-abc7-d8fdedd2471d",
 "sceneId": "t_646f4930",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "补充[{{user_input}}]",
 "variables": [
  {
   "name": "user_input",
   "required": false,
   "description": null
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
 "createdAt": "2026-11-03T07:58:26.394017103Z",
 "updatedAt": "2026-11-03T07:58:26.394017103Z",
 "version": 0
}
```

## step 4 （前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a1a06a-a65b-7ad7-abc7-d8fdedd2471d/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"补充[]\"}",
 "content": {
  "echo": "补充[]"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 59,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-11-03T07:58:26.484861258Z"
}
```

## step 5 （前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a1a06a-a65b-7ad7-abc7-d8fdedd2471d/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 6 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-66019f11' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_646f4930","clientRequestId":"c-b077d482-27fe-44dd-8349-85b1d32f02b8","inputs":{"user_input":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"}}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "变量 user_input 超过 2000 字符",
 "path": "/api/app/ai/generations"
}
```

## step 7 psql

```sql
select count(*) from loves_ai_generation where client_request_id='c-b077d482-27fe-44dd-8349-85b1d32f02b8'
```

```
0
```
