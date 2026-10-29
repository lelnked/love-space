# TC-ai-IT-099 请求/响应存证

> PUT /api/admin/ai/scenes/{id}/models 未传或 null 的 thinkingEnabled 视为关闭
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_149a8b96

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_149a8b96", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，6ms）

```json
{
 "id": "01a1865e-8ba4-7099-b34a-905522b8c93e",
 "sceneId": "t_149a8b96",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.507985338Z",
 "updatedAt": "2026-10-29T06:35:05.507985338Z"
}
```

## step 2 （前置）配置模型 {"primaryModel": "model-a", "thinkingEnabled": true}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-a", "thinkingEnabled": true}'
```

HTTP 204（Content-Type: -，6ms）

```json
(空)
```

## step 3 配置模型 {"primaryModel": "model-a"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-a"}'
```

HTTP 204（Content-Type: -，6ms）

```json
(空)
```

## step 4 场景详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，5ms）

```json
{
 "id": "01a1865e-8ba4-7099-b34a-905522b8c93e",
 "sceneId": "t_149a8b96",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "model-a",
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.507985Z",
 "updatedAt": "2026-10-29T06:35:05.521685Z"
}
```

## step 5 步骤4 恢复开启 {"primaryModel": "model-a", "thinkingEnabled": true}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-a", "thinkingEnabled": true}'
```

HTTP 204（Content-Type: -，6ms）

```json
(空)
```

## step 6 配置模型 {"primaryModel": "model-a", "thinkingEnabled": null}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-a", "thinkingEnabled": null}'
```

HTTP 204（Content-Type: -，5ms）

```json
(空)
```

## step 7 场景详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，3ms）

```json
{
 "id": "01a1865e-8ba4-7099-b34a-905522b8c93e",
 "sceneId": "t_149a8b96",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "model-a",
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.507985Z",
 "updatedAt": "2026-10-29T06:35:05.538971Z"
}
```

## step 8 步骤6 前恢复开启 {"primaryModel": "model-a", "thinkingEnabled": true}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-a", "thinkingEnabled": true}'
```

HTTP 204（Content-Type: -，5ms）

```json
(空)
```

## step 9 步骤6 非法模型 + thinkingEnabled=false

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-x", "thinkingEnabled": false}'
```

HTTP 400（Content-Type: application/json，3ms）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "主模型不在可选模型列表中：model-x",
 "path": "/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e/models"
}
```

## step 10 场景详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8ba4-7099-b34a-905522b8c93e' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，3ms）

```json
{
 "id": "01a1865e-8ba4-7099-b34a-905522b8c93e",
 "sceneId": "t_149a8b96",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "model-a",
 "backupModel": null,
 "thinkingEnabled": true,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.507985Z",
 "updatedAt": "2026-10-29T06:35:05.548154Z"
}
```
