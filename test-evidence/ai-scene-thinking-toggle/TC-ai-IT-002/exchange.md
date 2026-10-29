# TC-ai-IT-002 请求/响应存证

> POST /api/admin/ai/scenes 创建场景成功
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 创建场景

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "cycle_advice_3cbdca77", "name": "周期建议", "description": "按周期给建议"}'
```

HTTP 201（Content-Type: application/json，117ms）

```json
{
 "id": "01a1865e-8a21-7fed-9f0d-7420fe9d70dc",
 "sceneId": "cycle_advice_3cbdca77",
 "name": "周期建议",
 "description": "按周期给建议",
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.107086981Z",
 "updatedAt": "2026-10-29T06:35:05.107086981Z"
}
```

## step 2 场景详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8a21-7fed-9f0d-7420fe9d70dc' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，14ms）

```json
{
 "id": "01a1865e-8a21-7fed-9f0d-7420fe9d70dc",
 "sceneId": "cycle_advice_3cbdca77",
 "name": "周期建议",
 "description": "按周期给建议",
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.107087Z",
 "updatedAt": "2026-10-29T06:35:05.107087Z"
}
```
