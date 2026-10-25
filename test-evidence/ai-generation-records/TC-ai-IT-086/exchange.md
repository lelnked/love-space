# TC-ai-IT-086 请求/响应存证

> MEMBER 角色可评价生成记录，未登录返回 401
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 （前置） ADMIN 创建 MEMBER 账号 u_grc6c8c78b

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/managers' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"username": "u_grc6c8c78b", "password": "Member#2026x", "nickname": "生成记录成员"}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-5001-76c4-bfc5-d04f3f3ed43a",
 "username": "u_grc6c8c78b",
 "nickname": "生成记录成员",
 "role": "MEMBER",
 "enable": true,
 "createdAt": "2026-10-25T13:30:22.593355673Z"
}
```

## step 2 MEMBER 登录 u_grc6c8c78b

```bash
curl -s -X POST 'http://localhost:21423/api/admin/auth/login' -H 'Content-Type: application/json' -d '{"username":"u_grc6c8c78b","password":"Member#2026x"}'
```

HTTP 200（Content-Type: application/json）

```
{"token": "$MEMBER_TOKEN", ...}
```

## step 3 造数未评价 SUCCESS R（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-2bc3fb15'"'"', '"'"'anon-gr-16d951c1'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now()) RETURNING id;'
```

```
6da0ee31-3225-4831-92dd-1c8066a870e1
INSERT 0 1
```

## step 4 MEMBER GET 列表

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?requestId=6da0ee31-3225-4831-92dd-1c8066a870e1&page=1&size=20' \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "6da0ee31-3225-4831-92dd-1c8066a870e1",
   "createdAt": "2026-10-25T13:30:22.711099Z",
   "anonymousUserId": "anon-gr-16d951c1",
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

## step 5 MEMBER GET 详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/6da0ee31-3225-4831-92dd-1c8066a870e1' \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "6da0ee31-3225-4831-92dd-1c8066a870e1",
 "clientRequestId": "gr-2bc3fb15",
 "anonymousUserId": "anon-gr-16d951c1",
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
 "createdAt": "2026-10-25T13:30:22.711099Z",
 "completedAt": "2026-10-25T13:30:22.711099Z",
 "rating": null,
 "ratingTags": [],
 "ratingRemark": null,
 "ratedBy": null,
 "ratedAt": null,
 "attempts": []
}
```

## step 6 MEMBER PUT 评价

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/6da0ee31-3225-4831-92dd-1c8066a870e1/rating' \
  -H "Authorization: Bearer $MEMBER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"rating": "USABLE"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 7 详情核对 ratedBy

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/6da0ee31-3225-4831-92dd-1c8066a870e1' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "6da0ee31-3225-4831-92dd-1c8066a870e1",
 "clientRequestId": "gr-2bc3fb15",
 "anonymousUserId": "anon-gr-16d951c1",
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
 "createdAt": "2026-10-25T13:30:22.711099Z",
 "completedAt": "2026-10-25T13:30:22.711099Z",
 "rating": "USABLE",
 "ratingTags": [],
 "ratingRemark": null,
 "ratedBy": "u_grc6c8c78b",
 "ratedAt": "2026-10-25T13:30:22.751164Z",
 "attempts": []
}
```

## step 8 无 Authorization GET 列表

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page'
```

HTTP 401（Content-Type: application/json）

```
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/ai/generations/page"
}
```

## step 9 无 Authorization PUT 评价

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/6da0ee31-3225-4831-92dd-1c8066a870e1/rating' \
  -H 'Content-Type: application/json' \
  -d '{"rating": "USABLE"}'
```

HTTP 401（Content-Type: application/json）

```
{
 "status": 401,
 "error": "Unauthorized",
 "message": "未登录或登录已过期",
 "path": "/api/admin/ai/generations/6da0ee31-3225-4831-92dd-1c8066a870e1/rating"
}
```
