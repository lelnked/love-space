# TC-ai-IT-071 请求/响应存证

> GET /api/admin/ai/generations/page 场景 + 状态组合筛选且按时间倒序
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 S SUCCESS -3h（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-81948059'"'"', '"'"'anon-gr-08b1fe1a'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now() - interval '"'"'3 hours'"'"', now()) RETURNING id;'
```

```
e9221706-ac00-412e-8119-84625c845ca7
INSERT 0 1
```

## step 2 S SUCCESS -1h（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-8f5c3a90'"'"', '"'"'anon-gr-08b1fe1a'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now() - interval '"'"'1 hour'"'"', now()) RETURNING id;'
```

```
e97b3450-0ccb-4542-bd99-3b621d42a859
INSERT 0 1
```

## step 3 S SUCCESS -2h（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-3736a804'"'"', '"'"'anon-gr-08b1fe1a'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now() - interval '"'"'2 hours'"'"', now()) RETURNING id;'
```

```
ebb8be7f-891c-4ead-87e7-99b80ef4783b
INSERT 0 1
```

## step 4 S FORMAT_ERROR（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-a196d762'"'"', '"'"'anon-gr-08b1fe1a'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'FORMAT_ERROR'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now()) RETURNING id;'
```

```
b8f52c4e-8926-4502-ac92-a520d9af04e8
INSERT 0 1
```

## step 5 S PROCESSING（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-e3cf261e'"'"', '"'"'anon-gr-08b1fe1a'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'PROCESSING'"'"', NULL, 0, NULL, now(), NULL) RETURNING id;'
```

```
36506149-c8cb-444a-ac69-ce5f9b5ce051
INSERT 0 1
```

## step 6 S2 SUCCESS（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-bd342bc5'"'"', '"'"'anon-gr-08b1fe1a'"'"', '"'"'gr_f19de32d'"'"', '"'"'01a17341-3281-783b-ba62-0b24d9439be5'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now()) RETURNING id;'
```

```
c2e2e15a-874a-4c7b-8c9d-445cb56ba088
INSERT 0 1
```

## step 7 GET sceneId=S&status=SUCCESS

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?sceneId=gr_3f05e38d&status=SUCCESS&anonymousUserId=anon-gr-08b1fe1a&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "e97b3450-0ccb-4542-bd99-3b621d42a859",
   "createdAt": "2026-10-25T12:30:15.704723Z",
   "anonymousUserId": "anon-gr-08b1fe1a",
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
   "id": "ebb8be7f-891c-4ead-87e7-99b80ef4783b",
   "createdAt": "2026-10-25T11:30:15.723591Z",
   "anonymousUserId": "anon-gr-08b1fe1a",
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
   "id": "e9221706-ac00-412e-8119-84625c845ca7",
   "createdAt": "2026-10-25T10:30:15.684101Z",
   "anonymousUserId": "anon-gr-08b1fe1a",
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
 "totalElements": 3,
 "totalPages": 1
}
```

## step 8 GET status=PROCESSING

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?status=PROCESSING&anonymousUserId=anon-gr-08b1fe1a&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "36506149-c8cb-444a-ac69-ce5f9b5ce051",
   "createdAt": "2026-10-25T13:30:15.762289Z",
   "anonymousUserId": "anon-gr-08b1fe1a",
   "sceneId": "gr_3f05e38d",
   "sceneName": "生成记录场景S",
   "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
   "templateSeq": 1,
   "finalModel": null,
   "status": "PROCESSING",
   "latencyMs": null,
   "rating": null
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```
