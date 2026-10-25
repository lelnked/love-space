# TC-ai-IT-068 请求/响应存证

> GET /api/admin/ai/generations/page 开始晚于结束被拒绝
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 造数 2026-10-19 23:59:59+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-47f07ca7'"'"', '"'"'anon-gr-5727f3d3'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-19 23:59:59+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
f005a4f1-65f4-4baf-9cb2-2cbc5dbb973d
INSERT 0 1
```

## step 2 造数 2026-10-20 00:00:05+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-60cffb35'"'"', '"'"'anon-gr-5727f3d3'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-20 00:00:05+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
6f6749f2-059d-4a72-a3ac-35d46d86195b
INSERT 0 1
```

## step 3 造数 2026-10-21 23:59:50+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-4b7d42f3'"'"', '"'"'anon-gr-5727f3d3'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-21 23:59:50+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
f95e9c93-f72e-4d5b-920a-558f15496a06
INSERT 0 1
```

## step 4 造数 2026-10-22 00:00:00+08（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-5e99757e'"'"', '"'"'anon-gr-5727f3d3'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, '"'"'2026-10-22 00:00:00+08'"'"'::timestamptz, now()) RETURNING id;'
```

```
3ac98822-0aa0-4a9f-8758-48f9f59cc6a6
INSERT 0 1
```

## step 5 GET 列表 startDate=10-22 endDate=10-20

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?startDate=2026-10-22&endDate=2026-10-20&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "开始日期不能晚于结束日期",
 "path": "/api/admin/ai/generations/page"
}
```
