# TC-ai-IT-083 请求/响应存证

> PUT /api/admin/ai/generations/{id}/rating 失败记录可以评价
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 （前置） 创建场景 gr_b2fa9991

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_b2fa9991", "name": "格式错误场景"}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-4e82-7fe3-83f4-8c4105669272",
 "sceneId": "gr_b2fa9991",
 "name": "格式错误场景",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-25T13:30:22.210918308Z",
 "updatedAt": "2026-10-25T13:30:22.210918308Z"
}
```

## step 2 （前置） 配置主模型 mock-ok

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a17341-4e82-7fe3-83f4-8c4105669272/models' \
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
  -d '{"sceneId": "gr_b2fa9991", "name": "模板-gr_b2fa9991", "systemPrompt": "你是助手，只输出 JSON", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-4ea3-797c-8469-f0cf57df5f6f",
 "sceneId": "gr_b2fa9991",
 "seq": 1,
 "name": "模板-gr_b2fa9991",
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
 "createdAt": "2026-10-25T13:30:22.243537258Z",
 "updatedAt": "2026-10-25T13:30:22.243537258Z",
 "version": 0
}
```

## step 4 （前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-4ea3-797c-8469-f0cf57df5f6f/test' \
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
 "testedAt": "2026-10-25T13:30:22.264144918Z"
}
```

## step 5 （前置） 模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-4ea3-797c-8469-f0cf57df5f6f' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "01a17341-4ea3-797c-8469-f0cf57df5f6f",
 "sceneId": "gr_b2fa9991",
 "seq": 1,
 "name": "模板-gr_b2fa9991",
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
 "lastTestAt": "2026-10-25T13:30:22.264145Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-25T13:30:22.243537Z",
 "updatedAt": "2026-10-25T13:30:22.243537Z",
 "version": 0
}
```

## step 6 （前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-4ea3-797c-8469-f0cf57df5f6f/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 7 （前置） 主模型改为 mock-format-error（无备用）

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a17341-4e82-7fe3-83f4-8c4105669272/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-format-error"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 8 app 生成造数

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-gr-751fee43' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_b2fa9991", "clientRequestId": "c-92ccfbf2-cdf0-4b9f-8be6-5f27c57d5207", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```
{
 "requestId": "01a17341-4ef8-7817-8f42-9f4e3a9294db",
 "clientRequestId": "c-92ccfbf2-cdf0-4b9f-8be6-5f27c57d5207",
 "sceneId": "gr_b2fa9991",
 "status": "FORMAT_ERROR",
 "content": null
}
```

## step 9 PUT 评价 {"rating": "UNUSABLE", "tags": ["FORMAT_ERROR"]}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/01a17341-4ef8-7817-8f42-9f4e3a9294db/rating' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"rating": "UNUSABLE", "tags": ["FORMAT_ERROR"]}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 10 GET 详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/01a17341-4ef8-7817-8f42-9f4e3a9294db' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "01a17341-4ef8-7817-8f42-9f4e3a9294db",
 "clientRequestId": "c-92ccfbf2-cdf0-4b9f-8be6-5f27c57d5207",
 "anonymousUserId": "anon-gr-751fee43",
 "sceneId": "gr_b2fa9991",
 "sceneName": "格式错误场景",
 "templateId": "01a17341-4ea3-797c-8469-f0cf57df5f6f",
 "templateSeq": 1,
 "templateName": "模板-gr_b2fa9991",
 "appVersion": "1.0.0",
 "inputs": {
  "age": 28
 },
 "requestMessages": [
  {
   "role": "system",
   "content": "你是助手，只输出 JSON"
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
  "primaryModel": "mock-format-error",
  "timeoutSeconds": 3
 },
 "status": "FORMAT_ERROR",
 "errorCode": "FORMAT_ERROR",
 "errorMessage": "模型输出不是合法 JSON",
 "finalModel": "mock-format-error",
 "attemptCount": 2,
 "rawOutput": "not json",
 "content": null,
 "promptTokens": 24,
 "completionTokens": 12,
 "latencyMs": 7,
 "createdAt": "2026-10-25T13:30:22.328234Z",
 "completedAt": "2026-10-25T13:30:22.339308Z",
 "rating": "UNUSABLE",
 "ratingTags": [
  "FORMAT_ERROR"
 ],
 "ratingRemark": null,
 "ratedBy": "admin",
 "ratedAt": "2026-10-25T13:30:22.355815Z",
 "attempts": [
  {
   "seq": 1,
   "triggerReason": "INITIAL",
   "model": "mock-format-error",
   "status": "FORMAT_ERROR",
   "errorCode": null,
   "errorMessage": "模型输出不是合法 JSON",
   "providerRequestId": "mock-1792935022333",
   "rawResponse": "{\"id\":\"mock-1792935022333\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"not json\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":6,\"total_tokens\":18}}",
   "parsedContent": null,
   "validationError": "Unrecognized token 'not': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]",
   "promptTokens": 12,
   "completionTokens": 6,
   "usage": {
    "total_tokens": 18,
    "prompt_tokens": 12,
    "completion_tokens": 6
   },
   "startedAt": "2026-10-25T13:30:22.331286Z",
   "finishedAt": "2026-10-25T13:30:22.335329Z",
   "latencyMs": 4
  },
  {
   "seq": 2,
   "triggerReason": "RETRY",
   "model": "mock-format-error",
   "status": "FORMAT_ERROR",
   "errorCode": null,
   "errorMessage": "模型输出不是合法 JSON",
   "providerRequestId": "mock-1792935022336",
   "rawResponse": "{\"id\":\"mock-1792935022336\",\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"not json\"},\"finish_reason\":\"stop\"}],\"usage\":{\"prompt_tokens\":12,\"completion_tokens\":6,\"total_tokens\":18}}",
   "parsedContent": null,
   "validationError": "Unrecognized token 'not': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #UNKNOWN]",
   "promptTokens": 12,
   "completionTokens": 6,
   "usage": {
    "total_tokens": 18,
    "prompt_tokens": 12,
    "completion_tokens": 6
   },
   "startedAt": "2026-10-25T13:30:22.335406Z",
   "finishedAt": "2026-10-25T13:30:22.339013Z",
   "latencyMs": 3
  }
 ]
}
```
