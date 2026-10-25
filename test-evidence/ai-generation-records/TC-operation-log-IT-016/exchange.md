# TC-operation-log-IT-016 请求/响应存证

> AI 测试连接与模板测试不产生留痕
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。
> 共享前置 fixture（建法见 TC-ai-IT-065 存证 step 1~12）：场景 S=`gr_3f05e38d`（模板 T1 `01a17341-31b2-7887-a69c-5ae04440c5fb` seq 1，已发布，主模型 mock-ok）；场景 S2=`gr_f19de32d`（模板 T2 `01a17341-3281-783b-ba62-0b24d9439be5`，已发布）。
> 模型映射：用例中的 model-a → `mock-ok`（本轮 AI_MODELS 不含 model-a，沿用 ai-management 轮口径）。

## step 1 基线 ai-scene

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-scene&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
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
  },
  {
   "id": "01a17334-7d1c-7d9b-ac5c-3aadb82473ed",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:16:22.17275Z"
  }
 ],
 "page": 1,
 "size": 200,
 "totalElements": 20,
 "totalPages": 1
}
```

## step 2 基线 ai-prompt

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

## step 3 测试连接

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/models/test-connection' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"model": "mock-ok"}'
```

HTTP 200（Content-Type: application/json）

```
{
 "ok": true,
 "latencyMs": 4,
 "message": "可达"
}
```

## step 4 模板测试

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/ai/prompt-templates/01a17341-31b2-7887-a69c-5ae04440c5fb/test' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"inputs": {"age": 31}}'
```

HTTP 200（Content-Type: application/json）

```
{
 "success": true,
 "model": "mock-ok",
 "output": "{\"echo\":\"年龄31\"}",
 "content": {
  "echo": "年龄31"
 },
 "errorCode": null,
 "errorMessage": null,
 "latencyMs": 4,
 "promptTokens": 12,
 "completionTokens": 6,
 "testedAt": "2026-10-25T13:30:32.221790738Z"
}
```

## step 5 2s 后 ai-scene

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/logs/page?module=ai-scene&page=1&size=200' \
  -H "Authorization: Bearer $TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
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
  },
  {
   "id": "01a17334-7d1c-7d9b-ac5c-3aadb82473ed",
   "username": "admin",
   "module": "ai-scene",
   "action": "create",
   "target": null,
   "createdAt": "2026-10-25T13:16:22.17275Z"
  }
 ],
 "page": 1,
 "size": 200,
 "totalElements": 20,
 "totalPages": 1
}
```

## step 6 2s 后 ai-prompt

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
