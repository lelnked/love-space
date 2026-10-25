# TC-ai-IT-073 请求/响应存证

> GET /api/admin/ai/generations/page 非法日期与枚举返回 400
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 GET rating=GOOD

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?rating=GOOD&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "Invalid value for parameter 'rating': GOOD",
 "path": "/api/admin/ai/generations/page"
}
```

## step 2 GET startDate=2026/10/20

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?startDate=2026/10/20&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "Invalid value for parameter 'startDate': 2026/10/20",
 "path": "/api/admin/ai/generations/page"
}
```

## step 3 GET endDate=2026-13-01

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?endDate=2026-13-01&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "Invalid value for parameter 'endDate': 2026-13-01",
 "path": "/api/admin/ai/generations/page"
}
```

## step 4 GET status=DONE

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?status=DONE&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "Invalid value for parameter 'status': DONE",
 "path": "/api/admin/ai/generations/page"
}
```

## step 5 GET tag=X

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/generations/page?tag=X&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "Invalid value for parameter 'tag': X",
 "path": "/api/admin/ai/generations/page"
}
```
