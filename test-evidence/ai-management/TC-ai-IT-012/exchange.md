# TC-ai-IT-012 请求/响应存证

> PUT /api/admin/ai/scenes/{id}/models 清除备用模型
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_0269eb7c

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_0269eb7c","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2287-7d27-8bd6-231d9e7db872",
 "sceneId": "t_0269eb7c",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.607730626Z",
 "updatedAt": "2026-10-24T17:51:37.607730626Z"
}
```

## step 2（前置） 配置模型 mock-ok/mock-fenced

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-2287-7d27-8bd6-231d9e7db872/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok","backupModel":"mock-fenced"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 3 backupModel=null

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-2287-7d27-8bd6-231d9e7db872/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok","backupModel":null}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 4 场景详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-2287-7d27-8bd6-231d9e7db872' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2287-7d27-8bd6-231d9e7db872",
 "sceneId": "t_0269eb7c",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "mock-ok",
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.607731Z",
 "updatedAt": "2026-10-24T17:51:37.639816Z"
}
```

## step 5（前置） 配置模型 mock-ok/mock-fenced

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-2287-7d27-8bd6-231d9e7db872/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok","backupModel":"mock-fenced"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 6 backupModel=""

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-2287-7d27-8bd6-231d9e7db872/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok","backupModel":""}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 7 场景详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-2287-7d27-8bd6-231d9e7db872' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2287-7d27-8bd6-231d9e7db872",
 "sceneId": "t_0269eb7c",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": "mock-ok",
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.607731Z",
 "updatedAt": "2026-10-24T17:51:37.680631Z"
}
```

> 备注: 步骤5 前重新配回 backupModel=mock-fenced，使「空串清除」独立可观察。
