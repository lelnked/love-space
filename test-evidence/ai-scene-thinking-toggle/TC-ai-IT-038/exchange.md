# TC-ai-IT-038 请求/响应存证

> POST /api/app/ai/generations 生成成功
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_3e640c8d

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_3e640c8d", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，7ms）

```json
{
 "id": "01a1865e-8c55-771d-bb3e-6294fb04a63a",
 "sceneId": "t_3e640c8d",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.685384395Z",
 "updatedAt": "2026-10-29T06:35:05.685384395Z"
}
```

## step 2 （前置）配置模型 {"primaryModel": "mock-ok"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8c55-771d-bb3e-6294fb04a63a/models' \
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
  -d '{"sceneId": "t_3e640c8d", "name": "模板", "systemPrompt": "你是助手，只输出 JSON（t_3e640c8d）", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json，8ms）

```json
{
 "id": "01a1865e-8c65-71c6-aa08-ae6c000aed92",
 "sceneId": "t_3e640c8d",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_3e640c8d）",
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
 "createdAt": "2026-10-29T06:35:05.701057642Z",
 "updatedAt": "2026-10-29T06:35:05.701057642Z",
 "version": 0
}
```

## step 4 （前置）测试模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8c65-71c6-aa08-ae6c000aed92/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，13ms）

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
 "testedAt": "2026-10-29T06:35:05.713950148Z"
}
```

## step 5 （前置）模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8c65-71c6-aa08-ae6c000aed92' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，5ms）

```json
{
 "id": "01a1865e-8c65-71c6-aa08-ae6c000aed92",
 "sceneId": "t_3e640c8d",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_3e640c8d）",
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
 "lastTestAt": "2026-10-29T06:35:05.71395Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-29T06:35:05.701058Z",
 "updatedAt": "2026-10-29T06:35:05.701058Z",
 "version": 0
}
```

## step 6 （前置）发布模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8c65-71c6-aa08-ae6c000aed92/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -，13ms）

```json
(空)
```

## step 7 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H 'X-API-Key: $APP_API_KEY' \
  -H 'X-Anonymous-Id: anon-b06d4a82' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_3e640c8d", "clientRequestId": "c-5639583e-76a8-4189-9db5-95c810a354f0", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，209ms）

```json
{
 "requestId": "01a1865e-8ce8-77ec-8afe-2266aad9bd75",
 "clientRequestId": "c-5639583e-76a8-4189-9db5-95c810a354f0",
 "sceneId": "t_3e640c8d",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```
