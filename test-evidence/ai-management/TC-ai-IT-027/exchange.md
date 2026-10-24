# TC-ai-IT-027 请求/响应存证

> POST /api/admin/ai/prompt-templates/{id}/test 场景未配主模型返回 409
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_0f3abc73

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_0f3abc73","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-3382-768e-81e1-18522652d906",
 "sceneId": "t_0f3abc73",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:41.954350136Z",
 "updatedAt": "2026-10-24T17:51:41.954350136Z"
}
```

## step 2（前置） 新建模板 模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_0f3abc73","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"无变量","variables":[]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-3392-793c-92e1-3dfd3753f969",
 "sceneId": "t_0f3abc73",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "无变量",
 "variables": [],
 "remark": null,
 "active": false,
 "tested": false,
 "lastTestInputs": null,
 "lastTestOutput": null,
 "lastTestContent": null,
 "lastTestModel": null,
 "lastTestSuccess": null,
 "lastTestError": null,
 "lastTestLatencyMs": null,
 "lastTestAt": null,
 "lastTestBy": null,
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:41.970520384Z",
 "updatedAt": "2026-10-24T17:51:41.970520384Z",
 "version": 0
}
```

## step 3 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-3392-793c-92e1-3dfd3753f969/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{}}'
```

HTTP 409（Content-Type: application/json）

```json
{
 "status": 409,
 "error": "Conflict",
 "message": "场景未配置主模型，无法测试",
 "path": "/api/admin/ai/prompt-templates/01a16f0a-3392-793c-92e1-3dfd3753f969/test"
}
```
