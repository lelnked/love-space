# TC-system-config-IT-010 请求/响应存证

> GET /page 排序 updated_at DESC 与分页参数口径
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 前置 新增 sort_hjbj2n.a

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "sort_hjbj2n.a", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d5e5-7a71-b1f6-9eb3cd921112",
 "key": "sort_hjbj2n.a",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.085566638Z",
 "updatedAt": "2026-11-10T05:05:30.085566638Z"
}
```

## step 2 前置 新增 sort_hjbj2n.b

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "sort_hjbj2n.b", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d624-72c8-9fbd-55cd0d614519",
 "key": "sort_hjbj2n.b",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.148111576Z",
 "updatedAt": "2026-11-10T05:05:30.148111576Z"
}
```

## step 3 前置 新增 sort_hjbj2n.c

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "sort_hjbj2n.c", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d663-7058-9be8-aba7a360a7ba",
 "key": "sort_hjbj2n.c",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.210961093Z",
 "updatedAt": "2026-11-10T05:05:30.210961093Z"
}
```

## step 4 前置 PUT 编辑 a 的 name

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/01a1c3d8-d5e5-7a71-b1f6-9eb3cd921112 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "x改", "value": 1}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d5e5-7a71-b1f6-9eb3cd921112",
 "key": "sort_hjbj2n.a",
 "name": "x改",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.085567Z",
 "updatedAt": "2026-11-10T05:05:30.325697793Z"
}
```

## step 5 GET /page keyword=sort_hjbj2n

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=sort_hjbj2n&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d8-d5e5-7a71-b1f6-9eb3cd921112",
   "key": "sort_hjbj2n.a",
   "name": "x改",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.085567Z",
   "updatedAt": "2026-11-10T05:05:30.325698Z"
  },
  {
   "id": "01a1c3d8-d663-7058-9be8-aba7a360a7ba",
   "key": "sort_hjbj2n.c",
   "name": "x",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.210961Z",
   "updatedAt": "2026-11-10T05:05:30.210961Z"
  },
  {
   "id": "01a1c3d8-d624-72c8-9fbd-55cd0d614519",
   "key": "sort_hjbj2n.b",
   "name": "x",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.148112Z",
   "updatedAt": "2026-11-10T05:05:30.148112Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 3,
 "totalPages": 1
}
```

## step 6 GET /page keyword=sort_hjbj2n

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=sort_hjbj2n&page=1&size=50' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d8-d5e5-7a71-b1f6-9eb3cd921112",
   "key": "sort_hjbj2n.a",
   "name": "x改",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.085567Z",
   "updatedAt": "2026-11-10T05:05:30.325698Z"
  },
  {
   "id": "01a1c3d8-d663-7058-9be8-aba7a360a7ba",
   "key": "sort_hjbj2n.c",
   "name": "x",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.210961Z",
   "updatedAt": "2026-11-10T05:05:30.210961Z"
  },
  {
   "id": "01a1c3d8-d624-72c8-9fbd-55cd0d614519",
   "key": "sort_hjbj2n.b",
   "name": "x",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.148112Z",
   "updatedAt": "2026-11-10T05:05:30.148112Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 3,
 "totalPages": 1
}
```

## step 7 GET /page keyword=sort_hjbj2n

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=sort_hjbj2n&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a1c3d8-d5e5-7a71-b1f6-9eb3cd921112",
   "key": "sort_hjbj2n.a",
   "name": "x改",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.085567Z",
   "updatedAt": "2026-11-10T05:05:30.325698Z"
  },
  {
   "id": "01a1c3d8-d663-7058-9be8-aba7a360a7ba",
   "key": "sort_hjbj2n.c",
   "name": "x",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.210961Z",
   "updatedAt": "2026-11-10T05:05:30.210961Z"
  },
  {
   "id": "01a1c3d8-d624-72c8-9fbd-55cd0d614519",
   "key": "sort_hjbj2n.b",
   "name": "x",
   "value": 1,
   "description": null,
   "createdAt": "2026-11-10T05:05:30.148112Z",
   "updatedAt": "2026-11-10T05:05:30.148112Z"
  }
 ],
 "page": 1,
 "size": 200,
 "totalElements": 3,
 "totalPages": 1
}
```

## step 8 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d5e5-7a71-b1f6-9eb3cd921112 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 9 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d624-72c8-9fbd-55cd0d614519 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 10 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d663-7058-9be8-aba7a360a7ba \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
