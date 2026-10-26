# TC-ai-IT-036 请求/响应存证

> MEMBER 角色可访问 AI 管理接口
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-26　执行器: api-test-runner（change ai-limit-config）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行；psql 需 `export PGPASSWORD=iris`。
> 备注: 共享前置：admin 登录；创建 MEMBER `u_lcc015d5fa`（fixture 密码 `Member#2026x`）；场景 `t_lcc015d5fa`（主模型 mock-ok，模板 `01a17740-7cd0-7896-984f-a931fd3cf6e7` 已测试并发布，变量 age 必填）。完整前置请求见 `test-evidence/ai-limit-config/_setup/exchange.md`。

## step 1 登录 u_lcc015d5fa

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"username": "u_lcc015d5fa", "password": "Member#2026x"}'
```

HTTP 200（Content-Type: application/json）

```json
{
 "token": "$TOKEN",
 "manager": {
  "id": "01a17740-7c39-7f46-aa1e-e91e67673bf7",
  "username": "u_lcc015d5fa",
  "nickname": "限流成员",
  "role": "MEMBER"
 }
}
```

## step 2 MEMBER GET 场景分页

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/page?page=1&size=20' \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
{
 "content": [
  {
   "id": "01a17740-7c6f-7472-abe9-7ec4ef7a12eb",
   "sceneId": "t_lcc015d5fa",
   "name": "限流配置用例",
   "description": null,
   "enabled": true,
   "primaryModel": "mock-ok",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a17740-7cd0-7896-984f-a931fd3cf6e7",
    "seq": 1,
    "name": "模板",
    "publishedAt": "2026-10-26T08:07:57.58074Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-26T08:07:57.293528Z",
   "updatedAt": "2026-10-26T08:07:57.581331Z"
  },
  {
   "id": "01a1773a-0979-794c-9928-c8a412e80345",
   "sceneId": "t_3618009933f3",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.649526Z",
   "updatedAt": "2026-10-26T08:00:54.649526Z"
  },
  {
   "id": "01a1773a-0969-74fe-ae8a-0729d02ef4f8",
   "sceneId": "t_496e49d3e454",
   "name": "周期建议",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.633252Z",
   "updatedAt": "2026-10-26T08:00:54.633252Z"
  },
  {
   "id": "01a1773a-0948-7c06-911a-2f20cf0beaf6",
   "sceneId": "t_2eab7839c43a",
   "name": "新名称",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.600707Z",
   "updatedAt": "2026-10-26T08:00:54.616312Z"
  },
  {
   "id": "01a1773a-0932-79d2-a5f5-dc380131a322",
   "sceneId": "t_e3c6d7d3388d",
   "name": "测试场景",
   "description": null,
   "enabled": false,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.578563Z",
   "updatedAt": "2026-10-26T08:00:54.590997Z"
  },
  {
   "id": "01a1773a-091a-7196-a2fb-f842e76b6679",
   "sceneId": "t_a2570ad1f46a_off",
   "name": "禁用场景",
   "description": null,
   "enabled": false,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.554052Z",
   "updatedAt": "2026-10-26T08:00:54.557685Z"
  },
  {
   "id": "01a1773a-0916-7a64-9b9e-0468d9388631",
   "sceneId": "t_a2570ad1f46a_on",
   "name": "启用场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.5506Z",
   "updatedAt": "2026-10-26T08:00:54.5506Z"
  },
  {
   "id": "01a1773a-08f5-74e9-8c84-3d455198d5ea",
   "sceneId": "t_e5a4d3170487",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.517258Z",
   "updatedAt": "2026-10-26T08:00:54.517258Z"
  },
  {
   "id": "01a1773a-08d7-782c-80dd-d9d242021a7d",
   "sceneId": "t_e0e049a87ef2",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.487458Z",
   "updatedAt": "2026-10-26T08:00:54.487458Z"
  },
  {
   "id": "01a1773a-08ce-7dec-a0d4-83c9a8e2c128",
   "sceneId": "t_283923441205",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.478824Z",
   "updatedAt": "2026-10-26T08:00:54.478824Z"
  },
  {
   "id": "01a1773a-08c3-7a59-a801-2e8babae90bf",
   "sceneId": "t_499d8a2f7d3a",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.467609Z",
   "updatedAt": "2026-10-26T08:00:54.467609Z"
  },
  {
   "id": "01a1773a-08b1-7bf1-abc3-5ebe8052783f",
   "sceneId": "t_c63863371b8a",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.449694Z",
   "updatedAt": "2026-10-26T08:00:54.452973Z"
  },
  {
   "id": "01a1773a-08a2-79a7-876a-553cc28fea41",
   "sceneId": "t_eb1e030b63ce",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.434567Z",
   "updatedAt": "2026-10-26T08:00:54.434567Z"
  },
  {
   "id": "01a1773a-0891-7034-8590-d5375b54e11b",
   "sceneId": "t_00f8a510987a",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.41697Z",
   "updatedAt": "2026-10-26T08:00:54.419884Z"
  },
  {
   "id": "01a1773a-086f-7986-b0ff-bf01526442d3",
   "sceneId": "t_aa82a5c4b49a",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a1773a-0877-7097-bc53-75fc73787d51",
    "seq": 1,
    "name": "模板",
    "publishedAt": "2026-10-26T08:00:54.403869Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-26T08:00:54.383525Z",
   "updatedAt": "2026-10-26T08:00:54.404236Z"
  },
  {
   "id": "01a1773a-084b-7946-aa98-bc27a4457388",
   "sceneId": "t_6dd1483d447a",
   "name": "测试场景",
   "description": null,
   "enabled": false,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a1773a-0852-7ecd-80f8-a9a8c8172bb5",
    "seq": 1,
    "name": "模板",
    "publishedAt": "2026-10-26T08:00:54.372024Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-26T08:00:54.347525Z",
   "updatedAt": "2026-10-26T08:00:54.372403Z"
  },
  {
   "id": "01a1773a-0835-7450-85e0-d29400c33924",
   "sceneId": "t_cc39283d88d6",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.325227Z",
   "updatedAt": "2026-10-26T08:00:54.328495Z"
  },
  {
   "id": "01a1773a-0817-77f3-8e3b-29cfe140d735",
   "sceneId": "t_8c4e684a52e0",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.295428Z",
   "updatedAt": "2026-10-26T08:00:54.299141Z"
  },
  {
   "id": "01a1773a-07fc-7d01-887b-0eef844fe3ad",
   "sceneId": "t_4bc4f8978eee",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-26T08:00:54.26871Z",
   "updatedAt": "2026-10-26T08:00:54.27238Z"
  },
  {
   "id": "01a1773a-07c6-74df-99b6-de31529f9b95",
   "sceneId": "t_1d7d144a27cc",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a1773a-07cd-7840-af9e-9139d985a1a4",
    "seq": 1,
    "name": "模板",
    "publishedAt": "2026-10-26T08:00:54.255831Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-26T08:00:54.214256Z",
   "updatedAt": "2026-10-26T08:00:54.256089Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 281,
 "totalPages": 15
}
```

## step 3 MEMBER GET 模型列表

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/models' \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```json
[
 "mock-ok",
 "mock-fenced",
 "mock-format-error",
 "mock-empty",
 "mock-refused",
 "mock-429",
 "mock-500",
 "mock-timeout",
 "mock-flaky",
 "mock-slow"
]
```
