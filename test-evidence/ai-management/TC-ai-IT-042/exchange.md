# TC-ai-IT-042 请求/响应存证

> POST /api/app/ai/generations 场景不存在返回 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-638f5eed' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"unknown","clientRequestId":"c-e7b43859-d8f2-4470-8b0e-630bae583aac"}'
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "场景不存在：unknown",
 "path": "/api/app/ai/generations"
}
```

## step 2 psql 统计生成记录 client_request_id='c-e7b43859-d8f2-4470-8b0e-630bae583aac'

```sql
select count(*) from loves_ai_generation where client_request_id='c-e7b43859-d8f2-4470-8b0e-630bae583aac'
```

```
0
```
