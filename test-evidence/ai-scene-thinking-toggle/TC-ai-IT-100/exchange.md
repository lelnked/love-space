# TC-ai-IT-100 请求/响应存证

> 场景关闭深度思考时生成参数快照 thinkingEnabled=false
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_a75c8b26

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_a75c8b26", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，8ms）

```json
{
 "id": "01a1865e-9773-7721-8175-83bc1538ef2b",
 "sceneId": "t_a75c8b26",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:08.531365767Z",
 "updatedAt": "2026-10-29T06:35:08.531365767Z"
}
```

## step 2 （前置）配置模型 {"primaryModel": "mock-ok"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-9773-7721-8175-83bc1538ef2b/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok"}'
```

HTTP 204（Content-Type: -，6ms）

```json
(空)
```

## step 3 （前置）新建模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_a75c8b26", "name": "模板", "systemPrompt": "你是助手，只输出 JSON（t_a75c8b26）", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json，7ms）

```json
{
 "id": "01a1865e-9782-7872-9f90-f95f8a68f1cb",
 "sceneId": "t_a75c8b26",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_a75c8b26）",
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
 "createdAt": "2026-10-29T06:35:08.546465773Z",
 "updatedAt": "2026-10-29T06:35:08.546465773Z",
 "version": 0
}
```

## step 4 （前置）测试模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-9782-7872-9f90-f95f8a68f1cb/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，11ms）

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
 "testedAt": "2026-10-29T06:35:08.557618006Z"
}
```

## step 5 （前置）模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-9782-7872-9f90-f95f8a68f1cb' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，4ms）

```json
{
 "id": "01a1865e-9782-7872-9f90-f95f8a68f1cb",
 "sceneId": "t_a75c8b26",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_a75c8b26）",
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
 "lastTestAt": "2026-10-29T06:35:08.557618Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-29T06:35:08.546466Z",
 "updatedAt": "2026-10-29T06:35:08.546466Z",
 "version": 0
}
```

## step 6 （前置）发布模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-9782-7872-9f90-f95f8a68f1cb/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -，6ms）

```json
(空)
```

## step 7 （前置）配置模型 {"primaryModel": "mock-ok"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-9773-7721-8175-83bc1538ef2b/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok"}'
```

HTTP 204（Content-Type: -，3ms）

```json
(空)
```

## step 8 （前置）场景详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/scenes/01a1865e-9773-7721-8175-83bc1538ef2b' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，3ms）

```json
{
 "id": "01a1865e-9773-7721-8175-83bc1538ef2b",
 "sceneId": "t_a75c8b26",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "mock-ok",
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": {
  "id": "01a1865e-9782-7872-9f90-f95f8a68f1cb",
  "seq": 1,
  "name": "模板",
  "publishedAt": "2026-10-29T06:35:08.56903Z",
  "publishedBy": "admin"
 },
 "createdAt": "2026-10-29T06:35:08.531366Z",
 "updatedAt": "2026-10-29T06:35:08.569274Z"
}
```

## step 9 app 生成造数

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H 'X-API-Key: $APP_API_KEY' \
  -H 'X-Anonymous-Id: anon-b6fa6f92' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_a75c8b26", "clientRequestId": "c-dcac8282-1537-4612-9fc4-5c86a1ea2908", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，25ms）

```json
{
 "requestId": "01a1865e-97b0-781b-b0cf-78391b25996a",
 "clientRequestId": "c-dcac8282-1537-4612-9fc4-5c86a1ea2908",
 "sceneId": "t_a75c8b26",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 10 生成记录详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/generations/01a1865e-97b0-781b-b0cf-78391b25996a' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，8ms）

```json
{
 "id": "01a1865e-97b0-781b-b0cf-78391b25996a",
 "clientRequestId": "c-dcac8282-1537-4612-9fc4-5c86a1ea2908",
 "anonymousUserId": "anon-b6fa6f92",
 "sceneId": "t_a75c8b26",
 "sceneName": "周期建议",
 "templateId": "01a1865e-9782-7872-9f90-f95f8a68f1cb",
 "templateSeq": 1,
 "templateName": "模板",
 "appVersion": "1.0.0",
 "inputs": {
  "age": 28
 },
 "requestMessages": [
  {
   "role": "system",
   "content": "你是助手，只输出 JSON（t_a75c8b26）"
  },
  {
   "role": "user",
   "content": "年龄28"
  }
 ],
 "requestParams": {
  "topP": null,
  "maxTokens": null,
  "maxRetries": 1,
  "backupModel": null,
  "temperature": null,
  "primaryModel": "mock-ok",
  "timeoutSeconds": 3,
  "thinkingEnabled": false
 },
 "status": "SUCCESS",
 "errorCode": null,
 "errorMessage": null,
 "finalModel": "mock-ok",
 "attemptCount": 1,
 "rawOutput": "{\"echo\":\"年龄28\"}",
 "content": {
  "echo": "年龄28"
 },
 "promptTokens": 12,
 "completionTokens": 6,
 "latencyMs": 4,
 "createdAt": "2026-10-29T06:35:08.592187Z",
 "completedAt": "2026-10-29T06:35:08.599056Z",
 "rating": null,
 "ratingTags": [],
 "ratingRemark": null,
 "ratedBy": null,
 "ratedAt": null,
 "attempts": [
  {
   "seq": 1,
   "triggerReason": "INITIAL",
   "model": "mock-ok",
   "status": "SUCCESS",
   "errorCode": null,
   "errorMessage": null,
   "providerRequestId": "mock-1793255708596",
   "rawResponse": "{\"id\":\"mock-1793255708596\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"{\\\"echo\\\":\\\"年龄28\\\"}\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":6,\"total_tokens\":18}}",
   "parsedContent": {
    "echo": "年龄28"
   },
   "validationError": null,
   "promptTokens": 12,
   "completionTokens": 6,
   "usage": {
    "total_tokens": 18,
    "prompt_tokens": 12,
    "completion_tokens": 6
   },
   "startedAt": "2026-10-29T06:35:08.594781Z",
   "finishedAt": "2026-10-29T06:35:08.598773Z",
   "latencyMs": 3
  }
 ]
}
```
