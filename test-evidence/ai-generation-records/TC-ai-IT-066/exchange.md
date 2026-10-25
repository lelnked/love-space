# TC-ai-IT-066 请求/响应存证

> GET /api/admin/ai/generations/page 日期区间含两端整日（北京时间）
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 造数 2026-10-19 23:59:59+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-b51d4bac'"'"', '"'"'anon-gr-a732e813'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-19 23:59:59+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
60dcfeff-32da-4b08-a1e5-ff2194735b22
INSERT 0 1
```

## step 2 造数 2026-10-20 00:00:05+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-0bb95955'"'"', '"'"'anon-gr-a732e813'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-20 00:00:05+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
efb51be2-144d-4f34-8531-04824ee74355
INSERT 0 1
```

## step 3 造数 2026-10-21 23:59:50+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-d98af6b0'"'"', '"'"'anon-gr-a732e813'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-21 23:59:50+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
4cfcc790-9b61-4210-a40b-4501e5147071
INSERT 0 1
```

## step 4 造数 2026-10-22 00:00:00+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-b4626113'"'"', '"'"'anon-gr-a732e813'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-22 00:00:00+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
e98c9079-2b73-43e7-b282-6dc35e7bfdef
INSERT 0 1
```

## step 5 GET 列表 10-20~10-21

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?startDate=2026-10-20&endDate=2026-10-21&anonymousUserId=anon-gr-a732e813&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "4cfcc790-9b61-4210-a40b-4501e5147071",
   "createdAt": "2026-10-21T15:59:50Z",
   "anonymousUserId": "anon-gr-a732e813",
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
   "id": "efb51be2-144d-4f34-8531-04824ee74355",
   "createdAt": "2026-10-19T16:00:05Z",
   "anonymousUserId": "anon-gr-a732e813",
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
