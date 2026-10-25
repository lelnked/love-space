# TC-ai-IT-080 请求/响应存证

> PUT /api/admin/ai/generations/{id}/rating 再次评价整体覆盖、不保留历史
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 造数 SUCCESS R（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-2c14540b'"'"', '"'"'anon-gr-7dd723ba'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now()) RETURNING id;'
```

```
b209d36d-9414-4d5b-8d7a-824c94fbb168
INSERT 0 1
```

## step 2 （前置） ADMIN 创建 MEMBER 账号 ops1

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/managers' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"username": "ops1", "password": "Member#2026x", "nickname": "生成记录成员"}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-4158-7cad-9564-027b2d1daba3",
 "username": "ops1",
 "nickname": "生成记录成员",
 "role": "MEMBER",
 "enable": true,
 "createdAt": "2026-10-25T13:30:18.839347404Z"
}
```

## step 3 MEMBER 登录 ops1

```bash
curl -s -X POST 'http://localhost:21423/api/admin/auth/login' -H 'Content-Type: application/json' -d '{"username":"ops1","password":"Member#2026x"}'
```

HTTP 200（Content-Type: application/json）

```
{"token": "$MEMBER_TOKEN", ...}
```

## step 4 ops1 PUT 评价

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/b209d36d-9414-4d5b-8d7a-824c94fbb168/rating' \
  -H "Authorization: Bearer $MEMBER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"rating": "NEEDS_IMPROVEMENT", "tags": ["BAD_TONE"], "remark": "语气生硬"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 5 GET 详情记录 t1

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/b209d36d-9414-4d5b-8d7a-824c94fbb168' \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "b209d36d-9414-4d5b-8d7a-824c94fbb168",
 "clientRequestId": "gr-2c14540b",
 "anonymousUserId": "anon-gr-7dd723ba",
 "sceneId": "gr_3f05e38d",
 "sceneName": "生成记录场景S",
 "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
 "templateSeq": 1,
 "templateName": "模板-gr_3f05e38d",
 "appVersion": null,
 "inputs": {
  "age": 28
 },
 "requestMessages": [],
 "requestParams": {},
 "status": "SUCCESS",
 "errorCode": null,
 "errorMessage": null,
 "finalModel": "mock-ok",
 "attemptCount": 1,
 "rawOutput": null,
 "content": null,
 "promptTokens": null,
 "completionTokens": null,
 "latencyMs": 1200,
 "createdAt": "2026-10-25T13:30:18.707886Z",
 "completedAt": "2026-10-25T13:30:18.707886Z",
 "rating": "NEEDS_IMPROVEMENT",
 "ratingTags": [
  "BAD_TONE"
 ],
 "ratingRemark": "语气生硬",
 "ratedBy": "ops1",
 "ratedAt": "2026-10-25T13:30:18.955862Z",
 "attempts": []
}
```

## step 6 admin PUT 评价（不传 remark）

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/b209d36d-9414-4d5b-8d7a-824c94fbb168/rating' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"rating": "UNUSABLE", "tags": ["VAGUE"]}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 7 GET 详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/b209d36d-9414-4d5b-8d7a-824c94fbb168' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "b209d36d-9414-4d5b-8d7a-824c94fbb168",
 "clientRequestId": "gr-2c14540b",
 "anonymousUserId": "anon-gr-7dd723ba",
 "sceneId": "gr_3f05e38d",
 "sceneName": "生成记录场景S",
 "templateId": "01a17341-31b2-7887-a69c-5ae04440c5fb",
 "templateSeq": 1,
 "templateName": "模板-gr_3f05e38d",
 "appVersion": null,
 "inputs": {
  "age": 28
 },
 "requestMessages": [],
 "requestParams": {},
 "status": "SUCCESS",
 "errorCode": null,
 "errorMessage": null,
 "finalModel": "mock-ok",
 "attemptCount": 1,
 "rawOutput": null,
 "content": null,
 "promptTokens": null,
 "completionTokens": null,
 "latencyMs": 1200,
 "createdAt": "2026-10-25T13:30:18.707886Z",
 "completedAt": "2026-10-25T13:30:18.707886Z",
 "rating": "UNUSABLE",
 "ratingTags": [
  "VAGUE"
 ],
 "ratingRemark": null,
 "ratedBy": "admin",
 "ratedAt": "2026-10-25T13:30:19.986143Z",
 "attempts": []
}
```

## step 8 记录条数（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'SELECT count(*) FROM loves_ai_generation WHERE id='"'"'b209d36d-9414-4d5b-8d7a-824c94fbb168'"'"';'
```

```
1
```

## step 9 loves_ai_generation* 表清单（等价 \dt）（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'SELECT tablename FROM pg_tables WHERE tablename LIKE '"'"'loves_ai_generation%'"'"' ORDER BY 1;'
```

```
loves_ai_generation
loves_ai_generation_attempt
```
