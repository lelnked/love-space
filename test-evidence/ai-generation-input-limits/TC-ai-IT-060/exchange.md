# TC-ai-IT-060 请求/响应存证

> 单个变量恰 2000 字符通过长度校验
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-11-03　执行器: api-test-runner（change ai-generation-input-limits）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。

## step 1 （前置） 创建场景 t_ee8aaa3a

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_ee8aaa3a","name":"输入长度限制"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1a06a-a765-79e6-aea6-9c6bee65189f",
 "sceneId": "t_ee8aaa3a",
 "name": "输入长度限制",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "thinkingEnabled": false,
 "activeTemplate": null,
 "createdAt": "2026-11-03T07:58:26.661546329Z",
 "updatedAt": "2026-11-03T07:58:26.661546329Z"
}
```

## step 2 （前置） 配置模型 mock-ok

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a1a06a-a765-79e6-aea6-9c6bee65189f/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel":"mock-ok"}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 3 （前置） 新建模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_ee8aaa3a","name":"模板","systemPrompt":"你是助手，只输出 JSON","userPromptTemplate":"补充[{{user_input}}]","variables":[{"name":"user_input","required":false}]}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a1a06a-a77c-7f0d-9f7c-637f593d1336",
 "sceneId": "t_ee8aaa3a",
 "seq": 1,
 "name": "模板",
 "systemPrompt": "你是助手，只输出 JSON",
 "userPromptTemplate": "补充[{{user_input}}]",
 "variables": [
  {
   "name": "user_input",
   "required": false,
   "description": null
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
 "createdAt": "2026-11-03T07:58:26.684890301Z",
 "updatedAt": "2026-11-03T07:58:26.684890301Z",
 "version": 0
}
```

## step 4 （前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a1a06a-a77c-7f0d-9f7c-637f593d1336/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs":{}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"补充[]\"}",
 "content": {
  "echo": "补充[]"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 3,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-11-03T07:58:26.700945703Z"
}
```

## step 5 （前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a1a06a-a77c-7f0d-9f7c-637f593d1336/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version":0}'
```

HTTP 204（Content-Type: -）

```json
(空)
```

## step 6 提交生成

```bash
curl -s -i -X POST 'http://localhost:8081/api/app/ai/generations' \
  -H "X-API-Key: $APP_API_KEY" \
  -H 'X-Anonymous-Id: anon-fc0afb74' \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"t_ee8aaa3a","clientRequestId":"c-ab57ae1a-7213-40df-828d-9957a0128646","inputs":{"user_input":"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"}}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "requestId": "01a1a06a-a7ec-766f-afbb-36165da0c011",
 "clientRequestId": "c-ab57ae1a-7213-40df-828d-9957a0128646",
 "sceneId": "t_ee8aaa3a",
 "status": "SUCCESS",
 "content": {
  "echo": "补充[aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa]"
 }
}
```
