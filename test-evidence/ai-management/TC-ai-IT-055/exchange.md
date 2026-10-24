# TC-ai-IT-055 请求/响应存证

> 匿名 ID 超出每日次数返回 429
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

> 备注: 后端为默认额度（单匿名 ID 50/日），无法改为 2；按约定以 psql 为该匿名 ID 直插 50 条今日记录凑满上限（等价于用例「今日已有上限条数」），执行后删除这批填充记录。

## step 1（前置） 创建场景 t_31c118a2

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_31c118a2","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-6452-7987-ac3e-1024ee87d1a7",
 "sceneId": "t_31c118a2",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:54.450547284Z",
 "updatedAt": "2026-10-24T17:51:54.450547284Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-6452-7987-ac3e-1024ee87d1a7/models' \
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
  -d '{"sceneId":"t_31c118a2","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-646c-76a2-885d-ccc2eaf13d77",
 "sceneId": "t_31c118a2",
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
 "createdAt": "2026-10-24T17:51:54.47636846Z",
 "updatedAt": "2026-10-24T17:51:54.47636846Z",
 "version": 0
}
```

## step 4（前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-646c-76a2-885d-ccc2eaf13d77/test' \
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
 "testedAt": "2026-10-24T17:51:54.493510076Z"
}
```

## step 5（前置） 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-646c-76a2-885d-ccc2eaf13d77' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-646c-76a2-885d-ccc2eaf13d77",
 "sceneId": "t_31c118a2",
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
 "lastTestAt": "2026-10-24T17:51:54.49351Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:54.476368Z",
 "updatedAt": "2026-10-24T17:51:54.476368Z",
 "version": 0
}
```

## step 6（前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-646c-76a2-885d-ccc2eaf13d77/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 7 psql 为该匿名 ID 直插 50 条今日记录

```sql
insert into loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, attempt_count, created_at, completed_at)
select gen_random_uuid(), 'qfill-' || gen_random_uuid(), 'anon-9390fdb4', 't_31c118a2', '01a16f0a-646c-76a2-885d-ccc2eaf13d77', 1, '{}', '[]', '{}', 'SUCCESS', 1, now(), now() from generate_series(1, 50) returning 1
```

```
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
INSERT 0 50
```

## step 8 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-9390fdb4' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_31c118a2","clientRequestId":"c-aab389a1-fff4-4db1-a6d4-21f1d0843e79","inputs":{"age":28}}'
```

HTTP 429（Content-Type: application/json）

```json
{
 "status": 429,
 "error": "Too Many Requests",
 "message": "今日生成次数已达上限",
 "path": "/api/app/ai/generations"
}
```

## step 9 psql 统计生成记录 anonymous_user_id='anon-9390fdb4'

```sql
select count(*) from loves_ai_generation where anonymous_user_id='anon-9390fdb4'
```

```
50
```

## step 10 psql 清理填充记录

```sql
delete from loves_ai_generation where anonymous_user_id='anon-9390fdb4' and client_request_id like 'qfill-%'
```

```
DELETE 50
```
