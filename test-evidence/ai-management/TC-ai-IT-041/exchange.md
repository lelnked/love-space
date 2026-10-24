# TC-ai-IT-041 请求/响应存证

> POST /api/app/ai/generations 已处理的失败返回 200
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

> 备注: 假方舟只有一个「必返回非 JSON」的模型（mock-format-error），主备不能相同；故主断言用 主=mock-format-error、无备用 → FORMAT_ERROR。另以 备用=mock-empty 补测「配了备用且主备均失败」时同样 200 + content=null（终态取备用结果）。

## step 1（前置） 创建场景 t_2a98e1a6

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_2a98e1a6","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-429c-7eb1-8894-c300d6f6c3a2",
 "sceneId": "t_2a98e1a6",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:45.820868722Z",
 "updatedAt": "2026-10-24T17:51:45.820868722Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-429c-7eb1-8894-c300d6f6c3a2/models' \
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
  -d '{"sceneId":"t_2a98e1a6","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-42b7-7e7f-8ea9-8bcbccb95828",
 "sceneId": "t_2a98e1a6",
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
 "createdAt": "2026-10-24T17:51:45.84786025Z",
 "updatedAt": "2026-10-24T17:51:45.84786025Z",
 "version": 0
}
```

## step 4（前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-42b7-7e7f-8ea9-8bcbccb95828/test' \
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
 "testedAt": "2026-10-24T17:51:45.867030116Z"
}
```

## step 5（前置） 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-42b7-7e7f-8ea9-8bcbccb95828' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-42b7-7e7f-8ea9-8bcbccb95828",
 "sceneId": "t_2a98e1a6",
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
 "lastTestAt": "2026-10-24T17:51:45.86703Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:45.84786Z",
 "updatedAt": "2026-10-24T17:51:45.84786Z",
 "version": 0
}
```

## step 6（前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-42b7-7e7f-8ea9-8bcbccb95828/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 7（前置） 配置模型 mock-format-error/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-429c-7eb1-8894-c300d6f6c3a2/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-format-error"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 8 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-d480fc7f' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_2a98e1a6","clientRequestId":"c-4bfcc1fa-efcf-4186-aef6-fdd724260af5","inputs":{"age":28}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a16f0a-4308-7de5-934d-daef74cbcd73",
 "clientRequestId": "c-4bfcc1fa-efcf-4186-aef6-fdd724260af5",
 "sceneId": "t_2a98e1a6",
 "status": "FORMAT_ERROR",
 "content": null
}
```

## step 9 psql 生成记录

```sql
select status || '|' || (completed_at is not null) from loves_ai_generation where id='01a16f0a-4308-7de5-934d-daef74cbcd73'
```

```
FORMAT_ERROR|true
```

## step 10（前置） 配置模型 mock-format-error/mock-empty

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-429c-7eb1-8894-c300d6f6c3a2/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-format-error","backupModel":"mock-empty"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 11 补测：配备用 mock-empty 后提交

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-d5128f83' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_2a98e1a6","clientRequestId":"c-5a6c372b-83b5-4943-a5e4-aa9363837dde","inputs":{"age":29}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a16f0a-434c-7ae1-81bd-49d5eb0db540",
 "clientRequestId": "c-5a6c372b-83b5-4943-a5e4-aa9363837dde",
 "sceneId": "t_2a98e1a6",
 "status": "EMPTY",
 "content": null
}
```
