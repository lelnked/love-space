# TC-ai-IT-010 请求/响应存证

> PUT /api/admin/ai/scenes/{id}/models 模型不在白名单返回 400
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_b02f7532

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_b02f7532", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，8ms）

```json
{
 "id": "01a1865e-8a76-7004-9163-0314277b30cd",
 "sceneId": "t_b02f7532",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.2059384Z",
 "updatedAt": "2026-10-29T06:35:05.2059384Z"
}
```

## step 2 （前置）配置模型 {"primaryModel": "model-a"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8a76-7004-9163-0314277b30cd/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-a"}'
```

HTTP 204（Content-Type: -，8ms）

```json
(空)
```

## step 3 非法配置 {"primaryModel": "model-x"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8a76-7004-9163-0314277b30cd/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-x"}'
```

HTTP 400（Content-Type: application/json，9ms）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "主模型不在可选模型列表中：model-x",
 "path": "/api/admin/ai/scenes/01a1865e-8a76-7004-9163-0314277b30cd/models"
}
```

## step 4 非法配置 {"primaryModel": "model-a", "backupModel": "model-x"}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8a76-7004-9163-0314277b30cd/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-a", "backupModel": "model-x"}'
```

HTTP 400（Content-Type: application/json，4ms）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "备用模型不在可选模型列表中：model-x",
 "path": "/api/admin/ai/scenes/01a1865e-8a76-7004-9163-0314277b30cd/models"
}
```

## step 5 非法配置 {}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8a76-7004-9163-0314277b30cd/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{}'
```

HTTP 400（Content-Type: application/json，7ms）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "主模型不能为空",
 "path": "/api/admin/ai/scenes/01a1865e-8a76-7004-9163-0314277b30cd/models"
}
```

## step 6 场景详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8a76-7004-9163-0314277b30cd' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，4ms）

```json
{
 "id": "01a1865e-8a76-7004-9163-0314277b30cd",
 "sceneId": "t_b02f7532",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "model-a",
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.205938Z",
 "updatedAt": "2026-10-29T06:35:05.21589Z"
}
```
