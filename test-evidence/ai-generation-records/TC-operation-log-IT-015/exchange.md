# TC-operation-log-IT-015 请求/响应存证

> 创建 AI 场景后异步产生 ai-scene:create 留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 创建场景

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/scenes' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"sceneId": "log_scene_9d647730", "name": "留痕场景"}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-df65-7208-a54e-9bb30fe775aa",
 "sceneId": "log_scene_9d647730",
 "name": "留痕场景",
 "description": null,
 "enabled": true,
 "primaryModel": null,
 "backupModel": null,
 "activeTemplate": null,
 "createdAt": "2026-10-25T13:30:59.301053312Z",
 "updatedAt": "2026-10-25T13:30:59.301053312Z"
}
```

## step 2 轮询 ai-scene 日志（最多 20 次/100ms，存证为最后一次）

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?username=admin&module=ai-scene&page=1&size=20' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17341-df68-7efe-8a09-1a8552281360",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:59.30488Z"
  },
  {
   "id": "01a17341-6c68-752b-a4d3-66ec00e758ef",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:29.864263Z"
  },
  {
   "id": "01a17341-516a-75ca-a027-95e5776c03c6",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a17341-5158-7f9c-9642-3920e8ce61d1",
   "createdAt": "2026-10-25T13:30:22.954305Z"
  },
  {
   "id": "01a17341-515c-7bf5-bfed-cad3d596d5e3",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:22.940676Z"
  },
  {
   "id": "01a17341-4ee7-7f65-be2f-976351d33807",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a17341-4e82-7fe3-83f4-8c4105669272",
   "createdAt": "2026-10-25T13:30:22.311912Z"
  },
  {
   "id": "01a17341-4e97-7179-a690-49e1a18a8496",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a17341-4e82-7fe3-83f4-8c4105669272",
   "createdAt": "2026-10-25T13:30:22.231031Z"
  },
  {
   "id": "01a17341-4e87-7e2a-8d1d-0829130eac05",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:22.215811Z"
  },
  {
   "id": "01a17341-37db-7eff-af8f-48696d3338a6",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a17341-3775-72c8-8236-bd5c1ffd99b7",
   "createdAt": "2026-10-25T13:30:16.411881Z"
  },
  {
   "id": "01a17341-3788-7634-81f7-648bc8a1fc92",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a17341-3775-72c8-8236-bd5c1ffd99b7",
   "createdAt": "2026-10-25T13:30:16.328334Z"
  },
  {
   "id": "01a17341-3779-732e-bde4-a47f01badb2c",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:16.313139Z"
  },
  {
   "id": "01a17341-3275-7359-92db-264ece718399",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a17341-3261-7585-a517-fae5afe95aa0",
   "createdAt": "2026-10-25T13:30:15.029127Z"
  },
  {
   "id": "01a17341-3265-71c6-9b2b-dc2038d51adf",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:15.013009Z"
  },
  {
   "id": "01a17341-31a5-7e57-a013-a454b461168c",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a17341-3184-794e-af22-f8fb7612770c",
   "createdAt": "2026-10-25T13:30:14.821811Z"
  },
  {
   "id": "01a17341-3196-73a0-a74e-e183bb9ad31b",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:30:14.806148Z"
  },
  {
   "id": "01a1733e-e443-7063-8239-e344fb9955d0",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-models",
   "target": "01a1733e-e41f-7856-8162-65e656f372b9",
   "createdAt": "2026-10-25T13:27:43.938953Z"
  },
  {
   "id": "01a1733e-e423-7cdd-8392-d7c8be8a4db5",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:27:43.90773Z"
  },
  {
   "id": "01a1733e-aeaf-79fa-a33f-1c334f111845",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:27:30.22229Z"
  },
  {
   "id": "01a17334-8113-7f4d-b768-14096cecb660",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:16:23.18791Z"
  },
  {
   "id": "01a17334-8101-75f7-bb2d-02cf5130a5ce",
   "username": "admin",
   "module": "ai-scene",
   "action": "update",
   "target": "01a17334-80cf-76e3-b76b-1189e4601879",
   "createdAt": "2026-10-25T13:16:23.169316Z"
  },
  {
   "id": "01a17334-80c8-7ac6-8e63-2c215b77fb41",
   "username": "admin",
   "module": "ai-scene",
   "action": "set-enabled",
   "target": "01a17334-80b6-701f-b49f-997835a4a92f",
   "createdAt": "2026-10-25T13:16:23.112606Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 21,
 "totalPages": 2
}
```
