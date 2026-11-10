# TC-system-config-IT-013 请求/响应存证

> MEMBER 角色可完整使用系统配置接口
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 admin 创建 MEMBER 账号

```bash
curl -s -i -X POST http://localhost:21423/api/admin/managers \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"username": "it_sc_member_b695b4", "password": "Passw0rd!23"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d79c-7394-8b7a-bf476cd7b810",
 "username": "it_sc_member_b695b4",
 "nickname": null,
 "role": "MEMBER",
 "enable": true,
 "createdAt": "2026-11-10T05:05:30.524153084Z"
}
```

## step 2 MEMBER 登录

```bash
curl -s -i -X POST http://localhost:21423/api/admin/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username": "it_sc_member_b695b4", "password": "Passw0rd!23"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "token": "$TOKEN",
 "manager": {
  "id": "01a1c3d8-d79c-7394-8b7a-bf476cd7b810",
  "username": "it_sc_member_b695b4",
  "nickname": null,
  "role": "MEMBER"
 }
}
```

## step 3 MEMBER GET /page

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?page=1&size=20' \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d3-e049-7d5b-b16d-a595afb2f5c1",
   "key": "t53f6c10e2319",
   "name": "x",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:00:05.065758Z",
   "updatedAt": "2026-11-10T05:00:05.065758Z"
  },
  {
   "id": "01a1c3d3-dfe4-753e-b628-4d73d8a5d995",
   "key": "t7787eb24518e.days",
   "name": "默认周期",
   "value": 28,
   "description": null,
   "createdAt": "2026-11-10T05:00:04.964254Z",
   "updatedAt": "2026-11-10T05:00:04.964254Z"
  },
  {
   "id": "01a1c3d3-dfd9-7c2e-8e1d-9463da6dcf1c",
   "key": "t7787eb24518e.notice",
   "name": "首页公告",
   
... <截断，原长 135152 字符> ...
xxxxxxx",
   "description": null,
   "createdAt": "2026-11-10T04:59:52.653924Z",
   "updatedAt": "2026-11-10T04:59:52.653924Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 41,
 "totalPages": 3
}
```

## step 4 MEMBER POST 新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $MEMBER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "member.k_b695b4", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d81a-79c2-85bb-898462df248f",
 "key": "member.k_b695b4",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.65051385Z",
 "updatedAt": "2026-11-10T05:05:30.65051385Z"
}
```

## step 5 MEMBER GET /{id}

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/01a1c3d8-d81a-79c2-85bb-898462df248f \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d81a-79c2-85bb-898462df248f",
 "key": "member.k_b695b4",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.650514Z",
 "updatedAt": "2026-11-10T05:05:30.650514Z"
}
```

## step 6 MEMBER PUT /{id}

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/01a1c3d8-d81a-79c2-85bb-898462df248f \
  -H "Authorization: Bearer $MEMBER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "y", "value": 2}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d81a-79c2-85bb-898462df248f",
 "key": "member.k_b695b4",
 "name": "y",
 "value": 2,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.650514Z",
 "updatedAt": "2026-11-10T05:05:30.669861699Z"
}
```

## step 7 MEMBER DELETE /{id}

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d81a-79c2-85bb-898462df248f \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 8 收尾 admin 禁用 MEMBER 账号（后台无删除账号接口）

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/managers/01a1c3d8-d79c-7394-8b7a-bf476cd7b810/disable \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
