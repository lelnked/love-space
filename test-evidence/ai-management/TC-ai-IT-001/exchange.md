# TC-ai-IT-001 请求/响应存证

> GET /api/admin/ai/models 返回环境变量配置的模型列表
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

> 备注: 环境 AI_MODELS 为 10 个 mock 模型（model-a/model-b 映射），断言 body 与 AI_MODELS 顺序一致。

## step 1 GET 模型列表

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/models' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
[
 "mock-ok",
 "mock-fenced",
 "mock-format-error",
 "mock-empty",
 "mock-refused",
 "mock-429",
 "mock-500",
 "mock-timeout",
 "mock-flaky",
 "mock-slow"
]
```
