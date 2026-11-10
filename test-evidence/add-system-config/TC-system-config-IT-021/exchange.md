# TC-system-config-IT-021 请求/响应存证

> GET /api/app/system-configs?keys= 批量查询忽略不存在 key 并保持顺序
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 前置 新增 a.one

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "a.one_94d5hc", "name": "x", "value": 1}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d9d2-716d-8b78-12799c7e2de6",
 "key": "a.one_94d5hc",
 "name": "x",
 "value": 1,
 "description": null,
 "createdAt": "2026-11-10T05:05:31.090039889Z",
 "updatedAt": "2026-11-10T05:05:31.090039889Z"
}
```

## step 2 前置 新增 b.two

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "b.two_94d5hc", "name": "x", "value": "two"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d9dc-7c5c-931b-87ec201c61cf",
 "key": "b.two_94d5hc",
 "name": "x",
 "value": "two",
 "description": null,
 "createdAt": "2026-11-10T05:05:31.100736506Z",
 "updatedAt": "2026-11-10T05:05:31.100736506Z"
}
```

## step 3 App 批量 GET

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=b.two_94d5hc,c.none_94d5hc,a.one_94d5hc,b.two_94d5hc' \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
[
 {
  "key": "b.two_94d5hc",
  "value": "two"
 },
 {
  "key": "a.one_94d5hc",
  "value": 1
 }
]
```

## step 4 App 批量 GET（空白与空项）

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=%20b.two_94d5hc%20,,a.one_94d5hc,' \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
[
 {
  "key": "b.two_94d5hc",
  "value": "two"
 },
 {
  "key": "a.one_94d5hc",
  "value": 1
 }
]
```

## step 5 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d9d2-716d-8b78-12799c7e2de6 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 6 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d9dc-7c5c-931b-87ec201c61cf \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
