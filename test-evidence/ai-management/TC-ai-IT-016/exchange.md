# TC-ai-IT-016 请求/响应存证

> POST /api/admin/ai/prompt-templates 引用未声明变量也可保存
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_7c3429cd

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_7c3429cd","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2f25-76ef-9bc5-dfb3bc0bd07d",
 "sceneId": "t_7c3429cd",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:40.837378611Z",
 "updatedAt": "2026-10-24T17:51:40.837378611Z"
}
```

## step 2 新建引用 {{mood}} 未声明

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_7c3429cd","name":"未声明","systemPrompt":"你是助手","userPromptTemplate":"心情{{mood}}","variables":[]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2f36-7136-87d4-0c0348447de6",
 "sceneId": "t_7c3429cd",
 "seq": 1,
 "name": "未声明",
 "systemPrompt": "你是助手",
 "userPromptTemplate": "心情{{mood}}",
 "variables": [],
 "remark": null,
 "active": false,
 "tested": false,
 "lastTestInputs": null,
 "lastTestOutput": null,
 "lastTestContent": null,
 "lastTestModel": null,
 "lastTestSuccess": null,
 "lastTestError": null,
 "lastTestLatencyMs": null,
 "lastTestAt": null,
 "lastTestBy": null,
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:40.85401585Z",
 "updatedAt": "2026-10-24T17:51:40.85401585Z",
 "version": 0
}
```

## step 3 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-2f36-7136-87d4-0c0348447de6' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-2f36-7136-87d4-0c0348447de6",
 "sceneId": "t_7c3429cd",
 "seq": 1,
 "name": "未声明",
 "systemPrompt": "你是助手",
 "userPromptTemplate": "心情{{mood}}",
 "variables": [],
 "remark": null,
 "active": false,
 "tested": false,
 "lastTestInputs": null,
 "lastTestOutput": null,
 "lastTestContent": null,
 "lastTestModel": null,
 "lastTestSuccess": null,
 "lastTestError": null,
 "lastTestLatencyMs": null,
 "lastTestAt": null,
 "lastTestBy": null,
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-24T17:51:40.854016Z",
 "updatedAt": "2026-10-24T17:51:40.854016Z",
 "version": 0
}
```
