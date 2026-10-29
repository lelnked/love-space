# TC-ai-IT-013 请求/响应存证

> POST /api/admin/ai/models/test-connection 连接测试
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 测试连接 model-a(mock-ok)

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/models/test-connection' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"model": "mock-ok"}'
```

HTTP 200（Content-Type: application/json，79ms）

```json
{
 "ok": true,
 "latencyMs": 72,
 "message": "可达"
}
```

## step 2 测试连接 mock-500（代替停 mock：上游失败）

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/models/test-connection' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"model": "mock-500"}'
```

HTTP 200（Content-Type: application/json，7ms）

```json
{
 "ok": false,
 "latencyMs": 3,
 "message": "方舟返回 HTTP 500"
}
```
