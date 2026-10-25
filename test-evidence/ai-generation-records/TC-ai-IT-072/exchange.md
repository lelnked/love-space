# TC-ai-IT-072 请求/响应存证

> GET /api/admin/ai/generations/page requestId / templateId / model 精确匹配
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 R1 模板 T1 mock-ok（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-a4cb5a49'"'"', '"'"'anon-gr-4f4d4a06'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now()) RETURNING id;'
```

```
2b20d613-b771-4f0a-abe9-2c0d3151fb84
INSERT 0 1
```

## step 2 R2 模板 T2 mock-flaky（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-1e840661'"'"', '"'"'anon-gr-4f4d4a06'"'"', '"'"'gr_f19de32d'"'"', '"'"'01a17341-3281-783b-ba62-0b24d9439be5'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-flaky'"'"', 1, 1200, now(), now()) RETURNING id;'
```

```
377cdb72-a6bf-45a9-9e0e-d0da0c86af96
INSERT 0 1
```

## step 3 GET requestId=R1

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?requestId=2b20d613-b771-4f0a-abe9-2c0d3151fb84&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "2b20d613-b771-4f0a-abe9-2c0d3151fb84",
   "createdAt": "2026-10-25T13:30:15.832735Z",
   "anonymousUserId": "anon-gr-4f4d4a06",
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
 "totalElements": 1,
 "totalPages": 1
}
```

## step 4 GET requestId=随机 uuid

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?requestId=e91ee589-48c0-49f0-a151-14e274eded13&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [],
 "page": 1,
 "size": 20,
 "totalElements": 0,
 "totalPages": 0
}
```

## step 5 GET templateId=T2

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?templateId=01a17341-3281-783b-ba62-0b24d9439be5&anonymousUserId=anon-gr-4f4d4a06&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "377cdb72-a6bf-45a9-9e0e-d0da0c86af96",
   "createdAt": "2026-10-25T13:30:15.851215Z",
   "anonymousUserId": "anon-gr-4f4d4a06",
   "sceneId": "gr_f19de32d",
   "sceneName": "生成记录场景S2",
   "templateId": "01a17341-3281-783b-ba62-0b24d9439be5",
   "templateSeq": 1,
   "finalModel": "mock-flaky",
   "status": "SUCCESS",
   "latencyMs": 1200,
   "rating": null
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 6 GET model=mock-ok

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?model=mock-ok&anonymousUserId=anon-gr-4f4d4a06&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "2b20d613-b771-4f0a-abe9-2c0d3151fb84",
   "createdAt": "2026-10-25T13:30:15.832735Z",
   "anonymousUserId": "anon-gr-4f4d4a06",
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
 "totalElements": 1,
 "totalPages": 1
}
```

## step 7 GET anonymousUserId=前半段

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?anonymousUserId=anon-gr-4f4d&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [],
 "page": 1,
 "size": 20,
 "totalElements": 0,
 "totalPages": 0
}
```
