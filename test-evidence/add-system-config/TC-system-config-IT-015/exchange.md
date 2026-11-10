# TC-system-config-IT-015 请求/响应存证

> PUT /{id} 不存在与校验失败
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 PUT 不存在 id

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/00000000-0000-0000-0000-000000000000 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "x", "value": 1}'
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "配置不存在",
 "path": "/api/admin/system-configs/00000000-0000-0000-0000-000000000000"
}
```

## step 2 前置 新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "edit.k_axnsdk", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794",
 "key": "edit.k_axnsdk",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.817349585Z",
 "updatedAt": "2026-11-10T05:05:30.817349585Z"
}
```

## step 3 PUT value=null

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"x","value":null}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "value 不能为空",
 "path": "/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794"
}
```

## step 4 PUT 不含 value

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "x"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "value 不能为空",
 "path": "/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794"
}
```

## step 5 PUT name 65 字符

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn", "value": 2}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "配置名称长度不能超过 64 个字符",
 "path": "/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794"
}
```

## step 6 PUT value 序列化 65536 字符

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  --data-binary "$(python3 -c 'import json;print(json.dumps({"name":"x","value":"x"*65534}))')"
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "value 过长",
 "path": "/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794"
}
```

## step 7 GET 详情

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794",
 "key": "edit.k_axnsdk",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:30.81735Z",
 "updatedAt": "2026-11-10T05:05:30.81735Z"
}
```

## step 8 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d8c1-76d6-9b8c-aa072cdc1794 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
