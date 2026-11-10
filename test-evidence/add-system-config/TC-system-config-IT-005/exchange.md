# TC-system-config-IT-005 请求/响应存证

> POST key 格式与长度边界
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 key 大写与连字符

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "Home-Notice", "name": "x", "value": 1}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "key 须以小写字母开头，仅含小写字母、数字、下划线、点，长度 2~64",
 "path": "/api/admin/system-configs"
}
```

## step 2 key 数字开头

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "1abc", "name": "x", "value": 1}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "key 须以小写字母开头，仅含小写字母、数字、下划线、点，长度 2~64",
 "path": "/api/admin/system-configs"
}
```

## step 3 key 1 位

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "a", "name": "x", "value": 1}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "key 须以小写字母开头，仅含小写字母、数字、下划线、点，长度 2~64",
 "path": "/api/admin/system-configs"
}
```

## step 4 key 65 位

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "kedzhlaridmedzhlaridmedzhlaridmedzhlaridmedzhlaridmedzhlaridmedzh", "name": "x", "value": 1}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "key 须以小写字母开头，仅含小写字母、数字、下划线、点，长度 2~64",
 "path": "/api/admin/system-configs"
}
```

## step 5 不含 key 字段

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "x", "value": 1}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "key 不能为空",
 "path": "/api/admin/system-configs"
}
```

## step 6 核对 Home-Notice/1abc 未创建（keyword=1abc）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=1abc&page=1&size=20' \
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

## step 7 核对 65 位 key 未创建

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=kedzhlaridmedzhlaridmedzhlarid&page=1&size=20' \
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

## step 8 key 2 位 ka

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "ka", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d48e-74b6-b210-a214c6e3dac0",
 "key": "ka",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:29.74223845Z",
 "updatedAt": "2026-11-10T05:05:29.74223845Z"
}
```

## step 9 key 恰 64 位

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "k_edzhlaridm.zwd78warcoj11pazsyy728trdor1brilcilflfe3cyl3361mfdp", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d49b-7157-a174-baddb9b27483",
 "key": "k_edzhlaridm.zwd78warcoj11pazsyy728trdor1brilcilflfe3cyl3361mfdp",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:29.75502903Z",
 "updatedAt": "2026-11-10T05:05:29.75502903Z"
}
```

## step 10 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d48e-74b6-b210-a214c6e3dac0 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 11 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d49b-7157-a174-baddb9b27483 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
