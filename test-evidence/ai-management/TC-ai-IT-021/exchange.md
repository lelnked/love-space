# TC-ai-IT-021 请求/响应存证

> PUT /api/admin/ai/prompt-templates/{id} 版本冲突返回 409
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1（前置） 创建场景 t_7bb4a28e

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_7bb4a28e","name":"周期建议"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-30e5-7c09-b590-5a51960a7567",
 "sceneId": "t_7bb4a28e",
 "name": "周期建议",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:41.285663495Z",
 "updatedAt": "2026-10-24T17:51:41.285663495Z"
}
```

## step 2（前置） 新建模板 T

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_7bb4a28e","name":"T","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-30f7-70d6-8ca8-b551d6176378",
 "sceneId": "t_7bb4a28e",
 "seq": 1,
 "name": "T",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "年龄{{age}}",
 "variables": [
  {
   "name": "age",
   "required": true,
   "description": "年龄"
  }
 ],
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
 "createdAt": "2026-10-24T17:51:41.302993884Z",
 "updatedAt": "2026-10-24T17:51:41.302993884Z",
 "version": 0
}
```

## step 3（前置） 以正确 version 修改一次

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-30f7-70d6-8ca8-b551d6176378' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"T-改1","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}],"version":0}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-30f7-70d6-8ca8-b551d6176378",
 "sceneId": "t_7bb4a28e",
 "seq": 1,
 "name": "T-改1",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "年龄{{age}}",
 "variables": [
  {
   "name": "age",
   "required": true,
   "description": "年龄"
  }
 ],
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
 "createdAt": "2026-10-24T17:51:41.302994Z",
 "updatedAt": "2026-10-24T17:51:41.320556895Z",
 "version": 1
}
```

## step 4 带旧 version=0 修改

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-30f7-70d6-8ca8-b551d6176378' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"T-改2","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"年龄{{age}}","variables":[{"name":"age","required":true,"description":"年龄"}],"version":0}'
```

HTTP 409（Content-Type: application/json）

```json
{
 "status": 409,
 "error": "Conflict",
 "message": "内容已被他人更新，请刷新",
 "path": "/api/admin/ai/prompt-templates/01a16f0a-30f7-70d6-8ca8-b551d6176378"
}
```

## step 5 模板详情

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a16f0a-30f7-70d6-8ca8-b551d6176378' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "id": "01a16f0a-30f7-70d6-8ca8-b551d6176378",
 "sceneId": "t_7bb4a28e",
 "seq": 1,
 "name": "T-改1",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "年龄{{age}}",
 "variables": [
  {
   "name": "age",
   "required": true,
   "description": "年龄"
  }
 ],
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
 "createdAt": "2026-10-24T17:51:41.302994Z",
 "updatedAt": "2026-10-24T17:51:41.320557Z",
 "version": 1
}
```
