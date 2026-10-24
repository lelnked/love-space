# TC-ai-IT-064 请求/响应存证

> 测试期间模板被他人修改则结果不写回
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

> 备注: 主模型映射 mock-slow（已调整为 2s 后成功，小于 admin AI_TIMEOUT_SECONDS=3）；测试发起 1s 后并发 PUT。

## step 1（前置） 创建场景 t_a1aa1e0d

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_a1aa1e0d","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0c-ce0f-77a6-b3ac-5315c4deff7b",
 "sceneId": "t_a1aa1e0d",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:54:32.591432161Z",
 "updatedAt": "2026-10-24T17:54:32.591432161Z"
}
```

## step 2（前置） 配置模型 mock-slow/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0c-ce0f-77a6-b3ac-5315c4deff7b/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-slow"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 3（前置） 新建模板 T

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_a1aa1e0d","name":"T","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0c-ce28-75c4-9355-a5cd3e93059e",
 "sceneId": "t_a1aa1e0d",
 "seq": 1,
 "name": "T",
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
 "createdAt": "2026-10-24T17:54:32.616316392Z",
 "updatedAt": "2026-10-24T17:54:32.616316392Z",
 "version": 0
}
```

## step 4 psql 前置 last_test_* / tested

```sql
select coalesce(last_test_at::text,'null') || '|' || coalesce(last_test_inputs::text,'null') || '|' || coalesce(last_test_output,'null') || '|' || coalesce(last_test_success::text,'null') || '|' || tested from loves_ai_prompt_template where id='01a16f0c-ce28-75c4-9355-a5cd3e93059e'
```

```
null|null|null|null|false
```

## step 5 测试进行中修改 name（version=v）

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0c-ce28-75c4-9355-a5cd3e93059e' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"T-并发改","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}],"version":0}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0c-ce28-75c4-9355-a5cd3e93059e",
 "sceneId": "t_a1aa1e0d",
 "seq": 1,
 "name": "T-并发改",
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
 "createdAt": "2026-10-24T17:54:32.616316Z",
 "updatedAt": "2026-10-24T17:54:33.655060413Z",
 "version": 1
}
```

## step 6 异步发起测试（mock-slow）

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0c-ce28-75c4-9355-a5cd3e93059e/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{"age":28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "success": true,
 "model": "mock-slow",
 "output": "{\"echo\":\"年龄28\"}",
 "content": {
  "echo": "年龄28"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 2009,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-24T17:54:34.661177492Z"
}
```

## step 7 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0c-ce28-75c4-9355-a5cd3e93059e' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0c-ce28-75c4-9355-a5cd3e93059e",
 "sceneId": "t_a1aa1e0d",
 "seq": 1,
 "name": "T-并发改",
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
 "createdAt": "2026-10-24T17:54:32.616316Z",
 "updatedAt": "2026-10-24T17:54:33.65506Z",
 "version": 1
}
```

## step 8 psql last_test_* / tested

```sql
select coalesce(last_test_at::text,'null') || '|' || coalesce(last_test_inputs::text,'null') || '|' || coalesce(last_test_output,'null') || '|' || coalesce(last_test_success::text,'null') || '|' || tested from loves_ai_prompt_template where id='01a16f0c-ce28-75c4-9355-a5cd3e93059e'
```

```
null|null|null|null|false
```
