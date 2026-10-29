# TC-ai-IT-101 请求/响应存证

> 场景开启深度思考且切备用时参数快照 thinkingEnabled=true
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_bd269a36

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_bd269a36", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，8ms）

```json
{
 "id": "01a1865e-97c9-73b2-9d27-3ffe16b0ce54",
 "sceneId": "t_bd269a36",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:08.61715526Z",
 "updatedAt": "2026-10-29T06:35:08.61715526Z"
}
```

## step 2 （前置）配置模型 {"primaryModel": "mock-ok"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-97c9-73b2-9d27-3ffe16b0ce54/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok"}'
```

HTTP 204（Content-Type: -，5ms）

```json
(空)
```

## step 3 （前置）新建模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_bd269a36", "name": "模板", "systemPrompt": "你是助手，只输出 JSON（t_bd269a36）", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json，7ms）

```json
{
 "id": "01a1865e-97d8-7586-bd11-a4b2fa6ee80f",
 "sceneId": "t_bd269a36",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_bd269a36）",
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
 "createdAt": "2026-10-29T06:35:08.632291387Z",
 "updatedAt": "2026-10-29T06:35:08.632291387Z",
 "version": 0
}
```

## step 4 （前置）测试模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-97d8-7586-bd11-a4b2fa6ee80f/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，10ms）

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
 "testedAt": "2026-10-29T06:35:08.643098648Z"
}
```

## step 5 （前置）模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-97d8-7586-bd11-a4b2fa6ee80f' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，4ms）

```json
{
 "id": "01a1865e-97d8-7586-bd11-a4b2fa6ee80f",
 "sceneId": "t_bd269a36",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_bd269a36）",
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
 "lastTestAt": "2026-10-29T06:35:08.643099Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-29T06:35:08.632291Z",
 "updatedAt": "2026-10-29T06:35:08.632291Z",
 "version": 0
}
```

## step 6 （前置）发布模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-97d8-7586-bd11-a4b2fa6ee80f/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -，6ms）

```json
(空)
```

## step 7 （前置）配置模型 {"primaryModel": "mock-500", "backupModel": "mock-ok", "thinkingEnabled": true}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-97c9-73b2-9d27-3ffe16b0ce54/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-500", "backupModel": "mock-ok", "thinkingEnabled": true}'
```

HTTP 204（Content-Type: -，4ms）

```json
(空)
```

## step 8 app 生成造数

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H 'X-API-Key: $APP_API_KEY' \
  -H 'X-Anonymous-Id: anon-6b7cee4b' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_bd269a36", "clientRequestId": "c-b7c6d500-4de3-49c3-90eb-f9d18cd2de0e", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，32ms）

```json
{
 "requestId": "01a1865e-9802-70cf-9852-a24f38a7f927",
 "clientRequestId": "c-b7c6d500-4de3-49c3-90eb-f9d18cd2de0e",
 "sceneId": "t_bd269a36",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 9 生成记录详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/generations/01a1865e-9802-70cf-9852-a24f38a7f927' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，8ms）

```json
{
 "id": "01a1865e-9802-70cf-9852-a24f38a7f927",
 "clientRequestId": "c-b7c6d500-4de3-49c3-90eb-f9d18cd2de0e",
 "anonymousUserId": "anon-6b7cee4b",
 "sceneId": "t_bd269a36",
 "sceneName": "周期建议",
 "templateId": "01a1865e-97d8-7586-bd11-a4b2fa6ee80f",
 "templateSeq": 1,
 "templateName": "模板",
 "appVersion": "1.0.0",
 "inputs": {
  "age": 28
 },
 "requestMessages": [
  {
   "role": "system",
   "content": "你是助手，只输出 JSON（t_bd269a36）"
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
  "backupModel": "mock-ok",
  "temperature": null,
  "primaryModel": "mock-500",
  "timeoutSeconds": 3,
  "thinkingEnabled": true
 },
 "status": "SUCCESS",
 "errorCode": null,
 "errorMessage": null,
 "finalModel": "mock-ok",
 "attemptCount": 3,
 "rawOutput": "{\"echo\":\"年龄28\"}",
 "content": {
  "echo": "年龄28"
 },
 "promptTokens": 12,
 "completionTokens": 6,
 "latencyMs": 11,
 "createdAt": "2026-10-29T06:35:08.673818Z",
 "completedAt": "2026-10-29T06:35:08.687403Z",
 "rating": null,
 "ratingTags": [],
 "ratingRemark": null,
 "ratedBy": null,
 "ratedAt": null,
 "attempts": [
  {
   "seq": 1,
   "triggerReason": "INITIAL",
   "model": "mock-500",
   "status": "UPSTREAM_ERROR",
   "errorCode": "500",
   "errorMessage": "方舟返回 HTTP 500",
   "providerRequestId": null,
   "rawResponse": "{\"error\":{\"code\":\"InternalServiceError\"}}",
   "parsedContent": null,
   "validationError": null,
   "promptTokens": null,
   "completionTokens": null,
   "usage": null,
   "startedAt": "2026-10-29T06:35:08.676188Z",
   "finishedAt": "2026-10-29T06:35:08.680176Z",
   "latencyMs": 3
  },
  {
   "seq": 2,
   "triggerReason": "RETRY",
   "model": "mock-500",
   "status": "UPSTREAM_ERROR",
   "errorCode": "500",
   "errorMessage": "方舟返回 HTTP 500",
   "providerRequestId": null,
   "rawResponse": "{\"error\":{\"code\":\"InternalServiceError\"}}",
   "parsedContent": null,
   "validationError": null,
   "promptTokens": null,
   "completionTokens": null,
   "usage": null,
   "startedAt": "2026-10-29T06:35:08.680188Z",
   "finishedAt": "2026-10-29T06:35:08.683666Z",
   "latencyMs": 3
  },
  {
   "seq": 3,
   "triggerReason": "FALLBACK",
   "model": "mock-ok",
   "status": "SUCCESS",
   "errorCode": null,
   "errorMessage": null,
   "providerRequestId": "mock-1793255708685",
   "rawResponse": "{\"id\":\"mock-1793255708685\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"{\\\"echo\\\":\\\"年龄28\\\"}\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":6,\"total_tokens\":18}}",
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
   "startedAt": "2026-10-29T06:35:08.68368Z",
   "finishedAt": "2026-10-29T06:35:08.687108Z",
   "latencyMs": 3
  }
 ]
}
```
