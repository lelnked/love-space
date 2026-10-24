# TC-ai-IT-010 请求/响应存证

> PUT /api/admin/ai/scenes/{id}/models 模型不在白名单返回 400
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_d3421edf

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_d3421edf","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-21ff-743b-86f0-69344495723e",
 "sceneId": "t_d3421edf",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.471183929Z",
 "updatedAt": "2026-10-24T17:51:37.471183929Z"
}
```

## step 2（前置） 配置模型 mock-ok/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-21ff-743b-86f0-69344495723e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 3 非法配置 {"primaryModel":"model-x"}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-21ff-743b-86f0-69344495723e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"model-x"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "主模型不在可选模型列表中：model-x",
 "path": "/api/admin/ai/scenes/01a16f0a-21ff-743b-86f0-69344495723e/models"
}
```

## step 4 非法配置 {"primaryModel":"mock-ok","backupModel":"model-x"}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-21ff-743b-86f0-69344495723e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok","backupModel":"model-x"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "备用模型不在可选模型列表中：model-x",
 "path": "/api/admin/ai/scenes/01a16f0a-21ff-743b-86f0-69344495723e/models"
}
```

## step 5 非法配置 {}

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-21ff-743b-86f0-69344495723e/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "主模型不能为空",
 "path": "/api/admin/ai/scenes/01a16f0a-21ff-743b-86f0-69344495723e/models"
}
```

## step 6 场景详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-21ff-743b-86f0-69344495723e' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-21ff-743b-86f0-69344495723e",
 "sceneId": "t_d3421edf",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "mock-ok",
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.471184Z",
 "updatedAt": "2026-10-24T17:51:37.487465Z"
}
```
