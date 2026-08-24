# TC-featured-IT-023 GET /api/app/featured-cycle-items?type=UNKNOWN 非法类型值返回 400 — 请求/响应存证

执行日期: 2026-08-24 ｜ app=http://localhost:8081
认证: app 端请求头 `X-API-Key: $APP_API_KEY`（真机密脱敏；shell 中 `export APP_API_KEY=<key>` 后下列 curl 可原样执行）

## Step 1: 传入非法 type 值

```bash
curl -s -i -X GET "http://localhost:8081/api/app/featured-cycle-items?type=UNKNOWN" -H "X-API-Key: $APP_API_KEY"
```

实际响应（HTTP 400，Content-Type: application/json）:

```
HTTP/1.1 400
Content-Type: application/json
```

```json
{"status":400,"error":"Bad Request","message":"Invalid value for parameter 'type': UNKNOWN","path":"/api/app/featured-cycle-items"}
```
