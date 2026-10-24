# TC-ai-IT-003 请求/响应存证

> POST /api/admin/ai/scenes sceneId 重复返回 409
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_4b27b399

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_4b27b399","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-1f31-77d4-ab39-b89fc5a8f487",
 "sceneId": "t_4b27b399",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:36.753423266Z",
 "updatedAt": "2026-10-24T17:51:36.753423266Z"
}
```

## step 2 重复创建

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_4b27b399","name":"重复"}'
```

HTTP 409（Content-Type: application/json）

```json
{
 "status": 409,
 "error": "Conflict",
 "message": "scene_id 已存在",
 "path": "/api/admin/ai/scenes"
}
```

## step 3 psql 该 sceneId 场景数

```sql
select count(*) from loves_ai_scene where scene_id='t_4b27b399'
```

```
1
```
