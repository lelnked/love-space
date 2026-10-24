# TC-ai-IT-011 请求/响应存证

> PUT /api/admin/ai/scenes/{id}/models 主备相同返回 400
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_e71119ce

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_e71119ce","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-224e-7873-9e21-078c39bf4f70",
 "sceneId": "t_e71119ce",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.550426665Z",
 "updatedAt": "2026-10-24T17:51:37.550426665Z"
}
```

## step 2（前置） 配置模型 mock-fenced/-

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-224e-7873-9e21-078c39bf4f70/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-fenced"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 3 主备相同

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-224e-7873-9e21-078c39bf4f70/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok","backupModel":"mock-ok"}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "备用模型不能与主模型相同",
 "path": "/api/admin/ai/scenes/01a16f0a-224e-7873-9e21-078c39bf4f70/models"
}
```

## step 4 场景详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-224e-7873-9e21-078c39bf4f70' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-224e-7873-9e21-078c39bf4f70",
 "sceneId": "t_e71119ce",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "mock-fenced",
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.550427Z",
 "updatedAt": "2026-10-24T17:51:37.565938Z"
}
```
