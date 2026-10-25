# TC-ai-IT-081 请求/响应存证

> PUT /api/admin/ai/generations/{id}/rating 已评价记录不能清除评价
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 造数已评价 R（USABLE）（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at, rating, rated_by, rated_at) VALUES (gen_random_uuid(), '"'"'gr-df08ecb4'"'"', '"'"'anon-gr-557a65a8'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now(), '"'"'USABLE'"'"', '"'"'admin'"'"', now() - interval '"'"'1 hour'"'"') RETURNING id;'
```

```
6e1b69c5-4cbf-46d7-94d8-37b07d94f840
INSERT 0 1
```

## step 2 GET 详情（基线）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "6e1b69c5-4cbf-46d7-94d8-37b07d94f840",
 "clientRequestId": "gr-df08ecb4",
 "anonymousUserId": "anon-gr-557a65a8",
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
 "createdAt": "2026-10-25T13:30:20.062257Z",
 "completedAt": "2026-10-25T13:30:20.062257Z",
 "rating": "USABLE",
 "ratingTags": [],
 "ratingRemark": null,
 "ratedBy": "admin",
 "ratedAt": "2026-10-25T12:30:20.062257Z",
 "attempts": []
}
```

## step 3 PUT {"rating":null}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840/rating' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"rating":null}'
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "评价不能为空",
 "path": "/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840/rating"
}
```

## step 4 PUT {}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840/rating' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{}'
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "评价不能为空",
 "path": "/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840/rating"
}
```

## step 5 PUT {"rating":"NONE"}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840/rating' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"rating":"NONE"}'
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "Malformed request body",
 "path": "/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840/rating"
}
```

## step 6 GET 详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/6e1b69c5-4cbf-46d7-94d8-37b07d94f840' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "6e1b69c5-4cbf-46d7-94d8-37b07d94f840",
 "clientRequestId": "gr-df08ecb4",
 "anonymousUserId": "anon-gr-557a65a8",
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
 "createdAt": "2026-10-25T13:30:20.062257Z",
 "completedAt": "2026-10-25T13:30:20.062257Z",
 "rating": "USABLE",
 "ratingTags": [],
 "ratingRemark": null,
 "ratedBy": "admin",
 "ratedAt": "2026-10-25T12:30:20.062257Z",
 "attempts": []
}
```
