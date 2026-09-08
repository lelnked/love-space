# TC-city-IT-017 请求/响应存证

执行时间：2026-09-08 12:43 UTC ｜ baseUrl `http://localhost:21423`（admin，test profile）｜ JWT 脱敏为 `$TOKEN`

## step 1 — POST 创建城市，secondaryBackgroundImage 传完整 http URL

```bash
curl -s -i -X POST http://localhost:21423/api/admin/cities -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"chineseName":"非法第二背景图1723559","englishName":"BadSecond1723559","chineseProvince":"测试省","englishProvince":"TestProv","secondaryBackgroundImage":"https://x.com/a.png","online":false}'
```

HTTP/1.1 400 ｜ Content-Type: application/json
```json
{"status":400,"error":"Bad Request","message":"secondaryBackgroundImage 仅接受 OSS objectKey（images/<id>.<ext> 或 bound/<id>.<ext>）","path":"/api/admin/cities"}
```
