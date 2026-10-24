# TC-ai-IT-005 请求/响应存证

> PUT /api/admin/ai/scenes/{id} 更新名称且忽略 body 中 sceneId
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 psql sceneId=other 场景数（基线）

```sql
select count(*) from loves_ai_scene where scene_id='other'
```

```
0
```

## step 2（前置） 创建场景 cycle_advice_19eabb3a

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"cycle_advice_19eabb3a","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2070-7033-be03-6a2b38231328",
 "sceneId": "cycle_advice_19eabb3a",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.07194815Z",
 "updatedAt": "2026-10-24T17:51:37.07194815Z"
}
```

## step 3 更新场景（body 带 sceneId=other）

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-2070-7033-be03-6a2b38231328' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"other","name":"周期建议-改","description":"新说明"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2070-7033-be03-6a2b38231328",
 "sceneId": "cycle_advice_19eabb3a",
 "name": "周期建议-改",
 "description": "新说明",
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.071948Z",
 "updatedAt": "2026-10-24T17:51:37.094761492Z"
}
```

## step 4 场景详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/01a16f0a-2070-7033-be03-6a2b38231328' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2070-7033-be03-6a2b38231328",
 "sceneId": "cycle_advice_19eabb3a",
 "name": "周期建议-改",
 "description": "新说明",
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:37.071948Z",
 "updatedAt": "2026-10-24T17:51:37.094761Z"
}
```

## step 5 psql sceneId=other 场景数

```sql
select count(*) from loves_ai_scene where scene_id='other'
```

```
0
```
