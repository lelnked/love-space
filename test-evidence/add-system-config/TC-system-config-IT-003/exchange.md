# TC-system-config-IT-003 请求/响应存证

> 标量与数组均可作为 value
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 POST 新增 v.num_iaosyx

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "v.num_iaosyx", "name": "x", "value": 28}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d390-7591-9a2e-bb6bbaf21a22",
 "key": "v.num_iaosyx",
 "name": "x",
 "value": 28,
 "description": null,
 "createdAt": "2026-11-10T05:05:29.488283721Z",
 "updatedAt": "2026-11-10T05:05:29.488283721Z"
}
```

## step 2 POST 新增 v.str_iaosyx

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "v.str_iaosyx", "name": "x", "value": "hello"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d39c-7804-8e62-3df267b2552c",
 "key": "v.str_iaosyx",
 "name": "x",
 "value": "hello",
 "description": null,
 "createdAt": "2026-11-10T05:05:29.500452637Z",
 "updatedAt": "2026-11-10T05:05:29.500452637Z"
}
```

## step 3 POST 新增 v.bool_iaosyx

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "v.bool_iaosyx", "name": "x", "value": false}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d3a8-783c-9158-68849b11c80b",
 "key": "v.bool_iaosyx",
 "name": "x",
 "value": false,
 "description": null,
 "createdAt": "2026-11-10T05:05:29.512467052Z",
 "updatedAt": "2026-11-10T05:05:29.512467052Z"
}
```

## step 4 POST 新增 v.arr_iaosyx

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "v.arr_iaosyx", "name": "x", "value": [1, 2, 3]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d3b5-79f5-8cf2-432b225c3d18",
 "key": "v.arr_iaosyx",
 "name": "x",
 "value": [
  1,
  2,
  3
 ],
 "description": null,
 "createdAt": "2026-11-10T05:05:29.525572875Z",
 "updatedAt": "2026-11-10T05:05:29.525572875Z"
}
```

## step 5 GET 详情 v.num_iaosyx

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/01a1c3d8-d390-7591-9a2e-bb6bbaf21a22 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d390-7591-9a2e-bb6bbaf21a22",
 "key": "v.num_iaosyx",
 "name": "x",
 "value": 28,
 "description": null,
 "createdAt": "2026-11-10T05:05:29.488284Z",
 "updatedAt": "2026-11-10T05:05:29.488284Z"
}
```

## step 6 GET 详情 v.str_iaosyx

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/01a1c3d8-d39c-7804-8e62-3df267b2552c \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d39c-7804-8e62-3df267b2552c",
 "key": "v.str_iaosyx",
 "name": "x",
 "value": "hello",
 "description": null,
 "createdAt": "2026-11-10T05:05:29.500453Z",
 "updatedAt": "2026-11-10T05:05:29.500453Z"
}
```

## step 7 GET 详情 v.bool_iaosyx

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/01a1c3d8-d3a8-783c-9158-68849b11c80b \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d3a8-783c-9158-68849b11c80b",
 "key": "v.bool_iaosyx",
 "name": "x",
 "value": false,
 "description": null,
 "createdAt": "2026-11-10T05:05:29.512467Z",
 "updatedAt": "2026-11-10T05:05:29.512467Z"
}
```

## step 8 GET 详情 v.arr_iaosyx

```bash
curl -s -i -X GET http://localhost:21423/api/admin/system-configs/01a1c3d8-d3b5-79f5-8cf2-432b225c3d18 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d3b5-79f5-8cf2-432b225c3d18",
 "key": "v.arr_iaosyx",
 "name": "x",
 "value": [
  1,
  2,
  3
 ],
 "description": null,
 "createdAt": "2026-11-10T05:05:29.525573Z",
 "updatedAt": "2026-11-10T05:05:29.525573Z"
}
```

## step 9 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d390-7591-9a2e-bb6bbaf21a22 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 10 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d39c-7804-8e62-3df267b2552c \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 11 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d3a8-783c-9158-68849b11c80b \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 12 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d3b5-79f5-8cf2-432b225c3d18 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
