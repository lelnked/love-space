# TC-ai-IT-048 请求/响应存证

> 重试与切备用逐条写调用明细
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_32e30a1b

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_32e30a1b", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，8ms）

```json
{
 "id": "01a1865e-8e74-7e31-8474-d696cc4180ce",
 "sceneId": "t_32e30a1b",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:06.228809463Z",
 "updatedAt": "2026-10-29T06:35:06.228809463Z"
}
```

## step 2 （前置）配置模型 {"primaryModel": "mock-ok"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8e74-7e31-8474-d696cc4180ce/models' \
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
  -d '{"sceneId": "t_32e30a1b", "name": "模板", "systemPrompt": "你是助手，只输出 JSON（t_32e30a1b）", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json，7ms）

```json
{
 "id": "01a1865e-8e84-7aaf-8649-ba0d9d135cae",
 "sceneId": "t_32e30a1b",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_32e30a1b）",
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
 "createdAt": "2026-10-29T06:35:06.244616436Z",
 "updatedAt": "2026-10-29T06:35:06.244616436Z",
 "version": 0
}
```

## step 4 （前置）测试模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8e84-7aaf-8649-ba0d9d135cae/test' \
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
 "latencyMs": 4,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-29T06:35:06.256182261Z"
}
```

## step 5 （前置）模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8e84-7aaf-8649-ba0d9d135cae' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，4ms）

```json
{
 "id": "01a1865e-8e84-7aaf-8649-ba0d9d135cae",
 "sceneId": "t_32e30a1b",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_32e30a1b）",
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
 "lastTestAt": "2026-10-29T06:35:06.256182Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-29T06:35:06.244616Z",
 "updatedAt": "2026-10-29T06:35:06.244616Z",
 "version": 0
}
```

## step 6 （前置）发布模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8e84-7aaf-8649-ba0d9d135cae/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -，7ms）

```json
(空)
```

## step 7 （前置）配置模型 {"primaryModel": "mock-429", "backupModel": "mock-ok"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8e74-7e31-8474-d696cc4180ce/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-429", "backupModel": "mock-ok"}'
```

HTTP 204（Content-Type: -，5ms）

```json
(空)
```

## step 8 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H 'X-API-Key: $APP_API_KEY' \
  -H 'X-Anonymous-Id: anon-a2d1acc3' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_32e30a1b", "clientRequestId": "c-a50b4841-b59b-4105-828c-c7078e7b3834", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，34ms）

```json
{
 "requestId": "01a1865e-8eb1-7677-a6f0-72e67a2ceeb8",
 "clientRequestId": "c-a50b4841-b59b-4105-828c-c7078e7b3834",
 "sceneId": "t_32e30a1b",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 9 psql 调用明细（按 seq）

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris love_space -c "select seq, trigger_reason, model, coalesce(prompt_tokens,0), coalesce(completion_tokens,0) from loves_ai_generation_attempt where generation_id='01a1865e-8eb1-7677-a6f0-72e67a2ceeb8' order by seq"
```

```
seq | trigger_reason |  model   | coalesce | coalesce 
-----+----------------+----------+----------+----------
   1 | INITIAL        | mock-429 |        0 |        0
   2 | RETRY          | mock-429 |        0 |        0
   3 | FALLBACK       | mock-ok  |       12 |        6
(3 rows)
```

## step 10 psql 生成记录

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris love_space -c "select attempt_count, final_model, prompt_tokens, completion_tokens from loves_ai_generation where id='01a1865e-8eb1-7677-a6f0-72e67a2ceeb8'"
```

```
attempt_count | final_model | prompt_tokens | completion_tokens 
---------------+-------------+---------------+-------------------
             3 | mock-ok     |            12 |                 6
(1 row)
```
