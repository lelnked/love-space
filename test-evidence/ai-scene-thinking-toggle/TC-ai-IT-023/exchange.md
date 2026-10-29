# TC-ai-IT-023 请求/响应存证

> POST /api/admin/ai/prompt-templates/{id}/test 测试成功置已测试
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_48f93839

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_48f93839", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，6ms）

```json
{
 "id": "01a1865e-8bd9-7563-89c5-230cf5d737cb",
 "sceneId": "t_48f93839",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.561276684Z",
 "updatedAt": "2026-10-29T06:35:05.561276684Z"
}
```

## step 2 （前置）配置模型（附开启深度思考，验证调用点读开关不影响测试） {"primaryModel": "mock-ok", "thinkingEnabled": true}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8bd9-7563-89c5-230cf5d737cb/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok", "thinkingEnabled": true}'
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
  -d '{"sceneId": "t_48f93839", "name": "模板", "systemPrompt": "你是助手，只输出 JSON", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json，56ms）

```json
{
 "id": "01a1865e-8c12-7132-803f-70a06cd974ec",
 "sceneId": "t_48f93839",
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
 "createdAt": "2026-10-29T06:35:05.615726741Z",
 "updatedAt": "2026-10-29T06:35:05.615726741Z",
 "version": 0
}
```

## step 4 测试模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8c12-7132-803f-70a06cd974ec/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，45ms）

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
 "latencyMs": 5,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-29T06:35:05.647100576Z"
}
```

## step 5 模板详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8c12-7132-803f-70a06cd974ec' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，7ms）

```json
{
 "id": "01a1865e-8c12-7132-803f-70a06cd974ec",
 "sceneId": "t_48f93839",
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
 "lastTestLatencyMs": 5,
 "lastTestAt": "2026-10-29T06:35:05.647101Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-29T06:35:05.615727Z",
 "updatedAt": "2026-10-29T06:35:05.615727Z",
 "version": 0
}
```
