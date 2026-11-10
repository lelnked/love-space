# TC-system-config-IT-009 请求/响应存证

> GET /api/app/system-configs/{key} 单个查询
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081`
> 执行时间: 2026-11-10　执行器: api-test-runner（change add-system-config）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 app GET ios_llm_popup

```bash
curl -s -i -X GET http://localhost:8081/api/app/system-configs/ios_llm_popup \
  -H "X-API-Key: $APP_API_KEY"
```

HTTP 200（Content-Type: application/json）

```json
{
 "key": "ios_llm_popup",
 "value": {
  "enabled": true
 }
}
```
