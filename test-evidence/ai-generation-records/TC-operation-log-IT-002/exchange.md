# TC-operation-log-IT-002 请求/响应存证

> 业务校验失败（400）时不产生留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 基线 city 日志

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=city&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17341-5b6b-70ed-92c3-5b1e5e093abc",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:25.515Z"
  },
  {
   "id": "46a8ca8c-f3e4-46eb-9d25-aa230f744226",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:16:15.839224Z"
  },
  {
   "id": "09427d0a-d2f1-4a7a-88e2-d8f40b1780ca",
   "username": "alice",
   "module": "city",
   "action": "update",
   "target": null,
   "createdAt": "2026-10-25T13:16:15.839224Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 3,
 "totalPages": 1
}
```

## step 2 创建城市（中文名为空）

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/cities' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"chineseName": "", "englishName": "X", "chineseProvince": "X", "englishProvince": "X"}'
```

HTTP 400（Content-Type: application/json）

```
{
 "status": 400,
 "error": "Bad Request",
 "message": "中文名不能为空",
 "path": "/api/admin/cities"
}
```

## step 3 2s 后复查

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=city&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17341-5b6b-70ed-92c3-5b1e5e093abc",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:25.515Z"
  },
  {
   "id": "46a8ca8c-f3e4-46eb-9d25-aa230f744226",
   "username": "admin",
   "module": "city",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:16:15.839224Z"
  },
  {
   "id": "09427d0a-d2f1-4a7a-88e2-d8f40b1780ca",
   "username": "alice",
   "module": "city",
   "action": "update",
   "target": null,
   "createdAt": "2026-10-25T13:16:15.839224Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 3,
 "totalPages": 1
}
```
