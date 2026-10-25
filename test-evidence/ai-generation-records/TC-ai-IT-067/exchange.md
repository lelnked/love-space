# TC-ai-IT-067 请求/响应存证

> GET /api/admin/ai/generations/page 只传一端按单边过滤
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 造数 2026-10-19 23:59:59+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-bdf4aa3b'"'"', '"'"'anon-gr-0d3072f6'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-19 23:59:59+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
0fba6d8a-efd8-4750-9b5c-32517eb1145f
INSERT 0 1
```

## step 2 造数 2026-10-20 00:00:05+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-6f88b038'"'"', '"'"'anon-gr-0d3072f6'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-20 00:00:05+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
8cf9e1db-deda-4651-96b4-efc3386265b6
INSERT 0 1
```

## step 3 造数 2026-10-21 23:59:50+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-9510165e'"'"', '"'"'anon-gr-0d3072f6'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-21 23:59:50+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
ad848a61-88ff-4604-a80a-2582227a6f08
INSERT 0 1
```

## step 4 造数 2026-10-22 00:00:00+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-ea2a2a21'"'"', '"'"'anon-gr-0d3072f6'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-22 00:00:00+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
eb6d8276-188b-4ed4-a95d-494ea9cb7e03
INSERT 0 1
```

## step 5 GET 列表 startDate=2026-10-21

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?startDate=2026-10-21&anonymousUserId=anon-gr-0d3072f6&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "eb6d8276-188b-4ed4-a95d-494ea9cb7e03",
   "createdAt": "2026-10-21T16:00:00Z",
   "anonymousUserId": "anon-gr-0d3072f6",
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
   "id": "ad848a61-88ff-4604-a80a-2582227a6f08",
   "createdAt": "2026-10-21T15:59:50Z",
   "anonymousUserId": "anon-gr-0d3072f6",
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

## step 6 GET 列表 endDate=2026-10-20

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?endDate=2026-10-20&anonymousUserId=anon-gr-0d3072f6&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "8cf9e1db-deda-4651-96b4-efc3386265b6",
   "createdAt": "2026-10-19T16:00:05Z",
   "anonymousUserId": "anon-gr-0d3072f6",
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
   "id": "0fba6d8a-efd8-4750-9b5c-32517eb1145f",
   "createdAt": "2026-10-19T15:59:59Z",
   "anonymousUserId": "anon-gr-0d3072f6",
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
