# TC-ai-IT-048 请求/响应存证

> 重试与切备用逐条写调用明细
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

> 备注: 主模型映射 mock-429（两次均 429），备用 mock-ok。

## step 1（前置） 创建场景 t_af3ec4e7

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_af3ec4e7","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-46c1-71ad-98a6-6b744584e089",
 "sceneId": "t_af3ec4e7",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:46.881058667Z",
 "updatedAt": "2026-10-24T17:51:46.881058667Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-46c1-71ad-98a6-6b744584e089/models' \
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
  -d '{"sceneId":"t_af3ec4e7","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-46db-7dcc-91db-2e159925be60",
 "sceneId": "t_af3ec4e7",
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
 "createdAt": "2026-10-24T17:51:46.907822732Z",
 "updatedAt": "2026-10-24T17:51:46.907822732Z",
 "version": 0
}
```

## step 4（前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-46db-7dcc-91db-2e159925be60/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{"age":28}}'
```

HTTP 200（Content-Type: application/json）

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
 "latencyMs": 3,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-24T17:51:46.924134592Z"
}
```

## step 5（前置） 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-46db-7dcc-91db-2e159925be60' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-46db-7dcc-91db-2e159925be60",
 "sceneId": "t_af3ec4e7",
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
 "lastTestLatencyMs": 3,
 "lastTestAt": "2026-10-24T17:51:46.924135Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:46.907823Z",
 "updatedAt": "2026-10-24T17:51:46.907823Z",
 "version": 0
}
```

## step 6（前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-46db-7dcc-91db-2e159925be60/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 7（前置） 配置模型 mock-429/mock-ok

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-46c1-71ad-98a6-6b744584e089/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-429","backupModel":"mock-ok"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 8 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-1d182760' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_af3ec4e7","clientRequestId":"c-efca3d17-a3f0-47c9-8fc3-c32115ddf0c6","inputs":{"age":28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a16f0a-4727-7527-be78-28f234bad361",
 "clientRequestId": "c-efca3d17-a3f0-47c9-8fc3-c32115ddf0c6",
 "sceneId": "t_af3ec4e7",
 "status": "SUCCESS",
 "content": {
  "echo": "年龄28"
 }
}
```

## step 9 psql 调用明细（按 seq）

```sql
select seq || '|' || trigger_reason || '|' || model || '|' || status from loves_ai_generation_attempt where generation_id='01a16f0a-4727-7527-be78-28f234bad361' order by seq
```

```
1|INITIAL|mock-429|RATE_LIMITED
2|RETRY|mock-429|RATE_LIMITED
3|FALLBACK|mock-ok|SUCCESS
```

## step 10 psql 生成记录与 tokens 合计

```sql
select g.attempt_count || '|' || g.final_model || '|' || coalesce(g.prompt_tokens,-1) || '|' || coalesce(g.completion_tokens,-1) || '|' || coalesce(sum(a.prompt_tokens),0) || '|' || coalesce(sum(a.completion_tokens),0) from loves_ai_generation g join loves_ai_generation_attempt a on a.generation_id=g.id where g.id='01a16f0a-4727-7527-be78-28f234bad361' group by g.id
```

```
3|mock-ok|12|6|12|6
```
