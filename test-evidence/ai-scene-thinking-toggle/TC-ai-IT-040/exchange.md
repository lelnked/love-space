# TC-ai-IT-040 请求/响应存证

> 生成记录请求快照不含密钥与认证头
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_3d8f0e23

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_3d8f0e23", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，9ms）

```json
{
 "id": "01a1865e-8dff-7300-84a5-75f8e19f1f6c",
 "sceneId": "t_3d8f0e23",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:06.111108265Z",
 "updatedAt": "2026-10-29T06:35:06.111108265Z"
}
```

## step 2 （前置）配置模型 {"primaryModel": "mock-ok"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8dff-7300-84a5-75f8e19f1f6c/models' \
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
  -d '{"sceneId": "t_3d8f0e23", "name": "模板", "systemPrompt": "你是助手，只输出 JSON（t_3d8f0e23）", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json，9ms）

```json
{
 "id": "01a1865e-8e10-76a1-b5e6-de8ca34a1181",
 "sceneId": "t_3d8f0e23",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_3d8f0e23）",
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
 "createdAt": "2026-10-29T06:35:06.128373645Z",
 "updatedAt": "2026-10-29T06:35:06.128373645Z",
 "version": 0
}
```

## step 4 （前置）测试模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8e10-76a1-b5e6-de8ca34a1181/test' \
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
 "testedAt": "2026-10-29T06:35:06.14146976Z"
}
```

## step 5 （前置）模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8e10-76a1-b5e6-de8ca34a1181' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，4ms）

```json
{
 "id": "01a1865e-8e10-76a1-b5e6-de8ca34a1181",
 "sceneId": "t_3d8f0e23",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_3d8f0e23）",
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
 "lastTestAt": "2026-10-29T06:35:06.14147Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-29T06:35:06.128374Z",
 "updatedAt": "2026-10-29T06:35:06.128374Z",
 "version": 0
}
```

## step 6 （前置）发布模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8e10-76a1-b5e6-de8ca34a1181/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -，7ms）

```json
(空)
```

## step 7 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H 'X-API-Key: $APP_API_KEY' \
  -H 'X-Anonymous-Id: anon-6f06067d' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_3d8f0e23", "clientRequestId": "c-cc517b48-7024-46d5-94a7-9d6d34036b56", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，25ms）

```json
{
 "requestId": "01a1865e-8e3a-7398-a6a2-6ed6de3f4153",
 "clientRequestId": "c-cc517b48-7024-46d5-94a7-9d6d34036b56",
 "sceneId": "t_3d8f0e23",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 8 psql 请求快照

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris love_space -c "select request_messages::text, request_params::text from loves_ai_generation where id='01a1865e-8e3a-7398-a6a2-6ed6de3f4153'"
```

```
request_messages                                                |                                                                             request_params                                                                             
---------------------------------------------------------------------------------------------------------------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 [{"role": "system", "content": "你是助手，只输出 JSON（t_3d8f0e23）"}, {"role": "user", "content": "年龄28"}] | {"topP": null, "maxTokens": null, "maxRetries": 1, "backupModel": null, "temperature": null, "primaryModel": "mock-ok", "timeoutSeconds": 3, "thinkingEnabled": false}
(1 row)
```
