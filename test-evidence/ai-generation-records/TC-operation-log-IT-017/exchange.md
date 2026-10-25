# TC-operation-log-IT-017 请求/响应存证

> 评价 AI 生成记录后异步产生 ai-generation:rate 留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 造数 SUCCESS R（psql）

```bash
psql -h localhost -p 25432 -U iris love_space -At -c 'INSERT INTO loves_ai_generation (id, client_request_id, anonymous_user_id, scene_id, template_id, template_seq, inputs, request_messages, request_params, status, final_model, attempt_count, latency_ms, created_at, completed_at) VALUES (gen_random_uuid(), '"'"'gr-b13a3b71'"'"', '"'"'anon-gr-91ec6233'"'"', '"'"'gr_3f05e38d'"'"', '"'"'01a17341-31b2-7887-a69c-5ae04440c5fb'"'"', 1, '"'"'{"age":28}'"'"', '"'"'[]'"'"', '"'"'{}'"'"', '"'"'SUCCESS'"'"', '"'"'mock-ok'"'"', 1, 1200, now(), now()) RETURNING id;'
```

```
b1076930-17c5-4f79-b2bd-318c05b7242d
INSERT 0 1
```

## step 2 PUT 评价 {"rating": "USABLE"}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/generations/b1076930-17c5-4f79-b2bd-318c05b7242d/rating' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"rating": "USABLE"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 3 轮询 ai-generation 日志（最多 20 次/100ms，存证为最后一次）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=ai-generation&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17341-5133-7a57-8736-419df3c5e741",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "b1076930-17c5-4f79-b2bd-318c05b7242d",
   "createdAt": "2026-10-25T13:30:22.89958Z"
  },
  {
   "id": "01a17341-50e1-7a75-b526-8a8d49d9496c",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "9a5dd7ee-73d9-406c-bb26-bd2b632a7eb3",
   "createdAt": "2026-10-25T13:30:22.817595Z"
  },
  {
   "id": "01a17341-4f17-7778-8b12-73a0c56ee979",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "01a17341-4ef8-7817-8f42-9f4e3a9294db",
   "createdAt": "2026-10-25T13:30:22.359391Z"
  },
  {
   "id": "01a17341-45d6-749f-93c9-9c0b640220a4",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "b209d36d-9414-4d5b-8d7a-824c94fbb168",
   "createdAt": "2026-10-25T13:30:19.990228Z"
  },
  {
   "id": "01a17341-40b3-7859-a97a-1f831f3a0fca",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "eda230f6-62dc-43f9-aa74-3d9e16566785",
   "createdAt": "2026-10-25T13:30:18.675446Z"
  },
  {
   "id": "01a17341-4072-7e08-bfbb-5083071ed538",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "6ba3d8ad-dd57-4819-8997-161747a12d33",
   "createdAt": "2026-10-25T13:30:18.610817Z"
  },
  {
   "id": "01a17334-7dc8-796f-8c91-8a67ed1522fe",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "803c04b4-a976-4499-99b0-8fc6b5d34804",
   "createdAt": "2026-10-25T13:16:22.344515Z"
  },
  {
   "id": "01a17334-7a60-7ddb-906e-815049defc35",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "c27f802b-9238-4b0c-9fdd-30fd1fb2975b",
   "createdAt": "2026-10-25T13:16:21.472758Z"
  },
  {
   "id": "01a17334-7a27-7698-8d50-ac9e2179f06b",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "f4350343-2882-4bef-ab2f-b4f86052444a",
   "createdAt": "2026-10-25T13:16:21.415336Z"
  },
  {
   "id": "01a17334-79ef-702d-81f2-ff340b7e82e0",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "4c58e171-2101-4e00-b534-eca01e049e6b",
   "createdAt": "2026-10-25T13:16:21.358899Z"
  },
  {
   "id": "01a17334-7967-7b39-889a-d0f1d4510064",
   "username": "admin",
   "module": "ai-generation",
   "action": "rate",
   "target": "1e3421a1-593b-4331-bb0e-e62d77b133c6",
   "createdAt": "2026-10-25T13:16:21.221608Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 11,
 "totalPages": 1
}
```
