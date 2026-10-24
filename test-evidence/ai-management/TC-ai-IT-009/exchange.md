# TC-ai-IT-009 请求/响应存证

> PUT /api/admin/ai/scenes/{id}/models 配置主备模型
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_8406945e

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_8406945e","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-21cd-7c0e-9ed5-1f7bfadf716b",
 "sceneId": "t_8406945e",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.421676246Z",
 "updatedAt": "2026-10-24T17:51:37.421676246Z"
}
```

## step 2 配置主备

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-21cd-7c0e-9ed5-1f7bfadf716b/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok","backupModel":"mock-fenced"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 3 场景详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-21cd-7c0e-9ed5-1f7bfadf716b' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-21cd-7c0e-9ed5-1f7bfadf716b",
 "sceneId": "t_8406945e",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "mock-ok",
 "backupModel": "mock-fenced",
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.421676Z",
 "updatedAt": "2026-10-24T17:51:37.441885Z"
}
```
