# TC-system-config-IT-016 请求/响应存证

> DELETE /{id} 物理删除与重复删除
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 前置 新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "del.k_6zx08a", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d900-7b44-a85e-b395f8248f0a",
 "key": "del.k_6zx08a",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.880654283Z",
 "updatedAt": "2026-11-10T05:05:30.880654283Z"
}
```

## step 2 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d900-7b44-a85e-b395f8248f0a \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 3 GET 已删 id

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/01a1c3d8-d900-7b44-a85e-b395f8248f0a \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "配置不存在",
 "path": "/api/admin/system-configs/01a1c3d8-d900-7b44-a85e-b395f8248f0a"
}
```

## step 4 再次 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d900-7b44-a85e-b395f8248f0a \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "配置不存在",
 "path": "/api/admin/system-configs/01a1c3d8-d900-7b44-a85e-b395f8248f0a"
}
```

## step 5 同 key 重新新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "del.k_6zx08a", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d925-7d5d-9ee4-96da3cd15bfc",
 "key": "del.k_6zx08a",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.917785269Z",
 "updatedAt": "2026-11-10T05:05:30.917785269Z"
}
```

## step 6 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d925-7d5d-9ee4-96da3cd15bfc \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
