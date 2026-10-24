# TC-operation-log-IT-015 请求/响应存证

> 创建 AI 场景后异步产生 ai-scene:create 留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-24　执行器: api-test-runner（change ai-management）
> 认证: admin 走 `POST /api/admin/auth/login`（fixture: admin / 8@y2eoRLyStM*UVU），JWT 记为 `$TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 `export TOKEN=... APP_API_KEY=...` 后下列 curl 可原样执行。
> 模型映射: 用例中的 model-a → `mock-ok`，model-b → `mock-fenced`，model-x → `model-x`（不在 AI_MODELS）。

## step 1 创建场景

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId":"log_scene_dedab452","name":"留痕场景"}'
```

HTTP 201（Content-Type: application/json）

```json
{
 "id": "01a16f0a-673b-72f9-9989-0b24a256e48c",
 "sceneId": "log_scene_dedab452",
 "name": "留痕场景",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-24T17:51:55.195141077Z",
 "updatedAt": "2026-10-24T17:51:55.195141077Z"
}
```

## step 2 轮询 ai-scene 日志（最多 20 次/100ms，存证为最后一次）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=ai-scene&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a16f0a-673d-7738-aca5-64399f4f6f9d",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:55.197408Z"
  },
  {
   "id": "01a16f0a-6684-78b0-96d5-f2e5e9a08db4",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-6675-7301-8abc-88e0bdaeb498",
   "createdAt": "2026-10-24T17:51:55.012501Z"
  },
  {
   "id": "01a16f0a-6677-7a03-b580-8cd839146281",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.999587Z"
  },
  {
   "id": "01a16f0a-65fa-730f-a463-02e24e2a1e5f",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-65eb-7e91-abb9-34ddba7ef6b3",
   "createdAt": "2026-10-24T17:51:54.874144Z"
  },
  {
   "id": "01a16f0a-65ee-750d-831b-f8056cc9fcd4",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.862282Z"
  },
  {
   "id": "01a16f0a-6502-73c2-8340-54bd4c44989d",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-64f2-76e0-ab76-c52c584b328c",
   "createdAt": "2026-10-24T17:51:54.626181Z"
  },
  {
   "id": "01a16f0a-64f5-72b9-9fe9-81449e025ad2",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.613121Z"
  },
  {
   "id": "01a16f0a-6461-7a63-9293-6fbf16336325",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-6452-7987-ac3e-1024ee87d1a7",
   "createdAt": "2026-10-24T17:51:54.465604Z"
  },
  {
   "id": "01a16f0a-6455-795c-8780-5d41cdb33f9f",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:54.45354Z"
  },
  {
   "id": "01a16f0a-4c86-7168-b516-e1467b4d7988",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4c77-7056-8ba2-3819730b97ec",
   "createdAt": "2026-10-24T17:51:48.358036Z"
  },
  {
   "id": "01a16f0a-4c79-7f59-b47f-4f6303f25b8f",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.345916Z"
  },
  {
   "id": "01a16f0a-4c14-7a92-b316-795af83001ac",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4c05-7dca-95fa-534e16f6008f",
   "createdAt": "2026-10-24T17:51:48.244615Z"
  },
  {
   "id": "01a16f0a-4c08-760a-a573-06c4cb3210b4",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.232327Z"
  },
  {
   "id": "01a16f0a-4ba4-7a68-abbf-83042c9515ed",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4b96-7810-b4f9-73c44d887468",
   "createdAt": "2026-10-24T17:51:48.132606Z"
  },
  {
   "id": "01a16f0a-4b98-7f4f-a386-67fc64c516b6",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.12091Z"
  },
  {
   "id": "01a16f0a-4b37-7359-8f43-b2b40e1eeaad",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4b28-7363-b290-ade778dd0ac6",
   "createdAt": "2026-10-24T17:51:48.02317Z"
  },
  {
   "id": "01a16f0a-4b2a-7d39-9b98-347ebeb4985d",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:48.010761Z"
  },
  {
   "id": "01a16f0a-4b10-7b64-ae45-073ab2090212",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-enabled",
   "target": "01a16f0a-4a9b-7449-8178-fd38b217b615",
   "createdAt": "2026-10-24T17:51:47.984659Z"
  },
  {
   "id": "01a16f0a-4aaa-76f3-903c-ca1026cc3122",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a16f0a-4a9b-7449-8178-fd38b217b615",
   "createdAt": "2026-10-24T17:51:47.882379Z"
  },
  {
   "id": "01a16f0a-4a9d-7d82-8aff-ec74e092de1e",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-24T17:51:47.869787Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 128,
 "totalPages": 7
}
```
