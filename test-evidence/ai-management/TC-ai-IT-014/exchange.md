# TC-ai-IT-014 请求/响应存证

> POST /api/admin/ai/models/test-connection 模型不在白名单返回 400
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 测试连接 model-x

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/models/test-connection' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"model":"model-x"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "模型不在可选模型列表中：model-x",
 "path": "/api/admin/ai/models/test-connection"
}
```

> 备注: 「不调用方舟」无法从 mock 侧计数观察（mock 无调用计数接口）；mock 对未知模型返回 404，若被调用后端会给出 ok=false 的 200 而非 400，故 400 本身即证明白名单在调用前拦截。
