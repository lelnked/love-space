# TC-ai-IT-035 请求/响应存证

> 测试模板不产生留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。

## step 1 基线 ai-prompt 日志

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-prompt&page=1&size=200' \
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
 "size": 200,
 "totalElements": 12,
 "totalPages": 1
}
```

## step 2 测试模板 T1

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-31b2-7887-a69c-5ae04440c5fb/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 30}}'
```

HTTP 200（Content-Type: application/json）

```
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"年龄30\"}",
 "content": {
  "echo": "年龄30"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 4,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-25T13:30:23.07537542Z"
}
```

## step 3 2s 后复查

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-prompt&page=1&size=200' \
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
 "size": 200,
 "totalElements": 12,
 "totalPages": 1
}
```
