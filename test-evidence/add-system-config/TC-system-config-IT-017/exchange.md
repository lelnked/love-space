# TC-system-config-IT-017 请求/响应存证

> 编辑 value 后 App 立即读到新值
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 前置 admin 新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "home.notice_figiuz", "name": "首页公告", "value": {"text": "旧"}}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d93c-74f3-af92-104155e15746",
 "key": "home.notice_figiuz",
 "name": "首页公告",
 "value": {
  "text": "旧"
 },
 "description": null,
 "createdAt": "2026-11-10T05:05:30.94026082Z",
 "updatedAt": "2026-11-10T05:05:30.94026082Z"
}
```

## step 2 App GET（编辑前）

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/home.notice_figiuz \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "home.notice_figiuz",
 "value": {
  "text": "旧"
 }
}
```

## step 3 admin PUT

```bash
curl -s -i -X PUT http://localhost:21423/api/admin/system-configs/01a1c3d8-d93c-74f3-af92-104155e15746 \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name": "首页公告", "value": {"text": "新"}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d93c-74f3-af92-104155e15746",
 "key": "home.notice_figiuz",
 "name": "首页公告",
 "value": {
  "text": "新"
 },
 "description": null,
 "createdAt": "2026-11-10T05:05:30.940261Z",
 "updatedAt": "2026-11-10T05:05:30.962709049Z"
}
```

## step 4 App GET（编辑后立即）

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/home.notice_figiuz \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "home.notice_figiuz",
 "value": {
  "text": "新"
 }
}
```

## step 5 收尾 DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d93c-74f3-af92-104155e15746 \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```
