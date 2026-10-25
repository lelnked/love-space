# TC-ai-IT-036 请求/响应存证

> MEMBER 角色可访问 AI 管理接口
> 环境: admin `http://localhost:21423`(test profile) / app `http://localhost:8081` / 假方舟 `http://localhost:18080`（scripts/mock-ark.mjs），库 `localhost:25432/love_space`
> 执行时间: 2026-10-25　执行器: api-test-runner（change ai-generation-records）
> 认证: admin JWT 记为 `$TOKEN`、MEMBER JWT 记为 `$MEMBER_TOKEN`（脱敏）；app 端 `X-API-Key: $APP_API_KEY`（脱敏）。shell 中 export 后下列 curl 可原样执行。psql 命令需 `export PGPASSWORD=iris`。

## step 1 （前置） ADMIN 创建 MEMBER 账号 u_gr6f1a1eac

```bash
curl -s -i -X POST 'http://localhost:21423/api/admin/managers' \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"username": "u_gr6f1a1eac", "password": "Member#2026x", "nickname": "生成记录成员"}'
```

HTTP 201（Content-Type: application/json）

```
{
 "id": "01a17341-5a27-79d3-a7c2-e4dbe9999823",
 "username": "u_gr6f1a1eac",
 "nickname": "生成记录成员",
 "role": "MEMBER",
 "enable": true,
 "createdAt": "2026-10-25T13:30:25.191525484Z"
}
```

## step 2 MEMBER 登录 u_gr6f1a1eac

```bash
curl -s -X POST 'http://localhost:21423/api/admin/auth/login' -H 'Content-Type: application/json' -d '{"username":"u_gr6f1a1eac","password":"Member#2026x"}'
```

HTTP 200（Content-Type: application/json）

```
{"token": "$MEMBER_TOKEN", ...}
```

