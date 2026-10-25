# TC-ai-IT-070 请求/响应存证

> GET /api/admin/ai/generations/page 按问题标签筛选
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 造数 A [BAD_TONE,VAGUE]（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at, rating, rating_tags) VALUES (gen_random_uuid(), '"'"'gr-cfe3cf8e'"'"', '"'"'anon-gr-23133b10'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now() - interval '"'"'1 minute'"'"', now(), '"'"'NEEDS_IMPROVEMENT'"'"', '"'"'["BAD_TONE","VAGUE"]'"'"') RETURNING id;'
```

```
ab6f4d1d-dbc9-48f0-a5ac-3df66ad74050
INSERT 0 1
```

## step 2 造数 B [VAGUE]（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at, rating, rating_tags) VALUES (gen_random_uuid(), '"'"'gr-271ad47a'"'"', '"'"'anon-gr-23133b10'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now(), '"'"'NEEDS_IMPROVEMENT'"'"', '"'"'["VAGUE"]'"'"') RETURNING id;'
```

```
60ba3e9a-1e86-4d2a-bc43-7e04c22783c0
INSERT 0 1
```

## step 3 GET tag=BAD_TONE

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?tag=BAD_TONE&anonymousUserId=anon-gr-23133b10&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "ab6f4d1d-dbc9-48f0-a5ac-3df66ad74050",
   "createdAt": "2026-10-25T13:29:15.608411Z",
   "anonymousUserId": "anon-gr-23133b10",
   "sceneId": "gr_3f05e38d",
   "sceneName": "生成记录场景S",
   "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
   "templateSeq": 1,
   "finalModel": "mock-ok",
   "status": "SUCCESS",
   "latencyMs": 1200,
   "rating": "NEEDS_IMPROVEMENT"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 4 GET tag=VAGUE

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?tag=VAGUE&anonymousUserId=anon-gr-23133b10&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "60ba3e9a-1e86-4d2a-bc43-7e04c22783c0",
   "createdAt": "2026-10-25T13:30:15.627648Z",
   "anonymousUserId": "anon-gr-23133b10",
   "sceneId": "gr_3f05e38d",
   "sceneName": "生成记录场景S",
   "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
   "templateSeq": 1,
   "finalModel": "mock-ok",
   "status": "SUCCESS",
   "latencyMs": 1200,
   "rating": "NEEDS_IMPROVEMENT"
  },
  {
   "id": "ab6f4d1d-dbc9-48f0-a5ac-3df66ad74050",
   "createdAt": "2026-10-25T13:29:15.608411Z",
   "anonymousUserId": "anon-gr-23133b10",
   "sceneId": "gr_3f05e38d",
   "sceneName": "生成记录场景S",
   "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
   "templateSeq": 1,
   "finalModel": "mock-ok",
   "status": "SUCCESS",
   "latencyMs": 1200,
   "rating": "NEEDS_IMPROVEMENT"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 2,
 "totalPages": 1
}
```
