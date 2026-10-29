# TC-ai-IT-039 请求/响应存证

> 生成成功写完整生成记录与调用明细
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_a474e1ed

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_a474e1ed", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，9ms）

```json
{
 "id": "01a1865e-8d61-707d-aef8-0b0d209fa3bd",
 "sceneId": "t_a474e1ed",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.952925607Z",
 "updatedAt": "2026-10-29T06:35:05.952925607Z"
}
```

## step 2 （前置）配置模型 {"primaryModel": "mock-ok"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8d61-707d-aef8-0b0d209fa3bd/models' \
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
  -d '{"sceneId": "t_a474e1ed", "name": "模板", "systemPrompt": "你是助手，只输出 JSON（t_a474e1ed）", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json，9ms）

```json
{
 "id": "01a1865e-8d71-7493-9845-d4c6528e7ed4",
 "sceneId": "t_a474e1ed",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_a474e1ed）",
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
 "createdAt": "2026-10-29T06:35:05.969245886Z",
 "updatedAt": "2026-10-29T06:35:05.969245886Z",
 "version": 0
}
```

## step 4 （前置）测试模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8d71-7493-9845-d4c6528e7ed4/test' \
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
 "testedAt": "2026-10-29T06:35:05.982376886Z"
}
```

## step 5 （前置）模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8d71-7493-9845-d4c6528e7ed4' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，6ms）

```json
{
 "id": "01a1865e-8d71-7493-9845-d4c6528e7ed4",
 "sceneId": "t_a474e1ed",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON（t_a474e1ed）",
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
 "lastTestAt": "2026-10-29T06:35:05.982377Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-29T06:35:05.969246Z",
 "updatedAt": "2026-10-29T06:35:05.969246Z",
 "version": 0
}
```

## step 6 （前置）发布模板

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/prompt-templates/01a1865e-8d71-7493-9845-d4c6528e7ed4/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -，8ms）

```json
(空)
```

## step 7 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H 'X-API-Key: $APP_API_KEY' \
  -H 'X-Anonymous-Id: anon-a5770d7a' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_a474e1ed", "clientRequestId": "c-8356b46e-4b5a-46cc-9f19-0c259b3dfd1c", "appVersion": "1.0.0", "inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json，24ms）

```json
{
 "requestId": "01a1865e-8d9d-7723-a72e-e561423dcf0c",
 "clientRequestId": "c-8356b46e-4b5a-46cc-9f19-0c259b3dfd1c",
 "sceneId": "t_a474e1ed",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 8 psql 生成记录

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris love_space -c "select status, attempt_count, completed_at is not null, template_id, template_seq, anonymous_user_id from loves_ai_generation where id='01a1865e-8d9d-7723-a72e-e561423dcf0c'"
```

```
status  | attempt_count | ?column? |             template_id              | template_seq | anonymous_user_id 
---------+---------------+----------+--------------------------------------+--------------+-------------------
 SUCCESS |             1 | t        | 01a1865e-8d71-7493-9845-d4c6528e7ed4 |            1 | anon-a5770d7a
(1 row)
```

## step 9 psql 调用明细

```bash
PGPASSWORD=iris psql -h localhost -p 25432 -U iris love_space -c "select seq, trigger_reason from loves_ai_generation_attempt where generation_id='01a1865e-8d9d-7723-a72e-e561423dcf0c' order by seq"
```

```
seq | trigger_reason 
-----+----------------
   1 | INITIAL
(1 row)
```
