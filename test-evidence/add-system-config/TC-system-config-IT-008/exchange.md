# TC-system-config-IT-008 请求/响应存证

> POST name / description 必填与长度边界
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 不含 name

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "n.a_kkjl7d", "value": 1}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "配置名称不能为空",
 "path": "/api/admin/system-configs"
}
```

## step 2 GET /page keyword=n.a_kkjl7d

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=n.a_kkjl7d&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [],
 "page": 1,
 "size": 20,
 "totalElements": 0,
 "totalPages": 0
}
```

## step 3 name 65 字符

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "n.b_dbk6e7", "name": "名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名", "value": 1}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "配置名称长度不能超过 64 个字符",
 "path": "/api/admin/system-configs"
}
```

## step 4 GET /page keyword=n.b_dbk6e7

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=n.b_dbk6e7&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [],
 "page": 1,
 "size": 20,
 "totalElements": 0,
 "totalPages": 0
}
```

## step 5 name 恰 64 字符

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "n.c_g8ouys", "name": "名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d548-7b78-b0fe-a97a5dc32642",
 "key": "n.c_g8ouys",
 "name": "名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名名",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:29.92864315Z",
 "updatedAt": "2026-11-10T05:05:29.92864315Z"
}
```

## step 6 description 501 字符

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "n.d_uy8qyc", "name": "x", "value": 1, "description": "述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "说明长度不能超过 500 个字符",
 "path": "/api/admin/system-configs"
}
```

## step 7 GET /page keyword=n.d_uy8qyc

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=n.d_uy8qyc&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [],
 "page": 1,
 "size": 20,
 "totalElements": 0,
 "totalPages": 0
}
```

## step 8 description 恰 500 字符

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "n.e_4yyxbg", "name": "x", "value": 1, "description": "述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d568-7da3-b1de-4c128c7450ab",
 "key": "n.e_4yyxbg",
 "name": "x",
 "value": 1,
 "description": "述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述述",
 "createdAt": "2026-11-10T05:05:29.960779154Z",
 "updatedAt": "2026-11-10T05:05:29.960779154Z"
}
```

## step 9 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d548-7b78-b0fe-a97a5dc32642 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 10 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d568-7da3-b1de-4c128c7450ab \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
