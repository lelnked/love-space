# TC-system-config-IT-018 请求/响应存证

> 删除后 App 查询返回 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`，两后端共库
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（MEMBER 账号 JWT 记为 `$MEMBER_TOKEN`）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 前置 admin 新增

```bash
curl -s -i -X POST http://localhost:21423/api/admin/system-configs \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"key": "home.notice_gma1v4", "name": "首页公告", "value": {"text": "hi"}}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1c3d8-d973-706f-acd4-6ef349a6c46d",
 "key": "home.notice_gma1v4",
 "name": "首页公告",
 "value": {
  "text": "hi"
 },
 "description": null,
 "createdAt": "2026-11-10T05:05:30.994981986Z",
 "updatedAt": "2026-11-10T05:05:30.994981986Z"
}
```

## step 2 前置 App GET

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/home.notice_gma1v4 \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "home.notice_gma1v4",
 "value": {
  "text": "hi"
 }
}
```

## step 3 admin DELETE

```bash
curl -s -i -X DELETE http://localhost:21423/api/admin/system-configs/01a1c3d8-d973-706f-acd4-6ef349a6c46d \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 204（Content-Type: -）

```json
(empty body)
```

## step 4 App 再次 GET

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/home.notice_gma1v4 \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "配置不存在",
 "path": "/api/app/system-configs/home.notice_gma1v4"
}
```

## step 5 App 批量 GET

```bash
curl -s -i -X GET 'http://localhost:8081/api/app/system-configs?keys=home.notice_gma1v4' \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
[]
```
