# TC-ai-IT-065 请求/响应存证

> GET /api/admin/ai/generations/page 不传日期默认最近 7 天
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 （前置） 创建场景 gr_3f05e38d

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_3f05e38d", "name": "生成记录场景S"}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-3184-794e-af22-f8fb7612770c",
 "sceneId": "gr_3f05e38d",
 "name": "生成记录场景S",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-25T13:30:14.788503139Z",
 "updatedAt": "2026-10-25T13:30:14.788503139Z"
}
```

## step 2 （前置） 配置主模型 mock-ok

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a17341-3184-794e-af22-f8fb7612770c/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 3 （前置） 新建模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_3f05e38d", "name": "模板-gr_3f05e38d", "systemPrompt": "你是助手，只输出 JSON", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-31b2-7887-a69c-5ae04440c5fb",
 "sceneId": "gr_3f05e38d",
 "seq": 1,
 "name": "模板-gr_3f05e38d",
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
 "createdAt": "2026-10-25T13:30:14.834460316Z",
 "updatedAt": "2026-10-25T13:30:14.834460316Z",
 "version": 0
}
```

## step 4 （前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-31b2-7887-a69c-5ae04440c5fb/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"年龄28\"}",
 "content": {
  "echo": "年龄28"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 69,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-25T13:30:14.932828461Z"
}
```

## step 5 （前置） 模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-31b2-7887-a69c-5ae04440c5fb' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "01a17341-31b2-7887-a69c-5ae04440c5fb",
 "sceneId": "gr_3f05e38d",
 "seq": 1,
 "name": "模板-gr_3f05e38d",
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
 "lastTestLatencyMs": 69,
 "lastTestAt": "2026-10-25T13:30:14.932828Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-25T13:30:14.83446Z",
 "updatedAt": "2026-10-25T13:30:14.83446Z",
 "version": 0
}
```

## step 6 （前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-31b2-7887-a69c-5ae04440c5fb/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 7 （前置） 创建场景 gr_f19de32d

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_f19de32d", "name": "生成记录场景S2"}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-3261-7585-a517-fae5afe95aa0",
 "sceneId": "gr_f19de32d",
 "name": "生成记录场景S2",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-25T13:30:15.009225399Z",
 "updatedAt": "2026-10-25T13:30:15.009225399Z"
}
```

## step 8 （前置） 配置主模型 mock-ok

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a17341-3261-7585-a517-fae5afe95aa0/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 9 （前置） 新建模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_f19de32d", "name": "模板-gr_f19de32d", "systemPrompt": "你是助手，只输出 JSON", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-3281-783b-ba62-0b24d9439be5",
 "sceneId": "gr_f19de32d",
 "seq": 1,
 "name": "模板-gr_f19de32d",
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
 "createdAt": "2026-10-25T13:30:15.041427519Z",
 "updatedAt": "2026-10-25T13:30:15.041427519Z",
 "version": 0
}
```

## step 10 （前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-3281-783b-ba62-0b24d9439be5/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```
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
 "testedAt": "2026-10-25T13:30:15.064835629Z"
}
```

## step 11 （前置） 模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-3281-783b-ba62-0b24d9439be5' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "01a17341-3281-783b-ba62-0b24d9439be5",
 "sceneId": "gr_f19de32d",
 "seq": 1,
 "name": "模板-gr_f19de32d",
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
 "lastTestLatencyMs": 4,
 "lastTestAt": "2026-10-25T13:30:15.064836Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-25T13:30:15.041428Z",
 "updatedAt": "2026-10-25T13:30:15.041428Z",
 "version": 0
}
```

## step 12 （前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-3281-783b-ba62-0b24d9439be5/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 13 造数：今天当前时刻（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-11b75dd3'"'"', '"'"'anon-gr-9eaebf3f'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now()) RETURNING id;'
```

```
57249924-a8b9-423a-842d-d122edf03176
INSERT 0 1
```

## step 14 造数：6 天前 00:00:01（北京）（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-9bc7ed88'"'"', '"'"'anon-gr-9eaebf3f'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, (date_trunc('"'"'day'"'"', now() AT TIME ZONE '"'"'Asia/Shanghai'"'"') - interval '"'"'6 days'"'"' + interval '"'"'1 second'"'"') AT TIME ZONE '"'"'Asia/Shanghai'"'"', now()) RETURNING id;'
```

```
7b0238c7-eaff-43a4-bbe9-71187f1215a4
INSERT 0 1
```

## step 15 造数：8 天前 12:00（北京）（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-6baddd5e'"'"', '"'"'anon-gr-9eaebf3f'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, (date_trunc('"'"'day'"'"', now() AT TIME ZONE '"'"'Asia/Shanghai'"'"') - interval '"'"'8 days'"'"' + interval '"'"'12 hours'"'"') AT TIME ZONE '"'"'Asia/Shanghai'"'"', now()) RETURNING id;'
```

```
a0b91acd-a54a-4925-8754-51167d276d64
INSERT 0 1
```

## step 16 GET 列表（不传日期）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?anonymousUserId=anon-gr-9eaebf3f&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "57249924-a8b9-423a-842d-d122edf03176",
   "createdAt": "2026-10-25T13:30:15.118816Z",
   "anonymousUserId": "anon-gr-9eaebf3f",
   "sceneId": "gr_3f05e38d",
   "sceneName": "生成记录场景S",
   "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
   "templateSeq": 1,
   "finalModel": "mock-ok",
   "status": "SUCCESS",
   "latencyMs": 1200,
   "rating": null
  },
  {
   "id": "7b0238c7-eaff-43a4-bbe9-71187f1215a4",
   "createdAt": "2026-10-18T16:00:01Z",
   "anonymousUserId": "anon-gr-9eaebf3f",
   "sceneId": "gr_3f05e38d",
   "sceneName": "生成记录场景S",
   "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
   "templateSeq": 1,
   "finalModel": "mock-ok",
   "status": "SUCCESS",
   "latencyMs": 1200,
   "rating": null
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 2,
 "totalPages": 1
}
```
