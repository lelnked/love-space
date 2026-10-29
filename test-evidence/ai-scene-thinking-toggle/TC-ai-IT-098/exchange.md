# TC-ai-IT-098 请求/响应存证

> PUT /api/admin/ai/scenes/{id}/models 开启深度思考
> 环境: admin `http://localhost:8080` / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-29　执行器: api-test-runner（change ai-scene-thinking-toggle）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。`export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 生成/连接测试类用例中的 model-a → `mock-ok`（假方舟对未知 model 返回 404 ModelNotFound）；纯配置类用例 model-a/model-b 原样使用（均在 AI_MODELS 内）。

## step 1 （前置）创建场景 t_64a1884a

```bash
curl -s -i -X POST 'http://localhost:8080/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "t_64a1884a", "name": "周期建议"}'
```

HTTP 201（Content-Type: application/json，8ms）

```json
{
 "id": "01a1865e-8b54-7d1e-b460-0342bf28310b",
 "sceneId": "t_64a1884a",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.428756948Z",
 "updatedAt": "2026-10-29T06:35:05.428756948Z"
}
```

## step 2 配置模型 {"primaryModel": "model-a", "thinkingEnabled": true}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8b54-7d1e-b460-0342bf28310b/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-a", "thinkingEnabled": true}'
```

HTTP 204（Content-Type: -，7ms）

```json
(空)
```

## step 3 场景详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8b54-7d1e-b460-0342bf28310b' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，4ms）

```json
{
 "id": "01a1865e-8b54-7d1e-b460-0342bf28310b",
 "sceneId": "t_64a1884a",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "model-a",
 "backupModel": null,
 "thinkingEnabled": true,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.428757Z",
 "updatedAt": "2026-10-29T06:35:05.437896Z"
}
```

## step 4 场景分页

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/scenes/page?keyword=t_64a1884a&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，46ms）

```json
{
 "content": [
  {
   "id": "01a1865e-8b54-7d1e-b460-0342bf28310b",
   "sceneId": "t_64a1884a",
   "name": "周期建议",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "thinkingEnabled": true,
   "activeTemplate": null,
   "createdAt": "2026-10-29T06:35:05.428757Z",
   "updatedAt": "2026-10-29T06:35:05.437896Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 1,
 "totalPages": 1
}
```

## step 5 配置模型 {"primaryModel": "model-a", "thinkingEnabled": false}

```bash
curl -s -i -X PUT 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8b54-7d1e-b460-0342bf28310b/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "model-a", "thinkingEnabled": false}'
```

HTTP 204（Content-Type: -，6ms）

```json
(空)
```

## step 6 场景详情

```bash
curl -s -i -X GET 'http://localhost:8080/api/admin/ai/scenes/01a1865e-8b54-7d1e-b460-0342bf28310b' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json，4ms）

```json
{
 "id": "01a1865e-8b54-7d1e-b460-0342bf28310b",
 "sceneId": "t_64a1884a",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "model-a",
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-10-29T06:35:05.428757Z",
 "updatedAt": "2026-10-29T06:35:05.496161Z"
}
```
