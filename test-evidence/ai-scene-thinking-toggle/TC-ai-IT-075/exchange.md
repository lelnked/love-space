# TC-ai-IT-075 请求/响应存证

> GET /api/admin/ai/generations/{id} 详情含调用明细且查看不留痕
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_8fcec0a7

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_8fcec0a7", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，9ms）

```json
{
 "id": "01a1865e-8f1b-721f-9cb5-76b4f9e016b9",
 "sceneId": "t_8fcec0a7",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:06.39505979Z",
 "updatedAt": "2026-10-29T06:35:06.39505979Z"
}
```

## step 2 （前置）配置模型 {"primaryModel": "mock-ok"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8f1b-721f-9cb5-76b4f9e016b9/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok"}'
```

HTTP 204（Content-Type: -，7ms）

```json
(空)
```

## step 3 （前置）新建模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_8fcec0a7", "name": "模板", "systemPrompt": "你是助手，只输出 JSON（flaky-988137b1）", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json，8ms）

```json
{
 "id": "01a1865e-8f2b-7ad5-8e2f-35cec59ecb21",
 "sceneId": "t_8fcec0a7",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（flaky-988137b1）",
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
 "createdAt": "2026-10-29T06:35:06.411635831Z",
 "updatedAt": "2026-10-29T06:35:06.411635831Z",
 "version": 0
}
```

## step 4 （前置）测试模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8f2b-7ad5-8e2f-35cec59ecb21/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，12ms）

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
 "testedAt": "2026-10-29T06:35:06.423508073Z"
}
```

## step 5 （前置）模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8f2b-7ad5-8e2f-35cec59ecb21' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，5ms）

```json
{
 "id": "01a1865e-8f2b-7ad5-8e2f-35cec59ecb21",
 "sceneId": "t_8fcec0a7",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（flaky-988137b1）",
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
 "lastTestAt": "2026-10-29T06:35:06.423508Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-29T06:35:06.411636Z",
 "updatedAt": "2026-10-29T06:35:06.411636Z",
 "version": 0
}
```

## step 6 （前置）发布模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8f2b-7ad5-8e2f-35cec59ecb21/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -，7ms）

```json
(空)
```

