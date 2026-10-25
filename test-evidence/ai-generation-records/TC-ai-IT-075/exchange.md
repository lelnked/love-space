# TC-ai-IT-075 请求/响应存证

> GET /api/admin/ai/generations/{id} 详情含调用明细且查看不留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 （前置） 创建场景 gr_bc04b5dc

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_bc04b5dc", "name": "flaky场景"}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-3775-72c8-8236-bd5c1ffd99b7",
 "sceneId": "gr_bc04b5dc",
 "name": "flaky场景",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-25T13:30:16.30909229Z",
 "updatedAt": "2026-10-25T13:30:16.30909229Z"
}
```

## step 2 （前置） 配置主模型 mock-ok

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a17341-3775-72c8-8236-bd5c1ffd99b7/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 3 （前置） 新建模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_bc04b5dc", "name": "模板-gr_bc04b5dc", "systemPrompt": "你是助手，只输出 JSON", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-3795-740e-aedb-5226999e0a7f",
 "sceneId": "gr_bc04b5dc",
 "seq": 1,
 "name": "模板-gr_bc04b5dc",
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
 "createdAt": "2026-10-25T13:30:16.341176201Z",
 "updatedAt": "2026-10-25T13:30:16.341176201Z",
 "version": 0
}
```

## step 4 （前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-3795-740e-aedb-5226999e0a7f/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```
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
 "testedAt": "2026-10-25T13:30:16.362736637Z"
}
```

## step 5 （前置） 模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-3795-740e-aedb-5226999e0a7f' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "01a17341-3795-740e-aedb-5226999e0a7f",
 "sceneId": "gr_bc04b5dc",
 "seq": 1,
 "name": "模板-gr_bc04b5dc",
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
 "lastTestAt": "2026-10-25T13:30:16.362737Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-25T13:30:16.341176Z",
 "updatedAt": "2026-10-25T13:30:16.341176Z",
 "version": 0
}
```

## step 6 （前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-3795-740e-aedb-5226999e0a7f/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 7 （前置） 主模型改为 mock-flaky（无备用）

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a17341-3775-72c8-8236-bd5c1ffd99b7/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-flaky"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 8 app 生成造数

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-gr-e3685f46' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_bc04b5dc", "clientRequestId": "c-d6598430-bc5c-4900-84f3-3a2428c33f39", "appVersion": "1.0.0", "inputs": {"age": 118735562}}'
```

HTTP 200（Content-Type: application/json）

```
{
 "requestId": "01a17341-37ed-7a87-bf27-0ac38cd9937b",
 "clientRequestId": "c-d6598430-bc5c-4900-84f3-3a2428c33f39",
 "sceneId": "gr_bc04b5dc",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄118735562"
 }
}
```

## step 9 日志基线 module=ai-generation

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-generation&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17334-7dc8-796f-8c91-8a67ed1522fe",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "803c04b4-a976-4499-99b0-8fc6b5d34804",
   "createdAt": "2026-10-25T13:16:22.344515Z"
  },
  {
   "id": "01a17334-7a60-7ddb-906e-815049defc35",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "c27f802b-9238-4b0c-9fdd-30fd1fb2975b",
   "createdAt": "2026-10-25T13:16:21.472758Z"
  },
  {
   "id": "01a17334-7a27-7698-8d50-ac9e2179f06b",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "f4350343-2882-4bef-ab2f-b4f86052444a",
   "createdAt": "2026-10-25T13:16:21.415336Z"
  },
  {
   "id": "01a17334-79ef-702d-81f2-ff340b7e82e0",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "4c58e171-2101-4e00-b534-eca01e049e6b",
   "createdAt": "2026-10-25T13:16:21.358899Z"
  },
  {
   "id": "01a17334-79db-776b-bf21-60932e2564e0",
   "username": "ai_member",
   "module": "ai-generation",
   "action": "rate",
   "target": "4c58e171-2101-4e00-b534-eca01e049e6b",
   "createdAt": "2026-10-25T13:16:21.339393Z"
  },
  {
   "id": "01a17334-798a-77d3-a812-c71a920d9baa",
   "username": "ai_member",
   "module": "ai-generation",
   "action": "rate",
   "target": "a966c161-4e43-4643-ac73-8c51556dda0c",
   "createdAt": "2026-10-25T13:16:21.258413Z"
  },
  {
   "id": "01a17334-7967-7b39-889a-d0f1d4510064",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "1e3421a1-593b-4331-bb0e-e62d77b133c6",
   "createdAt": "2026-10-25T13:16:21.221608Z"
  }
 ],
 "page": 1,
 "size": 200,
 "totalElements": 7,
 "totalPages": 1
}
```

