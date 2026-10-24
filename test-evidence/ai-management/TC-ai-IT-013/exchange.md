# TC-ai-IT-013 请求/响应存证

> POST /api/admin/ai/models/test-connection 连接测试
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

> 备注: 步骤4 原写法需停掉 mock 或改 ARK_BASE_URL 重启 admin；按「不擅自停/启服务」规则，改用同一假方舟上必失败的模型 mock-500（HTTP 500）与 mock-timeout（超时）代替「方舟不可达」，验证 ok=false + 失败原因。

## step 1 测试连接 model-a(mock-ok)

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/models/test-connection' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"model":"mock-ok"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "ok": true,
 "latencyMs": 4,
 "message": "可达"
}
```

## step 2 测试连接 mock-500（模拟上游失败）

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/models/test-connection' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"model":"mock-500"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "ok": false,
 "latencyMs": 4,
 "message": "方舟返回 HTTP 500"
}
```

## step 3 测试连接 mock-timeout（模拟上游失败）

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/models/test-connection' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"model":"mock-timeout"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "ok": false,
 "latencyMs": 3002,
 "message": "调用超时（3 秒）"
}
```
