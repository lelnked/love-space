# TC-ai-IT-017 请求/响应存证

> POST /api/admin/ai/prompt-templates 变量声明非法或重复返回 400
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_c64b8432

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_c64b8432","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2f53-78f6-9753-bb0a07e8fb2f",
 "sceneId": "t_c64b8432",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:40.883500856Z",
 "updatedAt": "2026-10-24T17:51:40.883500856Z"
}
```

## step 2 psql 该场景模板数

```sql
select count(*) from loves_ai_prompt_template where scene_id='t_c64b8432'
```

```
0
```

## step 3 非法变量 [{"name":"1abc","required":false}]

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_c64b8432","name":"非法","systemPrompt":"x","userPromptTemplate":"年龄{{age}}","variables":[{"name":"1abc","required":false}]}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "变量名不合法：1abc",
 "path": "/api/admin/ai/prompt-templates"
}
```

## step 4 非法变量 [{"name":"age","required":true},{"name":"age","required":false}]

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_c64b8432","name":"非法","systemPrompt":"x","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true},{"name":"age","required":false}]}'
```

HTTP 400（Content-Type: application/json）

```json
{
 "status": 400,
 "error": "Bad Request",
 "message": "变量名重复：age",
 "path": "/api/admin/ai/prompt-templates"
}
```

## step 5 psql 该场景模板数

```sql
select count(*) from loves_ai_prompt_template where scene_id='t_c64b8432'
```

```
0
```
