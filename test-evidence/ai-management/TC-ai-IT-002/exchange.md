# TC-ai-IT-002 请求/响应存证

> POST /api/admin/ai/scenes 创建场景成功
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 创建场景

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"cycle_advice_55c74ed7","name":"周期建议","description":"按周期给建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-1f14-7ab3-b418-bd4622371eda",
 "sceneId": "cycle_advice_55c74ed7",
 "name": "周期建议",
 "description": "按周期给建议",
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:36.724598808Z",
 "updatedAt": "2026-10-24T17:51:36.724598808Z"
}
```

## step 2 场景详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-1f14-7ab3-b418-bd4622371eda' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-1f14-7ab3-b418-bd4622371eda",
 "sceneId": "cycle_advice_55c74ed7",
 "name": "周期建议",
 "description": "按周期给建议",
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:36.724599Z",
 "updatedAt": "2026-10-24T17:51:36.724599Z"
}
```