## step 3 MEMBER GET 场景分页

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/scenes/page?page=1&size=20' \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```
{
 "content": [
  {
   "id": "01a17341-5158-7f9c-9642-3920e8ce61d1",
   "sceneId": "gr_1410a443",
   "name": "发布留痕场景",
   "description": null,
   "enabled": true,
   "primaryModel": "mock-ok",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a17341-5175-7d0b-a390-792e2d01153e",
    "seq": 1,
    "name": "模板-gr_1410a443",
    "publishedAt": "2026-10-25T13:30:23.013243Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-25T13:30:22.936894Z",
   "updatedAt": "2026-10-25T13:30:23.013639Z"
  },
  {
   "id": "01a17341-4e82-7fe3-83f4-8c4105669272",
   "sceneId": "gr_b2fa9991",
   "name": "格式错误场景",
   "description": null,
   "enabled": true,
   "primaryModel": "mock-format-error",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a17341-4ea3-797c-8469-f0cf57df5f6f",
    "seq": 1,
    "name": "模板-gr_b2fa9991",
    "publishedAt": "2026-10-25T13:30:22.293859Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-25T13:30:22.210918Z",
   "updatedAt": "2026-10-25T13:30:22.30887Z"
  },
  {
   "id": "01a17341-3775-72c8-8236-bd5c1ffd99b7",
   "sceneId": "gr_bc04b5dc",
   "name": "flaky场景",
   "description": null,
   "enabled": true,
   "primaryModel": "mock-flaky",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a17341-3795-740e-aedb-5226999e0a7f",
    "seq": 1,
    "name": "模板-gr_bc04b5dc",
    "publishedAt": "2026-10-25T13:30:16.392462Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-25T13:30:16.309092Z",
   "updatedAt": "2026-10-25T13:30:16.408431Z"
  },
  {
   "id": "01a17341-3261-7585-a517-fae5afe95aa0",
   "sceneId": "gr_f19de32d",
   "name": "生成记录场景S2",
   "description": null,
   "enabled": true,
   "primaryModel": "mock-ok",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a17341-3281-783b-ba62-0b24d9439be5",
    "seq": 1,
    "name": "模板-gr_f19de32d",
    "publishedAt": "2026-10-25T13:30:15.097529Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-25T13:30:15.009225Z",
   "updatedAt": "2026-10-25T13:30:15.097925Z"
  },
  {
   "id": "01a17341-3184-794e-af22-f8fb7612770c",
   "sceneId": "gr_3f05e38d",
   "name": "生成记录场景S",
   "description": null,
   "enabled": true,
   "primaryModel": "mock-ok",
   "backupModel": null,
   "activeTemplate": {
    "id": "01a17341-31b2-7887-a69c-5ae04440c5fb",
    "seq": 1,
    "name": "模板-gr_3f05e38d",
    "publishedAt": "2026-10-25T13:30:14.992972Z",
    "publishedBy": "admin"
   },
   "createdAt": "2026-10-25T13:30:14.788503Z",
   "updatedAt": "2026-10-25T13:30:14.993427Z"
  },
  {
   "id": "01a1733e-e41f-7856-8162-65e656f372b9",
   "sceneId": "gr_probe_7390b7ea",
   "name": "probe",
   "description": null,
   "enabled": true,
   "primaryModel": "mock-ok",
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:27:43.903447Z",
   "updatedAt": "2026-10-25T13:27:43.928272Z"
  },
  {
   "id": "01a1733e-ae92-7e51-8502-da7cc4980bcd",
   "sceneId": "gr_probe_3e12051d",
   "name": "probe",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:27:30.180514Z",
   "updatedAt": "2026-10-25T13:27:30.180514Z"
  },
  {
   "id": "01a17334-811f-7665-b7f6-fa70d83a15b3",
   "sceneId": "t_228574b70fb2",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:23.199346Z",
   "updatedAt": "2026-10-25T13:16:23.199346Z"
  },
  {
   "id": "01a17334-8111-7218-9bdc-d66ef1e78ee8",
   "sceneId": "t_ecbc1992e8d0",
   "name": "周期建议",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:23.185082Z",
   "updatedAt": "2026-10-25T13:16:23.185082Z"
  },
  {
   "id": "01a17334-80cf-76e3-b76b-1189e4601879",
   "sceneId": "t_fd3c8a59ace4",
   "name": "新名称",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:23.119388Z",
   "updatedAt": "2026-10-25T13:16:23.163097Z"
  },
  {
   "id": "01a17334-80b6-701f-b49f-997835a4a92f",
   "sceneId": "t_ae59da9194b8",
   "name": "测试场景",
   "description": null,
   "enabled": false,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:23.093961Z",
   "updatedAt": "2026-10-25T13:16:23.108561Z"
  },
  {
   "id": "01a17334-809d-7d24-9dfd-be1d9081f06b",
   "sceneId": "t_44541ad79a72_off",
   "name": "禁用场景",
   "description": null,
   "enabled": false,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:23.069749Z",
   "updatedAt": "2026-10-25T13:16:23.074263Z"
  },
  {
   "id": "01a17334-8098-7af5-af69-343fa285c923",
   "sceneId": "t_44541ad79a72_on",
   "name": "启用场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:23.064543Z",
   "updatedAt": "2026-10-25T13:16:23.064543Z"
  },
  {
   "id": "01a17334-8060-7b65-99f5-bff4d510c008",
   "sceneId": "t_e7edc7429d99",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:23.008669Z",
   "updatedAt": "2026-10-25T13:16:23.008669Z"
  },
  {
   "id": "01a17334-8040-79e5-964d-3b4a1662a1d2",
   "sceneId": "t_51bf5fcb29f5",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:22.976538Z",
   "updatedAt": "2026-10-25T13:16:22.976538Z"
  },
  {
   "id": "01a17334-8035-705e-9074-5fa5fba5377d",
   "sceneId": "t_216cf59d447b",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:22.964967Z",
   "updatedAt": "2026-10-25T13:16:22.964967Z"
  },
  {
   "id": "01a17334-8026-7d2e-9d7b-4460a6b86b9f",
   "sceneId": "t_1352c429f10f",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:22.95075Z",
   "updatedAt": "2026-10-25T13:16:22.95075Z"
  },
  {
   "id": "01a17334-8012-7342-8644-77c30e6b76d6",
   "sceneId": "t_3caa412a7e7a",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:22.930135Z",
   "updatedAt": "2026-10-25T13:16:22.933517Z"
  },
  {
   "id": "01a17334-7fff-7b75-b1ef-30d626960bd4",
   "sceneId": "t_9560b751ac4e",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": null,
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:22.91164Z",
   "updatedAt": "2026-10-25T13:16:22.91164Z"
  },
  {
   "id": "01a17334-7fe9-70e3-9a39-e322f6bb7b4c",
   "sceneId": "t_7073aaa5286b",
   "name": "测试场景",
   "description": null,
   "enabled": true,
   "primaryModel": "model-a",
   "backupModel": null,
   "activeTemplate": null,
   "createdAt": "2026-10-25T13:16:22.888997Z",
   "updatedAt": "2026-10-25T13:16:22.892545Z"
  }
 ],
 "page": 1,
 "size": 20,
 "totalElements": 238,
 "totalPages": 12
}
```

## step 4 MEMBER GET 模型列表

```bash
curl -s -i -X GET 'http://localhost:21423/api/admin/ai/models' \
  -H "Authorization: Bearer $MEMBER_TOKEN"
```

HTTP 200（Content-Type: application/json）

```
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