## step 10 GET 详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/01a17341-37ed-7a87-bf27-0ac38cd9937b' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "01a17341-37ed-7a87-bf27-0ac38cd9937b",
 "clientRequestId": "c-d6598430-bc5c-4900-84f3-3a2428c33f39",
 "anonymousUserId": "anon-gr-e3685f46",
 "sceneId": "gr_bc04b5dc",
 "sceneName": "flaky场景",
 "templateId": "01a17341-3795-740e-aedb-5226999e0a7f",
 "templateSeq": 1,
 "templateName": "模板-gr_bc04b5dc",
 "appVersion": "1.0.0",
 "inputs": {
  "age": 118735562
 },
 "requestMessages": [
  {
   "role": "system",
   "content": "你是助手，只输出 JSON"
  },
  {
   "role": "user",
   "content": "年龄118735562"
  }
 ],
 "requestParams": {
  "topP": null,
  "maxTokens": null,
  "maxRetries": 1,
  "backupModel": null,
  "temperature": null,
  "primaryModel": "mock-flaky",
  "timeoutSeconds": 3
 },
 "status": "SUCCESS",
 "errorCode": null,
 "errorMessage": null,
 "finalModel": "mock-flaky",
 "attemptCount": 2,
 "rawOutput": "{\"echo\":\"年龄118735562\"}",
 "content": {
  "echo": "年龄118735562"
 },
 "promptTokens": 24,
 "completionTokens": 12,
 "latencyMs": 9,
 "createdAt": "2026-10-25T13:30:16.429399Z",
 "completedAt": "2026-10-25T13:30:16.442879Z",
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
   "providerRequestId": "mock-1792935016434",
   "rawResponse": "{\"id\":\"mock-1792935016434\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"not json\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":6,\"total_tokens\":18}}",
   "parsedContent": null,
   "validationError": "Unrecognized token 'not': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]",
   "promptTokens": 12,
   "completionTokens": 6,
   "usage": {
    "total_tokens": 18,
    "prompt_tokens": 12,
    "completion_tokens": 6
   },
   "startedAt": "2026-10-25T13:30:16.432707Z",
   "finishedAt": "2026-10-25T13:30:16.437341Z",
   "latencyMs": 4
  },
  {
   "seq": 2,
   "triggerReason": "RETRY",
   "model": "mock-flaky",
   "status": "SUCCESS",
   "errorCode": null,
   "errorMessage": null,
   "providerRequestId": "mock-1792935016439",
   "rawResponse": "{\"id\":\"mock-1792935016439\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"{\\\"echo\\\":\\\"年龄118735562\\\"}\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":6,\"total_tokens\":18}}",
   "parsedContent": {
    "echo": "年龄118735562"
   },
   "validationError": null,
   "promptTokens": 12,
   "completionTokens": 6,
   "usage": {
    "total_tokens": 18,
    "prompt_tokens": 12,
    "completion_tokens": 6
   },
   "startedAt": "2026-10-25T13:30:16.437859Z",
   "finishedAt": "2026-10-25T13:30:16.442447Z",
   "latencyMs": 4
  }
 ]
}
```

## step 11 等待 2s 后日志复查

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-generation&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17334-7dc8-796f-8c91-8a67ed1522fe",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "803c04b4-a976-4499-99b0-8fc6b5d34804",
   "createdAt": "2026-10-25T13:16:22.344515Z"
  },
  {
   "id": "01a17334-7a60-7ddb-906e-815049defc35",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "c27f802b-9238-4b0c-9fdd-30fd1fb2975b",
   "createdAt": "2026-10-25T13:16:21.472758Z"
  },
  {
   "id": "01a17334-7a27-7698-8d50-ac9e2179f06b",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "f4350343-2882-4bef-ab2f-b4f86052444a",
   "createdAt": "2026-10-25T13:16:21.415336Z"
  },
  {
   "id": "01a17334-79ef-702d-81f2-ff340b7e82e0",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "4c58e171-2101-4e00-b534-eca01e049e6b",
   "createdAt": "2026-10-25T13:16:21.358899Z"
  },
  {
   "id": "01a17334-79db-776b-bf21-60932e2564e0",
   "username": "ai_member",
   "module": "ai-generation",
   "action": "rate",
   "target": "4c58e171-2101-4e00-b534-eca01e049e6b",
   "createdAt": "2026-10-25T13:16:21.339393Z"
  },
  {
   "id": "01a17334-798a-77d3-a812-c71a920d9baa",
   "username": "ai_member",
   "module": "ai-generation",
   "action": "rate",
   "target": "a966c161-4e43-4643-ac73-8c51556dda0c",
   "createdAt": "2026-10-25T13:16:21.258413Z"
  },
  {
   "id": "01a17334-7967-7b39-889a-d0f1d4510064",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "1e3421a1-593b-4331-bb0e-e62d77b133c6",
   "createdAt": "2026-10-25T13:16:21.221608Z"
  }
 ],
 "page": 1,
 "size": 200,
 "totalElements": 7,
 "totalPages": 1
}
```