## step 7 （前置）配置模型 {"primaryModel": "mock-flaky"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8f1b-721f-9cb5-76b4f9e016b9/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-flaky"}'
```

HTTP 204（Content-Type: -，5ms）

```json
(空)
```

## step 8 （前置）app 生成造数

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H 'X-API-Key: $APP_API_KEY' \
  -H 'X-Anonymous-Id: anon-bc6dc02b' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_8fcec0a7", "clientRequestId": "c-5fbec586-f93b-4963-bc7f-a673c011062e", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，29ms）

```json
{
 "requestId": "01a1865e-8f5a-7be4-ba82-e64354eefe27",
 "clientRequestId": "c-5fbec586-f93b-4963-bc7f-a673c011062e",
 "sceneId": "t_8fcec0a7",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 9 日志基线

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/logs/page?module=ai-generation&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，15ms）

```json
{
 "content": [
  {
   "id": "01a18659-4f89-782a-8b6c-e4dcbf0291c7",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "dc87a93b-ce03-4fc2-8e6a-337c34343bb1",
   "createdAt": "2026-10-29T06:29:22.441463Z"
  },
  {
   "id": "01a18659-48e6-7339-9775-32db02930374",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "f3a73ae9-4713-4cd4-9428-1db4b49846ac",
   "createdAt": "2026-10-29T06:29:20.742134Z"
  },
  {
   "id": "01a18659-48aa-7cc3-87c6-e725e5dd6cc0",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "ce562f09-7e82-4637-a03d-14a89522eef5",
   "createdAt": "2026-10-29T06:29:20.682728Z"
  },
  {
   "id": "01a18659-486f-744d-a5bd-5d3b4b20cb1a",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "7662cb89-ae44-4996-a891-82d718cfdda6",
   "createdAt": "2026-10-29T06:29:20.623206Z"
  },
  {
   "id": "01a18659-485a-7fc0-afc1-414a6a0a0972",
   "username": "ai_member",
   "module": "ai-generation",
   "action": "rate",
   "target": "7662cb89-ae44-4996-a891-82d718cfdda6",
   "createdAt": "2026-10-29T06:29:20.602932Z"
  },
  {
   "id": "01a18659-480a-7ff7-b16e-736a1581ecbf",
   "username": "ai_member",
   "module": "ai-generation",
   "action": "rate",
   "target": "53c69850-552d-46a0-927c-cc836cdea565",
   "createdAt": "2026-10-29T06:29:20.522933Z"
  },
  {
   "id": "01a18659-47ea-70b1-b166-1fa8415ca576",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "a0ba8cc6-fd6b-424e-aa6e-c29616dd969c",
   "createdAt": "2026-10-29T06:29:20.487455Z"
  },
  {
   "id": "01a17db1-752e-7624-bb17-ee6865ad1451",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "01a17740-8df7-7dba-a96a-45e783f307a1",
   "createdAt": "2026-10-27T14:09:04.287616Z"
  }
 ],
 "page": 1,
 "size": 200,
 "totalElements": 8,
 "totalPages": 1
}
```

## step 10 生成记录详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/generations/01a1865e-8f5a-7be4-ba82-e64354eefe27' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，25ms）

```json
{
 "id": "01a1865e-8f5a-7be4-ba82-e64354eefe27",
 "clientRequestId": "c-5fbec586-f93b-4963-bc7f-a673c011062e",
 "anonymousUserId": "anon-bc6dc02b",
 "sceneId": "t_8fcec0a7",
 "sceneName": "周期建议",
 "templateId": "01a1865e-8f2b-7ad5-8e2f-35cec59ecb21",
 "templateSeq": 1,
 "templateName": "模板",
 "appVersion": "1.0.0",
 "inputs": {
  "age": 28
 },
 "requestMessages": [
  {
   "role": "system",
   "content": "你是助手，只输出 JSON（flaky-988137b1）"
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
  "primaryModel": "mock-flaky",
  "timeoutSeconds": 3,
  "thinkingEnabled": false
 },
 "status": "SUCCESS",
 "errorCode": null,
 "errorMessage": null,
 "finalModel": "mock-flaky",
 "attemptCount": 2,
 "rawOutput": "{\"echo\":\"年龄28\"}",
 "content": {
  "echo": "年龄28"
 },
 "promptTokens": 24,
 "completionTokens": 12,
 "latencyMs": 8,
 "createdAt": "2026-10-29T06:35:06.458462Z",
 "completedAt": "2026-10-29T06:35:06.469107Z",
 "rating": null,
 "ratingTags": [],
 "ratingRemark": null,
 "ratedBy": null,
 "ratedAt": null,
 "attempts": [
  {
   "seq": 1,
   "triggerReason": "INITIAL",
   "model": "mock-flaky",
   "status": "FORMAT_ERROR",
   "errorCode": null,
   "errorMessage": "模型输出不是合法 JSON",
   "providerRequestId": "mock-1793255706462",
   "rawResponse": "{\"id\":\"mock-1793255706462\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"not json\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":6,\"total_tokens\":18}}",
   "parsedContent": null,
   "validationError": "Unrecognized token 'not': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]",
   "promptTokens": 12,
   "completionTokens": 6,
   "usage": {
    "total_tokens": 18,
    "prompt_tokens": 12,
    "completion_tokens": 6
   },
   "startedAt": "2026-10-29T06:35:06.460894Z",
   "finishedAt": "2026-10-29T06:35:06.464866Z",
   "latencyMs": 3
  },
  {
   "seq": 2,
   "triggerReason": "RETRY",
   "model": "mock-flaky",
   "status": "SUCCESS",
   "errorCode": null,
   "errorMessage": null,
   "providerRequestId": "mock-1793255706466",
   "rawResponse": "{\"id\":\"mock-1793255706466\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"{\\\"echo\\\":\\\"年龄28\\\"}\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":6,\"total_tokens\":18}}",
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
   "startedAt": "2026-10-29T06:35:06.465288Z",
   "finishedAt": "2026-10-29T06:35:06.468868Z",
   "latencyMs": 3
  }
 ]
}
```

## step 11 2s 后日志查询

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/logs/page?module=ai-generation&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，9ms）

```json
{
 "content": [
  {
   "id": "01a18659-4f89-782a-8b6c-e4dcbf0291c7",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "dc87a93b-ce03-4fc2-8e6a-337c34343bb1",
   "createdAt": "2026-10-29T06:29:22.441463Z"
  },
  {
   "id": "01a18659-48e6-7339-9775-32db02930374",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "f3a73ae9-4713-4cd4-9428-1db4b49846ac",
   "createdAt": "2026-10-29T06:29:20.742134Z"
  },
  {
   "id": "01a18659-48aa-7cc3-87c6-e725e5dd6cc0",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "ce562f09-7e82-4637-a03d-14a89522eef5",
   "createdAt": "2026-10-29T06:29:20.682728Z"
  },
  {
   "id": "01a18659-486f-744d-a5bd-5d3b4b20cb1a",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "7662cb89-ae44-4996-a891-82d718cfdda6",
   "createdAt": "2026-10-29T06:29:20.623206Z"
  },
  {
   "id": "01a18659-485a-7fc0-afc1-414a6a0a0972",
   "username": "ai_member",
   "module": "ai-generation",
   "action": "rate",
   "target": "7662cb89-ae44-4996-a891-82d718cfdda6",
   "createdAt": "2026-10-29T06:29:20.602932Z"
  },
  {
   "id": "01a18659-480a-7ff7-b16e-736a1581ecbf",
   "username": "ai_member",
   "module": "ai-generation",
   "action": "rate",
   "target": "53c69850-552d-46a0-927c-cc836cdea565",
   "createdAt": "2026-10-29T06:29:20.522933Z"
  },
  {
   "id": "01a18659-47ea-70b1-b166-1fa8415ca576",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "a0ba8cc6-fd6b-424e-aa6e-c29616dd969c",
   "createdAt": "2026-10-29T06:29:20.487455Z"
  },
  {
   "id": "01a17db1-752e-7624-bb17-ee6865ad1451",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "01a17740-8df7-7dba-a96a-45e783f307a1",
   "createdAt": "2026-10-27T14:09:04.287616Z"
  }
 ],
 "page": 1,
 "size": 200,
 "totalElements": 8,
 "totalPages": 1
}
```
