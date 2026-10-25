# TC-ai-IT-034 请求/响应存证

> 发布模板产生 ai-prompt:publish 留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 （前置） 创建场景 gr_1410a443

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_1410a443", "name": "发布留痕场景"}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-5158-7f9c-9642-3920e8ce61d1",
 "sceneId": "gr_1410a443",
 "name": "发布留痕场景",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-25T13:30:22.936894Z",
 "updatedAt": "2026-10-25T13:30:22.936894Z"
}
```

## step 2 （前置） 配置主模型 mock-ok

```bash
curl -s -i -X PUT 'http://localhost:21423/api/admin/ai/scenes/01a17341-5158-7f9c-9642-3920e8ce61d1/models' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"primaryModel": "mock-ok"}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 3 （前置） 新建模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "gr_1410a443", "name": "模板-gr_1410a443", "systemPrompt": "你是助手，只输出 JSON", "userPromptTemplate": "年龄{{age}}", "variables": [{"name": "age", "required": true, "description": "年龄"}]}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-5175-7d0b-a390-792e2d01153e",
 "sceneId": "gr_1410a443",
 "seq": 1,
 "name": "模板-gr_1410a443",
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
 "createdAt": "2026-10-25T13:30:22.96572929Z",
 "updatedAt": "2026-10-25T13:30:22.96572929Z",
 "version": 0
}
```

## step 4 （前置） 测试模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-5175-7d0b-a390-792e2d01153e/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 28}}'
```

HTTP 200（Content-Type: application/json）

```
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"年龄28\"}",
 "content": {
  "echo": "年龄28"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 4,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-25T13:30:22.985965092Z"
}
```

## step 5 （前置） 模板详情取 version

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-5175-7d0b-a390-792e2d01153e' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "id": "01a17341-5175-7d0b-a390-792e2d01153e",
 "sceneId": "gr_1410a443",
 "seq": 1,
 "name": "模板-gr_1410a443",
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
 "tested": true,
 "lastTestInputs": {
  "age": 28
 },
 "lastTestOutput": "{\"echo\":\"年龄28\"}",
 "lastTestContent": {
  "echo": "年龄28"
 },
 "lastTestModel": "mock-ok",
 "lastTestSuccess": true,
 "lastTestError": null,
 "lastTestLatencyMs": 4,
 "lastTestAt": "2026-10-25T13:30:22.985965Z",
 "lastTestBy": "admin",
 "publishedAt": null,
 "publishedBy": null,
 "createdBy": "admin",
 "updatedBy": "admin",
 "createdAt": "2026-10-25T13:30:22.965729Z",
 "updatedAt": "2026-10-25T13:30:22.965729Z",
 "version": 0
}
```

## step 6 （前置） 发布模板

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-5175-7d0b-a390-792e2d01153e/publish' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"version": 0}'
```

HTTP 204（Content-Type: -）

```
(空)
```

## step 7 轮询 ai-prompt 日志（最多 20 次/100ms，存证为最后一次）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=ai-prompt&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17341-51a9-7e81-afd0-8f1c028020f3",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a17341-5175-7d0b-a390-792e2d01153e",
   "createdAt": "2026-10-25T13:30:23.017849Z"
  },
  {
   "id": "01a17341-5179-7b07-a6e1-0069f00d0316",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:22.969626Z"
  },
  {
   "id": "01a17341-4eda-7b6c-9a72-b09a75971b1d",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a17341-4ea3-797c-8469-f0cf57df5f6f",
   "createdAt": "2026-10-25T13:30:22.298656Z"
  },
  {
   "id": "01a17341-4ea7-709a-bd69-7fd387097d77",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:22.24699Z"
  },
  {
   "id": "01a17341-37cd-7d2a-8350-2a37b9b1b094",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a17341-3795-740e-aedb-5226999e0a7f",
   "createdAt": "2026-10-25T13:30:16.397739Z"
  },
  {
   "id": "01a17341-3799-7a9e-b50f-ba24859bf0e9",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:16.345606Z"
  },
  {
   "id": "01a17341-32be-7dee-b35f-96c2813f89bc",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a17341-3281-783b-ba62-0b24d9439be5",
   "createdAt": "2026-10-25T13:30:15.102753Z"
  },
  {
   "id": "01a17341-3285-7ab6-b7d6-ea981dabc20d",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:15.045604Z"
  },
  {
   "id": "01a17341-3256-7a53-a471-6ef1d5233625",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a17341-31b2-7887-a69c-5ae04440c5fb",
   "createdAt": "2026-10-25T13:30:14.998589Z"
  },
  {
   "id": "01a17341-31b7-72aa-906b-964eb0cd9ea9",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:14.8391Z"
  },
  {
   "id": "01a1733e-e486-77df-8c59-31d04347ea23",
   "username": "admin",
   "module": "ai-prompt",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:27:44.006421Z"
  },
  {
   "id": "01a17334-7d50-73be-a7dc-2eb488ccea94",
   "username": "admin",
   "module": "ai-prompt",
   "action": "publish",
   "target": "01a17334-7d33-7372-b37e-fb0a4c0d78c2",
   "createdAt": "2026-10-25T13:16:22.224163Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 12,
 "totalPages": 1
}
```
