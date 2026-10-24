# TC-ai-IT-018 请求/响应存证

> POST /api/admin/ai/prompt-templates 场景不存在返回 404
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 psql 确认 no_such_scene 不存在

```sql
select count(*) from loves_ai_scene where scene_id='no_such_scene'
```

```
0
```

## step 2 新建模板 sceneId=no_such_scene

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"no_such_scene","name":"x","systemPrompt":"x","userPromptTemplate":"x","variables":[]}'
```

HTTP 404（Content-Type: application/json）

```json
{
 "status": 404,
 "error": "Not Found",
 "message": "场景不存在：no_such_scene",
 "path": "/api/admin/ai/prompt-templates"
}
```
