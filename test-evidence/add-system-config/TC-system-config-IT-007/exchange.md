# TC-system-config-IT-007 请求/响应存证

> POST value 序列化长度边界 65535 / 65536
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 value 序列化 65535 字符

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  --data-binary "$(python3 -c 'import json;print(json.dumps({"key":"v.max_vg4gyl","name":"x","value":"x"*65533}))')"
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d4e8-712b-9f3a-ab54869fb1ea",
 "key": "v.max_vg4gyl",
 "name": "x",
 "value": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
... <截断，原长 65728 字符> ...
xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
 "description": null,
 "createdAt": "2026-11-10T05:05:29.83201923Z",
 "updatedAt": "2026-11-10T05:05:29.83201923Z"
}
```

## step 2 GET 详情

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/01a1c3d8-d4e8-712b-9f3a-ab54869fb1ea \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d4e8-712b-9f3a-ab54869fb1ea",
 "key": "v.max_vg4gyl",
 "name": "x",
 "value": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
... <截断，原长 65724 字符> ...
xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
 "description": null,
 "createdAt": "2026-11-10T05:05:29.832019Z",
 "updatedAt": "2026-11-10T05:05:29.832019Z"
}
```

## step 3 value 序列化 65536 字符

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  --data-binary "$(python3 -c 'import json;print(json.dumps({"key":"v.over_vg4gyl","name":"x","value":"x"*65534}))')"
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "value 过长",
 "path": "/api/admin/system-configs"
}
```

## step 4 核对 v.over 未创建

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/system-configs/page?keyword=v.over_vg4gyl&page=1&size=20' \
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

## step 5 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d4e8-712b-9f3a-ab54869fb1ea \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
