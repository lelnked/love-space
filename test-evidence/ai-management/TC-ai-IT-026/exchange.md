# TC-ai-IT-026 请求/响应存证

> POST /api/admin/ai/prompt-templates/{id}/test 必填变量缺失返回 400
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_1628e05f

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_1628e05f","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-32f0-7a3c-8d22-0bb642def163",
 "sceneId": "t_1628e05f",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:41.808585986Z",
 "updatedAt": "2026-10-24T17:51:41.808585986Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-32f0-7a3c-8d22-0bb642def163/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 3（前置） 新建模板 模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_1628e05f","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-330e-7893-9249-23e64b3f7233",
 "sceneId": "t_1628e05f",
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
 "createdAt": "2026-10-24T17:51:41.838488055Z",
 "updatedAt": "2026-10-24T17:51:41.838488055Z",
 "version": 0
}
```

## step 4（前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-330e-7893-9249-23e64b3f7233/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{"age":30}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"年龄30\"}",
 "content": {
  "echo": "年龄30"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 4,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-24T17:51:41.859246905Z"
}
```

## step 5 psql 前置 last_test_*

```sql
select last_test_at, last_test_inputs, last_test_output, last_test_success, last_test_by, tested from loves_ai_prompt_template where id='01a16f0a-330e-7893-9249-23e64b3f7233'
```

```
2026-10-24 17:51:41.859247+00|{"age": 30}|{"echo":"年龄30"}|t|admin|t
```

## step 6 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-330e-7893-9249-23e64b3f7233/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{}}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "缺少必填变量：age",
 "path": "/api/admin/ai/prompt-templates/01a16f0a-330e-7893-9249-23e64b3f7233/test"
}
```

## step 7 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-330e-7893-9249-23e64b3f7233/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{"age":null}}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "缺少必填变量：age",
 "path": "/api/admin/ai/prompt-templates/01a16f0a-330e-7893-9249-23e64b3f7233/test"
}
```

## step 8 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-330e-7893-9249-23e64b3f7233' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-330e-7893-9249-23e64b3f7233",
 "sceneId": "t_1628e05f",
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
  "age": 30
 },
 "lastTestOutput": "{\"echo\":\"年龄30\"}",
 "lastTestContent": {
  "echo": "年龄30"
 },
 "lastTestModel": "mock-ok",
 "lastTestSuccess": true,
 "lastTestError": null,
 "lastTestLatencyMs": 4,
 "lastTestAt": "2026-10-24T17:51:41.859247Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:41.838488Z",
 "updatedAt": "2026-10-24T17:51:41.838488Z",
 "version": 0
}
```

## step 9 psql last_test_*

```sql
select last_test_at, last_test_inputs, last_test_output, last_test_success, last_test_by, tested from loves_ai_prompt_template where id='01a16f0a-330e-7893-9249-23e64b3f7233'
```

```
2026-10-24 17:51:41.859247+00|{"age": 30}|{"echo":"年龄30"}|t|admin|t
```
